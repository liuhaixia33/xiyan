# Upball - 涨球小程序

> 业余足球队管理平台，让球队管理更简单

## 项目结构

```
upball/
├── upball-server/          # 后端服务（Spring Boot）
├── upball-admin/           # 管理后台（React + Ant Design Pro）
├── upball-mp/              # 微信小程序（Taro）
├── upball-common/          # 公共模块
├── docker/                 # Docker 配置文件
├── docs/                   # 项目文档
└── docker-compose.yml      # 一键启动
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0
- Redis 6.0+

### 本地启动

```bash
# 1. 启动基础设施
docker-compose up -d mysql redis

# 2. 启动后端服务
cd upball-server
./mvnw spring-boot:run

# 3. 启动管理后台
cd upball-admin
npm install
npm run dev

# 4. 启动小程序
cd upball-mp
npm install
npm run dev:weapp
```

## 技术栈

| 模块 | 技术 |
|------|------|
| 后端 | Spring Boot 3.x + MyBatis-Plus + MySQL + Redis |
| 后台 | React 18 + Ant Design Pro + TypeScript |
| 小程序 | Taro 3.x + React + TypeScript |

## 文档

- [需求文档](./docs/footBoll.md)
- [接口文档](./docs/api.md)
- [部署文档](./docs/deploy.md)

## License

MIT
