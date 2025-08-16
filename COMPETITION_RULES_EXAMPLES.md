# 比赛规则管理示例

## 权限说明
⚠️ **重要**: 比赛规则的所有操作现在只有管理员可以进行：
- 查看比赛规则 ✅ 仅管理员
- 创建比赛规则 ✅ 仅管理员  
- 编辑比赛规则 ✅ 仅管理员
- 删除比赛规则 ✅ 仅管理员

## API 使用示例

### 1. 管理员登录获取Token
```bash
POST /api/admin/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

响应：
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "role": "ADMIN"
  }
}
```

### 2. 创建比赛规则（需要管理员Token）
```bash
POST /api/competition-rules
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "ruleName": "日本麻将规则",
  "description": "标准的日本麻将比赛规则",
  "gameType": "日本麻将",
  "totalRounds": 8,
  "pointsPerGame": 25000,
  "bonusPoints": 30000,
  "penaltyPoints": -30000
}
```

### 3. 获取所有比赛规则（需要管理员Token）
```bash
GET /api/competition-rules
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 4. 根据ID获取比赛规则（需要管理员Token）
```bash
GET /api/competition-rules/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 5. 根据名称获取比赛规则（需要管理员Token）
```bash
GET /api/competition-rules/name/日本麻将规则
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 6. 搜索比赛规则（需要管理员Token）
```bash
GET /api/competition-rules/search?ruleName=日本
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 7. 更新比赛规则（需要管理员Token）
```bash
PUT /api/competition-rules/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "ruleName": "日本麻将规则(修订版)",
  "description": "修订后的日本麻将比赛规则",
  "gameType": "日本麻将",
  "totalRounds": 10,
  "pointsPerGame": 25000,
  "bonusPoints": 30000,
  "penaltyPoints": -30000
}
```

### 8. 删除比赛规则（需要管理员Token）
```bash
DELETE /api/competition-rules/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## 错误情况

### 玩家尝试访问比赛规则（会返回403）
```bash
GET /api/competition-rules
Authorization: Bearer <player_token>
```

响应：
```json
{
  "error": "Access Denied",
  "status": 403,
  "message": "Insufficient privileges"
}
```

### 未认证访问（会返回401）
```bash
GET /api/competition-rules
```

响应：
```json
{
  "error": "Unauthorized", 
  "status": 401,
  "message": "Authentication required"
}
```

## 注意事项

1. 所有比赛规则操作都需要管理员权限
2. 必须在请求头中携带有效的管理员Token
3. Token有效期为30分钟，过期后需要重新登录
4. 删除操作不可逆，请谨慎使用
