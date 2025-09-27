package com.coder.middleware.dynamic.thread.pool.sdk.registry;

import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * @author agentoflyra
 * @description
 * @date 2025/9/26
 */
public interface RegistryInterface {
    void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities);
    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}
