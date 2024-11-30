package com.example.cepengine.service.impl;

import com.example.cepengine.entity.SiddhiRule;
import com.example.cepengine.mapper.SiddhiRuleMapper;
import com.example.cepengine.service.SiddhiRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Siddhi规则服务实现类
 * 
 * 负责管理和处理Siddhi复杂事件处理规则的生命周期
 * 
 * 主要功能：
 * 1. 提供Siddhi规则的CRUD操作
 * 2. 管理规则缓存（使用Redis）
 * 3. 支持规则的事务性操作
 * 
 * 设计特点：
 * - 使用MyBatis进行数据库持久化
 * - 使用Redis缓存规则内容
 * - 支持事务管理
 * - 提供规则缓存的自动更新和过期机制
 */
@Service
@RequiredArgsConstructor
public class SiddhiRuleServiceImpl implements SiddhiRuleService {

    /** Siddhi规则数据库映射器 */
    private final SiddhiRuleMapper siddhiRuleMapper;

    /** Redis模板，用于缓存规则 */
    private final StringRedisTemplate redisTemplate;

    /** Redis缓存键前缀 */
    private static final String RULE_CACHE_PREFIX = "siddhi:rule:";

    /** 规则缓存过期时间（小时） */
    private static final long RULE_CACHE_TTL = 24; // 24小时

    /**
     * 查询所有活跃的Siddhi规则
     * 
     * @return 活跃规则列表
     */
    @Override
    public List<SiddhiRule> findAllActiveRules() {
        return siddhiRuleMapper.findAllActiveRules();
    }

    /**
     * 根据ID查询Siddhi规则
     * 
     * @param id 规则ID
     * @return 对应的Siddhi规则
     */
    @Override
    public SiddhiRule findById(Long id) {
        return siddhiRuleMapper.findById(id);
    }

    /**
     * 创建新的Siddhi规则
     * 
     * 使用事务管理，确保数据库操作的原子性
     * 创建规则后自动更新缓存
     * 
     * @param rule 待创建的Siddhi规则
     */
    @Override
    @Transactional
    public void createRule(SiddhiRule rule) {
        siddhiRuleMapper.insert(rule);
        updateRuleCache(rule);
    }

    /**
     * 更新现有的Siddhi规则
     * 
     * 使用事务管理，确保数据库操作的原子性
     * 更新规则后自动更新缓存
     * 
     * @param rule 待更新的Siddhi规则
     */
    @Override
    @Transactional
    public void updateRule(SiddhiRule rule) {
        siddhiRuleMapper.update(rule);
        updateRuleCache(rule);
    }

    /**
     * 删除指定ID的Siddhi规则
     * 
     * 使用事务管理，确保数据库操作的原子性
     * 删除规则后清除对应的缓存
     * 
     * @param id 待删除规则的ID
     */
    @Override
    @Transactional
    public void deleteRule(Long id) {
        siddhiRuleMapper.delete(id);
        redisTemplate.delete(RULE_CACHE_PREFIX + id);
    }

    /**
     * 从缓存获取Siddhi规则内容
     * 
     * 先尝试从Redis缓存获取规则内容
     * 如果缓存未命中，则从数据库查询并更新缓存
     * 
     * @param id 规则ID
     * @return 规则内容，如果规则不存在则返回null
     */
    @Override
    public String getRuleFromCache(Long id) {
        String cacheKey = RULE_CACHE_PREFIX + id;
        String ruleContent = redisTemplate.opsForValue().get(cacheKey);
        
        if (ruleContent == null) {
            SiddhiRule rule = findById(id);
            if (rule != null) {
                ruleContent = rule.getRuleContent();
                redisTemplate.opsForValue().set(cacheKey, ruleContent, RULE_CACHE_TTL, TimeUnit.HOURS);
            }
        }
        
        return ruleContent;
    }

    /**
     * 更新Siddhi规则缓存
     * 
     * 将规则内容写入Redis缓存
     * 设置缓存过期时间，避免缓存无效数据
     * 
     * @param rule 待缓存的Siddhi规则
     */
    @Override
    public void updateRuleCache(SiddhiRule rule) {
        String cacheKey = RULE_CACHE_PREFIX + rule.getId();
        redisTemplate.opsForValue().set(cacheKey, rule.getRuleContent(), RULE_CACHE_TTL, TimeUnit.HOURS);
    }
}
