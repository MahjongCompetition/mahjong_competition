# 玩家注册测试指南

## 问题修复说明

修复了玩家注册时的 `not-null property references a null or transient value: com.rogister.mjcompetition.entity.Player.createdAt` 错误。

### 修复内容：
1. 在 `PlayerService.createPlayer()` 中添加了默认值设置
2. 在 `Player` 实体中添加了 `@PrePersist` 方法确保字段不为null

## 测试玩家注册

### 1. 正确的注册请求

```bash
POST /api/player/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123", 
  "qq": "123456789",
  "nickname": "测试用户",
  "mahjongId": "MJ001",
  "mahjongNickname": "测试麻将昵称"
}
```

### 2. 期望的成功响应

```json
{
  "success": true,
  "message": "注册成功",
  "data": null
}
```

### 3. 字段说明

必填字段：
- `username`: 用户名（唯一，最大50字符）
- `password`: 密码（会被加密存储）
- `qq`: QQ号（唯一，最大20字符）
- `nickname`: 昵称（最大100字符）
- `mahjongId`: 麻将ID（唯一，最大50字符）
- `mahjongNickname`: 麻将昵称（最大100字符）

自动设置的字段：
- `id`: 自动生成的主键
- `createdAt`: 创建时间（自动设置为当前时间）
- `updatedAt`: 更新时间（自动设置为当前时间）
- `isActive`: 是否激活（默认为true）
- `lastLoginTime`: 最后登录时间（注册时为null）

### 4. 错误情况测试

#### 用户名重复
```bash
POST /api/player/register
Content-Type: application/json

{
  "username": "testuser",  # 重复的用户名
  "password": "password123",
  "qq": "987654321",
  "nickname": "另一个用户",
  "mahjongId": "MJ002", 
  "mahjongNickname": "另一个麻将昵称"
}
```

响应：
```json
{
  "success": false,
  "message": "用户名已存在: testuser"
}
```

#### QQ号重复
```bash
POST /api/player/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "password123",
  "qq": "123456789",  # 重复的QQ号
  "nickname": "新用户",
  "mahjongId": "MJ003",
  "mahjongNickname": "新麻将昵称"
}
```

响应：
```json
{
  "success": false,
  "message": "QQ号已存在: 123456789"
}
```

#### 麻将ID重复
```bash
POST /api/player/register
Content-Type: application/json

{
  "username": "newuser2",
  "password": "password123", 
  "qq": "111222333",
  "nickname": "新用户2",
  "mahjongId": "MJ001",  # 重复的麻将ID
  "mahjongNickname": "新麻将昵称2"
}
```

响应：
```json
{
  "success": false,
  "message": "麻将ID已存在: MJ001"
}
```

### 5. 注册后登录测试

注册成功后，可以立即进行登录测试：

```bash
POST /api/player/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

登录成功响应：
```json
{
  "success": true,
  "message": "登录成功",
  "data": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyVHlwZSI6IlBMQVlFUiIsInJvbGUiOiJQTEFZRVIiLCJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTcwNDA2NzIwMCwiZXhwIjoxNzA0MDY5MDAwfQ.abc123"
}
```

## 注意事项

1. **注册响应简化**：注册成功只返回消息，不返回用户数据
2. **登录响应简化**：登录成功只返回JWT token，不返回用户信息
3. 密码会自动加密存储
4. 所有唯一字段（username, qq, mahjongId）都会进行重复检查
5. `createdAt` 和 `updatedAt` 字段现在会自动设置，不会再出现null错误
6. Token有效期为30分钟
