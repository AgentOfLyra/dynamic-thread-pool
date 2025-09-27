package com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author agentoflyra
 * @description
 * @date 2025/9/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThreadPoolConfigEntity {
    /** 应用名称 */
    private String appName;
    /** 线程池名称 */
    private String threadPoolName;
    /** 核心线程数 */
    private int corePoolSize;
    /* 最大线程数 */
    private int maximumPoolSize;
    /* 最大存活时间 */
    private long keepAliveTime;
    /* 活动线程数 */
    private int activeCount;
    /* queue类型 */
    private String queueType;
    /** 当前队列长度 */
    private int queueSize;
    /** 剩余任务队列长度 */
    private int queueRemainingCapacity;
}
