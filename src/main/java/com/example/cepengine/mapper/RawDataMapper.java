package com.example.cepengine.mapper;

import com.example.cepengine.entity.RawData;
import org.apache.ibatis.annotations.*;

@Mapper
public interface RawDataMapper {
    
    @Insert("INSERT INTO raw_data (data_content, source) VALUES (#{dataContent}, #{source})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(RawData rawData);
    
    @Select("SELECT * FROM raw_data WHERE id = #{id}")
    RawData findById(@Param("id") Long id);
}
