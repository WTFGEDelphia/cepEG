package com.example.cepengine.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 处理后的数据实体类
 * 
 * 表示经过复杂事件处理（CEP）后的数据结果
 * 记录处理的规则、结果内容和处理时间
 * 
 * 主要功能：
 * 1. 存储处理后的数据信息
 * 2. 记录数据处理的上下文
 * 3. 支持后续的数据分析和追踪
 */
@Data
public class ProcessedData {
    /** 数据ID，唯一标识一条处理后的数据 */
    private Long id;
    
    /** 关联的规则ID，标识产生此数据的规则 */
    private Long ruleId;
    
    /** 原始数据ID，标识原始数据 */
    private Long rawDataId;
    
    /** 处理结果内容 */
    private String resultContent;
    
    /** 数据处理时间戳 */
    private LocalDateTime processedTime;
}
