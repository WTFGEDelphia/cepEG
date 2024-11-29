package com.example.cepengine.disruptor;

import lombok.Data;

@Data
public class DataEvent {
    private String data;
    private Long ruleId;
    private Long rawDataId;
}
