package com.coder.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

/**
 * @author agentoflyra
 * @description
 * @date 2025/9/25
 */
@Slf4j
@EnableAsync
@Configuration
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
public class ThreadPoolConfig {


    @Bean("threadPoolExecutor01")
    public ThreadPoolExecutor threadPoolExecutor01(ThreadPoolConfigProperties properties) {
        RejectedExecutionHandler rejectedExecutionHandler;
        switch (properties.getPolicy()) {
            case "DiscardPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "CallerRunsPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case "AbortPolicy":
            default:
                rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getBlockedQueueSize()),
                Executors.defaultThreadFactory(),
                rejectedExecutionHandler
        );
    }

    @Bean("threadPoolExecutor02")
    public ThreadPoolExecutor threadPoolExecutor02(ThreadPoolConfigProperties properties) {
        RejectedExecutionHandler rejectedExecutionHandler;
        switch (properties.getPolicy()) {
            case "DiscardPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            case "CallerRunsPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case "AbortPolicy":
            default:
                rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
                break;
        }
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getBlockedQueueSize()),
                Executors.defaultThreadFactory(),
                rejectedExecutionHandler
        );
    }

}
