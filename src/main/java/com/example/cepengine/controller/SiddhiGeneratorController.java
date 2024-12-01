package com.example.cepengine.controller;

import com.example.cepengine.dto.SiddhiGenerateRequest;
import com.example.cepengine.service.SiddhiGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Siddhi 语法生成控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/siddhi")
public class SiddhiGeneratorController {

    @Autowired
    private SiddhiGeneratorService siddhiGeneratorService;

    /**
     * 生成 Siddhi 流定义
     */
    @PostMapping("/stream")
    public String generateStreamDefinition(@RequestBody SiddhiGenerateRequest request) {
        log.info("Generating Siddhi stream definition for topic: {}", request.getTopicName());
        return siddhiGeneratorService.generateStreamDefinition(request);
    }

    /**
     * 生成示例查询
     */
    @PostMapping("/query")
    public String generateExampleQuery(@RequestBody SiddhiGenerateRequest request) {
        log.info("Generating example Siddhi query for topic: {}", request.getTopicName());
        return siddhiGeneratorService.generateExampleQuery(request);
    }
}
