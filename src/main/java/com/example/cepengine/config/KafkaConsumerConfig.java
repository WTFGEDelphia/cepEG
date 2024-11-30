package com.example.cepengine.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka消费者配置管理器
 * 
 * 在复杂事件处理（CEP）系统中，负责配置和管理Kafka消费者
 * 
 * 主要功能：
 * 1. 动态配置Kafka消费者连接参数
 * 2. 创建可定制的消费者工厂
 * 3. 构建Kafka监听容器工厂
 * 4. 支持手动偏移量管理
 * 
 * 设计特点：
 * - 使用Spring的@Value注解实现动态配置
 * - 支持灵活的Kafka消费者参数配置
 * - 提供细粒度的消费者和监听器控制
 * 
 * 使用场景：
 * - 实时数据流消费
 * - 复杂事件处理系统的消息接收
 * - 高性能、可配置的消息消费
 */
@Configuration
public class KafkaConsumerConfig {

    /**
     * Kafka服务器地址
     * 
     * 从应用程序配置文件中动态读取
     * 支持多环境、多集群配置
     */
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * 消费者组标识
     * 
     * 定义消费者组，实现消息负载均衡和状态管理
     * 从应用程序配置文件中动态读取
     */
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * 自动提交偏移量开关
     * 
     * 控制是否自动提交消费进度
     * 默认为手动提交，提高消息处理的可靠性
     */
    @Value("${spring.kafka.consumer.enable-auto-commit:false}")
    private boolean enableAutoCommit;

    /**
     * 构建Kafka消费者配置参数
     * 
     * 创建并配置Kafka消费者的连接和行为参数
     * 
     * 配置包括：
     * - 服务器地址
     * - 消费者组
     * - 反序列化策略
     * - 偏移量提交模式
     * 
     * @return Kafka消费者配置映射
     */
    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        
        // 配置Kafka服务器地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // 设置消费者组标识
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        // 配置键值反序列化器
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        // 配置偏移量提交策略
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        
        return props;
    }

    /**
     * 创建Kafka消费者工厂
     * 
     * 提供可配置、可重用的Kafka消费者实例
     * 支持动态创建不同配置的消费者
     * 
     * @return Kafka消费者工厂实例
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    /**
     * 构建Kafka监听容器工厂
     * 
     * 创建支持并发消费和手动偏移量管理的监听容器
     * 
     * 关键配置：
     * - 使用自定义消费者工厂
     * - 启用手动立即确认模式
     * 
     * @return Kafka监听容器工厂实例
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        
        // 设置消费者工厂
        factory.setConsumerFactory(consumerFactory());
        
        // 配置手动立即确认模式，提高消息处理可靠性
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        return factory;
    }
}
