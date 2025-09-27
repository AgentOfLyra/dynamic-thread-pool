package com.coder.middleware.dynamic.thread.pool.test;

import com.alibaba.fastjson.JSON;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.coder.middleware.dynamic.thread.pool.trigger.DynamicThreadPoolController;
import com.coder.middleware.dynamic.thread.pool.types.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DynamicThreadPoolController.class)
public class ApiTest {

    @InjectMocks
    private DynamicThreadPoolController dynamicThreadPoolController;

    @Mock
    private RList<ThreadPoolConfigEntity> threadPoolConfigList;

    @Mock
    private RBucket<ThreadPoolConfigEntity> threadPoolConfigBucket;

    @Mock
    private RTopic topic;

    @Mock
    private RedissonClient redissonClient;

    private MockMvc mockMvc;

    private final String appName = "dynamic-thread-pool-test-app";
    private final String threadPoolName01 = "threadPoolExecutor01";
    private final String threadPoolName02 = "threadPoolExecutor02";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dynamicThreadPoolController).build();
    }

    @Test
    public void testQueryThreadPoolList() throws Exception {
        List<ThreadPoolConfigEntity> configEntities = Arrays.asList(
                ThreadPoolConfigEntity.builder()
                                .appName(appName)
                                .threadPoolName(threadPoolName01)
                                .build(),
                ThreadPoolConfigEntity.builder()
                                .appName(appName)
                                .threadPoolName(threadPoolName02)
                                .build()
        );

        when(redissonClient.<ThreadPoolConfigEntity>getList("THREAD_POOL_CONFIG_LIST_KEY")).thenReturn(threadPoolConfigList);
        when(threadPoolConfigList.readAll()).thenReturn(configEntities);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dynamic/thread/pool/query_thread_pool_list")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(JSON.toJSONString(Response.<List<ThreadPoolConfigEntity>>builder()
                        .data(configEntities)
                        .code(Response.Code.SUCCESS.getCode())
                        .info(Response.Code.SUCCESS.getInfo())
                        .build())));
    }

    @Test
    public void testQueryThreadPoolConfig() throws Exception {
        ThreadPoolConfigEntity configEntity = ThreadPoolConfigEntity.builder()
                .appName(appName)
                .threadPoolName(threadPoolName01)
                .build();

        when(redissonClient.<ThreadPoolConfigEntity>getBucket("THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + appName + "_" + threadPoolName01)).thenReturn(threadPoolConfigBucket);
        when(threadPoolConfigBucket.get()).thenReturn(configEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dynamic/thread/pool/query_thread_pool_config")
                .param("appName", appName)
                .param("threadPoolName", threadPoolName01)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(JSON.toJSONString(Response.<ThreadPoolConfigEntity>builder()
                        .data(configEntity)
                        .code(Response.Code.SUCCESS.getCode())
                        .info(Response.Code.SUCCESS.getInfo())
                        .build())));
    }

    @Test
    public void testUpdateThreadPoolConfig() throws Exception {
        ThreadPoolConfigEntity configEntity = ThreadPoolConfigEntity.builder()
                .appName(appName)
                .threadPoolName(threadPoolName01)
                .build();
        
        when(redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + configEntity.getAppName())).thenReturn(topic);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/dynamic/thread/pool/update_thread_pool_config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(configEntity))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(JSON.toJSONString(Response.<Boolean>builder()
                        .data(true)
                        .code(Response.Code.SUCCESS.getCode())
                        .info(Response.Code.SUCCESS.getInfo())
                        .build())));
    }
}
