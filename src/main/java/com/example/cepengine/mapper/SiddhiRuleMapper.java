package com.example.cepengine.mapper;

import com.example.cepengine.entity.SiddhiRule;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SiddhiRuleMapper {
    
    @Select("SELECT * FROM siddhi_rule WHERE status = 1")
    List<SiddhiRule> findAllActiveRules();
    
    @Select("SELECT * FROM siddhi_rule WHERE id = #{id}")
    SiddhiRule findById(@Param("id") Long id);
    
    @Insert("INSERT INTO siddhi_rule (rule_name, rule_description, rule_content, input_stream, output_stream, status) " +
            "VALUES (#{ruleName}, #{ruleDescription}, #{ruleContent}, #{inputStream}, #{outputStream}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SiddhiRule rule);
    
    @Update("UPDATE siddhi_rule SET rule_name = #{ruleName}, rule_description = #{ruleDescription}, " +
            "rule_content = #{ruleContent}, input_stream = #{inputStream}, output_stream = #{outputStream}, " +
            "status = #{status} WHERE id = #{id}")
    void update(SiddhiRule rule);
    
    @Delete("DELETE FROM siddhi_rule WHERE id = #{id}")
    void delete(@Param("id") Long id);
}
