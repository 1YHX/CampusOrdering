-- 插入管理员数据
INSERT INTO admin (username, password, real_name, phone, email, status, create_time, update_time)
VALUES ('admin', '123456', '系统管理员', '13800138000', 'admin@example.com', 1, NOW(), NOW());

-- 插入学生数据
INSERT INTO student (username, password, student_no, real_name, phone, email, college, major, class_name, grade, status, create_time, update_time)
VALUES 
('student001', '123456', 'S001', '张三', '13800138001', 'student1@example.com', '计算机学院', '软件工程', '软工2101', '2021', 1, NOW(), NOW()),
('student002', '123456', 'S002', '李四', '13800138002', 'student2@example.com', '计算机学院', '计算机科学', '计科2101', '2021', 1, NOW(), NOW());

-- 插入员工数据
INSERT INTO staff (username, password, employee_no, real_name, phone, email, position, department, status, create_time, update_time)
VALUES 
('staff001', '123456', 'E001', '王五', '13800138003', 'staff1@example.com', '食堂经理', '第一食堂', 1, NOW(), NOW()),
('staff002', '123456', 'E002', '赵六', '13800138004', 'staff2@example.com', '厨师长', '第一食堂', 1, NOW(), NOW()); 