package com.example.cepengine.dto;

import java.util.List;
import lombok.Data;
import com.example.cepengine.model.TopicField;

/**
 * Siddhi 规则生成请求
 */
@Data
public class SiddhiGenerateRequest {
    /**
     * Kafka topic 名称
     */
    private String topicName;
    
    /**
     * 字段列表
     */
    private List<TopicField> fields;
    
    /**
     * 流名称（可选，默认使用 topic 名称）
     */
    private String streamName;
}
