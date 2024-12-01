package com.example.cepengine.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka生产者配置管理器
 * 
 * 在复杂事件处理（CEP）系统中，负责配置和管理Kafka消息生产者
 * 
 * 主要功能：
 * 1. 动态配置Kafka生产者连接参数
 * 2. 创建可定制的生产者工厂
 * 3. 构建高性能KafkaTemplate
 * 4. 支持灵活的消息发布策略
 * 
 * 设计特点：
 * - 使用Spring的@Value注解实现动态配置
 * - 支持灵活的Kafka生产者参数配置
 * - 提供细粒度的消息生产控制
 * 
 * 使用场景：
 * - 实时数据流生产
 * - 复杂事件处理系统的消息发布
 * - 高性能、可配置的消息生成
 */
@Configuration
public class KafkaProducerConfig {

    /**
     * Kafka服务器地址
     * 
     * 从应用程序配置文件中动态读取
     * 支持多环境、多集群配置
     * 确保生产者能够连接到正确的Kafka集群
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * 构建Kafka生产者配置参数
     * 
     * 创建并配置Kafka生产者的连接和行为参数
     * 
     * 配置包括：
     * - 服务器地址
     * - 键值序列化策略
     * - 消息发布配置
     * 
     * @return Kafka生产者配置映射
     */
    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        
        // 配置Kafka服务器地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // 配置键值序列化器
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        return props;
    }

    /**
     * 创建Kafka生产者工厂
     * 
     * 提供可配置、可重用的Kafka生产者实例
     * 支持动态创建不同配置的生产者
     * 
     * 关键特性：
     * - 使用动态配置参数
     * - 支持多种序列化策略
     * - 提供灵活的生产者实例管理
     * 
     * @return Kafka生产者工厂实例
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /**
     * 构建KafkaTemplate
     * 
     * 创建高性能、易用的Kafka消息发布模板
     * 
     * 关键配置：
     * - 使用自定义生产者工厂
     * - 简化消息发布流程
     * - 支持同步和异步消息发送
     * 
     * @param producerFactory Kafka生产者工厂
     * @return KafkaTemplate实例，用于消息发布
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
