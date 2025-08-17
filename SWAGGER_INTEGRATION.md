# 麻将比赛系统 Swagger 文档集成指南

## 概述

本文档描述了如何在麻将比赛系统中集成和使用 Swagger/OpenAPI 3.0 文档。

## 集成内容

### 1. 依赖添加

在 `build.gradle` 中添加了 SpringDoc OpenAPI 依赖：

```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
```

### 2. 配置文件

#### application.properties 配置

```properties
# SpringDoc OpenAPI 配置
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.disable-swagger-default-url=true
springdoc.swagger-ui.use-root-path=true
springdoc.show-actuator=false
springdoc.packages-to-scan=com.rogister.mjcompetition.controller
```

#### SwaggerConfig.java 配置类

创建了专门的配置类来设置：
- API 基本信息（标题、描述、版本）
- 联系方式和许可证信息
- 服务器环境配置
- JWT Bearer Token 认证配置

### 3. 安全配置

在 `SecurityConfig.java` 中添加了 Swagger 相关路径的公开访问权限：
- `/swagger-ui/**` - Swagger UI 资源
- `/swagger-ui.html` - Swagger UI 主页
- `/api-docs/**` - OpenAPI 文档
- `/v3/api-docs/**` - OpenAPI v3 文档

### 4. Controller 注解

为所有 Controller 添加了完整的 Swagger 注解：

#### 类级别注解
- `@Tag(name = "模块名称", description = "模块描述")`

#### 方法级别注解
- `@Operation(summary = "接口摘要", description = "详细描述")`
- `@ApiResponses` - 响应状态码说明
- `@SecurityRequirement(name = "bearerAuth")` - 需要认证的接口
- `@Parameter` - 参数说明

### 5. DTO 注解

为主要的 DTO 类添加了 Schema 注解：

#### ApiResponse.java
- 统一响应格式的文档说明
- 各字段的描述和示例

#### CompetitionCreateRequest.java
- 比赛创建请求的完整字段说明
- 时间格式和约束说明

#### MatchResultCreateRequest.java
- 比赛成绩录入的详细字段说明
- 东南西北四个方位的参数说明

## 访问方式

### 启动应用后访问

1. **Swagger UI 界面**：http://localhost:8080/swagger-ui.html
2. **OpenAPI 文档 JSON**：http://localhost:8080/api-docs
3. **OpenAPI 文档 YAML**：http://localhost:8080/api-docs.yaml

### 功能模块

文档中包含以下主要模块：

1. **玩家管理** - 玩家注册、登录、信息管理
2. **团队管理** - 团队创建、加入、查询、退出
3. **比赛管理** - 比赛的创建、查询、更新、删除
4. **比赛报名** - 个人和团队报名功能
5. **比赛成绩管理** - 成绩录入、查询、更新、统计
6. **晋级管理** - 个人赛和团队赛晋级
7. **管理员功能** - 管理员账户管理
8. **比赛状态管理** - 状态查询和更新
9. **比赛规则管理** - 规则的 CRUD 操作

## 认证说明

### JWT Bearer Token 认证

大部分接口需要 JWT 认证，使用方式：

1. 先调用登录接口获取 token
2. 在 Swagger UI 中点击右上角的 "Authorize" 按钮
3. 输入 `Bearer {your_token}` 格式的认证信息
4. 测试需要认证的接口

### 公开接口

以下接口无需认证：
- 玩家注册/登录
- 管理员登录
- 比赛列表查看
- 比赛状态查询
- Swagger 文档相关接口

## 使用示例

### 1. 玩家注册
```json
POST /api/player/register
{
  "username": "testplayer",
  "password": "password123",
  "email": "test@example.com",
  "phone": "13800138000",
  "realName": "测试玩家"
}
```

### 2. 创建比赛
```json
POST /api/competitions/create
{
  "name": "2024年麻将锦标赛",
  "description": "年度重要比赛",
  "type": "INDIVIDUAL",
  "ruleId": 1,
  "maxParticipants": 64,
  "registrationStartTime": "2024-01-01T00:00:00",
  "registrationEndTime": "2024-01-31T23:59:59",
  "startTime": "2024-02-01T09:00:00",
  "endTime": "2024-02-28T18:00:00"
}
```

### 3. 录入比赛成绩
```json
POST /api/match-results
{
  "competitionId": 1,
  "roundNumber": 1,
  "matchNumber": 1,
  "matchName": "第一轮第一场",
  "eastPlayerId": 1,
  "southPlayerId": 2,
  "westPlayerId": 3,
  "northPlayerId": 4,
  "eastScore": 1000,
  "southScore": 800,
  "westScore": 1200,
  "northScore": 1000
}
```

## 开发建议

1. **新增接口时**：
   - 为 Controller 方法添加 `@Operation` 注解
   - 为请求参数添加 `@Parameter` 注解
   - 为需要认证的接口添加 `@SecurityRequirement`

2. **新增 DTO 时**：
   - 为类添加 `@Schema` 注解
   - 为字段添加详细的 `@Schema` 说明

3. **测试接口时**：
   - 使用 Swagger UI 进行交互式测试
   - 检查请求/响应格式是否符合预期
   - 验证认证流程是否正常

## 注意事项

1. 生产环境建议禁用 Swagger UI 或添加访问控制
2. 敏感信息（如密码）不要在示例中显示真实值
3. 定期更新文档说明以保持与代码同步
4. 合理使用标签对接口进行分组管理

## 版本信息

- SpringDoc OpenAPI: 2.2.0
- OpenAPI 规范版本: 3.0.3
- 支持的格式: JSON, YAML
