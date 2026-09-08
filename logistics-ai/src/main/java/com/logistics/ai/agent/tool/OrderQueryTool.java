package com.logistics.ai.agent.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.logistics.ai.config.LogisticsBusinessConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

/**
 * 运单查询工具 — Function Calling
 *
 * 让 LLM 调用 Java 方法查询订单信息
 * 当前使用模拟数据，后续替换为数据库调用
 */
public class OrderQueryTool implements Function<OrderQueryTool.Request, OrderQueryTool.Response> {

    private static final Logger log = LoggerFactory.getLogger(OrderQueryTool.class);
    private final LogisticsBusinessConfig businessConfig;

    public OrderQueryTool() {
        // 默认构造函数，用于Spring AI工具注册
        this.businessConfig = null;
    }

    public OrderQueryTool(LogisticsBusinessConfig businessConfig) {
        this.businessConfig = businessConfig;
    }

    @Override
    public Response apply(Request request) {
        log.info("查询订单: orderId={}", request.orderId());

        // 使用配置类中的模拟数据，如果没有配置则使用默认数据
        Map<String, LogisticsBusinessConfig.MockOrder> mockDb;
        if (businessConfig != null && !businessConfig.getMockOrders().isEmpty()) {
            mockDb = businessConfig.getMockOrders();
        } else {
            // 默认模拟数据
            mockDb = getDefaultMockDb();
        }

        LogisticsBusinessConfig.MockOrder mockOrder = mockDb.get(request.orderId());
        if (mockOrder == null) {
            return new Response(false, "未找到运单 " + request.orderId(), null);
        }

        Order order = new Order(
                mockOrder.orderId(),
                mockOrder.sender(),
                mockOrder.senderCity(),
                mockOrder.receiver(),
                mockOrder.receiverCity(),
                mockOrder.itemType(),
                mockOrder.weight(),
                mockOrder.status(),
                mockOrder.createTime()
        );

        return new Response(true, "查询成功", order);
    }

    private Map<String, LogisticsBusinessConfig.MockOrder> getDefaultMockDb() {
        return Map.of(
                "SF1000001", new LogisticsBusinessConfig.MockOrder("SF1000001", "张三", "上海", "李四", "北京", "电子产品", 2.5, "运输中", "2026-09-05 10:30:00"),
                "SF1000002", new LogisticsBusinessConfig.MockOrder("SF1000002", "王五", "广州", "赵六", "成都", "服装", 1.2, "已签收", "2026-09-03 14:20:00")
        );
    }

    /**
     * 订单领域对象
     */
    private record Order(String orderId, String sender, String senderCity,
                         String receiver, String receiverCity,
                         String itemType, double weight,
                         String status, String createTime) {}

    public record Request(
            @JsonProperty(required = true)
            @JsonPropertyDescription("运单号，例如 SF1000001")
            String orderId
    ) {}

    public record Response(boolean success, String message, Order orderInfo) {}
}
