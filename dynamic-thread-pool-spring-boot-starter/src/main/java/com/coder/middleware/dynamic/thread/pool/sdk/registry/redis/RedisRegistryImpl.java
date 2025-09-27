package com.coder.middleware.dynamic.thread.pool.sdk.registry.redis;

import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.valobj.RegistryEnumVO;
import com.coder.middleware.dynamic.thread.pool.sdk.registry.RegistryInterface;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.List;

/**
 * @author agentoflyra
 * @description
 * @date 2025/9/26
 */
public class RedisRegistryImpl implements RegistryInterface {

    private final RedissonClient redissonClient;

    public RedisRegistryImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void reportThreadPool(List<ThreadPoolConfigEntity> threadPoolConfigEntities) {
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.delete();
        list.addAll(threadPoolConfigEntities);
    }

    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_"
                + threadPoolConfigEntity.getAppName() + "_"
                + threadPoolConfigEntity.getThreadPoolName();
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(cacheKey);
        RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
    }
}
