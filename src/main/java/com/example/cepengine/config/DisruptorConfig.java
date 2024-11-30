package com.example.cepengine.config;

import com.example.cepengine.disruptor.DataEvent;
import com.example.cepengine.disruptor.DataEventFactory;
import com.example.cepengine.disruptor.DataEventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * Disruptor配置类
 * 
 * 负责配置和初始化高性能并发事件处理框架Disruptor
 * 
 * 主要功能：
 * 1. 配置Disruptor的环形缓冲区大小
 * 2. 创建Disruptor实例
 * 3. 配置事件处理器
 * 4. 管理事件处理线程
 * 
 * 详细说明：
 * 本类提供了Disruptor的基本配置和初始化功能，包括环形缓冲区大小、事件处理器和事件处理线程的管理。
 * 通过本类，可以快速配置和部署Disruptor框架，实现高性能的事件处理和并发处理。
 */
@Configuration
public class DisruptorConfig {

    /**
     * 环形缓冲区默认大小
     * 
     * 该值可以通过配置文件进行动态配置，默认值为1024。
     */
    @Value("${app.disruptor.buffer-size:1024}")
    private int bufferSize;

    /**
     * 事件处理器消费者数量
     * 
     * 该值可以通过配置文件进行动态配置，默认值为4。
     */
    @Value("${app.disruptor.consumer-count:4}")
    private int consumerCount;

    /**
     * 创建Disruptor实例
     * 
     * 该方法创建了一个Disruptor实例，并配置了事件工厂、事件处理器和事件处理线程。
     * 
     * @param dataEventFactory 事件工厂
     * @param dataEventHandler 事件处理器
     * @return 配置好的Disruptor实例
     */
    @Bean
    public Disruptor<DataEvent> disruptor(
            DataEventFactory dataEventFactory, 
            DataEventHandler dataEventHandler) {
        // 创建Disruptor实例
        Disruptor<DataEvent> disruptor = new Disruptor<>(
            dataEventFactory, // 事件工厂
            bufferSize, // 环形缓冲区大小
            Executors.newFixedThreadPool(consumerCount) // 事件处理线程池
        );
        
        // 配置事件处理器
        disruptor.handleEventsWith(dataEventHandler);
        
        // 启动Disruptor
        disruptor.start();
        
        return disruptor;
    }

    /**
     * 获取Disruptor的环形缓冲区
     * 
     * 该方法返回Disruptor的环形缓冲区，用于事件的发布和处理。
     * 
     * @param disruptor Disruptor实例
     * @return 环形缓冲区
     */
    @Bean
    public RingBuffer<DataEvent> ringBuffer(Disruptor<DataEvent> disruptor) {
        return disruptor.getRingBuffer();
    }
}
