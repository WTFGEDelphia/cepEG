package com.example.cepengine.config.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Kafka监听器健康状态监控器
 * 
 * 在复杂事件处理（CEP）系统中，负责全面监控和评估Kafka监听器的健康状态
 * 
 * 主要功能：
 * 1. 实时监控所有Kafka监听器的运行状态
 * 2. 生成详细的健康检查报告
 * 3. 提供细粒度的监听器状态诊断
 * 4. 支持系统可观测性和故障预警
 * 
 * 设计特点：
 * - 集成Spring Boot Actuator健康检查机制
 * - 使用动态状态评估策略
 * - 提供实时、精确的监听器状态信息
 * - 支持系统级别的健康监控
 * 
 * 使用场景：
 * - 实时消息处理系统监控
 * - 分布式事件流健康评估
 * - 微服务可靠性检查
 * - 系统运行状态诊断
 * 
 * 关键组件：
 * - KafkaListenerEndpointRegistry：监听器注册表
 * - Spring Boot Actuator：健康检查框架
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaListenerHealthIndicator implements HealthIndicator {

    /**
     * Kafka监听器端点注册表
     * 
     * 提供对所有Kafka监听器的集中管理和状态追踪
     * 支持动态获取监听器容器信息
     */
    private final KafkaListenerEndpointRegistry registry;

    /**
     * 执行全面的Kafka监听器健康检查
     * 
     * 核心健康评估逻辑：
     * 1. 获取所有注册的监听器容器
     * 2. 逐一检查每个监听器的运行状态
     * 3. 动态构建健康状态报告
     * 4. 记录并标记异常监听器
     * 
     * 健康状态判定规则：
     * - 所有监听器正常运行：系统状态为UP
     * - 任一监听器未运行：系统状态为DOWN
     * 
     * @return 系统健康状态详细报告
     */
    @Override
    public Health health() {
        // 获取所有监听器容器
        Collection<MessageListenerContainer> containers = registry.getListenerContainers();
        
        // 初始化健康状态为UP
        Health.Builder healthBuilder = Health.up();
        
        // 遍历并评估每个监听器的状态
        for (MessageListenerContainer container : containers) {
            String listenerId = container.getListenerId();
            boolean isRunning = container.isRunning();
            
            if (!isRunning) {
                // 发现未运行的监听器，将系统健康状态设置为DOWN
                healthBuilder = Health.down()
                    .withDetail("listener_" + listenerId, "Not Running");
                log.warn("Kafka监听器 {} 未运行，可能影响系统消息处理能力", listenerId);
            } else {
                // 记录正常运行的监听器状态
                healthBuilder.withDetail("listener_" + listenerId, "Running");
            }
        }
        
        // 构建并返回最终的健康状态报告
        return healthBuilder.build();
    }
}
