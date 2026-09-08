package com.logistics.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(LogisticsProperties properties) {
        CorsConfiguration config = new CorsConfiguration();

        // 如果启用了CORS，则设置允许的源
        if (properties.getSecurity().isCorsEnabled()) {
            for (String origin : properties.getSecurity().getAllowedOrigins()) {
                config.addAllowedOrigin(origin);
            }
        }

        // 允许的HTTP方法
        config.addAllowedMethod("*");

        // 允许的请求头
        config.addAllowedHeader("*");

        // 允许携带凭证
        config.setAllowCredentials(true);

        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}