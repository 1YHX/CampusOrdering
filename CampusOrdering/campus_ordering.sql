/*
 Navicat Premium Dump SQL

 Source Server         : mysql8
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : campus_ordering

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 24/05/2025 23:16:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态:0禁用,1启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员表';

-- ----------------------------
-- Records of admin
-- ----------------------------
BEGIN;
INSERT INTO `admin` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (3, 'admin', '123456', '系统管理员', '13800138000', 'admin@example.com', 1, NULL, '2025-05-22 22:07:51', '2025-05-22 22:07:51');
INSERT INTO `admin` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (6, 'admin9527', '', 'yyyy', '13267319537', 'yyyy@163.com', 1, NULL, '2025-05-24 23:10:14', '2025-05-24 23:11:02');
COMMIT;

-- ----------------------------
-- Table structure for combo_dish
-- ----------------------------
DROP TABLE IF EXISTS `combo_dish`;
CREATE TABLE `combo_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `combo_id` bigint NOT NULL COMMENT '套餐ID',
  `dish_id` bigint NOT NULL COMMENT '菜品ID',
  `quantity` int DEFAULT '1' COMMENT '数量',
  `is_required` tinyint(1) DEFAULT '1' COMMENT '是否必选:0否,1是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_combo` (`combo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='套餐菜品关联表';

-- ----------------------------
-- Records of combo_dish
-- ----------------------------
BEGIN;
INSERT INTO `combo_dish` (`id`, `combo_id`, `dish_id`, `quantity`, `is_required`, `create_time`) VALUES (1, 6, 1, 1, 1, '2025-05-22 21:19:20');
INSERT INTO `combo_dish` (`id`, `combo_id`, `dish_id`, `quantity`, `is_required`, `create_time`) VALUES (2, 6, 3, 1, 1, '2025-05-22 21:19:20');
INSERT INTO `combo_dish` (`id`, `combo_id`, `dish_id`, `quantity`, `is_required`, `create_time`) VALUES (3, 6, 5, 1, 1, '2025-05-22 21:19:20');
COMMIT;

-- ----------------------------
-- Table structure for dish
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '菜品名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `image` varchar(255) DEFAULT NULL COMMENT '图片',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态:0下架,1上架',
  `type` varchar(20) NOT NULL COMMENT '类型:dish(菜品),staple(主食),combo(套餐),product(商品)',
  `stock` int DEFAULT '0' COMMENT '库存',
  `stock_alert` int DEFAULT '10' COMMENT '库存预警值',
  `sold` int DEFAULT '0' COMMENT '已售数量',
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '评分',
  `rating_count` int DEFAULT '0' COMMENT '评分次数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜品表';

-- ----------------------------
-- Records of dish
-- ----------------------------
BEGIN;
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`, `rating`, `rating_count`, `create_time`, `update_time`) VALUES (1, '米饭', 1, 1.00, '/uploads/images/1de79fe5-7196-45ba-a128-a1f9ba3e691a.png', '东北大米', 1, 'staple', 1000, 100, 0, 5.0, 0, '2025-05-22 21:19:20', '2025-05-24 17:50:37');
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`, `rating`, `rating_count`, `create_time`, `update_time`) VALUES (2, '馒头', 1, 1.50, '/uploads/images/7916da30-4216-4f34-87eb-28cb4610689b.png', '传统面食', 1, 'staple', 500, 50, 0, 5.0, 0, '2025-05-22 21:19:20', '2025-05-24 20:47:35');
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`, `rating`, `rating_count`, `create_time`, `update_time`) VALUES (3, '回锅肉', 2, 12.00, '/uploads/images/bc27e188-1bc0-437b-8c86-1054e39968a9.png', '川味回锅肉', 1, 'dish', 100, 10, 0, 5.0, 0, '2025-05-22 21:19:20', '2025-05-24 19:34:27');
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`, `rating`, `rating_count`, `create_time`, `update_time`) VALUES (4, '青椒炒肉', 2, 10.00, '/uploads/images/43b0a58c-7a11-4778-9827-1631ef256403.png', '农家小炒', 1, 'dish', 100, 10, 0, 5.0, 0, '2025-05-22 21:19:20', '2025-05-24 19:38:39');
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`, `rating`, `rating_count`, `create_time`, `update_time`) VALUES (5, '紫菜蛋花汤', 3, 6.00, '/uploads/images/15b6e456-d427-4cf7-b904-c6f3158099df.png', '营养美味', 1, 'dish', 200, 20, 0, 5.0, 0, '2025-05-22 21:19:20', '2025-05-24 20:10:57');
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`, `rating`, `rating_count`, `create_time`, `update_time`) VALUES (6, '学生套餐A', 5, 15.00, '/uploads/images/6375966c-8b74-4333-9b60-025603505e40.png', '米饭+回锅肉+紫菜蛋花汤', 1, 'combo', 49, 5, 1, 5.0, 0, '2025-05-22 21:19:20', '2025-05-24 20:39:52');
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`, `rating`, `rating_count`, `create_time`, `update_time`) VALUES (7, '矿泉水', 6, 2.00, '/uploads/images/db44a593-dad9-4b6f-b1e9-040fd770b73c.png', '550ml', 1, 'product', 1000, 100, 0, 5.0, 0, '2025-05-22 21:19:20', '2025-05-24 20:14:12');
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `description`, `status`, `type`, `stock`, `stock_alert`, `sold`, `rating`, `rating_count`, `create_time`, `update_time`) VALUES (8, '11', 4, 10.00, '/uploads/images/2c700466-77ce-4063-87f8-a9793a9434e5.png', '11', 1, 'dish', 7, 10, 3, 5.0, 0, '2025-05-24 18:00:28', '2025-05-24 18:09:39');
COMMIT;

-- ----------------------------
-- Table structure for dish_category
-- ----------------------------
DROP TABLE IF EXISTS `dish_category`;
CREATE TABLE `dish_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态:0禁用,1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜品分类表';

-- ----------------------------
-- Records of dish_category
-- ----------------------------
BEGIN;
INSERT INTO `dish_category` (`id`, `name`, `sort`, `status`, `create_time`, `update_time`) VALUES (1, '主食', 1, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
INSERT INTO `dish_category` (`id`, `name`, `sort`, `status`, `create_time`, `update_time`) VALUES (2, '炒菜', 2, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
INSERT INTO `dish_category` (`id`, `name`, `sort`, `status`, `create_time`, `update_time`) VALUES (3, '汤类', 3, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
INSERT INTO `dish_category` (`id`, `name`, `sort`, `status`, `create_time`, `update_time`) VALUES (4, '小吃', 4, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
INSERT INTO `dish_category` (`id`, `name`, `sort`, `status`, `create_time`, `update_time`) VALUES (5, '套餐', 5, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
INSERT INTO `dish_category` (`id`, `name`, `sort`, `status`, `create_time`, `update_time`) VALUES (6, '饮品', 6, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
COMMIT;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型:student,staff',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `subsidy_amount` decimal(10,2) DEFAULT '0.00' COMMENT '补贴金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式:cash(现金),subsidy(补贴),mixed(混合)',
  `order_type` varchar(20) NOT NULL COMMENT '订单类型:reservation(预订),dine_in(堂食)',
  `status` varchar(20) NOT NULL COMMENT '状态:pending(待支付),paid(已支付),preparing(准备中),ready(待取餐),completed(已完成),cancelled(已取消)',
  `eat_time` datetime NOT NULL COMMENT '就餐时间',
  `pickup_no` varchar(20) DEFAULT NULL COMMENT '取餐号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `operator_id` bigint DEFAULT NULL COMMENT '操作员ID(员工)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_user` (`user_type`,`user_id`),
  KEY `idx_eat_time` (`eat_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ----------------------------
-- Records of order
-- ----------------------------
BEGIN;
INSERT INTO `order` (`id`, `order_no`, `user_type`, `user_id`, `amount`, `subsidy_amount`, `actual_amount`, `payment_method`, `order_type`, `status`, `eat_time`, `pickup_no`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (1, 'ORD1748075539818', 'student', 3, 14.00, 0.00, 14.00, 'cash', 'reservation', 'pending', '2025-05-24 04:00:00', NULL, '', NULL, '2025-05-24 16:32:20', '2025-05-24 16:32:20');
INSERT INTO `order` (`id`, `order_no`, `user_type`, `user_id`, `amount`, `subsidy_amount`, `actual_amount`, `payment_method`, `order_type`, `status`, `eat_time`, `pickup_no`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (2, 'ORD1748077367865', 'student', 3, 3.00, 0.00, 3.00, 'cash', 'reservation', 'pending', '2025-05-24 10:30:00', NULL, '', NULL, '2025-05-24 17:02:48', '2025-05-24 17:02:48');
INSERT INTO `order` (`id`, `order_no`, `user_type`, `user_id`, `amount`, `subsidy_amount`, `actual_amount`, `payment_method`, `order_type`, `status`, `eat_time`, `pickup_no`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (3, 'ORD1748077418978', 'student', 3, 2.00, 0.00, 2.00, 'cash', 'dine_in', 'pending', '2025-05-24 09:03:38', NULL, '', NULL, '2025-05-24 17:03:39', '2025-05-24 17:03:39');
INSERT INTO `order` (`id`, `order_no`, `user_type`, `user_id`, `amount`, `subsidy_amount`, `actual_amount`, `payment_method`, `order_type`, `status`, `eat_time`, `pickup_no`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (4, 'ORD1748080876867', 'student', 3, 50.00, 0.00, 50.00, 'cash', 'reservation', 'pending', '2025-05-24 12:00:00', NULL, '', NULL, '2025-05-24 18:01:17', '2025-05-24 18:01:17');
INSERT INTO `order` (`id`, `order_no`, `user_type`, `user_id`, `amount`, `subsidy_amount`, `actual_amount`, `payment_method`, `order_type`, `status`, `eat_time`, `pickup_no`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (5, 'ORD1748081029805', 'student', 3, 4.00, 0.00, 4.00, 'cash', 'dine_in', 'pending', '2025-05-24 10:03:49', NULL, '', NULL, '2025-05-24 18:03:50', '2025-05-24 18:03:50');
INSERT INTO `order` (`id`, `order_no`, `user_type`, `user_id`, `amount`, `subsidy_amount`, `actual_amount`, `payment_method`, `order_type`, `status`, `eat_time`, `pickup_no`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (6, 'ORD1748081379055', 'student', 3, 30.00, 0.00, 30.00, 'cash', 'reservation', 'pending', '2025-05-24 12:00:00', NULL, '', NULL, '2025-05-24 18:09:39', '2025-05-24 18:09:39');
INSERT INTO `order` (`id`, `order_no`, `user_type`, `user_id`, `amount`, `subsidy_amount`, `actual_amount`, `payment_method`, `order_type`, `status`, `eat_time`, `pickup_no`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (7, 'ORD1748081393160', 'student', 3, 3.00, 0.00, 3.00, 'cash', 'dine_in', 'completed', '2025-05-24 10:09:53', NULL, '', NULL, '2025-05-24 18:09:53', '2025-05-24 22:47:09');
INSERT INTO `order` (`id`, `order_no`, `user_type`, `user_id`, `amount`, `subsidy_amount`, `actual_amount`, `payment_method`, `order_type`, `status`, `eat_time`, `pickup_no`, `remark`, `operator_id`, `create_time`, `update_time`) VALUES (8, 'ORD1748090391550', 'student', 4, 15.00, 15.00, 0.00, 'subsidy', 'dine_in', 'paid', '2025-05-24 12:39:51', NULL, '', NULL, '2025-05-24 20:39:52', '2025-05-24 22:02:08');
COMMIT;

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `dish_id` bigint NOT NULL COMMENT '菜品ID',
  `dish_name` varchar(50) NOT NULL COMMENT '菜品名称',
  `dish_type` varchar(20) NOT NULL COMMENT '类型:dish,staple,combo,product',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态:pending(待处理),processing(处理中),completed(已完成)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';

-- ----------------------------
-- Records of order_detail
-- ----------------------------
BEGIN;
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (1, 1, 1, '米饭', 'staple', 2.00, 1, 2.00, 'pending', '2025-05-24 16:32:20');
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (2, 1, 3, '回锅肉', 'dish', 12.00, 1, 12.00, 'pending', '2025-05-24 16:32:20');
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (3, 2, 2, '馒头', 'staple', 1.50, 2, 3.00, 'pending', '2025-05-24 17:02:48');
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (4, 3, 1, '米饭', 'staple', 2.00, 1, 2.00, 'pending', '2025-05-24 17:03:39');
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (5, 4, 8, '11', 'dish', 10.00, 5, 50.00, 'pending', '2025-05-24 18:01:17');
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (6, 5, 1, '米饭', 'staple', 1.00, 4, 4.00, 'pending', '2025-05-24 18:03:50');
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (7, 6, 8, '11', 'dish', 10.00, 3, 30.00, 'pending', '2025-05-24 18:09:39');
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (8, 7, 2, '馒头', 'staple', 1.50, 2, 3.00, 'pending', '2025-05-24 18:09:53');
INSERT INTO `order_detail` (`id`, `order_id`, `dish_id`, `dish_name`, `dish_type`, `price`, `quantity`, `amount`, `status`, `create_time`) VALUES (9, 8, 6, '学生套餐A', 'combo', 15.00, 1, 15.00, 'pending', '2025-05-24 20:39:52');
COMMIT;

-- ----------------------------
-- Table structure for order_rating
-- ----------------------------
DROP TABLE IF EXISTS `order_rating`;
CREATE TABLE `order_rating` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型:student,staff',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rating` int NOT NULL COMMENT '评分(1-5)',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片(JSON数组)',
  `reply` varchar(500) DEFAULT NULL COMMENT '回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_user` (`order_id`,`user_type`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单评价表';

-- ----------------------------
-- Records of order_rating
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for staff
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `position` varchar(50) NOT NULL COMMENT '职位',
  `department` varchar(50) NOT NULL COMMENT '部门',
  `employee_no` varchar(50) NOT NULL COMMENT '工号',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态:0禁用,1启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_employee_no` (`employee_no`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工表';

-- ----------------------------
-- Records of staff
-- ----------------------------
BEGIN;
INSERT INTO `staff` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `position`, `department`, `employee_no`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (3, 'staff001', '123456', '王五', '13800138003', 'staff1@example.com', '食堂经理', '第一食堂', 'E001', 1, NULL, '2025-05-22 22:07:51', '2025-05-22 22:07:51');
INSERT INTO `staff` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `position`, `department`, `employee_no`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (4, 'staff002', '123456', '赵六', '13800138004', 'staff2@example.com', '厨师长', '第一食堂', 'E002', 1, NULL, '2025-05-22 22:07:51', '2025-05-22 22:07:51');
INSERT INTO `staff` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `position`, `department`, `employee_no`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (5, 'yyy', '123456', 'yyy', '15437629871', 'yyy@163.com', '厨师长', '第一食堂', 'E11', 1, NULL, '2025-05-22 23:19:45', '2025-05-22 23:19:45');
COMMIT;

-- ----------------------------
-- Table structure for statistics
-- ----------------------------
DROP TABLE IF EXISTS `statistics`;
CREATE TABLE `statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL COMMENT '统计日期',
  `type` varchar(20) NOT NULL COMMENT '统计类型:daily(日报),weekly(周报),monthly(月报)',
  `category` varchar(20) NOT NULL COMMENT '类别:dish(菜品),order(订单),subsidy(补贴)',
  `data` json NOT NULL COMMENT '统计数据(JSON)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_date_type_category` (`date`,`type`,`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统计报表表';

-- ----------------------------
-- Records of statistics
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `student_no` varchar(50) NOT NULL COMMENT '学号',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `college` varchar(50) NOT NULL COMMENT '学院',
  `major` varchar(50) NOT NULL COMMENT '专业',
  `class_name` varchar(50) NOT NULL COMMENT '班级',
  `grade` varchar(20) NOT NULL COMMENT '年级',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态:0禁用,1启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_student_no` (`student_no`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';

-- ----------------------------
-- Records of student
-- ----------------------------
BEGIN;
INSERT INTO `student` (`id`, `username`, `password`, `real_name`, `student_no`, `phone`, `email`, `college`, `major`, `class_name`, `grade`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (3, 'student001', '123456', '张三', 'S001', '13800138011', 'student1@example.com', '计算机学院', '软件工程', '软工2101', '2021', 1, NULL, '2025-05-22 22:07:51', '2025-05-24 16:07:12');
INSERT INTO `student` (`id`, `username`, `password`, `real_name`, `student_no`, `phone`, `email`, `college`, `major`, `class_name`, `grade`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (4, 'student002', '123456', '李四', 'S002', '13800138002', 'student2@example.com', '计算机学院', '计算机科学', '计科2101', '2021', 1, NULL, '2025-05-22 22:07:51', '2025-05-22 22:07:51');
INSERT INTO `student` (`id`, `username`, `password`, `real_name`, `student_no`, `phone`, `email`, `college`, `major`, `class_name`, `grade`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES (6, 'yhx', '123456', 'yhx', 'S12', '13454289652', 'yhx@163.com', '计算机学院', '计算机科学与技术学院', '计科26-6', '2026', 1, NULL, '2025-05-22 23:10:51', '2025-05-22 23:10:51');
COMMIT;

-- ----------------------------
-- Table structure for subsidy_account
-- ----------------------------
DROP TABLE IF EXISTS `subsidy_account`;
CREATE TABLE `subsidy_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_type` varchar(20) NOT NULL COMMENT '用户类型:student,staff',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `subsidy_id` bigint NOT NULL COMMENT '补贴方案ID',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '剩余补贴金额',
  `total_received` decimal(10,2) DEFAULT '0.00' COMMENT '累计收到补贴',
  `total_used` decimal(10,2) DEFAULT '0.00' COMMENT '累计使用补贴',
  `last_reset_time` datetime DEFAULT NULL COMMENT '最后清零时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_subsidy` (`user_type`,`user_id`,`subsidy_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='补贴账户表';

-- ----------------------------
-- Records of subsidy_account
-- ----------------------------
BEGIN;
INSERT INTO `subsidy_account` (`id`, `user_type`, `user_id`, `subsidy_id`, `balance`, `total_received`, `total_used`, `last_reset_time`, `create_time`, `update_time`) VALUES (1, 'student', 3, 1, 15.00, 15.00, 0.00, NULL, '2025-05-24 21:52:13', '2025-05-24 21:52:13');
INSERT INTO `subsidy_account` (`id`, `user_type`, `user_id`, `subsidy_id`, `balance`, `total_received`, `total_used`, `last_reset_time`, `create_time`, `update_time`) VALUES (2, 'student', 4, 1, 0.00, 15.00, 15.00, NULL, '2025-05-24 21:52:13', '2025-05-24 22:02:08');
INSERT INTO `subsidy_account` (`id`, `user_type`, `user_id`, `subsidy_id`, `balance`, `total_received`, `total_used`, `last_reset_time`, `create_time`, `update_time`) VALUES (3, 'student', 6, 1, 15.00, 15.00, 0.00, NULL, '2025-05-24 21:52:13', '2025-05-24 21:52:13');
COMMIT;

-- ----------------------------
-- Table structure for subsidy_plan
-- ----------------------------
DROP TABLE IF EXISTS `subsidy_plan`;
CREATE TABLE `subsidy_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '方案名称',
  `amount` decimal(10,2) NOT NULL COMMENT '补贴金额',
  `period` varchar(20) NOT NULL COMMENT '补贴周期:daily(每日),weekly(每周),monthly(每月)',
  `target_type` varchar(20) NOT NULL COMMENT '补贴对象:student(学生),staff(员工)',
  `target_group` varchar(50) DEFAULT NULL COMMENT '补贴群体:college(学院),department(部门)',
  `group_value` varchar(50) DEFAULT NULL COMMENT '群体值',
  `is_reset` tinyint(1) DEFAULT '1' COMMENT '是否清零:0不清零,1清零',
  `cash_ratio` decimal(5,2) DEFAULT '0.00' COMMENT '现金支付比例',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态:0禁用,1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='补贴方案表';

-- ----------------------------
-- Records of subsidy_plan
-- ----------------------------
BEGIN;
INSERT INTO `subsidy_plan` (`id`, `name`, `amount`, `period`, `target_type`, `target_group`, `group_value`, `is_reset`, `cash_ratio`, `status`, `create_time`, `update_time`) VALUES (1, '学生每日基础补贴', 15.00, 'daily', 'student', NULL, NULL, 1, 0.00, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
INSERT INTO `subsidy_plan` (`id`, `name`, `amount`, `period`, `target_type`, `target_group`, `group_value`, `is_reset`, `cash_ratio`, `status`, `create_time`, `update_time`) VALUES (2, '计算机学院学生额外补贴', 200.00, 'monthly', 'student', 'college', '计算机学院', 1, 0.20, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
INSERT INTO `subsidy_plan` (`id`, `name`, `amount`, `period`, `target_type`, `target_group`, `group_value`, `is_reset`, `cash_ratio`, `status`, `create_time`, `update_time`) VALUES (3, '食堂员工工作餐补贴', 25.00, 'daily', 'staff', 'department', '食堂后厨', 1, 0.00, 1, '2025-05-22 21:19:20', '2025-05-22 21:19:20');
COMMIT;

-- ----------------------------
-- Table structure for subsidy_record
-- ----------------------------
DROP TABLE IF EXISTS `subsidy_record`;
CREATE TABLE `subsidy_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subsidy_account_id` bigint NOT NULL COMMENT '补贴账户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '发放金额',
  `type` varchar(20) NOT NULL COMMENT '类型:grant(发放),reset(清零)',
  `period_start` date DEFAULT NULL COMMENT '补贴周期开始',
  `period_end` date DEFAULT NULL COMMENT '补贴周期结束',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='补贴发放记录表';

-- ----------------------------
-- Records of subsidy_record
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
