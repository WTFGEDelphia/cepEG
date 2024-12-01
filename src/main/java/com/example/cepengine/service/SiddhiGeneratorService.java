package com.example.cepengine.service;

import com.example.cepengine.dto.SiddhiGenerateRequest;
import com.example.cepengine.model.TopicField;
import com.example.cepengine.enums.SiddhiTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import java.util.List;

/**
 * Siddhi 语法生成服务
 */
@Slf4j
@Service
public class SiddhiGeneratorService {

    /**
     * 生成 Siddhi 流定义
     * @param request 请求对象
     * @return Siddhi 流定义语句
     */
    public String generateStreamDefinition(SiddhiGenerateRequest request) {
        if (request == null || request.getTopicName() == null || request.getFields() == null || request.getFields().isEmpty()) {
            throw new IllegalArgumentException("Invalid request: topicName and fields are required");
        }

        String streamName = StringUtils.hasText(request.getStreamName()) ? 
            request.getStreamName() : request.getTopicName() + "Stream";
        
        // 生成字段定义
        String fieldDefinitions = request.getFields().stream()
            .filter(Objects::nonNull)
            .map(this::convertFieldToSiddhiType)
            .collect(Collectors.joining(", "));
            
        // 生成完整的流定义
        return String.format("@source(type='kafka',\n" +
                           "       topic.list='%s',\n" +
                           "       partition.no.list='0',\n" +
                           "       threading.option='single.thread',\n" +
                           "       group.id='${kafka.consumer.group.id}',\n" +
                           "       bootstrap.servers='${kafka.bootstrap.servers}',\n" +
                           "       @map(type='json'))\n" +
                           "define stream %s (%s);",
                           request.getTopicName(),
                           streamName,
                           fieldDefinitions);
    }
    
    /**
     * 生成示例查询
     * @param request 请求对象
     * @return Siddhi 查询语句
     */
    public String generateExampleQuery(SiddhiGenerateRequest request) {
        if (request == null || request.getFields() == null || request.getFields().isEmpty()) {
            throw new IllegalArgumentException("Invalid request: fields are required");
        }

        String streamName = StringUtils.hasText(request.getStreamName()) ? 
            request.getStreamName() : request.getTopicName() + "Stream";
        
        // 找到时间字段，如果没有则使用第一个字段
        TopicField timeField = request.getFields().stream()
            .filter(Objects::nonNull)
            .filter(TopicField::isTimeField)
            .findFirst()
            .orElse(request.getFields().get(0));
            
        // 生成示例查询
        return String.format("@info(name='%s_example_query')\n" +
                           "from %s#window.time(5 min)\n" +
                           "select %s\n" +
                           "insert into OutputStreamName;",
                           streamName,
                           streamName,
                           generateSelectClause(request.getFields()));
    }
    
    /**
     * 将字段转换为 Siddhi 类型定义
     * @param field TopicField 对象
     * @return Siddhi 字段定义
     */
    private String convertFieldToSiddhiType(TopicField field) {
        if (field == null || !StringUtils.hasText(field.getFieldName()) || !StringUtils.hasText(field.getFieldType())) {
            throw new IllegalArgumentException("Field name and type are required");
        }

        if (!SiddhiTypeEnum.isSupported(field.getFieldType())) {
            log.warn("Unsupported field type: {}, using default type 'string'", field.getFieldType());
        }

        String siddhiType = SiddhiTypeEnum.toSiddhiType(field.getFieldType());
        return String.format("%s %s", field.getFieldName(), siddhiType);
    }
    
    /**
     * 生成 SELECT 子句
     * @param fields 字段列表
     * @return SELECT 子句
     */
    private String generateSelectClause(List<TopicField> fields) {
        return fields.stream()
            .filter(Objects::nonNull)
            .map(TopicField::getFieldName)
            .filter(StringUtils::hasText)
            .collect(Collectors.joining(", "));
    }
}
