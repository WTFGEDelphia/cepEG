package com.example.cepengine.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 主题字段通用领域模型
 * 用于描述Kafka、Siddhi等场景下的字段信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicField implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 是否为时间字段
     */
    private boolean isTimeField;

    /**
     * 字段是否必填
     */
    private boolean required;

    /**
     * 字段默认值
     */
    private Object defaultValue;
}
