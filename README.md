# Real-Time Complex Event Processing Engine

基于Spring Boot的实时复杂事件处理引擎，集成了Siddhi、Kafka、Disruptor、Redis和MariaDB，用于高性能的实时数据处理和分析。

## 技术栈

- Spring Boot 2.7.12
- Siddhi 5.1.2
- Apache Kafka
- LMAX Disruptor 3.4.4
- Redis
- MariaDB
- MyBatis

## 项目目录结构

```
src/main/java/com/example/cepengine/
│
├── config/                     # 配置类目录
│   ├── kafka/                  # Kafka相关配置
│   │   ├── consumer/           # Kafka消费者配置
│   │   ├── producer/           # Kafka生产者配置
│   │   └── listener/           # Kafka监听器配置
│   │       └── manager/        # Kafka监听器管理
│   │
│   └── disruptor/              # Disruptor配置
│       ├── DataEvent.java      # 事件数据模型
│       ├── DataEventFactory.java  # 事件工厂
│       ├── DataEventHandler.java  # 事件处理器
│       └── DisruptorConfig.java   # Disruptor配置
│
├── controller/                 # 控制器层
│   └── SiddhiGeneratorController.java  # Siddhi语法生成控制器
│
├── service/                    # 服务层
│   ├── SiddhiGeneratorService.java  # Siddhi语法生成服务
│   └── SiddhiRuleService.java   # Siddhi规则服务
│
├── domain/                     # 领域模型
│   └── TopicField.java         # 通用主题字段模型
│
├── dto/                        # 数据传输对象
│   └── SiddhiGenerateRequest.java  # Siddhi语法生成请求DTO
│
├── entity/                     # 持久化实体
│   └── ProcessedData.java      # 处理后的数据实体
│
├── enums/                      # 枚举类
│   └── SiddhiTypeEnum.java     # Siddhi类型枚举
│
└── repository/                 # 数据访问层
    └── ProcessedDataRepository.java  # 处理数据仓库
```

## 系统架构

### 核心组件

1. **数据接入层**
   - Kafka消费者：接收实时数据流
   - 支持多主题订阅
   - 可配置的消费者组和偏移量管理

2. **规则引擎**
   - Siddhi CEP引擎：执行复杂事件处理
   - 支持实时规则更新
   - Redis规则缓存机制

3. **高性能处理层**
   - Disruptor环形缓冲区：高效的事件处理
   - 多消费者并行处理
   - 无锁设计，低延迟

4. **数据存储层**
   - MariaDB：持久化存储
   - Redis：规则缓存
   - MyBatis：数据库访问框架

### 数据模型

1. **规则配置表(siddhi_rule)**
   - 规则ID
   - 规则名称
   - 规则描述
   - 规则内容
   - 输入/输出流配置
   - 规则状态

2. **原始数据表(raw_data)**
   - 数据ID
   - 数据内容
   - 数据来源
   - 创建时间

3. **处理结果表(processed_data)**
   - 结果ID
   - 关联规则ID
   - 原始数据ID
   - 处理结果
   - 处理时间

## 主要功能

1. **实时数据处理**
   - 从Kafka实时接收数据
   - 使用Disruptor进行高性能事件处理
   - 支持并行处理多个规则

2. **规则管理**
   - 动态规则配置和更新
   - Redis规则缓存
   - 规则状态管理（启用/禁用）

3. **结果输出**
   - 处理结果推送到Kafka
   - 结果持久化到MariaDB
   - 支持多目标输出

4. **性能优化**
   - 使用Disruptor实现高吞吐量
   - Redis缓存减少数据库访问
   - 多线程并行处理

5. **Siddhi语法生成**
   - 自动生成Siddhi流定义
   - 支持Kafka Topic字段映射
   - 提供示例查询生成
   - REST API接口

### Siddhi语法生成API

#### 1. 生成流定义

```bash
POST /api/siddhi/stream
```

请求示例：
```json
{
  "topicName": "sensor-data",
  "fields": [
    {
      "fieldName": "timestamp",
      "fieldType": "long",
      "isTimeField": true
    },
    {
      "fieldName": "temperature",
      "fieldType": "double"
    },
    {
      "fieldName": "deviceId",
      "fieldType": "string"
    }
  ]
}
```

响应示例：
```sql
@source(type='kafka',
       topic.list='sensor-data',
       partition.no.list='0',
       threading.option='single.thread',
       group.id='${kafka.consumer.group.id}',
       bootstrap.servers='${kafka.bootstrap.servers}',
       @map(type='json'))
define stream SensorDataStream (timestamp long, temperature double, deviceId string);
```

#### 2. 生成示例查询

```bash
POST /api/siddhi/query
```

请求示例：
```json
{
  "topicName": "sensor-data",
  "streamName": "SensorDataStream",
  "fields": [
    {
      "fieldName": "timestamp",
      "fieldType": "long",
      "isTimeField": true
    },
    {
      "fieldName": "temperature",
      "fieldType": "double"
    },
    {
      "fieldName": "deviceId",
      "fieldType": "string"
    }
  ]
}
```

响应示例：
```sql
@info(name='sensor_data_example_query')
from SensorDataStream#window.time(5 min)
select timestamp, temperature, deviceId
insert into OutputStreamName;
```

### 支持的数据类型映射

| Java/Kafka类型 | Siddhi类型 |
|---------------|------------|
| string        | string     |
| integer/int   | int        |
| long          | long       |
| double/float  | double     |
| boolean       | bool       |
| timestamp     | long       |

## 快速开始

### 环境要求

- JDK 11+
- Maven 3.6+
- Kafka 2.x
- Redis 6.x
- MariaDB 10.x

### 安装步骤

1. **克隆项目**
   ```bash
   git clone [repository-url]
   cd cepEG
   ```

2. **配置数据库**
   ```bash
   # 执行数据库初始化脚本
   mysql -u root -p < src/main/resources/db/schema.sql
   ```

3. **配置应用**
   - 修改 `application.yml` 中的配置：
     - Kafka连接信息
     - Redis连接信息
     - MariaDB连接信息

4. **编译运行**
   ```bash
   mvn clean package
   java -jar target/cep-engine-1.0-SNAPSHOT.jar
   ```

### 使用示例

1. **添加规则**
   ```sql
   INSERT INTO siddhi_rule (rule_name, rule_content, input_stream, output_stream, status)
   VALUES ('温度告警规则',
           'define stream inputStream (temperature double, deviceId string);
            define stream outputStream (deviceId string, alert string);
            
            from inputStream[temperature > 30]
            select deviceId, "High temperature alert" as alert
            insert into outputStream;',
           'inputStream',
           'outputStream',
           1);
   ```

2. **发送测试数据到Kafka**
   ```bash
   # 发送测试数据到input-topic
   kafka-console-producer.sh --broker-list localhost:9092 --topic raw-data
   > {"temperature": 35, "deviceId": "device001"}
   ```

3. **查看处理结果**
   ```bash
   # 消费output-topic的数据
   kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic processed-data --from-beginning
   ```

## 性能优化建议

1. **Kafka配置**
   - 适当调整分区数
   - 配置合适的批处理大小
   - 优化消费者组数量

2. **Disruptor配置**
   - 调整RingBuffer大小
   - 配置合适的消费者数量
   - 选择合适的等待策略

3. **Redis优化**
   - 配置合适的缓存过期时间
   - 使用适当的缓存策略
   - 监控缓存命中率

4. **数据库优化**
   - 添加必要的索引
   - 定期维护和优化
   - 配置合适的连接池

## 监控和维护

1. **系统监控**
   - JVM监控
   - Kafka消费延迟监控
   - Redis缓存监控
   - 数据库性能监控

2. **日志管理**
   - 业务日志
   - 性能日志
   - 错误日志

3. **告警机制**
   - 处理延迟告警
   - 错误率告警
   - 资源使用告警

## 贡献指南

欢迎提交Issue和Pull Request来帮助改进项目。

## 许可证

This project is licensed under the MIT License - see the LICENSE file for details
