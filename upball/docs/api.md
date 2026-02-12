# Upball API 文档

## 接口基础信息

- **Base URL**: `https://api.upball.com/api/v1`
- **认证方式**: JWT Token (Header: `Authorization: Bearer {token}`)

## 接口模块

### 1. 用户模块

#### 微信登录
```http
POST /auth/wx-login
Content-Type: application/json

{
  "code": "wx-auth-code"
}
```

响应：
```json
{
  "code": 200,
  "data": {
    "token": "jwt-token",
    "refreshToken": "refresh-token",
    "expiresIn": 86400,
    "userInfo": {
      "id": 1,
      "nickname": "张三",
      "avatar": "https://..."
    }
  }
}
```

### 2. 球队模块

#### 创建球队
```http
POST /teams
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "龙腾足球队",
  "city": "上海",
  "homeGround": "虹口足球场",
  "description": "业余爱好球队"
}
```

#### 获取球队列表
```http
GET /teams?page=1&size=10
Authorization: Bearer {token}
```

### 3. 会员模块

#### 获取会员套餐
```http
GET /membership/plans?teamId=1
Authorization: Bearer {token}
```

#### 购买会员
```http
POST /membership/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "teamId": 1,
  "planId": 2,
  "payType": 1
}
```

### 4. 赛事模块

#### 创建赛事
```http
POST /matches
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "周末友谊赛",
  "homeTeamId": 1,
  "awayTeamId": 2,
  "matchTime": "2024-03-15 14:00:00",
  "venue": "上海体育场"
}
```

#### 报名参赛
```http
POST /matches/{matchId}/register
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": 1
}
```

## 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 500 | 服务器错误 |
| 1001 | 球队不存在 |
| 2001 | 会员已过期 |
| 2002 | 剩余次数不足 |

## 更多接口

详见 Postman 集合或 Swagger 文档。
