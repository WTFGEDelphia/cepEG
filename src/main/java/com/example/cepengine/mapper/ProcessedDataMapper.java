package com.example.cepengine.mapper;

import com.example.cepengine.entity.ProcessedData;
import org.apache.ibatis.annotations.*;

/**
 * 处理后数据映射器接口
 * 
 * 定义对ProcessedData实体的数据库操作
 * 使用MyBatis框架进行持久化和查询
 * 
 * 主要功能：
 * 1. 数据库插入操作
 * 2. 数据库查询操作
 * 3. 数据库更新操作
 * 4. 数据库删除操作
 */
@Mapper
public interface ProcessedDataMapper {
    
    /**
     * 插入处理后的数据
     * 
     * @param processedData 待插入的处理后数据实体
     */
    @Insert("INSERT INTO processed_data (rule_id, raw_data_id, result_content) " +
            "VALUES (#{ruleId}, #{rawDataId}, #{resultContent})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProcessedData processedData);
    
    /**
     * 根据ID查询处理后的数据
     * 
     * @param id 数据ID
     * @return 查询到的处理后数据实体
     */
    @Select("SELECT * FROM processed_data WHERE id = #{id}")
    ProcessedData findById(@Param("id") Long id);
    
    /**
     * 根据原始数据ID查询处理后的数据
     * 
     * @param rawDataId 原始数据ID
     * @return 查询到的处理后数据实体
     */
    @Select("SELECT * FROM processed_data WHERE raw_data_id = #{rawDataId}")
    ProcessedData findByRawDataId(@Param("rawDataId") Long rawDataId);
}
