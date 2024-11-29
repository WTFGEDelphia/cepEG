package com.example.cepengine.service;

import com.example.cepengine.entity.SiddhiRule;
import java.util.List;

public interface SiddhiRuleService {
    List<SiddhiRule> findAllActiveRules();
    SiddhiRule findById(Long id);
    void createRule(SiddhiRule rule);
    void updateRule(SiddhiRule rule);
    void deleteRule(Long id);
    String getRuleFromCache(Long id);
    void updateRuleCache(SiddhiRule rule);
}
