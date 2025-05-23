package com.campus.ordering.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.ordering.entity.ComboDish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐菜品关联Mapper接口
 */
@Mapper
public interface ComboDishMapper extends BaseMapper<ComboDish> {
} 