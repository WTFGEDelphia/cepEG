package com.example.cepengine.disruptor;

import com.example.cepengine.entity.ProcessedData;
import com.example.cepengine.service.SiddhiRuleService;
import com.lmax.disruptor.EventHandler;
import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Disruptor数据事件处理器
 * 
 * 负责处理Disruptor环形缓冲区中的数据事件
 * 使用Siddhi规则引擎进行复杂事件处理
 * 
 * 主要功能：
 * 1. 接收和处理Disruptor事件
 * 2. 动态加载和执行Siddhi规则
 * 3. 处理事件并将结果发送到Kafka
 * 4. 管理Siddhi运行时实例缓存
 * 
 * 设计特点：
 * - 使用@Slf4j注解简化日志记录
 * - 通过构造器注入依赖
 * - 支持灵活的事件处理策略
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataEventHandler implements EventHandler<DataEvent> {

    /**
     * Siddhi规则服务，用于获取和管理规则
     */
    private final SiddhiRuleService siddhiRuleService;

    /**
     * Kafka模板，用于发送处理结果
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Siddhi管理器，用于创建和管理Siddhi应用运行时
     */
    private final SiddhiManager siddhiManager = new SiddhiManager();

    /**
     * Siddhi运行时实例缓存，提高性能和减少重复创建开销
     */
    private final Map<Long, SiddhiAppRuntime> runtimeCache = new ConcurrentHashMap<>();

    /**
     * Disruptor事件处理方法
     * 
     * 处理从Disruptor环形缓冲区接收的数据事件
     * 
     * @param event 待处理的数据事件
     * @param sequence 事件序列号
     * @param endOfBatch 是否为批次中的最后一个事件
     * @throws Exception 事件处理过程中可能发生的异常
     */
    @Override
    public void onEvent(DataEvent event, long sequence, boolean endOfBatch) throws Exception {
        try {
            // 检查事件是否有效
            if (event.getData() == null || event.getRuleId() == null) {
                log.warn("接收到无效事件，跳过处理：事件数据={}, 规则ID={}", event.getData(), event.getRuleId());
                return;
            }

            // 获取或创建Siddhi运行时实例
            SiddhiAppRuntime runtime = runtimeCache.computeIfAbsent(event.getRuleId(), this::createSiddhiRuntime);

            // 获取输入处理器
            String inputStream = runtime.getInputHandler("inputStream").toString();
            InputHandler inputHandler = runtime.getInputHandler(inputStream);

            // 发送数据到Siddhi进行处理
            inputHandler.send(new Object[]{event.getData()});

            // 事件处理完成后重置事件
            event.reset();
        } catch (Exception e) {
            log.error("事件处理发生异常：规则ID={}", event.getRuleId(), e);
        }
    }

    /**
     * 创建Siddhi运行时实例
     * 
     * 根据规则ID动态创建Siddhi应用运行时
     * 
     * @param ruleId 规则ID
     * @return Siddhi应用运行时实例
     */
    private SiddhiAppRuntime createSiddhiRuntime(Long ruleId) {
        try {
            // 从规则服务获取Siddhi规则定义
            String siddhiApp = siddhiRuleService.getRuleFromCache(ruleId);

            // 创建并初始化Siddhi运行时
            SiddhiAppRuntime runtime = siddhiManager.createSiddhiAppRuntime(siddhiApp);

            // 添加输出流回调
            runtime.addCallback("outputStream", new StreamCallback() {
                @Override
                public void receive(Event[] events) {
                    for (Event event : events) {
                        // 处理输出数据
                        String outputData = event.getData()[0].toString();

                        // 发送到Kafka
                        kafkaTemplate.send("processed-data", outputData);

                        // 保存到数据库
                        ProcessedData processedData = new ProcessedData();
                        processedData.setRuleId(ruleId);
                        processedData.setResultContent(outputData);
                        // TODO: 保存到数据库
                    }
                }
            });

            runtime.start();
            log.info("成功创建Siddhi运行时：规则ID={}", ruleId);
            return runtime;
        } catch (Exception e) {
            log.error("创建Siddhi运行时失败：规则ID={}", ruleId, e);
            throw new RuntimeException("创建Siddhi运行时失败", e);
        }
    }
}
