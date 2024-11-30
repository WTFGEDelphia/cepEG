package com.example.cepengine.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 原始数据实体类
 * 
 * 表示进入复杂事件处理（CEP）系统的原始数据
 * 记录数据的基本信息和来源
 * 
 * 主要功能：
 * 1. 存储原始输入数据
 * 2. 记录数据的元信息
 * 3. 支持数据追踪和溯源
 */
@Data
public class RawData {
    /** 数据ID，唯一标识一条原始数据 */
    private Long id;
    
    /** 数据内容 */
    private String dataContent;
    
    /** 数据来源 */
    private String source;
    
    /** 数据接收时间戳 */
    private LocalDateTime createdTime;
}
