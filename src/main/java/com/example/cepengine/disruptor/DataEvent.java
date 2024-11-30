package com.example.cepengine.disruptor;

import lombok.Data;

/**
 * Disruptor事件实体类
 * 
 * 表示在Disruptor高性能并发框架中传输的事件
 * 
 * 主要功能：
 * 1. 封装事件数据
 * 2. 支持在Disruptor环形缓冲区中传输
 * 3. 提供灵活的数据存储机制
 * 
 * 详细说明：
 * 本类是Disruptor事件处理的基本单元。
 * 通过Lombok的@Data注解，自动生成getter、setter和其他常用方法。
 * 可以存储不同类型的事件数据，支持复杂事件处理系统的灵活性。
 */
@Data
public class DataEvent {
    /** 事件数据 */
    private Object data;

    /** 规则ID */
    private Long ruleId;

    /** 原始数据ID */
    private Long rawDataId;

    /** 事件类型 */
    private String eventType;

    /** 事件时间戳 */
    private Long timestamp;

    /**
     * 重置事件
     * 
     * 清空事件中的数据，准备重用
     */
    public void reset() {
        this.data = null;
        this.ruleId = null;
        this.rawDataId = null;
        this.eventType = null;
        this.timestamp = null;
    }
}
