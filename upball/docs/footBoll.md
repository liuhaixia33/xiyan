# 球队管理 App 需求文档

## 1. 项目概述

一款面向业余足球队的管理平台，提供球队管理、赛事组织、会员等级管理、费用管理等功能。

**产品形态：**
- **小程序端**：球员/队长日常使用（报名、查看、支付）
- **管理后台**：球队管理员/系统运营使用（球队管理、数据统计、财务审核）

---

## 2. 产品形态与架构

```
┌─────────────────────────────────────────────────────────┐
│                      前端层                              │
│  ┌─────────────────────┐  ┌─────────────────────────┐  │
│  │      小程序端        │  │       管理后台          │  │
│  │  (球员/队长使用)     │  │   (管理员/运营使用)      │  │
│  │                     │  │                         │  │
│  │ • 微信一键登录      │  │ • 球队管理              │  │
│  │ • 球队创建/加入     │  │ • 用户管理              │  │
│  │ • 赛事报名/查看     │  │ • 会员套餐配置          │  │
│  │ • 会员购买/查看     │  │ • 财务报表              │  │
│  │ • 个人中心          │  │ • 数据统计大屏          │  │
│  └─────────────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                      后端服务层                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │ 用户服务  │ │ 球队服务  │ │ 赛事服务  │ │ 会员服务  │  │
│  │ 微信登录  │ │ 成员管理  │ │ 报名管理  │ │ 等级计算  │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐  │
│  │ 支付服务  │ │ 消息服务  │ │ 文件存储  │ │ 数据统计  │  │
│  │ 微信支付  │ │ 订阅消息  │ │ 阿里云OSS │ │ 报表生成  │  │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                      数据存储层                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐               │
│  │  MySQL   │ │  Redis   │ │ 阿里云OSS │               │
│  │ 业务数据 │ │ 缓存会话 │ │ 文件存储  │               │
│  └──────────┘ └──────────┘ └──────────┘               │
└─────────────────────────────────────────────────────────┘
```

---

### 3.1 页面结构

| 页面 | 说明 | 主要功能 |
|------|------|---------|
| **首页** | 我的球队入口 | 展示我已加入的球队列表 |
| **球队页** | 球队详情 | 球队信息、成员列表、近期赛事 |
| **赛事页** | 赛事中心 | 赛事列表、报名状态、比赛结果 |
| **会员页** | 会员中心 | 我的会员状态、购买套餐、购买记录 |
| **我的** | 个人中心 | 个人信息、我的数据、设置 |

### 3.2 功能模块

#### 3.2.1 球队管理
- 创建球队（名称、队徽、城市、主场、简介）
- 编辑球队信息
- 解散/暂停球队
- 设置队长/副队长
- 设置加入方式（申请/邀请/公开）
- 球队主页展示

#### 3.2.2 成员管理
- 申请加入球队
- 邀请成员加入
- 审核入队申请
- 设置球员角色/位置/球衣号码
- 成员退出/移除
- 请假状态管理
- 成员权限管理

#### 3.2.3 赛事管理
- 创建赛事（时间、地点、对手、类型）
- 编辑/取消赛事
- 邀请对手球队
- 设置费用类型和金额
- 录入比赛结果
- 赛事相册/视频

#### 3.2.4 报名/出勤统计
- 赛事报名通知（推送/短信）
- 球员报名反馈（参加/待定/不参加）
- 首发/替补安排
- 实时人数统计
- 自动提醒（赛前1天/2小时）
- 报名人数不足预警

#### 3.2.5 会费缴纳（会员等级体系）
- 会员等级管理（见第4章）
- 套餐管理
- 在线支付（微信/支付宝）
- 线下现金记录
- 缴费提醒（逾期）
- 缴费记录查询

#### 3.2.6 球队费用管理（仅队长/管理员可见）
- 收入/支出记录
- 费用分类管理
- 收支明细查询
- 财务报表统计
- 余额计算
- 费用审批（大额支出）
- 凭证上传

---

## 3. 小程序端功能模块

### 3.1 页面结构

| Tab 页面 | 功能说明 | 核心功能点 |
|---------|---------|-----------|
| **首页** | 我的球队 | 展示已加入的球队列表、快捷入口 |
| **球队页** | 球队详情 | 球队信息、成员列表、近期赛事、球队相册 |
| **赛事页** | 赛事中心 | 赛事列表、我的报名、比赛结果、赛事排行 |
| **会员页** | 会员中心 | 我的会员状态、购买套餐、购买记录、剩余次数 |
| **我的** | 个人中心 | 个人信息、我的数据（进球/助攻/出勤）、设置 |

### 3.2 详细功能描述

#### 3.2.1 首页 - 我的球队
- 已加入球队列表（卡片展示）
- 快捷操作：最新赛事、待缴费提醒
- 创建球队入口
- 加入球队（扫码/搜索）

#### 3.2.2 球队页 - 球队详情
- **球队信息区**：队徽、名称、城市、主场、成立时间
- **数据统计**：总比赛场次、胜率、成员数
- **成员列表**：按角色排序（队长/副队长/队员）
- **近期赛事**：最近5场比赛结果
- **快捷操作**：邀请成员、创建赛事（仅队长）

#### 3.2.3 赛事页 - 赛事中心
- **赛事列表**：按时间排序，状态标签（未开始/进行中/已结束）
- **赛事详情**：时间、地点、对阵双方、报名人数
- **报名操作**：能参加/待定/不参加
- **我的报名**：查看我的报名记录
- **赛事结果**：比分、进球人员、赛后评分

#### 3.2.4 会员页 - 会员中心
- **我的会员卡片**：
  - 当前等级图标（平民/白银/黄金/VIP）
  - 剩余次数或有效期
  - 权益列表
- **购买套餐**：套餐列表、价格对比、立即购买
- **购买记录**：历史订单、剩余次数详情

#### 3.2.5 我的 - 个人中心
- **个人信息**：头像、昵称、手机号
- **我的数据**：
  - 总参赛场次
  - 进球数/助攻数
  - 出勤率
  - 场均评分
- **我的球队**：切换/退出球队
- **设置**：消息通知、隐私设置、关于我们

---

## 4. 管理后台功能模块

### 4.1 页面结构

| 一级菜单 | 二级菜单 | 功能说明 |
|---------|---------|---------|
| **数据概览** | Dashboard | 数据大屏、关键指标 |
| **球队管理** | 球队列表、球队审核 | 所有球队的管理 |
| **用户管理** | 用户列表、实名审核 | 平台用户管理 |
| **赛事管理** | 赛事列表、赛事审核 | 平台赛事监管 |
| **会员管理** | 套餐配置、订单管理 | 会员体系运营 |
| **财务管理** | 收支记录、报表、对账 | 资金流水管理 |
| **系统设置** | 权限管理、系统配置 | 系统基础设置 |

### 4.2 详细功能描述

#### 4.2.1 数据概览（Dashboard）
- **今日数据卡片**：
  - 新增球队数
  - 新增注册用户数
  - 今日收入金额
  - 今日订单数
- **关键指标**：
  - 总球队数 / 活跃球队数
  - 总用户数 / 付费用户数 / 付费率
  - 本月收入 / 本月支出 / 平台结余
- **趋势图表**：
  - 近30天用户增长趋势
  - 近30天收入增长趋势
  - 会员等级分布饼图
- **实时动态**：最新注册球队、最新支付订单

#### 4.2.2 球队管理
- **球队列表**：
  - 搜索：球队名称、城市、队长姓名
  - 筛选：状态（正常/冻结/解散）、创建时间
  - 表格：球队信息、成员数、赛事数、状态、操作
  - 导出：Excel 导出球队数据
- **球队详情页**：
  - 基本信息
  - 成员列表（可移除成员）
  - 赛事历史
  - 财务流水
  - 操作记录
- **球队审核**：
  - 新创建球队待审核列表
  - 审核操作：通过/拒绝（填写原因）
- **状态管理**：冻结球队（暂停使用）/ 解冻

#### 4.2.3 用户管理
- **用户列表**：
  - 搜索：手机号、昵称、ID
  - 筛选：注册时间、会员等级、状态
  - 表格：用户信息、加入球队数、会员状态
- **用户详情**：
  - 基本信息
  - 加入的球队列表
  - 会员购买记录
  - 消费统计
- **实名认证审核**：
  - 待审核列表
  - 实名信息查看
  - 审核：通过/拒绝

#### 4.2.4 会员管理
- **套餐配置**：
  - 套餐列表：名称、类型、价格、状态
  - 新增/编辑套餐：
    - 名称、类型（次卡/周期卡）
    - 等级（白银/黄金/VIP）
    - 次数/有效期
    - 价格、原价
    - 权益描述
    - 排序、上下架
- **订单管理**：
  - 订单列表：订单号、用户、套餐、金额、状态
  - 筛选：支付状态、套餐类型、时间范围
  - 详情查看
  - 退款处理（手动退款）
- **会员统计**：
  - 各等级会员人数占比
  - 会员增长趋势
  - 会员转化率

#### 4.2.5 财务管理
- **收支记录**：
  - 全部流水：时间、类型、金额、关联球队/用户
  - 筛选：收支类型、球队、时间
  - 详情查看（含凭证图片）
- **财务报表**：
  - 收入报表：按类型/按球队/按月份
  - 支出报表：分类统计
  - 球队余额报表
  - 图表可视化
- **对账管理**：
  - 微信支付账单导入
  - 系统自动对账
  - 异常订单标记
- **数据导出**：财务报表导出 Excel

#### 4.2.6 系统设置
- **权限管理（RBAC）**：
  - 角色管理：超级管理员、运营人员、财务人员
  - 菜单权限配置
  - 数据权限（只能看自己负责的球队）
  - 管理员账号增删改
- **日志管理**：
  - 操作日志：谁在什么时间做了什么
  - 登录日志：登录时间、IP、设备
  - 异常日志：系统错误记录
- **基础配置**：
  - 小程序参数配置
  - 微信支付配置
  - 短信服务配置
  - 文件存储配置

---

## 5. 数据库设计

### 5.1 球队表 (teams)

```sql
CREATE TABLE teams (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '球队名称',
    logo VARCHAR(500) COMMENT '队徽',
    description TEXT COMMENT '球队简介',
    city VARCHAR(50) COMMENT '所在城市',
    home_ground VARCHAR(200) COMMENT '主场场地',
    founded_at DATE COMMENT '成立日期',
    captain_id BIGINT COMMENT '队长用户ID',
    vice_captain_id BIGINT COMMENT '副队长用户ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-解散 1-正常 2-暂停',
    join_type TINYINT DEFAULT 1 COMMENT '加入方式: 1-申请 2-邀请 3-公开',
    member_count INT DEFAULT 0 COMMENT '成员数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_city (city),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='球队表';
```

### 5.2 球队成员表 (team_members)

```sql
CREATE TABLE team_members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL COMMENT '球队ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role TINYINT DEFAULT 3 COMMENT '角色: 1-队长 2-副队长 3-队员 4-候补',
    position VARCHAR(50) COMMENT '场上位置: 门将/后卫/中场/前锋',
    jersey_number INT COMMENT '球衣号码',
    join_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-退出 1-正常 2-请假中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_team_user (team_id, user_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB COMMENT='球队成员表';
```

### 5.3 赛事表 (matches)

```sql
CREATE TABLE matches (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '赛事名称',
    type TINYINT DEFAULT 1 COMMENT '类型: 1-友谊赛 2-联赛 3-杯赛 4-锦标赛',
    home_team_id BIGINT NOT NULL COMMENT '主队ID',
    away_team_id BIGINT COMMENT '客队ID (null表示待定)',
    match_time DATETIME COMMENT '比赛时间',
    venue VARCHAR(200) COMMENT '比赛场地',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-未开始 1-进行中 2-已结束 3-取消',
    home_score INT DEFAULT 0 COMMENT '主队比分',
    away_score INT DEFAULT 0 COMMENT '客队比分',
    description TEXT COMMENT '赛事说明',
    referee VARCHAR(100) COMMENT '裁判',
    fee_type TINYINT DEFAULT 0 COMMENT '费用类型: 0-免费 1-AA制 2-主队承担 3-客队承担',
    fee_amount DECIMAL(10,2) DEFAULT 0 COMMENT '人均费用',
    created_by BIGINT COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_time (match_time),
    INDEX idx_teams (home_team_id, away_team_id),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='赛事表';
```

### 5.4 赛事报名/出勤表 (match_attendance)

```sql
CREATE TABLE match_attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    match_id BIGINT NOT NULL COMMENT '赛事ID',
    team_id BIGINT NOT NULL COMMENT '球队ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-未响应 1-能参加 2-待定 3-不能参加',
    is_starter TINYINT DEFAULT 0 COMMENT '是否首发: 0-替补 1-首发',
    goals INT DEFAULT 0 COMMENT '进球数',
    assists INT DEFAULT 0 COMMENT '助攻数',
    yellow_cards INT DEFAULT 0 COMMENT '黄牌数',
    red_cards INT DEFAULT 0 COMMENT '红牌数',
    rating DECIMAL(3,1) COMMENT '评分',
    comment TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_match_user (match_id, user_id),
    INDEX idx_team (team_id)
) ENGINE=InnoDB COMMENT='赛事出勤/报名统计表';
```

### 5.5 费用记录表 (team_finances)

```sql
CREATE TABLE team_finances (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL COMMENT '球队ID',
    type TINYINT NOT NULL COMMENT '类型: 1-会费 2-场地费 3-装备费 4-赛事费 5-其他支出 6-赞助收入',
    title VARCHAR(200) COMMENT '费用标题',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额(正数收入/负数支出)',
    user_id BIGINT COMMENT '关联用户ID(如某人的会费)',
    match_id BIGINT COMMENT '关联赛事ID',
    description TEXT COMMENT '详细说明',
    attachment VARCHAR(500) COMMENT '凭证图片/发票',
    created_by BIGINT COMMENT '记录人',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-作废 1-有效',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_team (team_id),
    INDEX idx_type (type),
    INDEX idx_user (user_id)
) ENGINE=InnoDB COMMENT='球队费用记录表';
```

---

## 6. 会员等级体系（核心功能）

### 6.1 等级定义

| 等级 | 名称 | 缴费类型 | 权益 |
|------|------|---------|------|
| 0 | 平民 | 无/过期 | 基础功能，无法报名参赛 |
| 1 | 白银会员 | 10次卡 | 10次参赛资格 |
| 2 | 黄金会员 | 20次卡 | 20次参赛资格 |
| 3 | VIP | 年卡 | 无限次参赛 + 优先报名 + 其他特权 |

### 6.2 会员套餐表 (membership_plans)

```sql
CREATE TABLE membership_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_id BIGINT NOT NULL COMMENT '所属球队ID',
    name VARCHAR(50) NOT NULL COMMENT '套餐名称',
    type TINYINT NOT NULL COMMENT '类型: 1-次卡 2-周期卡',
    level TINYINT NOT NULL COMMENT '对应等级: 1-白银 2-黄金 3-VIP',
    times INT COMMENT '可用次数(次卡类型)',
    duration_months INT COMMENT '有效期月数(周期卡类型)',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) COMMENT '原价(用于显示优惠)',
    description TEXT COMMENT '套餐说明',
    benefits JSON COMMENT '权益清单["优先报名", "免费饮水", "队服折扣"]',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_team (team_id),
    INDEX idx_level (level)
) ENGINE=InnoDB COMMENT='会员套餐表';
```

**初始化数据示例：**

```sql
INSERT INTO membership_plans (team_id, name, type, level, times, duration_months, price, description) VALUES
(1, '白银会员(10次卡)', 1, 1, 10, NULL, 200.00, '可参加10次活动'),
(1, '黄金会员(20次卡)', 1, 2, 20, NULL, 350.00, '可参加20次活动，享9折优惠'),
(1, 'VIP年卡', 2, 3, NULL, 12, 800.00, '全年无限次参加，享8折优惠，优先报名');
```

### 6.3 会员购买记录表 (membership_orders)

```sql
CREATE TABLE membership_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号 M20240315120001',
    team_id BIGINT NOT NULL COMMENT '球队ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    plan_id BIGINT NOT NULL COMMENT '套餐ID',
    plan_name VARCHAR(50) COMMENT '套餐名称(快照)',
    plan_type TINYINT COMMENT '套餐类型',
    plan_level TINYINT COMMENT '套餐等级',
    amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    pay_type TINYINT DEFAULT 1 COMMENT '支付方式: 1-微信 2-支付宝 3-现金',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态: 0-未支付 1-已支付 2-已退款',
    paid_at TIMESTAMP COMMENT '支付时间',
    trade_no VARCHAR(100) COMMENT '第三方流水号',
    level TINYINT NOT NULL COMMENT '获得等级: 1-白银 2-黄金 3-VIP',
    total_times INT COMMENT '总次数(次卡)',
    used_times INT DEFAULT 0 COMMENT '已使用次数',
    remaining_times INT COMMENT '剩余次数',
    valid_start_date DATE COMMENT '有效期开始',
    valid_end_date DATE COMMENT '有效期结束',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-作废 1-有效 2-已用完 3-已过期',
    remark VARCHAR(500) COMMENT '备注',
    created_by BIGINT COMMENT '创建人(管理员代买)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_team_user (team_id, user_id),
    INDEX idx_status (status),
    INDEX idx_valid_date (valid_end_date)
) ENGINE=InnoDB COMMENT='会员购买记录表';
```

### 6.4 会员使用记录表 (membership_usage_logs)

```sql
CREATE TABLE membership_usage_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '会员订单ID',
    team_id BIGINT NOT NULL COMMENT '球队ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    match_id BIGINT COMMENT '关联赛事ID',
    used_times INT DEFAULT 1 COMMENT '使用次数（通常为1）',
    remaining_after INT COMMENT '使用后剩余次数',
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '使用时间',
    remark VARCHAR(200) COMMENT '备注',
    INDEX idx_order (order_id),
    INDEX idx_user (user_id),
    INDEX idx_match (match_id)
) ENGINE=InnoDB COMMENT='会员使用记录表';
```

---

## 7. 会员等级业务规则

### 7.1 等级优先级
```
VIP > 黄金会员 > 白银会员 > 平民
```

### 7.2 核心规则

1. **VIP 特权**：VIP 有效期内，次卡暂停使用（保留剩余次数）；VIP 过期后，自动恢复使用次卡剩余次数

2. **次卡规则**：
   - 无固定过期时间，用完为止
   - 可同时拥有多个次卡，次数累计
   - 报名优先扣除低等级卡的次数（先用白银，再用黄金）

3. **等级判定**：取当前所有有效订单中的最高等级

4. **参赛资格检查**：
   - VIP：直接通过，不扣次数
   - 次卡用户：检查剩余次数，扣除1次后通过
   - 平民：无有效会员，无法报名

5. **权益继承**：
   - 次卡用户权益 = 对应等级的固定权益
   - VIP 权益 = 无限次 + 优先报名 + 装备折扣 + 首发优先权

---

## 8. 关键业务流程

### 8.1 购买会员流程

```
1. 用户选择套餐
2. 创建订单（状态：未支付）
3. 调起支付（微信/支付宝）
4. 支付回调成功
5. 更新订单状态为"已支付"
6. 生成会员权益
7. 更新用户会员缓存
```

### 8.2 报名参赛流程

```
1. 用户点击报名
2. 检查会员状态
   ├─ 无有效会员 → 提示购买
   ├─ VIP → 直接通过
   └─ 次卡用户 → 检查剩余次数
       ├─ 次数 > 0 → 扣除次数，报名成功
       └─ 次数 = 0 → 提示购买新卡
3. 更新报名人数统计
4. 发送报名成功通知
```

### 8.3 次卡扣费策略

```
1. 查询用户所有有效次卡订单
2. 按等级升序排序（白银优先）
3. 按创建时间升序（先买的优先用完）
4. 扣除第一个有剩余次数的订单
5. 如该订单剩余次数=0，标记为"已用完"
6. 记录使用日志
```

---

## 9. 核心接口设计

### 9.1 会员相关接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/membership/plans | GET | 获取会员套餐列表 |
| /api/v1/membership/purchase | POST | 购买会员（创建订单） |
| /api/v1/membership/pay-callback | POST | 支付回调 |
| /api/v1/membership/my-status | GET | 查询我的会员状态 |
| /api/v1/membership/orders | GET | 我的购买记录 |
| /api/v1/membership/check-register | GET | 检查报名资格 |

### 9.2 球队相关接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/teams | POST | 创建球队 |
| /api/v1/teams/{id} | GET | 获取球队详情 |
| /api/v1/teams/{id}/members | GET | 获取成员列表 |
| /api/v1/teams/{id}/join | POST | 申请加入球队 |
| /api/v1/teams/{id}/invite | POST | 邀请成员 |

### 9.3 赛事相关接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/matches | POST | 创建赛事 |
| /api/v1/matches/{id} | GET | 获取赛事详情 |
| /api/v1/matches/{id}/attendance | POST | 报名/出勤反馈 |
| /api/v1/matches/{id}/lineup | PUT | 设置首发阵容 |
| /api/v1/matches/{id}/result | PUT | 录入比赛结果 |

### 9.4 费用相关接口

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/finances | POST | 记录费用 |
| /api/v1/finances/report | GET | 财务报表 |
| /api/v1/finances/balance | GET | 查询余额 |

---

## 10. 数据统计需求

### 10.1 球队统计

- 总比赛场次 / 胜 / 平 / 负
- 进球数 / 失球数
- 胜率
- 球员出勤率排名
- 射手榜 / 助攻榜

### 10.2 财务统计

- 总收入 / 总支出 / 余额
- 会费收入占比
- 场地费支出占比
- 月度收支趋势
- 欠费人员名单

### 10.3 会员统计

- 各等级会员人数
- 次卡平均使用率
- VIP 续费率
- 会员到期预警（7天内）

---

## 11. 技术栈建议

### 11.1 小程序端
| 技术 | 说明 |
|------|------|
| Taro 3.x + React/Vue | 跨端框架，支持微信小程序 |
| TypeScript | 类型安全 |
| 微信原生 API | 支付、登录、分享、订阅消息 |

### 11.2 管理后台
| 技术 | 说明 |
|------|------|
| React 18 + Ant Design Pro | 企业级中后台前端框架 |
| TypeScript | 类型安全 |
| ECharts | 数据可视化图表 |

### 11.3 后端服务
| 技术 | 说明 |
|------|------|
| Spring Boot 3.x | Java 后端框架 |
| MySQL 8.0 | 主数据库 |
| Redis | 缓存、会话、分布式锁 |
| 微信支付 SDK | 小程序支付 |
| 微信小程序 SDK | 登录、订阅消息 |
| 阿里云 OSS | 图片/文件存储 |

### 11.4 部署
| 技术 | 说明 |
|------|------|
| Docker | 容器化部署 |
| Nginx | 反向代理、静态资源 |
| 阿里云/腾讯云 | 云服务器 |

---

## 12. 开发阶段规划

### 12.1 第一阶段（MVP - 4周）

**小程序端：**
- [ ] 微信授权登录
- [ ] 创建/加入球队
- [ ] 球队主页展示
- [ ] 成员列表查看
- [ ] 赛事列表查看
- [ ] 赛事报名/取消报名
- [ ] 个人中心（我的球队、我的会员）

**管理后台：**
- [ ] 球队管理（审核、冻结）
- [ ] 用户管理
- [ ] 系统配置

### 12.2 第二阶段（会员体系 - 3周）

**小程序端：**
- [ ] 会员套餐展示
- [ ] 微信支付购买会员
- [ ] 我的会员状态查看
- [ ] 次卡剩余次数显示
- [ ] 购买记录查询

**管理后台：**
- [ ] 会员套餐配置
- [ ] 会员订单管理
- [ ] 财务流水记录
- [ ] 收支统计报表

### 12.3 第三阶段（完善 - 3周）

**小程序端：**
- [ ] 赛事结果录入（队长权限）
- [ ] 球员数据统计查看
- [ ] 球队相册功能
- [ ] 订阅消息通知（报名提醒、赛事提醒）
- [ ] 分享功能（邀请加入球队）

**管理后台：**
- [ ] 数据大屏（球队数、用户数、收入统计）
- [ ] 财务报表导出
- [ ] 运营数据分析
- [ ] 权限管理（RBAC）

---

## 13. 注意事项

1. **支付安全**：所有支付必须走服务端签名，回调要做幂等处理
2. **数据一致性**：次卡扣费要使用数据库事务，防止超扣
3. **缓存更新**：会员状态变更后要及时刷新缓存
4. **过期处理**：VIP 过期要有自动降级逻辑
5. **退款策略**：明确退款规则（未使用的次卡是否可退）
