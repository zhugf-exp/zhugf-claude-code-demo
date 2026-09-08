package com.logistics.ai.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统一API响应格式
 *
 * @param <T> 响应数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "统一API响应")
public record ApiResponse<T>(

        @Schema(description = "是否成功")
        boolean success,

        @Schema(description = "响应码")
        String code,

        @Schema(description = "响应消息")
        String message,

        @Schema(description = "响应数据")
        T data,

        @Schema(description = "时间戳")
        long timestamp
) {

    private static final String SUCCESS_CODE = "200";
    private static final String SUCCESS_MESSAGE = "操作成功";
    private static final String ERROR_CODE = "500";
    private static final String ERROR_MESSAGE = "操作失败";

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, data, System.currentTimeMillis());
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, SUCCESS_CODE, message, data, System.currentTimeMillis());
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, ERROR_CODE, message, null, System.currentTimeMillis());
    }

    /**
     * 失败响应（带错误码）
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null, System.currentTimeMillis());
    }

    /**
     * 失败响应（异常信息）
     */
    public static <T> ApiResponse<T> error(Throwable throwable) {
        return error(throwable.getMessage());
    }
}