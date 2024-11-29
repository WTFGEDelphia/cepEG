package com.example.cepengine.mapper;

import com.example.cepengine.entity.ProcessedData;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProcessedDataMapper {
    
    @Insert("INSERT INTO processed_data (rule_id, raw_data_id, result_content) " +
            "VALUES (#{ruleId}, #{rawDataId}, #{resultContent})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProcessedData processedData);
    
    @Select("SELECT * FROM processed_data WHERE id = #{id}")
    ProcessedData findById(@Param("id") Long id);
    
    @Select("SELECT * FROM processed_data WHERE raw_data_id = #{rawDataId}")
    ProcessedData findByRawDataId(@Param("rawDataId") Long rawDataId);
}
