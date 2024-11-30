package com.example.cepengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * CEP引擎应用程序主入口类
 * 
 * 负责启动复杂事件处理（CEP）引擎应用程序
 * 
 * 主要功能：
 * 1. 启动Spring Boot应用程序
 * 2. 启用Kafka支持
 * 3. 初始化应用程序上下文
 * 
 * 详细说明：
 * 本类是整个CEP引擎应用程序的入口点。
 * 通过@SpringBootApplication注解，启用自动配置和组件扫描。
 * 通过@EnableKafka注解，启用Kafka消息处理能力。
 */
@SpringBootApplication
@EnableKafka
public class CepEngineApplication {

    /**
     * 应用程序主方法
     * 
     * 启动Spring Boot应用程序
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(CepEngineApplication.class, args);
    }
}
