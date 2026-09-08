package com.logistics.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 物流 AI 系统启动类
 *
 * 基于 Spring AI 框架，提供智能物流查询、运费计算、路线规划等功能
 */
@SpringBootApplication
public class LogisticsAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsAiApplication.class, args);
    }
}
