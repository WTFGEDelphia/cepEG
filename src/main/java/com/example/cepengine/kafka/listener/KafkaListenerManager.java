package com.example.cepengine.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Kafka监听器生命周期管理器
 * 
 * 在复杂事件处理（CEP）系统中，负责全面管理Kafka监听器的动态生命周期
 * 
 * 主要功能：
 * 1. 动态启动、停止和控制Kafka监听器
 * 2. 实时监控监听器运行状态
 * 3. 提供自动恢复和故障处理机制
 * 4. 支持细粒度的监听器管理
 * 
 * 设计特点：
 * - 使用线程安全的并发数据结构
 * - 提供定期健康检查机制
 * - 实现自动重启和状态跟踪
 * - 支持灵活的监听器控制策略
 * 
 * 使用场景：
 * - 实时数据流处理系统
 * - 高可用性消息监听架构
 * - 动态可扩展的消息处理框架
 * 
 * 关键组件：
 * - KafkaListenerEndpointRegistry：监听器注册表
 * - ScheduledExecutorService：定时监控服务
 * - ConcurrentHashMap：线程安全的状态存储
 * 
 * 性能和可靠性：
 * - 单线程定时调度，降低并发复杂度
 * - 30秒周期的健康检查
 * - 自动重试和状态管理机制
 * - 最多重试3次的失败容错策略
 */
@Slf4j
@Component
public class KafkaListenerManager {

    /**
     * Kafka监听器端点注册表
     * 
     * 提供对Kafka监听器的集中管理和控制
     * 支持动态注册、启动和停止监听器
     * 核心功能：
     * - 管理所有Kafka监听器实例
     * - 提供监听器容器查询接口
     * - 支持运行时动态管理
     */
    private final KafkaListenerEndpointRegistry registry;
    
    /**
     * 监听器状态映射表
     * 
     * 使用线程安全的ConcurrentHashMap存储监听器状态
     * 键：监听器ID
     * 值：监听器当前状态（启用/禁用、失败次数）
     * 
     * 特点：
     * - 线程安全的状态存储
     * - 支持并发访问和修改
     * - 实时跟踪每个监听器的状态
     */
    private final Map<String, ListenerStatus> listenerStatusMap = new ConcurrentHashMap<>();
    
    /**
     * 定时任务执行器
     * 
     * 用于周期性执行监听器状态检查任务
     * 单线程调度，确保线程安全和执行顺序
     * 
     * 配置特点：
     * - 单线程执行
     * - 低开销的调度机制
     * - 定期健康检查
     */
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
    
    /**
     * 监控任务控制句柄
     * 
     * 用于管理定期监控任务的生命周期
     * 支持取消和控制监控周期
     * 
     * 功能：
     * - 控制监控任务的启动和停止
     * - 提供任务生命周期管理
     */
    private ScheduledFuture<?> monitorTask;

    /**
     * 构造方法
     * 
     * 初始化Kafka监听器管理器
     * 自动启动监听器状态监控
     * 
     * 初始化流程：
     * 1. 注入监听器注册表
     * 2. 启动后台监控任务
     * 
     * @param registry Kafka监听器端点注册表
     */
    public KafkaListenerManager(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
        startMonitoring();
    }

    /**
     * 启动指定ID的Kafka监听器
     * 
     * 动态启动特定监听器，并更新其状态
     * 
     * @param listenerId 监听器唯一标识符
     */
    public void startListener(String listenerId) {
        MessageListenerContainer container = registry.getListenerContainer(listenerId);
        if (container != null) {
            container.start();
            listenerStatusMap.computeIfAbsent(listenerId, id -> new ListenerStatus()).setEnabled(true);
            log.info("Kafka监听器 {} 已启动", listenerId);
        }
    }

    /**
     * 停止指定ID的Kafka监听器
     * 
     * 动态停止特定监听器，并更新其状态
     * 
     * @param listenerId 监听器唯一标识符
     */
    public void stopListener(String listenerId) {
        MessageListenerContainer container = registry.getListenerContainer(listenerId);
        if (container != null) {
            container.stop();
            listenerStatusMap.computeIfAbsent(listenerId, id -> new ListenerStatus()).setEnabled(false);
            log.info("Kafka监听器 {} 已停止", listenerId);
        }
    }

    /**
     * 暂停指定ID的Kafka监听器
     * 
     * 临时中断特定监听器的消息处理
     * 
     * @param listenerId 监听器唯一标识符
     */
    public void pauseListener(String listenerId) {
        MessageListenerContainer container = registry.getListenerContainer(listenerId);
        if (container != null) {
            container.pause();
            log.info("Kafka监听器 {} 已暂停", listenerId);
        }
    }

    /**
     * 恢复指定ID的Kafka监听器
     * 
     * 重新激活之前暂停的监听器
     * 
     * @param listenerId 监听器唯一标识符
     */
    public void resumeListener(String listenerId) {
        MessageListenerContainer container = registry.getListenerContainer(listenerId);
        if (container != null) {
            container.resume();
            log.info("Kafka监听器 {} 已恢复", listenerId);
        }
    }

    /**
     * 检查指定ID的Kafka监听器运行状态
     * 
     * 判断特定监听器是否正在运行
     * 
     * @param listenerId 监听器唯一标识符
     * @return 监听器是否处于运行状态
     */
    public boolean isListenerRunning(String listenerId) {
        MessageListenerContainer container = registry.getListenerContainer(listenerId);
        return container != null && container.isRunning();
    }

    /**
     * 启动监听器状态监控任务
     * 
     * 初始化定期检查所有监听器状态的后台任务
     * 默认每30秒执行一次健康检查
     */
    private void startMonitoring() {
        monitorTask = scheduler.scheduleAtFixedRate(this::checkListeners, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * 执行监听器状态全面检查
     * 
     * 核心监控逻辑：
     * 1. 遍历所有注册的监听器
     * 2. 检查已启用但未运行的监听器
     * 3. 尝试自动重启
     * 4. 跟踪重启失败次数
     * 5. 在多次重启失败后禁用监听器
     */
    private void checkListeners() {
        Collection<MessageListenerContainer> containers = registry.getListenerContainers();
        for (MessageListenerContainer container : containers) {
            String listenerId = container.getListenerId();
            ListenerStatus status = listenerStatusMap.computeIfAbsent(listenerId, id -> new ListenerStatus());

            if (status.isEnabled()) {
                if (!container.isRunning()) {
                    log.warn("监听器 {} 未运行但应该运行。尝试重启...", listenerId);
                    try {
                        container.start();
                        status.resetFailureCount();
                    } catch (Exception e) {
                        status.incrementFailureCount();
                        log.error("重启监听器 {} 失败: {}", listenerId, e.getMessage());
                        
                        if (status.getFailureCount() >= 3) {
                            log.error("监听器 {} 已失败 {} 次。标记为禁用状态。", 
                                    listenerId, status.getFailureCount());
                            status.setEnabled(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * 关闭监听器管理器
     * 
     * 执行资源清理：
     * 1. 取消监控任务
     * 2. 关闭定时调度器
     */
    public void shutdown() {
        if (monitorTask != null) {
            monitorTask.cancel(true);
        }
        scheduler.shutdown();
    }

    /**
     * 监听器状态内部类
     * 
     * 提供细粒度的监听器状态管理
     * 跟踪监听器的启用状态和失败重试次数
     */
    private static class ListenerStatus {
        /** 监听器是否启用 */
        private boolean enabled = true;
        /** 重启失败次数 */
        private int failureCount = 0;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public void incrementFailureCount() {
            failureCount++;
        }

        public void resetFailureCount() {
            failureCount = 0;
        }

        public int getFailureCount() {
            return failureCount;
        }
    }
}
