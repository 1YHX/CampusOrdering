# 校园订餐系统 - 个人信息接口测试文档

## 基础配置

**服务器地址：** `http://localhost:8080`
**API前缀：** `/api`

## 1. 用户登录接口（获取Token）

### 请求信息
- **URL：** `http://localhost:8080/api/user/login`
- **方法：** `POST`
- **Content-Type：** `application/json`

### 请求体
```json
{
    "username": "admin",
    "password": "123456"
}
```

### 响应示例
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "token": "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MDk1NjY0MjIsImV4cCI6MTcwOTU3MDAyMn0.xxxxx",
        "user": {
            "id": 1,
            "username": "admin",
            "role": "admin",
            "realName": "管理员",
            "phone": null,
            "email": null,
            "department": null,
            "status": 1,
            "createTime": "2024-01-01T10:00:00",
            "updateTime": "2024-01-01T10:00:00"
        }
    }
}
```

**⚠️ 重要：** 复制响应中的 `token` 值，后续接口需要使用！

---

## 2. 获取个人信息接口

### 请求信息
- **URL：** `http://localhost:8080/api/user/profile`
- **方法：** `GET`
- **Headers：**
  - `Authorization: Bearer {你的token}`
  - `Content-Type: application/json`

### 请求示例
```
GET /api/user/profile HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE3MDk1NjY0MjIsImV4cCI6MTcwOTU3MDAyMn0.xxxxx
Content-Type: application/json
```

### 响应示例
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "admin",
        "password": null,
        "realName": "管理员",
        "phone": "13800138000",
        "email": "admin@example.com",
        "role": "admin",
        "department": "系统管理部",
        "status": 1,
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T10:00:00"
    }
}
```

---

## 3. 更新个人信息接口

### 请求信息
- **URL：** `http://localhost:8080/api/user/profile`
- **方法：** `PUT`
- **Headers：**
  - `Authorization: Bearer {你的token}`
  - `Content-Type: application/json`

### 请求体
```json
{
    "realName": "张三",
    "phone": "13900139000",
    "email": "zhangsan@example.com",
    "department": "计算机科学与技术学院"
}
```

### 响应示例
```json
{
    "code": 200,
    "message": "个人信息更新成功",
    "data": "个人信息更新成功"
}
```

---

## 4. 修改密码接口

### 请求信息
- **URL：** `http://localhost:8080/api/user/password`
- **方法：** `PUT`
- **Headers：**
  - `Authorization: Bearer {你的token}`
  - `Content-Type: application/json`

### 请求体
```json
{
    "oldPassword": "123456",
    "newPassword": "newpass123",
    "confirmPassword": "newpass123"
}
```

### 响应示例
```json
{
    "code": 200,
    "message": "密码修改成功",
    "data": "密码修改成功"
}
```

---

## ApiPost 操作步骤

### 步骤1：登录获取Token
1. 创建新请求
2. 设置方法为 `POST`
3. 输入URL：`http://localhost:8080/api/user/login`
4. 在Body中选择 `raw` → `JSON`
5. 输入登录信息：
   ```json
   {
       "username": "admin",
       "password": "123456"
   }
   ```
6. 点击发送
7. 复制响应中的 `token` 值

### 步骤2：测试获取个人信息
1. 创建新请求
2. 设置方法为 `GET`
3. 输入URL：`http://localhost:8080/api/user/profile`
4. 在Headers中添加：
   - Key: `Authorization`
   - Value: `Bearer {刚才复制的token}`
5. 点击发送

### 步骤3：测试更新个人信息
1. 创建新请求
2. 设置方法为 `PUT`
3. 输入URL：`http://localhost:8080/api/user/profile`
4. 在Headers中添加：
   - Key: `Authorization`
   - Value: `Bearer {你的token}`
5. 在Body中选择 `raw` → `JSON`
6. 输入要更新的信息
7. 点击发送

### 步骤4：测试修改密码
1. 创建新请求
2. 设置方法为 `PUT`
3. 输入URL：`http://localhost:8080/api/user/password`
4. 在Headers中添加：
   - Key: `Authorization`
   - Value: `Bearer {你的token}`
5. 在Body中选择 `raw` → `JSON`
6. 输入密码信息
7. 点击发送

---

## 错误响应示例

### 401 未授权
```json
{
    "code": 401,
    "message": "未授权的访问",
    "data": null
}
```

### 400 参数错误
```json
{
    "code": 400,
    "message": "真实姓名不能为空",
    "data": null
}
```

### 500 服务器错误
```json
{
    "code": 500,
    "message": "原密码错误",
    "data": null
}
```

---

## 注意事项

1. **Token有效期：** 1小时，过期需要重新登录
2. **请求头必须包含：** `Authorization: Bearer {token}`
3. **Content-Type：** 所有POST/PUT请求都需要设置为 `application/json`
4. **字段验证：**
   - 真实姓名：必填
   - 手机号：可选，格式必须正确（1开头的11位数字）
   - 邮箱：可选，格式必须正确
   - 密码：6-20位字符 