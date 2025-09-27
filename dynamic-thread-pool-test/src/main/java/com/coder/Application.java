package com.coder;

import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author agentoflyra
 * @description
 * @date 2025/9/25
 */
@Configurable
@SpringBootApplication(exclude = {RedissonAutoConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(ExecutorService threadPoolExecutor01) {
        return args -> {
            while (true) {
                Random random = new Random();
                int initialDelay = random.nextInt(10);
                int randomSleep = random.nextInt(10) + 1;

                threadPoolExecutor01.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(initialDelay);
                        System.out.println("Thread start after " + initialDelay + "s");

                        TimeUnit.SECONDS.sleep(randomSleep);
                        System.out.println("Thread execute for " + randomSleep + "s");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                Thread.sleep(random.nextInt(50) + 1);
            }
        };
    }
}
