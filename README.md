# 麻将比赛管理系统

这是一个基于Spring Boot的麻将比赛管理系统，使用MariaDB作为数据库。

## 技术栈

- Spring Boot 3.5.4
- Spring Data JPA
- MariaDB
- Java 17
- Gradle

## 数据库配置

### 1. 数据库连接信息

数据库已配置为连接远程服务器：
- 地址：115.159.211.128:3306
- 数据库名：mahjong_competition
- 用户名：mahjong_competition
- 密码：37efa4RFwjpjhE68

### 2. 数据库表结构

系统会自动创建以下表：

#### players表（玩家表）
- `id`: 玩家ID（主键，自增）
- `username`: 用户名（唯一，非空）
- `password`: 密码（非空）
- `qq`: QQ号码（唯一，非空）
- `nickname`: QQ昵称（非空）
- `mahjong_id`: 雀魂数字串（唯一，非空）
- `mahjong_nickname`: 雀魂昵称（非空）
- `is_active`: 账户是否激活（非空）
- `last_login_time`: 最后登录时间
- `created_at`: 创建时间（非空）
- `updated_at`: 更新时间

#### competition_rules表（比赛规则表）
- `id`: 规则ID（主键，自增）
- `rule_name`: 规则名称（非空）
- `origin_points`: 原点（大于0小于100000的数字，非空）
- `first_place_points`: 一位码点（非空）
- `second_place_points`: 二位码点（非空）
- `third_place_points`: 三位码点（非空）
- `fourth_place_points`: 四位码点（非空）
- `created_at`: 创建时间（非空）
- `updated_at`: 更新时间

#### competitions表（比赛表）
- `id`: 比赛ID（主键，自增）
- `competition_name`: 比赛名称（非空）
- `competition_type`: 比赛类型（团体赛/个人赛，非空）
- `rule_id`: 比赛规则ID（外键，非空）
- `created_at`: 创建时间（非空）
- `updated_at`: 更新时间

#### individual_competition_registrations表（个人赛报名表）
- `id`: 报名ID（主键，自增）
- `competition_id`: 比赛ID（外键，非空）
- `player_id`: 玩家ID（外键，非空）
- `registration_time`: 报名时间（非空）
- `status`: 报名状态（已报名/已通过/已拒绝/已退赛，非空）

#### competition_rounds表（比赛轮次表）
- `id`: 轮次ID（主键，自增）
- `competition_id`: 比赛ID（外键，非空）
- `round_number`: 轮次编号（非空）
- `round_name`: 轮次名称（非空）
- `is_active`: 是否活跃（非空）
- `start_time`: 开始时间
- `end_time`: 结束时间
- `created_at`: 创建时间（非空）
- `updated_at`: 更新时间

#### player_round_status表（选手轮次状态表）
- `id`: 状态ID（主键，自增）
- `competition_id`: 比赛ID（外键，非空）
- `round_id`: 轮次ID（外键，非空）
- `player_id`: 玩家ID（外键，非空）
- `is_advanced`: 是否晋级（非空）
- `starting_score`: 起始得分（非空）
- `current_score`: 当前得分
- `rank_in_round`: 轮次排名
- `created_at`: 创建时间（非空）
- `updated_at`: 更新时间

#### match_results表（比赛成绩表）
- `id`: 成绩ID（主键，自增）
- `competition_id`: 比赛ID（外键，非空）
- `round_id`: 轮次ID（外键，非空）
- `match_number`: 比赛编号（非空）
- `match_name`: 比赛名称
- `east_player_id`: 东家玩家ID（外键，非空）
- `east_score`: 东家得分（非空）
- `east_penalty`: 东家罚分（非空，默认为0）
- `south_player_id`: 南家玩家ID（外键，非空）
- `south_score`: 南家得分（非空）
- `south_penalty`: 南家罚分（非空，默认为0）
- `west_player_id`: 西家玩家ID（外键，非空）
- `west_score`: 西家得分（非空）
- `west_penalty`: 西家罚分（非空，默认为0）
- `north_player_id`: 北家玩家ID（外键，非空）
- `north_score`: 北家得分（非空）
- `north_penalty`: 北家罚分（非空，默认为0）
- `total_score`: 总分（非空，必须为100000）
- `match_time`: 比赛时间
- `remarks`: 比赛备注（描述比赛情况）
- `created_at`: 创建时间（非空）
- `updated_at`: 更新时间

## 排名规则说明

### 东南西北座位安排
比赛采用标准的麻将座位安排，按照逆时针顺序：
- **东家** (East) - 起始位置
- **南家** (South) - 东家右侧
- **西家** (West) - 南家右侧  
- **北家** (North) - 西家右侧

### 排名计算逻辑
1. **主要排序**: 按照得分从高到低排序
2. **同分处理**: 当两个玩家得分相同时，按照逆时针顺序（东南西北）确定排名
3. **示例**: 
   - 东家：25000分
   - 南家：13000分
   - 西家：27000分
   - 北家：25000分
   
   排名结果：
   - 第1名：西家（27000分）
   - 第2名：东家（25000分，在东家位置）
   - 第3名：北家（25000分，在北家位置）
   - 第4名：南家（13000分）

## 得分计算规则

### 实际得分计算公式
每个玩家的实际得分按照以下公式计算：

**实际得分 = (玩家得分 - 比赛规则原点) / 1000 + 比赛规则的顺位点 + 罚分**

#### 公式说明：
1. **基础得分**: `(玩家得分 - 比赛规则原点) / 1000`
   - 玩家得分：比赛中的实际得分
   - 原点：比赛规则中设定的基准分数
   - 除以1000：将分数转换为更合理的数值范围

2. **顺位点**: 根据排名获得的额外分数
   - 第1名：获得 `first_place_points` 分数
   - 第2名：获得 `second_place_points` 分数
   - 第3名：获得 `third_place_points` 分数
   - 第4名：获得 `fourth_place_points` 分数

3. **罚分**: 因违规或其他原因扣除的分数（默认为0）

#### 计算示例：
假设比赛规则：
- 原点：25000
- 第1名：100分，第2名：50分，第3名：25分，第4名：0分

某场比赛结果：
- 东家：30000分，罚分：0
- 南家：20000分，罚分：5
- 西家：35000分，罚分：0
- 北家：15000分，罚分：10

排名计算：
1. 西家：35000分（第1名）
2. 东家：30000分（第2名）
3. 南家：20000分（第3名）
4. 北家：15000分（第4名）

实际得分计算：
- 西家：(35000-25000)/1000 + 100 + 0 = 10 + 100 + 0 = **110分**
- 东家：(30000-25000)/1000 + 50 + 0 = 5 + 50 + 0 = **55分**
- 南家：(20000-25000)/1000 + 25 + 5 = -5 + 25 + 5 = **25分**
- 北家：(15000-25000)/1000 + 0 + 10 = -10 + 0 + 10 = **0分**

## 运行项目

### 1. 使用Gradle Wrapper

```bash
# Windows
./gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

### 2. 构建JAR文件

```bash
./gradlew build
java -jar build/libs/mjcompetiton-0.0.1-SNAPSHOT.jar
```

## API接口

### 玩家账户管理

- `POST /api/players/register` - 玩家注册账户
- `POST /api/players/login` - 玩家登录
- `POST /api/players/{id}/change-password` - 修改密码
- `GET /api/players/check-username?username={username}` - 检查用户名是否可用
- `GET /api/players/check-qq?qq={qq}` - 检查QQ是否可用
- `GET /api/players/check-mahjong-id?mahjongId={mahjongId}` - 检查雀魂ID是否可用

### 玩家信息管理

- `GET /api/players` - 获取所有玩家
- `GET /api/players/{id}` - 根据ID获取玩家
- `GET /api/players/username/{username}` - 根据用户名获取玩家
- `GET /api/players/qq/{qq}` - 根据QQ获取玩家
- `GET /api/players/mahjong/{mahjongId}` - 根据雀魂ID获取玩家
- `GET /api/players/search/nickname?nickname={nickname}` - 根据QQ昵称搜索玩家
- `GET /api/players/search/mahjong-nickname?mahjongNickname={mahjongNickname}` - 根据雀魂昵称搜索玩家
- `PUT /api/players/{id}` - 更新玩家信息
- `PATCH /api/players/{id}/status` - 启用/禁用玩家账户
- `DELETE /api/players/{id}` - 删除玩家

### 比赛规则管理

- `POST /api/competition-rules` - 创建新比赛规则
- `GET /api/competition-rules` - 获取所有比赛规则
- `GET /api/competition-rules/{id}` - 根据ID获取比赛规则
- `GET /api/competition-rules/name/{ruleName}` - 根据规则名称获取比赛规则
- `GET /api/competition-rules/search?ruleName={ruleName}` - 根据规则名称搜索比赛规则
- `PUT /api/competition-rules/{id}` - 更新比赛规则
- `DELETE /api/competition-rules/{id}` - 删除比赛规则

### 比赛管理

- `POST /api/competitions` - 创建新比赛
- `GET /api/competitions` - 获取所有比赛
- `GET /api/competitions/{id}` - 根据ID获取比赛
- `GET /api/competitions/name/{competitionName}` - 根据比赛名称获取比赛
- `GET /api/competitions/type/{competitionType}` - 根据比赛类型获取比赛（TEAM/INDIVIDUAL）
- `GET /api/competitions/rule/{ruleId}` - 根据比赛规则获取比赛
- `GET /api/competitions/search?competitionName={competitionName}` - 根据比赛名称搜索比赛
- `PUT /api/competitions/{id}` - 更新比赛信息
- `DELETE /api/competitions/{id}` - 删除比赛

### 比赛成绩管理

- `POST /api/match-results` - 创建比赛成绩
- `GET /api/match-results/{id}` - 根据ID获取比赛成绩
- `GET /api/match-results/{id}/detail` - 获取比赛成绩的详细排名和得分信息
- `GET /api/match-results/{id}/ranks` - 计算并返回比赛排名
- `PUT /api/match-results/{id}` - 更新比赛成绩
- `DELETE /api/match-results/{id}` - 删除比赛成绩
- `POST /api/match-results/validate-scores` - 验证比赛成绩总分
- `GET /api/match-results/next-match-number` - 获取下一场比赛编号

## 示例请求

### 玩家注册
```bash
curl -X POST http://localhost:8080/api/players/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan",
    "password": "password123",
    "qq": "123456789",
    "nickname": "张三",
    "mahjongId": "123456789",
    "mahjongNickname": "雀魂玩家"
  }'
```

### 玩家登录
```bash
curl -X POST http://localhost:8080/api/players/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhangsan",
    "password": "password123"
  }'
```

### 修改密码
```bash
curl -X POST http://localhost:8080/api/players/1/change-password \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "password123",
    "newPassword": "newpassword456"
  }'
```

### 检查用户名是否可用
```bash
curl http://localhost:8080/api/players/check-username?username=zhangsan
```

### 创建比赛规则（包含原点）
```bash
curl -X POST http://localhost:8080/api/competition-rules \
  -H "Content-Type: application/json" \
  -d '{
    "ruleName": "标准规则",
    "originPoints": 25000,
    "firstPlacePoints": 100,
    "secondPlacePoints": 50,
    "thirdPlacePoints": 25,
    "fourthPlacePoints": 0
  }'
```

### 创建比赛
```bash
curl -X POST http://localhost:8080/api/competitions \
  -H "Content-Type: application/json" \
  -d '{
    "competitionName": "2024春季麻将大赛",
    "competitionType": "TEAM",
    "rule": {
      "id": 1
    }
  }'
```

### 录入比赛成绩（包含罚分和备注）
```bash
curl -X POST http://localhost:8080/api/match-results \
  -H "Content-Type: application/json" \
  -d '{
    "competition": {"id": 1},
    "round": {"id": 1},
    "matchNumber": 1,
    "matchName": "第一场比赛",
    "eastPlayer": {"id": 1},
    "eastScore": 30000,
    "eastPenalty": 0,
    "southPlayer": {"id": 2},
    "southScore": 20000,
    "southPenalty": 5,
    "westPlayer": {"id": 3},
    "westScore": 35000,
    "westPenalty": 0,
    "northPlayer": {"id": 4},
    "northScore": 15000,
    "northPenalty": 10,
    "remarks": "比赛进行顺利，南家和北家有轻微违规"
  }'
```

### 获取比赛成绩详细信息和排名
```bash
curl http://localhost:8080/api/match-results/1/detail
```

### 获取比赛排名
```bash
curl http://localhost:8080/api/match-results/1/ranks
```

### 验证比赛成绩总分
```bash
curl -X POST http://localhost:8080/api/match-results/validate-scores \
  -H "Content-Type: application/json" \
  -d '{
    "eastScore": 30000,
    "southScore": 20000,
    "westScore": 35000,
    "northScore": 15000
  }'
```

### 根据类型查找比赛
```bash
curl http://localhost:8080/api/competitions/type/TEAM
```

## 项目结构

```
src/main/java/com/rogister/mjcompetiton/
├── MjcompetitonApplication.java           # 主应用类
├── controller/                            # 控制器层
│   ├── PlayerController.java             # 玩家控制器
│   ├── CompetitionRuleController.java    # 比赛规则控制器
│   ├── CompetitionController.java        # 比赛控制器
│   └── MatchResultController.java        # 比赛成绩控制器
├── service/                              # 服务层
│   ├── PlayerService.java               # 玩家服务
│   ├── CompetitionRuleService.java      # 比赛规则服务
│   ├── CompetitionService.java          # 比赛服务
│   └── MatchResultService.java          # 比赛成绩服务
├── repository/                           # 数据访问层
│   ├── PlayerRepository.java            # 玩家数据访问
│   ├── CompetitionRuleRepository.java   # 比赛规则数据访问
│   ├── CompetitionRepository.java       # 比赛数据访问
│   └── MatchResultRepository.java       # 比赛成绩数据访问
└── entity/                              # 实体类
    ├── Player.java                     # 玩家实体
    ├── CompetitionRule.java            # 比赛规则实体
    ├── Competition.java                # 比赛实体
    └── MatchResult.java                # 比赛成绩实体
```

## 注意事项

1. 确保网络能够访问远程数据库服务器 115.159.211.128
2. 用户名、QQ号码和雀魂ID都是唯一字段，不能重复
3. 比赛规则名称是唯一字段，不能重复
4. 比赛名称是唯一字段，不能重复
5. 原点字段必须是一个大于0小于100000的数字
6. 首次运行时会自动创建数据表（`spring.jpa.hibernate.ddl-auto=update`）
7. 所有时间字段使用系统当前时间自动设置
8. 玩家密码建议在生产环境中进行加密存储
9. 账户状态管理支持启用/禁用功能
10. 比赛成绩总分必须为100000分
11. 排名按照得分高低和逆时针顺序确定
12. 罚分字段默认为0，可以为负数（表示加分）
13. 实际得分计算公式：(玩家得分-原点)/1000+顺位点+罚分
14. 备注字段用于记录比赛的特殊情况和说明

## 开发建议

1. 使用IDE（如IntelliJ IDEA）导入Gradle项目
2. 确保Java版本为17或更高
3. 可以使用Spring Boot DevTools进行热重载开发
4. 建议在开发环境中使用本地数据库进行测试
5. 生产环境中建议添加密码加密和JWT认证机制
6. 注意东南西北座位的正确安排和排名逻辑
7. 罚分系统可以灵活使用，支持正负值
8. 实际得分计算考虑了原点、顺位点和罚分三个因素
9. 备注字段有助于记录比赛中的特殊情况 