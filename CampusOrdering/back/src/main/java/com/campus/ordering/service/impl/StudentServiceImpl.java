package com.campus.ordering.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.ordering.entity.Student;
import com.campus.ordering.mapper.StudentMapper;
import com.campus.ordering.service.StudentService;
import org.springframework.stereotype.Service;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

} 