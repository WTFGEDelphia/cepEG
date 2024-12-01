package com.example.cepengine.config.disruptor;

import com.lmax.disruptor.EventFactory;
import org.springframework.stereotype.Component;

/**
 * Disruptor事件工厂类
 * 
 * 负责创建和预分配Disruptor事件实例
 * 
 * 主要功能：
 * 1. 实现Disruptor的EventFactory接口
 * 2. 预分配DataEvent事件实例
 * 3. 提高Disruptor性能和内存利用率
 * 
 * 详细说明：
 * 本类用于在Disruptor环形缓冲区中预先创建事件对象。
 * 通过预分配事件实例，减少运行时对象创建和垃圾回收的开销。
 * 遵循Disruptor框架的性能优化最佳实践。
 */
@Component
public class DataEventFactory implements EventFactory<DataEvent> {

    /**
     * 创建新的DataEvent实例
     * 
     * 在Disruptor环形缓冲区初始化时调用
     * 
     * @return 新创建的DataEvent实例
     */
    @Override
    public DataEvent newInstance() {
        return new DataEvent();
    }
}
