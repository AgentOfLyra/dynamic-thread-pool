package com.coder.middleware.dynamic.thread.pool.sdk.trigger.job;

import com.alibaba.fastjson.JSON;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.DynamicThreadPoolInterface;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.coder.middleware.dynamic.thread.pool.sdk.registry.RegistryInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * @author agentoflyra
 * @description 线程池数据上报任务
 * @date 2025/9/26
 */
public class ThreadPoolDataReportJob {
    private final Logger logger = LoggerFactory.getLogger(ThreadPoolDataReportJob.class);

    private final DynamicThreadPoolInterface dynamicThreadPoolService;

    private final RegistryInterface registry;

    public ThreadPoolDataReportJob(DynamicThreadPoolInterface dynamicThreadPoolInterface, RegistryInterface registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolInterface;
        this.registry = registry;
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void execute() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.queryAllThreadPoolConfig();
        registry.reportThreadPool(threadPoolConfigEntities);
        logger.info("动态线程池上报线程池信息：{}", JSON.toJSONString(threadPoolConfigEntities));

        // 逐个上报数据
        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntities) {
            registry.reportThreadPoolConfigParameter(threadPoolConfigEntity);
            logger.info("动态线程池上报线程池配置：{}", JSON.toJSONString(threadPoolConfigEntity));
        }
    }
}
