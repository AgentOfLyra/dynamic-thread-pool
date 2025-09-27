package com.coder.middleware.dynamic.thread.pool.sdk.domain;

import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author agentoflyra
 * @description 查询各个线程池的配置
 * @date 2025/9/25
 */
public class DynamicThreadPoolImpl implements DynamicThreadPoolInterface{

    private final Logger logger = LoggerFactory.getLogger(DynamicThreadPoolImpl.class);
    private final String applicationName;
    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public DynamicThreadPoolImpl(Map<String, ThreadPoolExecutor> threadPoolExecutorMap, String applicationName) {
        this.threadPoolExecutorMap = threadPoolExecutorMap;
        this.applicationName = applicationName;
    }

    /**
     * 查询所有线程池的配置，并装载在ThreadPoolConfigEntity实例当中，以List形式返回
     * @return List&lt;ThreadPoolConfigEntity&gt;
     */
    @Override
    public List<ThreadPoolConfigEntity> queryAllThreadPoolConfig(){
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = new ArrayList<>();
        for (Map.Entry<String, ThreadPoolExecutor> entry : threadPoolExecutorMap.entrySet()) {
            String threadPoolName = entry.getKey();
            ThreadPoolExecutor threadPoolExecutor = entry.getValue();
            ThreadPoolConfigEntity threadPoolConfigEntity = createThreadConfigEntity(threadPoolName, threadPoolExecutor);
            threadPoolConfigEntities.add(threadPoolConfigEntity);
        }
        return threadPoolConfigEntities;
    }

    /**
     * 通过查询线程池名称的方式查询对应线程池的信息，如果不存在会返回一个初始的线程池，并在控制台打印警告信息
     * @param name
     * @return
     */
    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String name){
        if (!threadPoolExecutorMap.containsKey(name)) {
            logger.warn("未找到该线程池，返回初始配置");
            ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity();
            threadPoolConfigEntity.setAppName(applicationName);
            threadPoolConfigEntity.setThreadPoolName(name);
            return threadPoolConfigEntity;
        }
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(name);
        return createThreadConfigEntity(name, threadPoolExecutor);
    }

    /**
     * 用于更新threadPoolConfigEntity的信息
     * @param threadPoolConfigEntity
     */
    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity){
        if (null == threadPoolConfigEntity || !applicationName.equals(threadPoolConfigEntity.getAppName())) return;
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if (null == threadPoolExecutor) return;

        // 设置参数 「调整核心线程数和最大线程数」
        threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
    }

    /**
     * 私有方法，用于创建Config实例
     * @param threadPoolName
     * @param threadPoolExecutor
     * @return
     */
    private ThreadPoolConfigEntity createThreadConfigEntity(String threadPoolName, ThreadPoolExecutor threadPoolExecutor) {
        ThreadPoolConfigEntity threadPoolConfigEntity = new ThreadPoolConfigEntity();
        threadPoolConfigEntity.setThreadPoolName(threadPoolName);
        threadPoolConfigEntity.setCorePoolSize(threadPoolExecutor.getCorePoolSize());
        threadPoolConfigEntity.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize());
        threadPoolConfigEntity.setAppName(applicationName);
        threadPoolConfigEntity.setQueueType(threadPoolExecutor.getQueue().getClass().getSimpleName());
        threadPoolConfigEntity.setQueueSize(threadPoolExecutor.getQueue().size());
        threadPoolConfigEntity.setQueueRemainingCapacity(threadPoolExecutor.getQueue().remainingCapacity());
        threadPoolConfigEntity.setKeepAliveTime(threadPoolConfigEntity.getKeepAliveTime());
        threadPoolConfigEntity.setActiveCount(threadPoolExecutor.getActiveCount());
        return threadPoolConfigEntity;
    }
}
