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

@Slf4j
@Component
@RequiredArgsConstructor
public class DataEventHandler implements EventHandler<DataEvent> {

    private final SiddhiRuleService siddhiRuleService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SiddhiManager siddhiManager = new SiddhiManager();
    private final Map<Long, SiddhiAppRuntime> runtimeCache = new ConcurrentHashMap<>();

    @Override
    public void onEvent(DataEvent event, long sequence, boolean endOfBatch) {
        try {
            processEvent(event);
        } catch (Exception e) {
            log.error("Error processing event: {}", e.getMessage(), e);
        }
    }

    private void processEvent(DataEvent event) throws Exception {
        // 获取规则
        Long ruleId = event.getRuleId();
        SiddhiAppRuntime runtime = getRuntimeForRule(ruleId);
        
        if (runtime != null) {
            // 获取输入处理器
            String inputStream = runtime.getInputHandler("inputStream").toString();
            InputHandler inputHandler = runtime.getInputHandler(inputStream);
            
            // 发送数据到Siddhi进行处理
            inputHandler.send(new Object[]{event.getData()});
        }
    }

    private SiddhiAppRuntime getRuntimeForRule(Long ruleId) {
        return runtimeCache.computeIfAbsent(ruleId, this::createRuntime);
    }

    private SiddhiAppRuntime createRuntime(Long ruleId) {
        String ruleContent = siddhiRuleService.getRuleFromCache(ruleId);
        if (ruleContent == null) {
            return null;
        }

        SiddhiAppRuntime runtime = siddhiManager.createSiddhiAppRuntime(ruleContent);
        
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
        return runtime;
    }
}
