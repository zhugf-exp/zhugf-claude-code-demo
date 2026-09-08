package com.logistics.ai.config;

import com.logistics.ai.agent.tool.OrderQueryTool;
import com.logistics.ai.agent.tool.ShippingFeeCalculatorTool;
import com.logistics.ai.agent.tool.TrackingTool;
import com.logistics.ai.service.SessionManager;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring AI 配置 — 构建 ChatClient 并注册业务工具
 *
 * ChatModel 由 Anthropic Starter 自动注入，ChatClient 是上层通用客户端
 */
@Configuration
@EnableScheduling
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, LogisticsProperties properties) {
        return ChatClient.builder(chatModel)
                .defaultSystem(properties.getSystemPrompt())
                .defaultTools(
                        new OrderQueryTool(),
                        new TrackingTool(),
                        new ShippingFeeCalculatorTool()
                )
                .build();
    }

    @Bean
    public SessionManager sessionManager(LogisticsProperties properties) {
        return new SessionManager(
                properties.getSession().getMaxSessions(),
                properties.getSession().getTimeoutMinutes()
        );
    }
}
