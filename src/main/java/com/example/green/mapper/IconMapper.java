package com.example.green.mapper;

import com.example.green.entity.Icon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface IconMapper extends BaseMapper<Icon> {

    List<Icon> getIconCategoryMapList();
}
