package com.example.cepengine.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RawData {
    private Long id;
    private String dataContent;
    private String source;
    private LocalDateTime createdTime;
}
