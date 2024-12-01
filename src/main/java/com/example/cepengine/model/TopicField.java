package com.example.cepengine.model;

import lombok.Data;

/**
 * Kafka Topic 字段描述
 */
@Data
public class TopicField {
    /**
     * 字段名称
     */
    private String fieldName;
    
    /**
     * 字段类型
     */
    private String fieldType;
    
    /**
     * 字段描述
     */
    private String description;
    
    /**
     * 是否是时间字段
     */
    private boolean isTimeField;
}
