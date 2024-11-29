package com.example.cepengine.kafka;

import com.example.cepengine.disruptor.DataEvent;
import com.example.cepengine.entity.RawData;
import com.example.cepengine.entity.SiddhiRule;
import com.example.cepengine.service.SiddhiRuleService;
import com.lmax.disruptor.RingBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDataListener {

    private final RingBuffer<DataEvent> ringBuffer;
    private final SiddhiRuleService siddhiRuleService;

    @KafkaListener(topics = "${app.kafka.input-topic}")
    public void listen(String message) {
        log.info("Received message: {}", message);
        
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
                    // 设置事件数据
                    event.setData(message);
                    event.setRuleId(rule.getId());
                } finally {
                    // 发布事件
                    ringBuffer.publish(sequence);
                }
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
    }
}
