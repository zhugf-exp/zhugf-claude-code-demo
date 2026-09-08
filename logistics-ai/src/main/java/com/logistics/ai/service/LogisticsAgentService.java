package com.logistics.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 物流 AI 对话服务
 *
 * 基于 ChatClient 封装同步对话、流式对话、会话管理
 */
@Service
public class LogisticsAgentService {

    private static final Logger log = LoggerFactory.getLogger(LogisticsAgentService.class);

    private final ChatClient chatClient;
    private final SessionManager sessionManager;

    public LogisticsAgentService(ChatClient chatClient, SessionManager sessionManager) {
        this.chatClient = chatClient;
        this.sessionManager = sessionManager;
    }

    /**
     * 同步对话 — 保留上下文
     */
    public String chat(String sessionId, String userMessage) {
        log.debug("同步对话: sessionId={}, message={}", sessionId, userMessage);
        List<Message> history = sessionManager.getOrCreateSession(sessionId);
        history.add(new UserMessage(userMessage));

        try {
            String reply = chatClient.prompt()
                    .messages(history)
                    .call()
                    .content();

            history.add(new AssistantMessage(reply));
            log.debug("同步对话完成: sessionId={}, replyLength={}", sessionId, reply.length());
            return reply;
        } catch (Exception e) {
            log.error("同步对话失败: sessionId={}", sessionId, e);
            throw new RuntimeException("对话处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式对话 — SSE 输出（保留上下文）
     */
    public Flux<String> chatStream(String sessionId, String userMessage) {
        log.debug("流式对话开始: sessionId={}, message={}", sessionId, userMessage);
        List<Message> history = sessionManager.getOrCreateSession(sessionId);
        history.add(new UserMessage(userMessage));

        return chatClient.prompt()
                .messages(history)
                .stream()
                .content()
                .collectList()
                .flatMapMany(contentList -> {
                    // 收集完整回复并保存到历史记录
                    String fullReply = String.join("", contentList);
                    history.add(new AssistantMessage(fullReply));
                    log.debug("流式对话完成: sessionId={}, replyLength={}", sessionId, fullReply.length());
                    return Flux.fromIterable(contentList);
                })
                .onErrorResume(e -> {
                    log.error("流式对话失败: sessionId={}", sessionId, e);
                    return Mono.just("对话处理失败: " + e.getMessage());
                });
    }

    /**
     * 单次对话 — 不保留上下文
     */
    public String chatOnce(String userMessage) {
        log.debug("单次对话: message={}", userMessage);
        try {
            return chatClient.prompt()
                    .user(userMessage)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("单次对话失败: message={}", userMessage, e);
            throw new RuntimeException("对话处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清除会话
     */
    public void clearSession(String sessionId) {
        log.debug("清除会话: sessionId={}", sessionId);
        sessionManager.clearSession(sessionId);
    }

    /**
     * 获取会话信息
     */
    public SessionManager.SessionInfo getSessionInfo(String sessionId) {
        return sessionManager.getSessionInfo(sessionId);
    }

    /**
     * 获取所有会话信息
     */
    public List<SessionManager.SessionInfo> getAllSessionInfos() {
        return sessionManager.getAllSessionInfos();
    }

    /**
     * 清理过期会话
     */
    public int cleanExpiredSessions() {
        return sessionManager.cleanExpiredSessionsNow();
    }

    /**
     * 获取当前会话数量
     */
    public int getCurrentSessionCount() {
        return sessionManager.getCurrentSessionCount();
    }
}
