package com.example.cepengine.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProcessedData {
    private Long id;
    private Long ruleId;
    private Long rawDataId;
    private String resultContent;
    private LocalDateTime processedTime;
}
