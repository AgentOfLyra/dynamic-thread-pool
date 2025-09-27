package com.coder.middleware.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson.JSON;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolImpl;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolInterface;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import com.coder.middleware.dynamic.thread.pool.sdk.registry.RegistryInterface;
import com.coder.middleware.dynamic.thread.pool.sdk.registry.redis.RedisRegistryImpl;
import com.coder.middleware.dynamic.thread.pool.sdk.trigger.job.ThreadPoolDataReportJob;
import com.coder.middleware.dynamic.thread.pool.sdk.trigger.listener.ThreadPoolConfigAdjustListener;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description 
 * @author agentoflyra
 * @date 2025/9/25
 */
@Configuration
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
@EnableScheduling
public class DynamicThreadPoolAutoConfig {
    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolAutoConfig.class);

    private String applicationName;


    @Bean("dynamicThreadRedissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties dynamicThreadPoolAutoProperties) {
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + dynamicThreadPoolAutoProperties.getHost() + ":" + dynamicThreadPoolAutoProperties.getPort())
                .setPassword(dynamicThreadPoolAutoProperties.getPassword())
                .setConnectionPoolSize(dynamicThreadPoolAutoProperties.getPoolSize())
                .setConnectionMinimumIdleSize(dynamicThreadPoolAutoProperties.getMinIdleSize())
                .setIdleConnectionTimeout(dynamicThreadPoolAutoProperties.getIdleTimeout())
                .setRetryAttempts(dynamicThreadPoolAutoProperties.getRetryAttempts())
                .setRetryInterval(dynamicThreadPoolAutoProperties.getRetryInterval())
                .setPingConnectionInterval(dynamicThreadPoolAutoProperties.getPingInterval())
                .setKeepAlive(dynamicThreadPoolAutoProperties.isKeepAlive());

        RedissonClient redissonClient = Redisson.create(config);

        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", dynamicThreadPoolAutoProperties.getHost(), dynamicThreadPoolAutoProperties.getPoolSize(), !redissonClient.isShutdown());


        return redissonClient;
    }

    @Bean
    public RegistryInterface redisRegistry(RedissonClient dynamicThreadRedissonClient) {
        return new RedisRegistryImpl(dynamicThreadRedissonClient);
    }

    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolInterface dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warn("动态线程池已经启动，但是Spring Boot应用未配置，无法获取到spring.application.name");
        }
        logger.info("线程池信息：{}", JSON.toJSONString(threadPoolExecutorMap.keySet()));
        // 获取缓存数据，更新本地线程池
        for (Map.Entry<String, ThreadPoolExecutor> entry : threadPoolExecutorMap.entrySet()) {
            String threadPoolName = entry.getKey();
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPoolName).get();
            if (threadPoolConfigEntity == null) continue;
            ThreadPoolExecutor threadPoolExecutor = entry.getValue();
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
        }
        return new DynamicThreadPoolImpl(threadPoolExecutorMap, applicationName);
    }

    @Bean
    public ThreadPoolDataReportJob  threadPoolDataReportJob(DynamicThreadPoolInterface dynamicThreadPool, RegistryInterface redisRegistry) {
        return new ThreadPoolDataReportJob(dynamicThreadPool, redisRegistry);
    }

    @Bean
    public ThreadPoolConfigAdjustListener poolConfigAdjustListener(DynamicThreadPoolInterface dynamicThreadPool, RegistryInterface registryInterface) {
        return new ThreadPoolConfigAdjustListener(dynamicThreadPool, registryInterface);
    }

    @Bean("dynamicThreadPoolRedisTopic")
    public RTopic dynamicThreadPoolRedisTopic(RedissonClient redissonClient, ThreadPoolConfigAdjustListener poolConfigAdjustListener) {
        RTopic dynamicThreadPoolRedisTopic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + applicationName);
        dynamicThreadPoolRedisTopic.addListener(ThreadPoolConfigEntity.class, poolConfigAdjustListener);
        return dynamicThreadPoolRedisTopic;
    }
}
