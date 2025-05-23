package com.campus.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.ordering.entity.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
    
    @Select("SELECT * FROM staff WHERE username = #{username}")
    Staff findByUsername(String username);

    @Select("SELECT * FROM staff WHERE employee_no = #{employeeNo}")
    Staff findByEmployeeNo(String employeeNo);
} 