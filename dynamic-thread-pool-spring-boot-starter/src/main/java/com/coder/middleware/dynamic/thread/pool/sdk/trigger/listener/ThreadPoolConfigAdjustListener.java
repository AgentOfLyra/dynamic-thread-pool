package com.coder.middleware.dynamic.thread.pool.sdk.trigger.listener;

import com.alibaba.fastjson.JSON;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolInterface;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.coder.middleware.dynamic.thread.pool.sdk.registry.RegistryInterface;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author agentoflyra
 * @description
 * @date 2025/9/26
 */
public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

    private final Logger logger = LoggerFactory.getLogger(ThreadPoolConfigAdjustListener.class);

    private final DynamicThreadPoolInterface dynamicThreadPool;

    private final RegistryInterface registry;

    public ThreadPoolConfigAdjustListener(DynamicThreadPoolInterface dynamicThreadPool, RegistryInterface registry) {
        this.dynamicThreadPool = dynamicThreadPool;
        this.registry = registry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        logger.info("动态线程池调整配置信息。" +
                "线程池名称：{}" +
                "核心线程数：{}" +
                "最大线程数：{}", threadPoolConfigEntity.getThreadPoolName(),
                threadPoolConfigEntity.getCorePoolSize(),
                threadPoolConfigEntity.getMaximumPoolSize());
        dynamicThreadPool.updateThreadPoolConfig(threadPoolConfigEntity);

        // 更新后上报最新数据
        registry.reportThreadPool(dynamicThreadPool.queryAllThreadPoolConfig());

        ThreadPoolConfigEntity threadPoolConfigEntityCurrent = dynamicThreadPool.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        registry.reportThreadPoolConfigParameter(threadPoolConfigEntityCurrent);
        logger.info("动态线程池上报线程池配置信息：{}", JSON.toJSONString(threadPoolConfigEntityCurrent));
    }
}
