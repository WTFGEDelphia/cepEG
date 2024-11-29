CREATE DATABASE IF NOT EXISTS cep_engine;
USE cep_engine;

-- Siddhi规则配置表
CREATE TABLE IF NOT EXISTS siddhi_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_description TEXT COMMENT '规则描述',
    rule_content TEXT NOT NULL COMMENT 'Siddhi规则内容',
    input_stream VARCHAR(100) NOT NULL COMMENT '输入流名称',
    output_stream VARCHAR(100) NOT NULL COMMENT '输出流名称',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '规则状态：0-禁用，1-启用',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_rule_name (rule_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Siddhi规则配置表';

-- 原始数据表
CREATE TABLE IF NOT EXISTS raw_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_content TEXT NOT NULL COMMENT '原始数据内容',
    source VARCHAR(50) NOT NULL COMMENT '数据来源',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='原始数据表';

-- 处理结果表
CREATE TABLE IF NOT EXISTS processed_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_id BIGINT NOT NULL COMMENT '关联的规则ID',
    raw_data_id BIGINT NOT NULL COMMENT '原始数据ID',
    result_content TEXT NOT NULL COMMENT '处理结果内容',
    processed_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rule_id) REFERENCES siddhi_rule(id),
    FOREIGN KEY (raw_data_id) REFERENCES raw_data(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理结果表';
