package com.example.cepengine.enums;

import lombok.Getter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Siddhi类型枚举
 * 用于Java/Kafka类型到Siddhi类型的映射
 */
@Getter
public enum SiddhiTypeEnum {
    STRING("string", "string"),
    INTEGER("integer", "int"),
    INT("int", "int"),
    LONG("long", "long"),
    DOUBLE("double", "double"),
    FLOAT("float", "double"),
    BOOLEAN("boolean", "bool"),
    BOOL("bool", "bool"),
    TIMESTAMP("timestamp", "long"),
    DATETIME("datetime", "long"),
    DATE("date", "string");

    private final String sourceType;
    private final String siddhiType;

    private static final Map<String, String> TYPE_MAPPING = Arrays.stream(values())
            .collect(Collectors.toMap(
                    type -> type.sourceType,
                    type -> type.siddhiType,
                    (existing, replacement) -> existing
            ));

    SiddhiTypeEnum(String sourceType, String siddhiType) {
        this.sourceType = sourceType;
        this.siddhiType = siddhiType;
    }

    /**
     * 将Java/Kafka类型转换为Siddhi类型
     * @param sourceType Java/Kafka类型
     * @return Siddhi类型
     */
    public static String toSiddhiType(String sourceType) {
        if (sourceType == null || sourceType.trim().isEmpty()) {
            return "string";
        }
        return TYPE_MAPPING.getOrDefault(sourceType.toLowerCase(), "string");
    }

    /**
     * 检查是否支持该类型
     * @param sourceType Java/Kafka类型
     * @return 是否支持
     */
    public static boolean isSupported(String sourceType) {
        return sourceType != null && TYPE_MAPPING.containsKey(sourceType.toLowerCase());
    }
}
