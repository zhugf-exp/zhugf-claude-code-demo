package com.logistics.ai.agent.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.logistics.ai.config.LogisticsBusinessConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

/**
 * 运费计算工具 — Function Calling
 *
 * 根据出发地、目的地和重量计算运费
 * 当前使用模拟规则，后续对接真实计费引擎
 */
public class ShippingFeeCalculatorTool implements Function<ShippingFeeCalculatorTool.Request, ShippingFeeCalculatorTool.Response> {

    private static final Logger log = LoggerFactory.getLogger(ShippingFeeCalculatorTool.class);
    private final LogisticsBusinessConfig businessConfig;

    public ShippingFeeCalculatorTool() {
        // 默认构造函数，用于Spring AI工具注册
        this.businessConfig = null;
    }

    public ShippingFeeCalculatorTool(LogisticsBusinessConfig businessConfig) {
        this.businessConfig = businessConfig;
    }

    @Override
    public Response apply(Request request) {
        log.info("计算运费: from={}, to={}, weight={}kg", request.from(), request.to(), request.weight());

        String region = determineRegion(request.from(), request.to());

        // 使用配置类中的费率规则，如果没有配置则使用默认规则
        double baseFee, extraFee;
        if (businessConfig != null && businessConfig.getShippingFeeRules().containsKey(region)) {
            LogisticsBusinessConfig.ShippingFeeRule rule = businessConfig.getShippingFeeRules().get(region);
            baseFee = rule.baseFee();
            extraFee = rule.extraFee();
        } else {
            // 默认规则
            baseFee = getBaseFee(region);
            extraFee = getExtraFee(region);
        }

        double calculatedExtraFee = request.weight() > 1 ? Math.ceil(request.weight() - 1) * extraFee : 0;
        double totalFee = baseFee + calculatedExtraFee;

        return new Response(true, String.format("从%s到%s（%s），首重￥%.0f + 续重￥%.0f = ￥%.0f",
                request.from(), request.to(), region, baseFee, calculatedExtraFee, totalFee),
                region, baseFee, calculatedExtraFee, totalFee);
    }

    /** 区域判断 */
    private String determineRegion(String from, String to) {
        if (from.equals(to)) return "同城";

        // 使用配置类中的区域配置
        if (businessConfig != null) {
            boolean fromHuaDong = matchAny(businessConfig.getRegionConfig().getHuadong(), from);
            boolean toHuaDong = matchAny(businessConfig.getRegionConfig().getHuadong(), to);
            if (fromHuaDong && toHuaDong) return "华东";
            if (matchAny(businessConfig.getRegionConfig().getRemote(), to)) return "偏远";
        } else {
            // 默认区域判断
            String[] HUADONG = {"上海", "南京", "杭州", "苏州", "合肥", "无锡", "宁波"};
            String[] REMOTE = {"西藏", "新疆", "青海", "内蒙古"};
            boolean fromHuaDong = matchAny(HUADONG, from);
            boolean toHuaDong = matchAny(HUADONG, to);
            if (fromHuaDong && toHuaDong) return "华东";
            if (matchAny(REMOTE, to)) return "偏远";
        }

        return "跨省";
    }

    private boolean matchAny(java.util.List<String> keywords, String target) {
        for (String kw : keywords) {
            if (target.contains(kw) || kw.contains(target)) return true;
        }
        return false;
    }

    private double getBaseFee(String region) {
        return switch (region) {
            case "同城" -> 10.0;
            case "省内" -> 12.0;
            case "华东" -> 15.0;
            case "偏远" -> 25.0;
            default -> 18.0; // 跨省
        };
    }

    private double getExtraFee(String region) {
        return switch (region) {
            case "同城" -> 3.0;
            case "省内" -> 5.0;
            case "华东" -> 8.0;
            case "偏远" -> 15.0;
            default -> 10.0; // 跨省
        };
    }

    public record Request(
            @JsonProperty(required = true)
            @JsonPropertyDescription("出发城市，例如：上海")
            String from,

            @JsonProperty(required = true)
            @JsonPropertyDescription("目的城市，例如：北京")
            String to,

            @JsonProperty(required = true)
            @JsonPropertyDescription("包裹重量（公斤）")
            double weight
    ) {}

    public record Response(boolean success, String message,
                           String region, double baseFee, double extraFee, double totalFee) {}
}
