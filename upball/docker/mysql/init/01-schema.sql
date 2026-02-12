-- 涨球小程序数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS upball DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE upball;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    openid VARCHAR(64) UNIQUE COMMENT '微信openid',
    unionid VARCHAR(64) COMMENT '微信unionid',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    id_card VARCHAR(18) COMMENT '身份证号',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_openid (openid),
    INDEX idx_phone (phone)
) ENGINE=InnoDB COMMENT='用户表';

-- 球队表
CREATE TABLE IF NOT EXISTS teams (
    id BIGINT PRIMARY KEY,
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
    deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    INDEX idx_city (city),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='球队表';

-- 球队成员表
CREATE TABLE IF NOT EXISTS team_members (
    id BIGINT PRIMARY KEY,
    team_id BIGINT NOT NULL COMMENT '球队ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role TINYINT DEFAULT 3 COMMENT '角色: 1-队长 2-副队长 3-队员 4-候补',
    position VARCHAR(50) COMMENT '场上位置',
    jersey_number INT COMMENT '球衣号码',
    join_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-退出 1-正常 2-请假中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_team_user (team_id, user_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB COMMENT='球队成员表';

-- 赛事表
CREATE TABLE IF NOT EXISTS matches (
    id BIGINT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '赛事名称',
    type TINYINT DEFAULT 1 COMMENT '类型: 1-友谊赛 2-联赛 3-杯赛 4-锦标赛',
    home_team_id BIGINT NOT NULL COMMENT '主队ID',
    away_team_id BIGINT COMMENT '客队ID',
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_time (match_time),
    INDEX idx_teams (home_team_id, away_team_id)
) ENGINE=InnoDB COMMENT='赛事表';

-- 赛事出勤表
CREATE TABLE IF NOT EXISTS match_attendance (
    id BIGINT PRIMARY KEY,
    match_id BIGINT NOT NULL COMMENT '赛事ID',
    team_id BIGINT NOT NULL COMMENT '球队ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-未响应 1-能参加 2-待定 3-不能参加',
    is_starter TINYINT DEFAULT 0 COMMENT '是否首发',
    goals INT DEFAULT 0 COMMENT '进球数',
    assists INT DEFAULT 0 COMMENT '助攻数',
    yellow_cards INT DEFAULT 0 COMMENT '黄牌',
    red_cards INT DEFAULT 0 COMMENT '红牌',
    rating DECIMAL(3,1) COMMENT '评分',
    comment TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_match_user (match_id, user_id),
    INDEX idx_team (team_id)
) ENGINE=InnoDB COMMENT='赛事出勤表';

-- 会员套餐表
CREATE TABLE IF NOT EXISTS membership_plans (
    id BIGINT PRIMARY KEY,
    team_id BIGINT NOT NULL COMMENT '球队ID',
    name VARCHAR(50) NOT NULL COMMENT '套餐名称',
    type TINYINT NOT NULL COMMENT '类型: 1-次卡 2-周期卡',
    level TINYINT NOT NULL COMMENT '等级: 1-白银 2-黄金 3-VIP',
    times INT COMMENT '可用次数',
    duration_months INT COMMENT '有效期月数',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) COMMENT '原价',
    description TEXT COMMENT '说明',
    benefits VARCHAR(500) COMMENT '权益JSON',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架 1-上架',
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_team (team_id)
) ENGINE=InnoDB COMMENT='会员套餐表';

-- 会员订单表
CREATE TABLE IF NOT EXISTS membership_orders (
    id BIGINT PRIMARY KEY,
    order_no VARCHAR(32) UNIQUE COMMENT '订单号',
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    plan_name VARCHAR(50),
    plan_type TINYINT,
    plan_level TINYINT,
    amount DECIMAL(10,2) NOT NULL,
    pay_type TINYINT DEFAULT 1 COMMENT '支付方式: 1-微信 2-支付宝 3-现金',
    pay_status TINYINT DEFAULT 0 COMMENT '支付状态: 0-未支付 1-已支付 2-已退款',
    paid_at TIMESTAMP,
    trade_no VARCHAR(100) COMMENT '第三方流水号',
    level TINYINT NOT NULL COMMENT '获得等级',
    total_times INT,
    used_times INT DEFAULT 0,
    remaining_times INT,
    valid_start_date DATE,
    valid_end_date DATE,
    status TINYINT DEFAULT 1 COMMENT '状态: 0-作废 1-有效 2-已用完 3-已过期',
    remark VARCHAR(500),
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_order_no (order_no),
    INDEX idx_team_user (team_id, user_id)
) ENGINE=InnoDB COMMENT='会员订单表';

-- 费用记录表
CREATE TABLE IF NOT EXISTS team_finances (
    id BIGINT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    type TINYINT NOT NULL COMMENT '类型: 1-会费 2-场地费 3-装备费 4-赛事费 5-其他支出 6-赞助收入',
    title VARCHAR(200),
    amount DECIMAL(10,2) NOT NULL COMMENT '正数收入/负数支出',
    user_id BIGINT,
    match_id BIGINT,
    description TEXT,
    attachment VARCHAR(500),
    created_by BIGINT,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_team (team_id),
    INDEX idx_type (type)
) ENGINE=InnoDB COMMENT='费用记录表';

-- 管理员表
CREATE TABLE IF NOT EXISTS admins (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    role VARCHAR(20) DEFAULT 'operator' COMMENT '角色: super/operator/finance',
    status TINYINT DEFAULT 1,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB COMMENT='管理员表';

-- 插入默认管理员 (密码: admin123)
INSERT INTO admins (id, username, password, name, role) VALUES 
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '超级管理员', 'super')
ON DUPLICATE KEY UPDATE id=id;

-- 公告表
CREATE TABLE IF NOT EXISTS announcements (
    id BIGINT PRIMARY KEY,
    team_id BIGINT NOT NULL COMMENT '球队ID',
    author_id BIGINT NOT NULL COMMENT '发布者ID',
    type TINYINT DEFAULT 1 COMMENT '类型: 1-普通公告 2-重要通知 3-活动邀请',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    images VARCHAR(2000) COMMENT '图片URL，逗号分隔',
    is_top TINYINT DEFAULT 0 COMMENT '置顶: 0-否 1-是',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否 1-是',
    INDEX idx_team (team_id),
    INDEX idx_author (author_id),
    INDEX idx_top_time (is_top, created_at)
) ENGINE=InnoDB COMMENT='公告表';

-- 赛事相册表
CREATE TABLE IF NOT EXISTS match_albums (
    id BIGINT PRIMARY KEY,
    match_id BIGINT NOT NULL COMMENT '赛事ID',
    team_id BIGINT NOT NULL COMMENT '球队ID',
    user_id BIGINT NOT NULL COMMENT '上传用户ID',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    description VARCHAR(200) COMMENT '图片描述',
    file_size INT COMMENT '文件大小(字节)',
    width INT COMMENT '图片宽度',
    height INT COMMENT '图片高度',
    is_cover TINYINT DEFAULT 0 COMMENT '是否封面: 0-否 1-是',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_match (match_id),
    INDEX idx_team (team_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB COMMENT='赛事相册表';
