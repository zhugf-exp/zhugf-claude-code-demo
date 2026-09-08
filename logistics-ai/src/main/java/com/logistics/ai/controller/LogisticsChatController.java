package com.logistics.ai.controller;

import com.logistics.ai.model.dto.ApiResponse;
import com.logistics.ai.model.dto.ChatRequest;
import com.logistics.ai.model.dto.ChatResponse;
import com.logistics.ai.service.LogisticsAgentService;
import com.logistics.ai.service.SessionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 物流 AI 对话接口
 *
 * 提供同步对话和流式对话两种接口
 */
@RestController
@RequestMapping("/api/chat")
@Tag(name = "物流AI对话", description = "提供物流AI对话相关接口")
public class LogisticsChatController {

    private static final Logger log = LoggerFactory.getLogger(LogisticsChatController.class);

    private final LogisticsAgentService agentService;

    public LogisticsChatController(LogisticsAgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * 同步对话接口
     *
     * POST /api/chat
     * Body: { "sessionId": "xxx", "message": "查询运单 SF1000001" }
     */
    @PostMapping
    @Operation(summary = "同步对话", description = "发送消息并等待AI回复")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("收到对话请求: sessionId={}, message={}", request.getSessionId(), request.getMessage());
        String reply = agentService.chat(request.getSessionId(), request.getMessage());
        return ApiResponse.success(new ChatResponse(request.getSessionId(), reply));
    }

    /**
     * 流式对话接口 - SSE
     *
     * POST /api/chat/stream
     * Body: { "sessionId": "xxx", "message": "帮我查一下 SF1000001 的物流信息" }
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式对话", description = "发送消息并通过SSE流式接收AI回复")
    public org.springframework.http.ResponseEntity<String> chatStream(@Valid @RequestBody ChatRequest request) {
        log.info("收到流式对话请求: sessionId={}, message={}", request.getSessionId(), request.getMessage());
        return org.springframework.http.ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body("data: " + agentService.chatStream(request.getSessionId(), request.getMessage()).blockFirst());
    }

    /**
     * 清除会话
     *
     * DELETE /api/chat/{sessionId}
     */
    @DeleteMapping("/{sessionId}")
    @Operation(summary = "清除会话", description = "清除指定会话的历史记录")
    public ApiResponse<ChatResponse> clearSession(@PathVariable String sessionId) {
        log.info("清除会话: sessionId={}", sessionId);
        agentService.clearSession(sessionId);
        return ApiResponse.success(new ChatResponse(sessionId, "会话已清除"));
    }

    /**
     * 获取会话信息
     *
     * GET /api/chat/{sessionId}/info
     */
    @GetMapping("/{sessionId}/info")
    @Operation(summary = "获取会话信息", description = "获取指定会话的详细信息")
    public ApiResponse<SessionManager.SessionInfo> getSessionInfo(@PathVariable String sessionId) {
        SessionManager.SessionInfo sessionInfo = agentService.getSessionInfo(sessionId);
        if (sessionInfo == null) {
            return ApiResponse.error("会话不存在");
        }
        return ApiResponse.success(sessionInfo);
    }

    /**
     * 获取所有会话信息
     *
     * GET /api/chat/sessions
     */
    @GetMapping("/sessions")
    @Operation(summary = "获取所有会话", description = "获取当前所有活跃会话的信息")
    public ApiResponse<List<SessionManager.SessionInfo>> getAllSessions() {
        return ApiResponse.success(agentService.getAllSessionInfos());
    }

    /**
     * 清理过期会话
     *
     * POST /api/chat/cleanup
     */
    @PostMapping("/cleanup")
    @Operation(summary = "清理过期会话", description = "手动清理所有过期会话")
    public ApiResponse<Map<String, Integer>> cleanupExpiredSessions() {
        int cleanedCount = agentService.cleanExpiredSessions();
        return ApiResponse.success(Map.of("cleanedCount", cleanedCount));
    }

    /**
     * 获取会话统计
     *
     * GET /api/chat/stats
     */
    @GetMapping("/stats")
    @Operation(summary = "获取会话统计", description = "获取当前会话数量统计")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(Map.of(
                "currentSessionCount", agentService.getCurrentSessionCount(),
                "timestamp", System.currentTimeMillis()
        ));
    }
}
