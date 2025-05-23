-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_ordering DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE campus_ordering;

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) COMMENT '真实姓名',
  `phone` varchar(20) COMMENT '手机号',
  `email` varchar(100) COMMENT '邮箱',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `last_login_time` datetime COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 员工表
CREATE TABLE IF NOT EXISTS `staff` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) COMMENT '邮箱',
  `position` varchar(50) NOT NULL COMMENT '职位',
  `department` varchar(50) NOT NULL COMMENT '部门',
  `employee_no` varchar(50) NOT NULL COMMENT '工号',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `last_login_time` datetime COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_employee_no` (`employee_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 学生表
CREATE TABLE IF NOT EXISTS `student` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `student_no` varchar(50) NOT NULL COMMENT '学号',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) COMMENT '邮箱',
  `college` varchar(50) NOT NULL COMMENT '学院',
  `major` varchar(50) NOT NULL COMMENT '专业',
  `class_name` varchar(50) NOT NULL COMMENT '班级',
  `grade` varchar(20) NOT NULL COMMENT '年级',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `last_login_time` datetime COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_student_no` (`student_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 补贴方案表
CREATE TABLE IF NOT EXISTS `subsidy_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '方案名称',
  `amount` decimal(10, 2) NOT NULL COMMENT '补贴金额',
  `period` varchar(20) NOT NULL COMMENT '补贴周期:daily(每日),weekly(每周),monthly(每月)',
  `target_type` varchar(20) NOT NULL COMMENT '补贴对象:student(学生),staff(员工)',
  `target_group` varchar(50) COMMENT '补贴群体:college(学院),department(部门)',
  `group_value` varchar(50) COMMENT '群体值',
  `is_reset` tinyint(1) DEFAULT 1 COMMENT '是否清零:0不清零,1清零',
  `cash_ratio` decimal(5, 2) DEFAULT 0 COMMENT '现金支付比例',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='补贴方案表';

-- 补贴账户表
CREATE TABLE IF NOT EXISTS `subsidy_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_type` varchar(20) NOT NULL COMMENT '用户类型:student,staff',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `subsidy_id` bigint(20) NOT NULL COMMENT '补贴方案ID',
  `balance` decimal(10, 2) DEFAULT 0 COMMENT '剩余补贴金额',
  `total_received` decimal(10, 2) DEFAULT 0 COMMENT '累计收到补贴',
  `total_used` decimal(10, 2) DEFAULT 0 COMMENT '累计使用补贴',
  `last_reset_time` datetime COMMENT '最后清零时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_subsidy` (`user_type`,`user_id`,`subsidy_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='补贴账户表';

-- 补贴发放记录表
CREATE TABLE IF NOT EXISTS `subsidy_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subsidy_account_id` bigint(20) NOT NULL COMMENT '补贴账户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '发放金额',
  `type` varchar(20) NOT NULL COMMENT '类型:grant(发放),reset(清零)',
  `period_start` date COMMENT '补贴周期开始',
  `period_end` date COMMENT '补贴周期结束',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='补贴发放记录表';

-- 菜品分类表
CREATE TABLE IF NOT EXISTS `dish_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- 菜品表
CREATE TABLE IF NOT EXISTS `dish` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '菜品名称',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `image` varchar(255) COMMENT '图片',
  `description` varchar(255) COMMENT '描述',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态:0下架,1上架',
  `type` varchar(20) NOT NULL COMMENT '类型:dish(菜品),staple(主食),combo(套餐),product(商品)',
  `stock` int(11) DEFAULT 0 COMMENT '库存',
  `stock_alert` int(11) DEFAULT 10 COMMENT '库存预警值',
  `sold` int(11) DEFAULT 0 COMMENT '已售数量',
  `rating` decimal(2, 1) DEFAULT 5.0 COMMENT '评分',
  `rating_count` int(11) DEFAULT 0 COMMENT '评分次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 套餐菜品关联表
CREATE TABLE IF NOT EXISTS `combo_dish` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `combo_id` bigint(20) NOT NULL COMMENT '套餐ID',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品ID',
  `quantity` int(11) DEFAULT 1 COMMENT '数量',
  `is_required` tinyint(1) DEFAULT 1 COMMENT '是否必选:0否,1是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_combo` (`combo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='套餐菜品关联表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型:student,staff',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `subsidy_amount` decimal(10, 2) DEFAULT 0 COMMENT '补贴金额',
  `actual_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `payment_method` varchar(20) COMMENT '支付方式:cash(现金),subsidy(补贴),mixed(混合)',
  `order_type` varchar(20) NOT NULL COMMENT '订单类型:reservation(预订),dine_in(堂食)',
  `status` varchar(20) NOT NULL COMMENT '状态:pending(待支付),paid(已支付),preparing(准备中),ready(待取餐),completed(已完成),cancelled(已取消)',
  `eat_time` datetime NOT NULL COMMENT '就餐时间',
  `pickup_no` varchar(20) COMMENT '取餐号',
  `remark` varchar(255) COMMENT '备注',
  `operator_id` bigint(20) COMMENT '操作员ID(员工)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_user` (`user_type`, `user_id`),
  KEY `idx_eat_time` (`eat_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `dish_id` bigint(20) NOT NULL COMMENT '菜品ID',
  `dish_name` varchar(50) NOT NULL COMMENT '菜品名称',
  `dish_type` varchar(20) NOT NULL COMMENT '类型:dish,staple,combo,product',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态:pending(待处理),processing(处理中),completed(已完成)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 订单评价表
CREATE TABLE IF NOT EXISTS `order_rating` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型:student,staff',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `rating` int(11) NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) COMMENT '评价内容',
  `images` varchar(1000) COMMENT '图片(JSON数组)',
  `reply` varchar(500) COMMENT '回复内容',
  `reply_time` datetime COMMENT '回复时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_user` (`order_id`, `user_type`, `user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

-- 统计报表表
CREATE TABLE IF NOT EXISTS `statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL COMMENT '统计日期',
  `type` varchar(20) NOT NULL COMMENT '统计类型:daily(日报),weekly(周报),monthly(月报)',
  `category` varchar(20) NOT NULL COMMENT '类别:dish(菜品),order(订单),subsidy(补贴)',
  `data` json NOT NULL COMMENT '统计数据(JSON)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_date_type_category` (`date`, `type`, `category`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='统计报表表';

-- 插入初始数据

-- 管理员数据
INSERT INTO `admin` (`username`, `password`, `real_name`, `phone`, `email`, `status`) VALUES
('admin', '123456', '系统管理员', '13800000000', 'admin@school.edu', 1);

-- 员工数据
INSERT INTO `staff` (`username`, `password`, `real_name`, `phone`, `email`, `position`, `department`, `employee_no`, `status`) VALUES
('staff001', '123456', '张厨师', '13800000001', 'staff001@school.edu', '厨师长', '食堂后厨', 'EMP001', 1),
('staff002', '123456', '李服务', '13800000002', 'staff002@school.edu', '服务员', '食堂前台', 'EMP002', 1);

-- 学生数据
INSERT INTO `student` (`username`, `password`, `real_name`, `student_no`, `phone`, `email`, `college`, `major`, `class_name`, `grade`, `status`) VALUES
('student001', '123456', '王小明', '2023001', '13800000003', 'student001@school.edu', '计算机学院', '软件工程', '软工2301', '2023', 1),
('student002', '123456', '李小红', '2023002', '13800000004', 'student002@school.edu', '经济学院', '金融学', '金融2301', '2023', 1);

-- 补贴方案数据
INSERT INTO `subsidy_plan` (`name`, `amount`, `period`, `target_type`, `target_group`, `group_value`, `is_reset`, `cash_ratio`, `status`) VALUES
('学生每日基础补贴', 15.00, 'daily', 'student', NULL, NULL, 1, 0.00, 1),
('计算机学院学生额外补贴', 200.00, 'monthly', 'student', 'college', '计算机学院', 1, 0.20, 1),
('食堂员工工作餐补贴', 25.00, 'daily', 'staff', 'department', '食堂后厨', 1, 0.00, 1);

-- 菜品分类数据
INSERT INTO `dish_category` (`name`, `sort`, `status`) VALUES
('主食', 1, 1),
('炒菜', 2, 1),
('汤类', 3, 1),
('小吃', 4, 1),
('套餐', 5, 1),
('饮品', 6, 1);

-- 菜品数据
INSERT INTO `dish` (`name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`) VALUES
('米饭', 1, 2.00, '/images/rice.jpg', '东北大米', 1, 'staple', 1000, 100, 0),
('馒头', 1, 1.50, '/images/mantou.jpg', '传统面食', 1, 'staple', 500, 50, 0),
('回锅肉', 2, 12.00, '/images/huiguorou.jpg', '川味回锅肉', 1, 'dish', 100, 10, 0),
('青椒炒肉', 2, 10.00, '/images/qingjiaochaorou.jpg', '农家小炒', 1, 'dish', 100, 10, 0),
('紫菜蛋花汤', 3, 6.00, '/images/soup.jpg', '营养美味', 1, 'dish', 200, 20, 0),
('学生套餐A', 5, 15.00, '/images/combo_a.jpg', '米饭+回锅肉+紫菜蛋花汤', 1, 'combo', 50, 5, 0),
('矿泉水', 6, 2.00, '/images/water.jpg', '550ml', 1, 'product', 1000, 100, 0);

-- 套餐菜品关联
INSERT INTO `combo_dish` (`combo_id`, `dish_id`, `quantity`, `is_required`) VALUES
(6, 1, 1, 1),  -- 学生套餐A: 米饭
(6, 3, 1, 1),  -- 学生套餐A: 回锅肉
(6, 5, 1, 1);  -- 学生套餐A: 紫菜蛋花汤 