# 基于角色的API使用指南

## 系统概述

系统已重构为基于角色的权限控制，分为两类用户：
- **玩家 (PLAYER)**: 可以登录、报名比赛、修改自己的信息
- **管理员 (ADMIN/SUPER_ADMIN)**: 可以管理比赛、管理用户、查看所有数据

## 认证方式

所有需要认证的接口都使用 JWT Token 认证。在请求头中添加：
```
Authorization: Bearer <your_jwt_token>
```

## API 接口分类

### 1. 公开接口（无需认证）

#### 玩家注册
```
POST /api/player/register
```

#### 玩家登录
```
POST /api/player/login
```
请求体：
```json
{
  "username": "player_username",
  "password": "player_password"
}
```
响应：
```json
{
  "success": true,
  "message": "登录成功",
  "data": "jwt_token_here"
}
```

#### 管理员登录
```
POST /api/admin/login
```
请求体：
```json
{
  "username": "admin_username_or_email",
  "password": "admin_password"
}
```
响应：
```json
{
  "success": true,
  "message": "管理员登录成功",
  "data": {
    "token": "jwt_token_here",
    "username": "admin_username",
    "name": "admin_name",
    "role": "ADMIN",
    "email": "admin@example.com"
  }
}
```

#### 查看比赛列表
```
GET /api/competitions/list
```

### 2. 玩家权限接口（需要PLAYER角色）

#### 玩家信息管理
```
GET /api/player/{id}          # 获取玩家信息
PUT /api/player/{id}          # 更新玩家信息
POST /api/player/{id}/change-password  # 修改密码
```

#### 比赛报名
```
GET /api/player-competition-registrations/**    # 查看自己的报名
POST /api/player-competition-registrations/**   # 报名比赛
PUT /api/player-competition-registrations/**    # 修改报名信息
```

### 3. 管理员权限接口（需要ADMIN或SUPER_ADMIN角色）

#### 管理员管理
```
POST /api/admin/create                    # 创建管理员
GET /api/admin/list                       # 获取所有管理员
GET /api/admin/active                     # 获取激活管理员
GET /api/admin/{id}                       # 获取管理员信息
PUT /api/admin/{id}                       # 更新管理员信息
DELETE /api/admin/{id}                    # 删除管理员
PATCH /api/admin/{id}/toggle-status       # 激活/禁用管理员
POST /api/admin/{id}/change-password      # 修改管理员密码
```

#### 比赛规则管理
```
GET /api/competition-rules               # 获取所有比赛规则
GET /api/competition-rules/{id}          # 根据ID获取比赛规则
GET /api/competition-rules/name/{name}   # 根据名称获取比赛规则
GET /api/competition-rules/search        # 搜索比赛规则
POST /api/competition-rules              # 创建比赛规则
PUT /api/competition-rules/{id}          # 更新比赛规则
DELETE /api/competition-rules/{id}       # 删除比赛规则
```

#### 比赛管理
```
POST /api/competitions/create            # 创建比赛
PUT /api/competitions/{id}/update        # 更新比赛
DELETE /api/competitions/{id}/delete     # 删除比赛
```

#### 比赛结果管理
```
GET /api/match-results/**               # 查看比赛结果
POST /api/match-results/**              # 添加比赛结果
PUT /api/match-results/**               # 修改比赛结果
DELETE /api/match-results/**            # 删除比赛结果
```

## 权限说明

1. **PLAYER**: 只能访问与自己相关的数据，可以报名比赛和修改自己的信息
2. **ADMIN**: 可以管理比赛、用户数据，但不能管理其他管理员
3. **SUPER_ADMIN**: 拥有所有权限，包括管理其他管理员

## 使用流程

### 玩家使用流程：
1. 用户注册：`POST /api/player/register`
2. 用户登录：`POST /api/player/login` 获取token
3. 带token访问：`GET /api/player/{id}` 查看信息
4. 带token报名：`POST /api/player-competition-registrations/...`

### 管理员使用流程：
1. 管理员登录：`POST /api/admin/login` 获取token
2. 带token管理：`POST /api/competitions/create` 创建比赛
3. 带token管理：`POST /api/match-results/...` 添加比赛结果

## 错误处理

- **401 Unauthorized**: Token无效或已过期
- **403 Forbidden**: 权限不足，无法访问该资源
- **404 Not Found**: 资源不存在

## 注意事项

1. JWT Token 有效期为30分钟，过期后需要重新登录
2. 所有密码都经过 BCrypt 加密存储
3. 管理员可以用用户名或邮箱登录
4. 玩家只能用用户名登录
5. 删除的接口操作不可逆，请谨慎使用
