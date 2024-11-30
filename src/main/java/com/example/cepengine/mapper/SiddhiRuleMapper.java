package com.example.cepengine.mapper;

import com.example.cepengine.entity.SiddhiRule;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Siddhi规则数据访问接口
 * 
 * 使用MyBatis框架定义对SiddhiRule实体的数据库持久化操作
 * 
 * 主要功能：
 * 1. 提供Siddhi规则的CRUD（增删改查）操作
 * 2. 支持复杂事件处理规则的持久化管理
 * 3. 提供灵活的规则状态查询
 * 
 * 设计特点：
 * - 使用注解方式定义SQL语句
 * - 支持自动生成主键
 * - 提供参数化查询
 * - 支持动态规则状态管理
 * 
 * 使用场景：
 * - 复杂事件处理系统中规则的动态管理
 * - 规则的持久化存储和检索
 * - 支持实时规则更新和状态变更
 */
@Mapper
public interface SiddhiRuleMapper {
    
    /**
     * 插入新的Siddhi规则
     * 
     * 将新的Siddhi规则持久化到数据库
     * 支持自动生成主键，便于后续引用
     * 
     * @param rule 待插入的Siddhi规则实体
     */
    @Insert("INSERT INTO siddhi_rule (rule_name, rule_description, rule_content, input_stream, output_stream, status) " +
            "VALUES (#{ruleName}, #{ruleDescription}, #{ruleContent}, #{inputStream}, #{outputStream}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SiddhiRule rule);
    
    /**
     * 根据ID查询Siddhi规则
     * 
     * 精确查找特定ID的Siddhi规则
     * 返回完整的规则详细信息
     * 
     * @param id 规则的唯一标识符
     * @return 查询到的Siddhi规则实体，如果未找到则返回null
     */
    @Select("SELECT * FROM siddhi_rule WHERE id = #{id}")
    SiddhiRule findById(@Param("id") Long id);
    
    /**
     * 查询所有激活状态的Siddhi规则
     * 
     * 获取当前系统中所有处于激活状态的规则
     * 用于动态加载和管理活跃的复杂事件处理规则
     * 
     * @return 激活状态的Siddhi规则列表，如果没有激活规则则返回空列表
     */
    @Select("SELECT * FROM siddhi_rule WHERE status = 1")
    List<SiddhiRule> findAllActiveRules();
    
    /**
     * 更新现有的Siddhi规则
     * 
     * 根据规则ID更新规则的详细信息
     * 支持规则名称、描述、内容、输入输出流和状态的修改
     * 
     * @param rule 包含更新信息的Siddhi规则实体
     */
    @Update("UPDATE siddhi_rule SET rule_name = #{ruleName}, rule_description = #{ruleDescription}, " +
            "rule_content = #{ruleContent}, input_stream = #{inputStream}, output_stream = #{outputStream}, " +
            "status = #{status} WHERE id = #{id}")
    void update(SiddhiRule rule);
    
    /**
     * 根据ID删除Siddhi规则
     * 
     * 从数据库中删除指定ID的规则
     * 用于规则管理和清理
     * 
     * @param id 待删除规则的唯一标识符
     */
    @Delete("DELETE FROM siddhi_rule WHERE id = #{id}")
    void delete(@Param("id") Long id);
}
