package com.logistics.ai.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 对话请求 DTO
 */
public record ChatRequest(

        /** 会话ID，用于维持上下文 */
        @NotBlank(message = "sessionId 不能为空")
        String sessionId,

        /** 用户消息 */
        @NotBlank(message = "message 不能为空")
        String message
) {}
