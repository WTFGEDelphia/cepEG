package com.example.cepengine.kafka;

import com.example.cepengine.disruptor.DataEvent;
import com.example.cepengine.entity.SiddhiRule;
import com.example.cepengine.service.SiddhiRuleService;
import com.lmax.disruptor.RingBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kafka数据消息监听器
 * 
 * 在复杂事件处理（CEP）系统中，负责接收和预处理Kafka消息
 * 
 * 主要功能：
 * 1. 监听指定Kafka主题的消息
 * 2. 获取所有活跃的Siddhi处理规则
 * 3. 将消息转换为Disruptor事件
 * 4. 并行分发事件到Disruptor环形缓冲区
 * 
 * 设计特点：
 * - 使用Spring Kafka的@KafkaListener注解
 * - 集成Disruptor高性能事件处理框架
 * - 支持动态规则匹配
 * - 提供详细的日志记录
 * 
 * 使用场景：
 * - 实时数据流处理
 * - 复杂事件的动态规则匹配
 * - 高吞吐量消息预处理
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDataListener {

    /**
     * Disruptor环形缓冲区
     * 
     * 用于高性能、无锁的事件传输和处理
     * 提供高并发、低延迟的事件分发机制
     */
    private final RingBuffer<DataEvent> ringBuffer;

    /**
     * Siddhi规则服务
     * 
     * 提供规则的动态获取和管理
     * 支持实时规则查询和过滤
     */
    private final SiddhiRuleService siddhiRuleService;

    /**
     * Kafka消息监听方法
     * 
     * 核心消息处理逻辑：
     * 1. 接收Kafka消息
     * 2. 获取所有活跃的Siddhi规则
     * 3. 为每个规则创建并发布Disruptor事件
     * 
     * 处理流程：
     * - 记录接收到的消息
     * - 获取活跃规则列表
     * - 为每个规则创建事件并发布到Disruptor
     * - 处理可能的异常情况
     * 
     * @param message 从Kafka主题接收的消息内容
     */
    @KafkaListener(
        topics = "${app.kafka.input-topic}",
        containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void listen(String message) {
        log.info("数据处理消费者接收到消息: {}", message);

        try {
            // 获取所有活跃的规则
            List<SiddhiRule> activeRules = siddhiRuleService.findAllActiveRules();

            // 对每个规则都创建一个事件
            for (SiddhiRule rule : activeRules) {
                // 获取RingBuffer的下一个序列号
                long sequence = ringBuffer.next();
                try {
                    // 获取该序列号对应的事件对象
                    DataEvent event = ringBuffer.get(sequence);
                    
                    // 设置事件数据和规则ID
                    event.setData(message);
                    event.setRuleId(rule.getId());
                    event.setEventType("DATA_PROCESSING");
                    event.setTimestamp(System.currentTimeMillis());
                } finally {
                    // 发布事件到Disruptor
                    ringBuffer.publish(sequence);
                }
            }
        } catch (Exception e) {
            // 记录处理异常，确保系统稳定性
            log.error("处理消息时发生错误: {}", e.getMessage(), e);
        }
    }
}
