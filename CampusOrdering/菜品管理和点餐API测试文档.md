# 菜品管理和点餐系统API测试文档

## 1. 文件上传接口

### 1.1 图片上传
- **接口地址**: `POST /api/file/upload`
- **请求方式**: POST (multipart/form-data)
- **请求参数**: 
  - `file`: 图片文件 (支持jpg、jpeg、png、gif、bmp格式，最大5MB)

**请求示例**:
```
POST /api/file/upload
Content-Type: multipart/form-data

file: [选择图片文件]
```

**响应示例**:
```json
{
    "code": 1,
    "msg": "操作成功",
    "data": "/uploads/images/uuid-filename.jpg"
}
```

## 2. 菜品分类管理

### 2.1 获取所有分类
- **接口地址**: `GET /api/category/list`
- **请求方式**: GET

**响应示例**:
```json
{
    "code": 1,
    "msg": "操作成功",
    "data": [
        {
            "id": 1,
            "name": "主食",
            "sort": 1,
            "status": 1,
            "createTime": "2025-05-22T21:19:20",
            "updateTime": "2025-05-22T21:19:20"
        }
    ]
}
```

### 2.2 添加分类
- **接口地址**: `POST /api/category`
- **请求方式**: POST

**请求示例**:
```json
{
    "name": "新分类",
    "sort": 10,
    "status": 1
}
```

### 2.3 更新分类
- **接口地址**: `PUT /api/category/{id}`
- **请求方式**: PUT

### 2.4 删除分类
- **接口地址**: `DELETE /api/category/{id}`
- **请求方式**: DELETE

## 3. 菜品管理

### 3.1 分页查询菜品
- **接口地址**: `GET /api/dish/page`
- **请求方式**: GET
- **请求参数**:
  - `page`: 页码 (默认1)
  - `size`: 每页大小 (默认10)
  - `name`: 菜品名称 (可选)
  - `categoryId`: 分类ID (可选)
  - `status`: 状态 (可选，1-上架，0-下架)

**请求示例**:
```
GET /api/dish/page?page=1&size=10&status=1
```

### 3.2 根据分类查询菜品
- **接口地址**: `GET /api/dish/category/{categoryId}`
- **请求方式**: GET

**请求示例**:
```
GET /api/dish/category/1?page=1&size=20
```

### 3.3 查询所有上架菜品
- **接口地址**: `GET /api/dish/available`
- **请求方式**: GET

### 3.4 添加菜品
- **接口地址**: `POST /api/dish`
- **请求方式**: POST

**请求示例**:
```json
{
    "name": "宫保鸡丁",
    "categoryId": 2,
    "price": 18.00,
    "image": "/uploads/images/uuid-filename.jpg",
    "description": "经典川菜，香辣可口",
    "status": 1,
    "type": "dish",
    "stock": 100,
    "stockAlert": 10
}
```

### 3.5 更新菜品
- **接口地址**: `PUT /api/dish/{id}`
- **请求方式**: PUT

### 3.6 删除菜品
- **接口地址**: `DELETE /api/dish/{id}`
- **请求方式**: DELETE

### 3.7 更新菜品状态
- **接口地址**: `PUT /api/dish/{id}/status`
- **请求方式**: PUT
- **请求参数**: `status` (1-上架，0-下架)

## 4. 订单管理

### 4.1 创建订单
- **接口地址**: `POST /api/order`
- **请求方式**: POST
- **请求头**: 
  - `userId`: 用户ID
  - `userType`: 用户类型 (student/staff)

**请求示例**:
```json
{
    "amount": 25.00,
    "subsidyAmount": 5.00,
    "actualAmount": 20.00,
    "paymentMethod": "mixed",
    "orderType": "dine_in",
    "eatTime": "2025-05-23 12:30:00",
    "remark": "少辣",
    "details": [
        {
            "dishId": 3,
            "dishName": "回锅肉",
            "dishType": "dish",
            "price": 12.00,
            "quantity": 1,
            "amount": 12.00
        },
        {
            "dishId": 1,
            "dishName": "米饭",
            "dishType": "staple",
            "price": 2.00,
            "quantity": 1,
            "amount": 2.00
        }
    ]
}
```

### 4.2 查询用户订单
- **接口地址**: `GET /api/order/user`
- **请求方式**: GET
- **请求头**: 
  - `userId`: 用户ID
  - `userType`: 用户类型
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `status`: 订单状态 (可选)

### 4.3 查询所有订单（管理员）
- **接口地址**: `GET /api/order/admin`
- **请求方式**: GET

### 4.4 查询订单详情
- **接口地址**: `GET /api/order/{id}`
- **请求方式**: GET

### 4.5 查询订单明细
- **接口地址**: `GET /api/order/{id}/details`
- **请求方式**: GET

### 4.6 更新订单状态
- **接口地址**: `PUT /api/order/{id}/status`
- **请求方式**: PUT
- **请求参数**: `status` (pending/paid/preparing/ready/completed/cancelled)

### 4.7 取消订单
- **接口地址**: `PUT /api/order/{id}/cancel`
- **请求方式**: PUT
- **请求头**: 
  - `userId`: 用户ID
  - `userType`: 用户类型

### 4.8 完成订单
- **接口地址**: `PUT /api/order/{id}/complete`
- **请求方式**: PUT

## 5. 测试账号

根据数据库数据，可以使用以下测试账号：

### 管理员账号
- 用户名: `admin`
- 密码: `123456`
- 角色: admin

### 学生账号
- 用户名: `student001`
- 密码: `123456`
- 角色: student

### 员工账号
- 用户名: `staff001`
- 密码: `123456`
- 角色: staff

## 6. 订单状态说明

- `pending`: 待支付
- `paid`: 已支付
- `preparing`: 准备中
- `ready`: 待取餐
- `completed`: 已完成
- `cancelled`: 已取消

## 7. 菜品类型说明

- `dish`: 菜品
- `staple`: 主食
- `combo`: 套餐
- `product`: 商品

## 8. 注意事项

1. 所有接口都需要在URL前加上 `/api` 前缀
2. 图片上传后返回的路径可以直接用于菜品的image字段
3. 上传的图片文件会保存在项目根目录的 `uploads/images/` 文件夹中
4. 图片可以通过 `/uploads/images/filename` 路径访问
5. 订单创建时需要在请求头中传递用户信息
6. 价格字段使用BigDecimal类型，支持两位小数 