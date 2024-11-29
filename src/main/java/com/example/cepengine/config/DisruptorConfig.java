package com.example.cepengine.config;

import com.example.cepengine.disruptor.DataEvent;
import com.example.cepengine.disruptor.DataEventFactory;
import com.example.cepengine.disruptor.DataEventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DisruptorConfig {

    @Value("${app.disruptor.buffer-size}")
    private int bufferSize;

    @Value("${app.disruptor.consumer-count}")
    private int consumerCount;

    @Bean
    public Disruptor<DataEvent> disruptor(DataEventHandler dataEventHandler) {
        // 创建DisruptorFactory
        DataEventFactory factory = new DataEventFactory();
        
        // 创建Disruptor
        Disruptor<DataEvent> disruptor = new Disruptor<>(
            factory,
            bufferSize,
            DaemonThreadFactory.INSTANCE
        );

        // 设置多个消费者
        DataEventHandler[] handlers = new DataEventHandler[consumerCount];
        for (int i = 0; i < consumerCount; i++) {
            handlers[i] = dataEventHandler;
        }
        disruptor.handleEventsWith(handlers);

        // 启动Disruptor
        disruptor.start();

        return disruptor;
    }

    @Bean
    public RingBuffer<DataEvent> ringBuffer(Disruptor<DataEvent> disruptor) {
        return disruptor.getRingBuffer();
    }
}
