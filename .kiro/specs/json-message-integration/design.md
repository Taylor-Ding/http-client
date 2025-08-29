# Design Document

## Overview

本设计文档描述了如何将用户提供的真实JSON报文结构整合到现有的HTTP客户端测试框架中。设计重点关注数据模型的抽象、测试数据的构建以及与现有HttpService的集成。

## Architecture

### 整体架构
```
测试层 (Test Layer)
├── MessageBuilder - 报文构建器
├── TestDataFactory - 测试数据工厂
└── HttpServiceTest - 增强的测试类

数据层 (Data Layer)
├── TxHeaderModel - 头部数据模型
├── TxEntityModel - 实体数据模型
├── TxComnModel - 通用数据模型
└── CompleteMessageModel - 完整报文模型

服务层 (Service Layer)
└── HttpService - 现有HTTP服务（保持不变）
```

## Components and Interfaces

### 1. 数据模型组件

#### TxHeaderModel
负责管理txHeader部分的所有固定字段名和可变值：
- 包含所有必要的头部字段（msgGrptMac, globalBusiTrackNo等）
- 提供字段值的设置和获取方法
- 支持默认值配置

#### TxEntityModel  
管理txEntity部分的用户自定义数据：
- custNo（客户号）
- qryVchrTpCd（查询凭证类型代码）
- txSceneCd（交易场景代码）
- 支持灵活的字段扩展

#### TxComnModel
处理txComn相关的通用字段：
- accountingDate（记账日期）
- 各种txComn子字段（txComn1-8）
- addtData（附加数据）

#### CompleteMessageModel
整合所有组件形成完整报文：
- 组合txHeader、txEntity、txComn
- 提供JSON序列化功能
- 验证报文完整性

### 2. 构建器组件

#### MessageBuilder
提供流式API构建复杂报文：
```java
MessageBuilder.create()
    .withTxHeader(header -> header
        .msgGrptMac("{{msgGrptMac}}")
        .globalBusiTrackNo("{{globalBusiTrackNo}}")
        // ... 其他字段
    )
    .withTxEntity(entity -> entity
        .custNo("040000037480013")
        .qryVchrTpCd("1")
        .txSceneCd("C203")
    )
    .withTxComn(comn -> comn
        .accountingDate("00000000")
        .busiSendSysOrCmptNo("99710730008")
    )
    .build();
```

#### TestDataFactory
提供预定义的测试数据模板：
- 标准业务场景数据
- 边界值测试数据
- 异常情况测试数据

## Data Models

### JSON结构映射

基于提供的报文，数据模型将映射如下结构：

```json
{
  "txBody": {
    "txEntity": {
      "custNo": "040000037480013",
      "qryVchrTpCd": "1",
      "txSceneCd": "C203"
    },
    "txComn": {
      "accountingDate": "00000000"
    },
    "addtData": {},
    "txComn5": {},
    "txComn6": {},
    "txComn7": {},
    "txComn8": {
      "busiSendSysOrCmptNo": "99710730008"
    },
    "txComn1": {
      "curQryReqNum": "0",
      "bgnIndexNo": "0"
    },
    "txComn2": {},
    "txComn3": {},
    "txComn4": {}
  },
  "txHeader": {
    "msgGrptMac": "{{msgGrptMac}}",
    "globalBusiTrackNo": "{{globalBusiTrackNo}}",
    "subtxNo": "{{subtxNo}}",
    "txStartTime": "{{txStartTime}}",
    "txSendTime": "{{txSendTime}}",
    // ... 其他固定字段
  }
}
```

### 字段类型定义

- **固定字段名，可变值**：txHeader中的所有字段
- **用户自定义结构**：txEntity和各txComn字段
- **嵌套对象支持**：支持多层级的JSON结构

## Error Handling

### 数据验证
- 必填字段检查
- 数据格式验证
- 字段长度限制

### 异常处理
- JSON序列化异常
- 网络请求异常
- 数据不完整异常

### 测试异常场景
- 缺失关键字段的报文
- 格式错误的数据
- 超长字段值

## Testing Strategy

### 单元测试
- 各数据模型的独立测试
- MessageBuilder的构建逻辑测试
- JSON序列化/反序列化测试

### 集成测试
- 完整报文的HTTP传输测试
- MockWebServer的请求验证
- 端到端的数据流测试

### 参数化测试
- 多种业务场景的数据驱动测试
- 边界值和异常值测试
- 性能基准测试

### 测试数据管理
- 使用TestDataFactory生成标准测试数据
- 支持外部配置文件加载测试数据
- 提供测试数据的版本管理