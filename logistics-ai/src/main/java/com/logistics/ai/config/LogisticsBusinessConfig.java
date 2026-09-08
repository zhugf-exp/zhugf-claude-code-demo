package com.logistics.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流业务配置
 */
@Configuration
@ConfigurationProperties(prefix = "logistics.business")
public class LogisticsBusinessConfig {

    /**
     * 运费规则配置
     */
    private Map<String, ShippingFeeRule> shippingFeeRules = new HashMap<>();

    /**
     * 区域配置
     */
    private RegionConfig regionConfig = new RegionConfig();

    /**
     * 模拟订单数据
     */
    private Map<String, MockOrder> mockOrders = new HashMap<>();

    /**
     * 模拟物流轨迹数据
     */
    private Map<String, List<MockTrackingNode>> mockTrackingData = new HashMap<>();

    public Map<String, ShippingFeeRule> getShippingFeeRules() {
        return shippingFeeRules;
    }

    public void setShippingFeeRules(Map<String, ShippingFeeRule> shippingFeeRules) {
        this.shippingFeeRules = shippingFeeRules;
    }

    public RegionConfig getRegionConfig() {
        return regionConfig;
    }

    public void setRegionConfig(RegionConfig regionConfig) {
        this.regionConfig = regionConfig;
    }

    public Map<String, MockOrder> getMockOrders() {
        return mockOrders;
    }

    public void setMockOrders(Map<String, MockOrder> mockOrders) {
        this.mockOrders = mockOrders;
    }

    public Map<String, List<MockTrackingNode>> getMockTrackingData() {
        return mockTrackingData;
    }

    public void setMockTrackingData(Map<String, List<MockTrackingNode>> mockTrackingData) {
        this.mockTrackingData = mockTrackingData;
    }

    /**
     * 运费规则
     */
    public record ShippingFeeRule(
            double baseFee,      // 首重价格
            double extraFee      // 续重价格（元/kg）
    ) {}

    /**
     * 区域配置
     */
    public static class RegionConfig {
        private List<String> huadong = List.of("上海", "南京", "杭州", "苏州", "合肥", "无锡", "宁波");
        private List<String> remote = List.of("西藏", "新疆", "青海", "内蒙古");

        public List<String> getHuadong() {
            return huadong;
        }

        public void setHuadong(List<String> huadong) {
            this.huadong = huadong;
        }

        public List<String> getRemote() {
            return remote;
        }

        public void setRemote(List<String> remote) {
            this.remote = remote;
        }
    }

    /**
     * 模拟订单
     */
    public record MockOrder(
            String orderId,
            String sender,
            String senderCity,
            String receiver,
            String receiverCity,
            String itemType,
            double weight,
            String status,
            String createTime
    ) {}

    /**
     * 模拟物流轨迹节点
     */
    public record MockTrackingNode(
            String time,
            String location,
            String status
    ) {}
}