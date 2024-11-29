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

@Service
@RequiredArgsConstructor
public class SiddhiRuleServiceImpl implements SiddhiRuleService {

    private final SiddhiRuleMapper siddhiRuleMapper;
    private final StringRedisTemplate redisTemplate;
    private static final String RULE_CACHE_PREFIX = "siddhi:rule:";
    private static final long RULE_CACHE_TTL = 24; // 24小时

    @Override
    public List<SiddhiRule> findAllActiveRules() {
        return siddhiRuleMapper.findAllActiveRules();
    }

    @Override
    public SiddhiRule findById(Long id) {
        return siddhiRuleMapper.findById(id);
    }

    @Override
    @Transactional
    public void createRule(SiddhiRule rule) {
        siddhiRuleMapper.insert(rule);
        updateRuleCache(rule);
    }

    @Override
    @Transactional
    public void updateRule(SiddhiRule rule) {
        siddhiRuleMapper.update(rule);
        updateRuleCache(rule);
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        siddhiRuleMapper.delete(id);
        redisTemplate.delete(RULE_CACHE_PREFIX + id);
    }

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

    @Override
    public void updateRuleCache(SiddhiRule rule) {
        String cacheKey = RULE_CACHE_PREFIX + rule.getId();
        redisTemplate.opsForValue().set(cacheKey, rule.getRuleContent(), RULE_CACHE_TTL, TimeUnit.HOURS);
    }
}
