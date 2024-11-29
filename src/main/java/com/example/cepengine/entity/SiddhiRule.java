package com.example.cepengine.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SiddhiRule {
    private Long id;
    private String ruleName;
    private String ruleDescription;
    private String ruleContent;
    private String inputStream;
    private String outputStream;
    private Integer status;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
