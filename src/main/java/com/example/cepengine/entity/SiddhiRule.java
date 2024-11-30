package com.example.cepengine.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Siddhi复杂事件处理规则实体
 * 
 * 表示复杂事件处理（CEP）系统中的单个规则定义
 * 
 * 主要功能：
 * 1. 封装Siddhi规则的完整元数据
 * 2. 提供规则的结构化描述
 * 3. 支持规则的状态管理
 * 
 * 设计特点：
 * - 使用Lombok的@Data注解简化代码
 * - 包含规则的基本属性和时间戳
 * - 支持规则的动态配置和状态追踪
 * 
 * 使用场景：
 * - 复杂事件处理规则的定义和存储
 * - 规则的动态加载和管理
 * - 支持规则的版本控制和审计
 */
@Data
public class SiddhiRule {

    /**
     * 规则唯一标识符
     * 
     * 在数据库中作为主键，唯一标识一个Siddhi规则
     * 通常由数据库自动生成
     */
    private Long id;
    
    /**
     * 规则名称
     * 
     * 用于标识和描述规则的简短名称
     * 建议使用有意义且易于理解的命名
     */
    private String ruleName;
    
    /**
     * 规则描述
     * 
     * 提供规则的详细说明和背景信息
     * 帮助开发者和运维人员理解规则的purpose和行为
     */
    private String ruleDescription;
    
    /**
     * 规则内容
     * 
     * 存储完整的Siddhi规则定义
     * 包含查询逻辑、过滤条件和处理流程
     * 
     * 示例：
     * @code
     * from inputStream
     * select *
     * filter temperature > 30
     * insert into highTempStream
     */
    private String ruleContent;
    
    /**
     * 输入流名称
     * 
     * 定义规则监听和处理的输入数据流
     * 与Siddhi流定义中的输入流名称对应
     */
    private String inputStream;
    
    /**
     * 输出流名称
     * 
     * 定义规则处理后的输出数据流
     * 用于后续的事件处理或持久化
     */
    private String outputStream;
    
    /**
     * 规则状态
     * 
     * 标识规则的当前运行状态
     * 可能的值：
     * - 0：禁用
     * - 1：启用
     * - 其他自定义状态
     */
    private Integer status;
    
    /**
     * 规则创建时间
     * 
     * 记录规则首次创建的时间戳
     * 用于审计和版本追踪
     */
    private LocalDateTime createdTime;
    
    /**
     * 规则更新时间
     * 
     * 记录规则最近一次修改的时间戳
     * 支持规则变更的追踪和审计
     */
    private LocalDateTime updatedTime;
}
