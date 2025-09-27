package com.coder.middleware.dynamic.thread.pool.trigger;

import com.alibaba.fastjson.JSON;
import com.coder.middleware.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.coder.middleware.dynamic.thread.pool.types.Response;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author agentoflyra
 * @description 用于提供对外接口，分别用于查询和修改。目前支持查询线程池配置列表、查询特定线程池配置和修改特定线程池配置。
 * @date 2025/9/26
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("api/v1/dynamic/thread/pool/")
public class DynamicThreadPoolController {
    @Resource
    private RedissonClient redissonClient;


    /**
     * 查询并获取线程池配置列表。
     *
     * @return 一个包含 ThreadPoolConfigEntity 列表的 Response 对象，
     * 该对象封装了每个线程池的配置详情。Response 对象还包括状态码和信息消息，以指示操作的结果。
     * <br/>
     * 测试命令：<br/>
     * curl --request GET  --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_list'
     */
    @RequestMapping(value = "query_thread_pool_list", method = RequestMethod.GET)
    public Response<List<ThreadPoolConfigEntity>> queryThreadPoolList() {
        try {
            RList<ThreadPoolConfigEntity> list = redissonClient.getList("THREAD_POOL_CONFIG_LIST_KEY");
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .data(list.readAll())
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("线程池查询异常");
            return Response.<List<ThreadPoolConfigEntity>>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 获取特定线程池的配置。
     *
     * @return 一个包含 ThreadPoolConfigEntity 的 Response 对象，
     * 该对象封装了线程池的配置详情。Response 对象还包括状态码和信息消息，以指示操作的结果。
     *<br/>
     * 测试命令：<br/>
     * curl --request GET --url 'http://localhost:8089/api/v1/dynamic/thread/pool/query_thread_pool_config?appName=dynamic-thread-pool-test-app&threadPoolName=threadPoolExecutor01'
     */
    @RequestMapping(value = "query_thread_pool_config", method = RequestMethod.GET)
    public Response<ThreadPoolConfigEntity> queryThreadPoolConfig(@RequestParam String appName, @RequestParam String threadPoolName) {
        try {
            RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket("THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + appName + "_" + threadPoolName);
            ThreadPoolConfigEntity threadPoolConfig = bucket.get();
            return Response.<ThreadPoolConfigEntity>builder()
                    .data(threadPoolConfig)
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("线程池配置查询异常");
            return Response.<ThreadPoolConfigEntity>builder()
                    .code(Response.Code.UN_ERROR.getCode())
                    .info(Response.Code.UN_ERROR.getInfo())
                    .build();
        }
    }


    /**
     * 使用提供的详细信息更新线程池的配置。
     *
     * @param threadPoolConfigEntity 包含线程池更新配置的 ThreadPoolConfigEntity 对象。这包括核心线程数、最大线程数、存活时间等属性，定义了线程池的行为和容量。
     * @return 一个包含布尔值的 Response 对象，指示更新操作是否成功。
     * 此外，Response 对象还将包括状态码和信息消息，以提供更多关于操作结果的上下文。
     * <br/>
     * 测试命令： <br/>
     * curl --request POST  --url http://localhost:8089/api/v1/dynamic/thread/pool/update_thread_pool_config  --header 'content-type: application/json'  --data '{ "appName":"dynamic-thread-pool-test-app", "threadPoolName": "threadPoolExecutor01", "corePoolSize": 1, "maximumPoolSize": 10 }'
     */
    @RequestMapping(value = "update_thread_pool_config", method = RequestMethod.POST)
    public Response<Boolean> updateThreadPoolConfig(@RequestBody ThreadPoolConfigEntity threadPoolConfigEntity) {
        try {
            log.info("更新线程池参数：{}{}{}", threadPoolConfigEntity.getAppName(), threadPoolConfigEntity.getThreadPoolName(), JSON.toJSONString(threadPoolConfigEntity));
            RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + threadPoolConfigEntity.getAppName());
            topic.publish(threadPoolConfigEntity);
            log.info("修改改成：{}{}{}", threadPoolConfigEntity.getAppName(), threadPoolConfigEntity.getThreadPoolName(), JSON.toJSONString(threadPoolConfigEntity));
            return Response.<Boolean>builder()
                    .data(true)
                    .code(Response.Code.SUCCESS.getCode())
                    .info(Response.Code.SUCCESS.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("线程池参数修改失败");
            return Response.<Boolean>builder()
                    .code(Response.Code.ILLEGAL_PARAMETER.getCode())
                    .info(Response.Code.ILLEGAL_PARAMETER.getInfo())
                    .build();
        }
    }

}
