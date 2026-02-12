# Upball 部署文档

## 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- 服务器配置：2C4G 起步

## 快速部署

### 1. 克隆代码

```bash
git clone https://github.com/yourteam/upball.git
cd upball
```

### 2. 启动基础设施

```bash
docker-compose up -d mysql redis minio
```

### 3. 初始化数据库

```bash
docker exec -i upball-mysql mysql -uroot -pupball123 upball < docker/mysql/init/01-schema.sql
```

### 4. 构建并启动后端服务

```bash
cd upball-server
./mvnw clean package -DskipTests
docker build -t upball-server:latest .
cd ..
docker-compose up -d server
```

### 5. 构建管理后台

```bash
cd upball-admin
npm install
npm run build
# 将 dist 目录部署到 Nginx 或静态服务器
```

### 6. 构建小程序

```bash
cd upball-mp
npm install
npm run build:weapp
# 使用微信开发者工具上传
```

## 生产环境配置

### 1. 修改配置

编辑 `upball-server/src/main/resources/application-prod.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/upball
    username: ${MYSQL_USER}
    password: ${MYSQL_PASSWORD}
```

### 2. 环境变量

创建 `.env` 文件：

```bash
MYSQL_HOST=your-mysql-host
MYSQL_PORT=3306
MYSQL_USER=upball
MYSQL_PASSWORD=your-password
REDIS_HOST=your-redis-host
REDIS_PORT=6379
```

### 3. Nginx 配置

```nginx
server {
    listen 80;
    server_name api.upball.com;
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

server {
    listen 80;
    server_name admin.upball.com;
    
    location / {
        root /var/www/upball-admin;
        try_files $uri $uri/ /index.html;
    }
}
```

## 监控与日志

### 查看日志

```bash
# 后端服务日志
docker logs -f upball-server

# 数据库日志
docker logs -f upball-mysql
```

### 备份数据

```bash
# 备份数据库
docker exec upball-mysql mysqldump -uroot -pupball123 upball > backup.sql
```

## 更新部署

```bash
# 拉取最新代码
git pull origin main

# 重新构建
docker-compose down
docker-compose up -d --build
```
