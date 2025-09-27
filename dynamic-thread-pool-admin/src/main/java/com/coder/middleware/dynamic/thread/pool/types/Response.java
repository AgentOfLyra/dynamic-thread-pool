package com.coder.middleware.dynamic.thread.pool.types;

import lombok.*;

import java.io.Serializable;

/**
 * @author agentoflyra
 * @description
 * @date 2025/9/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;
    private String info;
    private T data;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum Code{
        SUCCESS("0000", "调用成功"),
        UN_ERROR("0001", "调用失败"),
        ILLEGAL_PARAMETER("0002", "非法参数");

        private String code;
        private String info;
    }
}
