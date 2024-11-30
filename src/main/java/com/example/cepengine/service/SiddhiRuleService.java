package com.example.cepengine.service;

import com.example.cepengine.entity.SiddhiRule;
import java.util.List;

/**
 * Siddhi规则服务接口
 * 
 * 定义复杂事件处理（CEP）规则的服务契约
 * 
 * 主要功能：
 * 1. 提供Siddhi规则的生命周期管理
 * 2. 支持规则的缓存和检索
 * 3. 定义规则操作的标准接口
 * 
 * 设计特点：
 * - 提供规则的增删改查操作
 * - 支持规则缓存机制
 * - 定义规则管理的抽象接口
 * 
 * 使用场景：
 * - 动态管理复杂事件处理规则
 * - 支持规则的实时更新和缓存
 * - 在分布式事件处理系统中灵活配置规则
 */
public interface SiddhiRuleService {

    /**
     * 查询所有激活状态的Siddhi规则
     * 
     * 获取当前系统中处于激活状态的所有规则
     * 用于动态加载和管理复杂事件处理规则
     * 
     * @return 激活状态的Siddhi规则列表
     */
    List<SiddhiRule> findAllActiveRules();

    /**
     * 根据ID查询Siddhi规则
     * 
     * 精确查找特定ID的Siddhi规则
     * 返回完整的规则详细信息
     * 
     * @param id 规则的唯一标识符
     * @return 查询到的Siddhi规则，如果未找到则返回null
     */
    SiddhiRule findById(Long id);

    /**
     * 创建新的Siddhi规则
     * 
     * 将新的Siddhi规则持久化到存储系统
     * 并更新规则缓存
     * 
     * @param rule 待创建的Siddhi规则
     */
    void createRule(SiddhiRule rule);

    /**
     * 更新现有的Siddhi规则
     * 
     * 修改指定的Siddhi规则
     * 更新持久化存储并刷新规则缓存
     * 
     * @param rule 包含更新信息的Siddhi规则
     */
    void updateRule(SiddhiRule rule);

    /**
     * 删除指定ID的Siddhi规则
     * 
     * 从存储系统中删除规则
     * 同时清除对应的规则缓存
     * 
     * @param id 待删除规则的唯一标识符
     */
    void deleteRule(Long id);

    /**
     * 从缓存获取Siddhi规则内容
     * 
     * 优先从缓存（如Redis）获取规则内容
     * 如果缓存未命中，则从持久化存储获取并更新缓存
     * 
     * @param id 规则的唯一标识符
     * @return 规则内容，如果规则不存在则返回null
     */
    String getRuleFromCache(Long id);

    /**
     * 更新Siddhi规则缓存
     * 
     * 将规则内容写入缓存（如Redis）
     * 设置适当的过期时间
     * 
     * @param rule 待缓存的Siddhi规则
     */
    void updateRuleCache(SiddhiRule rule);
}
