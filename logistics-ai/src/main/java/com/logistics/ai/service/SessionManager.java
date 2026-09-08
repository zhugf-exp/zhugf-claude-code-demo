package com.logistics.ai.service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 会话管理器
 *
 * 管理用户会话历史记录，支持会话过期和清理
 */
@Component
public class SessionManager {

    private final Map<String, SessionData> sessionStore = new ConcurrentHashMap<>();
    private final AtomicInteger sessionCounter = new AtomicInteger(0);

    private int maxSessions;
    private int sessionTimeoutMinutes;

    public SessionManager(int maxSessions, int sessionTimeoutMinutes) {
        this.maxSessions = maxSessions;
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }

    /**
     * 获取或创建会话
     */
    public List<Message> getOrCreateSession(String sessionId) {
        // 检查会话数量限制
        if (sessionCounter.get() >= maxSessions && !sessionStore.containsKey(sessionId)) {
            // 清理过期会话
            cleanExpiredSessions();
            // 如果清理后仍然超出限制，抛出异常
            if (sessionCounter.get() >= maxSessions) {
                throw new IllegalStateException("会话数量已达到上限，请稍后再试");
            }
        }

        return sessionStore.computeIfAbsent(sessionId, k -> {
            SessionData sessionData = new SessionData();
            sessionCounter.incrementAndGet();
            return sessionData;
        }).getMessages();
    }

    /**
     * 清除指定会话
     */
    public void clearSession(String sessionId) {
        SessionData removed = sessionStore.remove(sessionId);
        if (removed != null) {
            sessionCounter.decrementAndGet();
        }
    }

    /**
     * 获取会话信息
     */
    public SessionInfo getSessionInfo(String sessionId) {
        SessionData sessionData = sessionStore.get(sessionId);
        if (sessionData == null) {
            return null;
        }
        return new SessionInfo(sessionId, sessionData.getMessages().size(), sessionData.getLastAccessTime());
    }

    /**
     * 获取所有会话信息
     */
    public List<SessionInfo> getAllSessionInfos() {
        return sessionStore.entrySet().stream()
                .map(entry -> new SessionInfo(
                        entry.getKey(),
                        entry.getValue().getMessages().size(),
                        entry.getValue().getLastAccessTime()
                ))
                .toList();
    }

    /**
     * 定时清理过期会话
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void cleanExpiredSessions() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
        int removedCount = 0;

        sessionStore.entrySet().removeIf(entry -> {
            if (entry.getValue().getLastAccessTime().isBefore(expireTime)) {
                sessionCounter.decrementAndGet();
                removedCount++;
                return true;
            }
            return false;
        });

        if (removedCount > 0) {
            System.out.println("清理了 " + removedCount + " 个过期会话");
        }
    }

    /**
     * 手动清理过期会话
     */
    public int cleanExpiredSessionsNow() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
        int initialCount = sessionCounter.get();

        sessionStore.entrySet().removeIf(entry -> {
            if (entry.getValue().getLastAccessTime().isBefore(expireTime)) {
                sessionCounter.decrementAndGet();
                return true;
            }
            return false;
        });

        return initialCount - sessionCounter.get();
    }

    /**
     * 获取当前会话数量
     */
    public int getCurrentSessionCount() {
        return sessionCounter.get();
    }

    /**
     * 会话数据
     */
    private static class SessionData {
        private final List<Message> messages = new ArrayList<>();
        private LocalDateTime lastAccessTime = LocalDateTime.now();

        public List<Message> getMessages() {
            lastAccessTime = LocalDateTime.now();
            return messages;
        }

        public LocalDateTime getLastAccessTime() {
            return lastAccessTime;
        }
    }

    /**
     * 会话信息
     */
    public record SessionInfo(String sessionId, int messageCount, LocalDateTime lastAccessTime) {}
}