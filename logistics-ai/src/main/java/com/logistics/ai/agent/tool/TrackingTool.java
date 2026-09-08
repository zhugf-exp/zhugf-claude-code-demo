package com.logistics.ai.agent.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.logistics.ai.config.LogisticsBusinessConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 物流轨迹查询工具 — Function Calling
 *
 * 让 LLM 调用 Java 方法查询运单的物流轨迹节点
 */
public class TrackingTool implements Function<TrackingTool.Request, TrackingTool.Response> {

    private static final Logger log = LoggerFactory.getLogger(TrackingTool.class);
    private final LogisticsBusinessConfig businessConfig;

    public TrackingTool() {
        // 默认构造函数，用于Spring AI工具注册
        this.businessConfig = null;
    }

    public TrackingTool(LogisticsBusinessConfig businessConfig) {
        this.businessConfig = businessConfig;
    }

    @Override
    public Response apply(Request request) {
        log.info("查询物流轨迹: orderId={}", request.orderId());

        // 使用配置类中的模拟数据，如果没有配置则使用默认数据
        Map<String, List<LogisticsBusinessConfig.MockTrackingNode>> mockDb;
        if (businessConfig != null && !businessConfig.getMockTrackingData().isEmpty()) {
            mockDb = businessConfig.getMockTrackingData();
        } else {
            // 默认模拟数据
            mockDb = getDefaultMockDb();
        }

        List<LogisticsBusinessConfig.MockTrackingNode> mockNodes = mockDb.get(request.orderId());
        if (mockNodes == null) {
            return new Response(false, "未找到运单 " + request.orderId() + " 的轨迹信息", Collections.emptyList());
        }

        List<TrackingNode> nodes = mockNodes.stream()
                .map(mock -> new TrackingNode(mock.time(), mock.location(), mock.status()))
                .toList();

        return new Response(true, "查询成功", nodes);
    }

    private Map<String, List<LogisticsBusinessConfig.MockTrackingNode>> getDefaultMockDb() {
        return Map.of(
                "SF1000001", List.of(
                        new LogisticsBusinessConfig.MockTrackingNode("2026-09-05 10:30:00", "上海集散中心", "已揽收"),
                        new LogisticsBusinessConfig.MockTrackingNode("2026-09-05 18:00:00", "上海转运中心", "已发出"),
                        new LogisticsBusinessConfig.MockTrackingNode("2026-09-06 06:30:00", "南京转运中心", "运输中"),
                        new LogisticsBusinessConfig.MockTrackingNode("2026-09-06 14:00:00", "北京集散中心", "到达目的地")
                ),
                "SF1000002", List.of(
                        new LogisticsBusinessConfig.MockTrackingNode("2026-09-03 14:20:00", "广州集散中心", "已揽收"),
                        new LogisticsBusinessConfig.MockTrackingNode("2026-09-04 20:00:00", "成都集散中心", "派送中"),
                        new LogisticsBusinessConfig.MockTrackingNode("2026-09-05 09:15:00", "成都市武侯区", "已签收")
                )
        );
    }

    /**
     * 轨迹节点
     */
    private record TrackingNode(String time, String location, String status) {}

    public record Request(
            @JsonProperty(required = true)
            @JsonPropertyDescription("运单号，例如 SF1000001")
            String orderId
    ) {}

    public record Response(boolean success, String message, List<TrackingNode> nodes) {}
}