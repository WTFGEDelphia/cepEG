package com.example.cepengine.config.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Disruptor配置类
 * 
 * 负责配置和初始化高性能并发事件处理框架Disruptor
 */
@Configuration
public class DisruptorConfig {

  @Value("${app.disruptor.buffer-size:1024}")
  private int bufferSize;

  @Value("${app.disruptor.consumer-count:4}")
  private int consumerCount;

  @Value("${app.disruptor.producer-type:SINGLE}")
  private String producerType;

  @Value("${app.disruptor.wait-strategy:SLEEPING}")
  private String waitStrategy;

  private ExecutorService executorService;
  private Disruptor<DataEvent> disruptor;

  /**
   * 创建自定义的线程工厂
   */
  private ThreadFactory createThreadFactory() {
    return new ThreadFactory() {
      private final AtomicInteger threadCounter = new AtomicInteger(1);

      @Override
      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("disruptor-thread-" + threadCounter.getAndIncrement());
        thread.setDaemon(true); // 设置为守护线程
        return thread;
      }
    };
  }

  /**
   * 获取等待策略
   */
  private WaitStrategy getWaitStrategy() {
    switch (waitStrategy.toUpperCase()) {
      case "BLOCKING":
        return new com.lmax.disruptor.BlockingWaitStrategy();
      case "YIELDING":
        return new com.lmax.disruptor.YieldingWaitStrategy();
      case "BUSY_SPIN":
        return new com.lmax.disruptor.BusySpinWaitStrategy();
      default:
        return new SleepingWaitStrategy();
    }
  }

  /**
   * 创建Disruptor实例
   */
  @Bean
  public Disruptor<DataEvent> disruptor(
      DataEventFactory dataEventFactory,
      DataEventHandler dataEventHandler) {
    // 创建线程池
    executorService = Executors.newFixedThreadPool(consumerCount, createThreadFactory());

    // 创建Disruptor实例
    disruptor = new Disruptor<>(
        dataEventFactory,
        bufferSize,
        executorService,
        ProducerType.valueOf(producerType.toUpperCase()),
        getWaitStrategy());

    // 配置事件处理器
    disruptor.handleEventsWith(dataEventHandler)
        .then((event, sequence, endOfBatch) -> {
          // 事件处理完成后的回调，可用于监控或清理
        });

    // 配置异常处理
    disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler());

    // 启动Disruptor
    disruptor.start();

    return disruptor;
  }

  @Bean
  public RingBuffer<DataEvent> ringBuffer(Disruptor<DataEvent> disruptor) {
    return disruptor.getRingBuffer();
  }

  /**
   * 在应用关闭时优雅地关闭Disruptor和线程池
   */
  @PreDestroy
  public void shutdown() {
    if (disruptor != null) {
      disruptor.shutdown();
    }
    if (executorService != null) {
      executorService.shutdown();
    }
  }
}

/**
 * Disruptor异常处理器
 */
class DisruptorExceptionHandler implements com.lmax.disruptor.ExceptionHandler<DataEvent> {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DisruptorExceptionHandler.class);

  @Override
  public void handleEventException(Throwable ex, long sequence, DataEvent event) {
    log.error("处理事件时发生异常，序号: {}, 事件: {}", sequence, event, ex);
  }

  @Override
  public void handleOnStartException(Throwable ex) {
    log.error("Disruptor启动时发生异常", ex);
  }

  @Override
  public void handleOnShutdownException(Throwable ex) {
    log.error("Disruptor关闭时发生异常", ex);
  }
}
