package com.logistics.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 业务配置属性
 */
@Configuration
@ConfigurationProperties(prefix = "logistics.ai")
public class LogisticsProperties {

    /**
     * AI系统提示词
     */
    private String systemPrompt = "你是一个专业的物流AI助手，帮助用户处理物流相关问题。";

    /**
     * 会话配置
     */
    private Session session = new Session();

    /**
     * 安全配置
     */
    private Security security = new Security();

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * 会话配置
     */
    public static class Session {
        /**
         * 会话超时时间（分钟）
         */
        private int timeoutMinutes = 30;

        /**
         * 最大会话数
         */
        private int maxSessions = 1000;

        public int getTimeoutMinutes() {
            return timeoutMinutes;
        }

        public void setTimeoutMinutes(int timeoutMinutes) {
            this.timeoutMinutes = timeoutMinutes;
        }

        public int getMaxSessions() {
            return maxSessions;
        }

        public void setMaxSessions(int maxSessions) {
            this.maxSessions = maxSessions;
        }
    }

    /**
     * 安全配置
     */
    public static class Security {
        /**
         * 允许的CORS源
         */
        private String[] allowedOrigins = new String[]{"http://localhost:3000", "http://localhost:8080"};

        /**
         * 是否启用CORS
         */
        private boolean corsEnabled = true;

        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String[] allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public boolean isCorsEnabled() {
            return corsEnabled;
        }

        public void setCorsEnabled(boolean corsEnabled) {
            this.corsEnabled = corsEnabled;
        }
    }
}