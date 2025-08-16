# 麻将比赛系统 API 使用说明

## 概述
本系统实现了麻将比赛管理、玩家认证、比赛报名等功能。

## 统一响应格式

所有API都返回统一的JSON格式：

```json
{
  "code": 0,
  "message": "请求成功",
  "data": {}
}
```

### 响应字段说明
- **code**: 响应状态码
  - `0`: 请求执行成功，没有异常
  - `-1`: 请求执行失败，有异常
- **message**: 响应消息，描述请求执行结果
- **data**: 响应数据，包含具体的业务数据

### 成功响应示例
```json
{
  "code": 0,
  "message": "创建比赛成功",
  "data": {
    "id": 1,
    "competitionName": "2024春季麻将大赛",
    "competitionType": "TEAM"
  }
}
```

### 失败响应示例
```json
{
  "code": -1,
  "message": "比赛名称已存在",
  "data": null
}
```

## 主要功能

### 1. 比赛管理
- 创建比赛（包含报名结束时间）
- 查询比赛信息
- 更新比赛信息
- 删除比赛

### 2. 比赛规则管理
- 创建比赛规则
- 查询规则信息
- 更新规则
- 删除规则

### 3. 玩家认证
- 玩家注册
- 玩家登录（返回30分钟有效期的JWT token）
- Token验证

### 4. 比赛报名
- 玩家报名比赛
- 取消报名
- 查询报名状态
- 报名截止时间验证

## API 端点

### 认证相关 (`/api/auth`)

#### 玩家登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "玩家用户名",
  "password": "密码"
}
```

响应：
```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "玩家用户名",
    "nickname": "玩家昵称",
    "message": "登录成功"
  }
}
```

#### 玩家注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "玩家用户名",
  "password": "密码",
  "qq": "QQ号",
  "nickname": "昵称",
  "mahjongId": "麻将ID",
  "mahjongNickname": "麻将昵称"
}
```

### 比赛规则管理 (`/api/competition-rules`)

#### 创建比赛规则
```
POST /api/competition-rules
Content-Type: application/json

{
  "ruleName": "标准规则",
  "originPoints": 25000,
  "firstPlacePoints": 100,
  "secondPlacePoints": 50,
  "thirdPlacePoints": 25,
  "fourthPlacePoints": 0
}
```

### 比赛管理 (`/api/competitions`)

#### 创建比赛
```
POST /api/competitions
Content-Type: application/json

{
  "competitionName": "2024春季麻将大赛",
  "competitionType": "TEAM",
  "rule": {
    "id": 1
  },
  "registrationDeadline": "2024-03-01T23:59:59"
}
```

### 比赛报名 (`/api/player-registrations`)

#### 报名比赛
```
POST /api/player-registrations/register
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "competitionId": 1
}
```

#### 取消报名
```
POST /api/player-registrations/cancel
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "competitionId": 1
}
```

## 重要特性

### 1. 报名截止时间
- 每个比赛都有报名截止时间
- 报名截止后无法报名或取消报名
- 报名截止后才能录入比赛成绩

### 2. JWT Token认证
- 登录成功后返回JWT token
- Token有效期30分钟
- 需要认证的API需要在Header中携带 `Authorization: Bearer <token>`

### 3. 数据验证
- 比赛名称唯一性验证
- 规则ID存在性验证
- 报名截止时间验证
- 重复报名检查

### 4. 统一响应格式
- 所有API都返回统一的JSON格式
- 成功响应code为0，失败响应code为-1
- 清晰的错误消息和成功消息

## 使用流程

1. **创建比赛规则**：先创建比赛规则，获取规则ID
2. **创建比赛**：使用规则ID创建比赛，设置报名截止时间
3. **玩家注册**：玩家注册账户
4. **玩家登录**：获取JWT token
5. **报名比赛**：使用token报名比赛
6. **管理报名**：查看报名状态，取消报名等

## 注意事项

- 所有时间字段使用ISO 8601格式
- 密码在传输和存储时都会加密
- 报名截止后无法修改报名状态
- Token过期需要重新登录
- 比赛名称必须唯一
- 所有API都返回统一的响应格式，便于前端处理
