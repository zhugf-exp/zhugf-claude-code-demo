package com.logistics.ai.controller;

import com.logistics.ai.model.dto.ApiResponse;
import com.logistics.ai.service.LogisticsAgentService;
import com.logistics.ai.service.SessionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统健康检查控制器
 */
@RestController
@RequestMapping("/health")
@Tag(name = "健康检查", description = "系统健康检查和状态监控接口")
public class HealthController {

    private final LogisticsAgentService agentService;

    public HealthController(LogisticsAgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * 健康检查
     */
    @GetMapping
    @Operation(summary = "健康检查", description = "检查系统是否正常运行")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("service", "logistics-ai");

        return ApiResponse.success(healthInfo);
    }

    /**
     * 就绪检查
     */
    @GetMapping("/ready")
    @Operation(summary = "就绪检查", description = "检查系统是否准备好接收请求")
    public ApiResponse<Map<String, Object>> ready() {
        Map<String, Object> readyInfo = new HashMap<>();
        readyInfo.put("status", "READY");
        readyInfo.put("timestamp", LocalDateTime.now());
        readyInfo.put("sessionCount", agentService.getCurrentSessionCount());

        return ApiResponse.success(readyInfo);
    }

    /**
     * 存活检查
     */
    @GetMapping("/live")
    @Operation(summary = "存活检查", description = "检查系统是否存活")
    public ApiResponse<Map<String, Object>> live() {
        Map<String, Object> liveInfo = new HashMap<>();
        liveInfo.put("status", "ALIVE");
        liveInfo.put("timestamp", LocalDateTime.now());

        return ApiResponse.success(liveInfo);
    }

    /**
     * 详细状态
     */
    @GetMapping("/detailed")
    @Operation(summary = "详细状态", description = "获取系统详细状态信息")
    public ApiResponse<Map<String, Object>> detailed() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "logistics-ai");
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("uptime", getUptime());
        status.put("sessionCount", agentService.getCurrentSessionCount());
        status.put("javaVersion", System.getProperty("java.version"));
        status.put("osName", System.getProperty("os.name"));

        return ApiResponse.success(status);
    }

    private String getUptime() {
        long uptimeMillis = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        return String.format("%d天 %d小时 %d分钟", days, hours % 24, minutes % 60);
    }
}