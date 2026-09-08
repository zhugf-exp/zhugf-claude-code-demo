package com.logistics.ai.model.dto;

/**
 * 对话响应 DTO
 */
public record ChatResponse(
        /** 会话ID */
        String sessionId,
        /** AI 回复内容 */
        String reply
) {}
