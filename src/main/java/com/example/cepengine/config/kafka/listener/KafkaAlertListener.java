package com.example.cepengine.kafka;

import com.example.cepengine.entity.ProcessedData;
import com.example.cepengine.mapper.ProcessedDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Kafka告警消息处理监听器
 * 
 * 在复杂事件处理（CEP）系统中，负责实时接收、处理和持久化告警消息
 * 
 * 主要功能：
 * 1. 实时监听告警主题的Kafka消息
 * 2. 将告警消息转换为结构化的ProcessedData实体
 * 3. 持久化告警数据到数据库
 * 4. 提供可靠的消息处理和确认机制
 * 5. 记录和跟踪告警事件
 * 
 * 设计特点：
 * - 使用Spring Kafka的@KafkaListener注解
 * - 支持手动消息确认
 * - 提供异常处理和日志记录
 * - 实现事件数据的结构化存储
 * 
 * 使用场景：
 * - 实时告警监控系统
 * - 复杂事件告警处理
 * - 告警数据持久化和审计
 * - 分布式告警管理
 * 
 * 关键组件：
 * - ProcessedDataMapper：数据持久化接口
 * - Kafka消息监听机制
 * - 本地时间戳记录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaAlertListener {

    /**
     * 处理后数据持久化映射器
     * 
     * 提供告警数据的数据库持久化能力
     * 支持将告警事件转换并存储到数据库
     */
    private final ProcessedDataMapper processedDataMapper;

    /**
     * Kafka告警消息处理方法
     * 
     * 核心告警消息处理逻辑：
     * 1. 接收Kafka告警主题的消息
     * 2. 构建ProcessedData实体
     * 3. 持久化告警数据
     * 4. 执行消息确认
     * 5. 处理可能的异常情况
     * 
     * 处理流程：
     * - 创建ProcessedData对象
     * - 设置消息内容和处理时间
     * - 插入数据库
     * - 记录处理日志
     * - 手动确认消息
     * 
     * 注意事项：
     * - 使用手动确认模式，提高消息处理可靠性
     * - TODO：需要实现动态规则ID获取
     * 
     * @param message 从Kafka告警主题接收的消息内容
     * @param acknowledgment Kafka消息确认对象
     */
    @KafkaListener(
        topics = "${app.kafka.topic.alert}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void processAlertMessage(String message, Acknowledgment acknowledgment) {
        try {
            // 创建ProcessedData实体
            ProcessedData processedData = new ProcessedData();
            processedData.setResultContent(message);
            processedData.setRuleId(1L);  // TODO: 动态获取规则ID
            processedData.setProcessedTime(LocalDateTime.now());

            // 持久化告警数据
            processedDataMapper.insert(processedData);

            // 记录告警日志
            log.info("处理告警消息: {}", message);

            // 手动确认消息
            acknowledgment.acknowledge();
        } catch (Exception e) {
            // 处理异常情况，记录详细错误信息
            log.error("处理告警消息时发生错误: {}", message, e);
        }
    }
}
