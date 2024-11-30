package com.example.cepengine.mapper;

import com.example.cepengine.entity.RawData;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 原始数据映射器接口
 * 
 * 定义对RawData实体的数据库操作
 * 使用MyBatis框架进行持久化和查询
 * 
 * 主要功能：
 * 1. 数据库插入操作
 * 2. 数据库查询操作
 * 3. 数据库更新操作
 * 4. 数据库删除操作
 */
@Mapper
public interface RawDataMapper {
    
    /**
     * 插入原始数据
     * 
     * @param rawData 待插入的原始数据实体
     */
    @Insert("INSERT INTO raw_data (data_content, source, created_time) " +
            "VALUES (#{dataContent}, #{source}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(RawData rawData);
    
    /**
     * 根据ID查询原始数据
     * 
     * @param id 数据ID
     * @return 查询到的原始数据实体
     */
    @Select("SELECT * FROM raw_data WHERE id = #{id}")
    RawData findById(@Param("id") Long id);
    
    /**
     * 根据数据来源查询原始数据列表
     * 
     * @param source 数据来源
     * @return 匹配的原始数据列表
     */
    @Select("SELECT * FROM raw_data WHERE source = #{source}")
    List<RawData> findBySource(@Param("source") String source);
    
    /**
     * 更新原始数据
     * 
     * @param rawData 待更新的原始数据实体
     */
    @Update("UPDATE raw_data SET data_content = #{dataContent}, source = #{source} WHERE id = #{id}")
    void update(RawData rawData);
    
    /**
     * 根据ID删除原始数据
     * 
     * @param id 数据ID
     */
    @Delete("DELETE FROM raw_data WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
