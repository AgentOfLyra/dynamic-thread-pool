package com.coder.middleware.dynamic.thread.pool.sdk.domain;

import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @author agentoflyra
 * @description 动态线程池服务
 * @date 2025/9/25
 */
public interface DynamicThreadPoolInterface {
    /**
     * 查询所有ThreadPool的配置
     * @return 返回List&lt;ThreadPoolConfigEntity&gt;
     */
    List<ThreadPoolConfigEntity> queryAllThreadPoolConfig();
    ThreadPoolConfigEntity queryThreadPoolConfigByName(String name);
    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);
}
