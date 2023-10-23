package com.example.green.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.green.mapper.IconMapper;
import com.example.green.utils.BaseApi;
import com.example.green.entity.Icon;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.green.entity.IconCategory;
import com.example.green.mapper.IconCategoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class IconService extends ServiceImpl<IconMapper, Icon> {

    @Resource
    private IconMapper iconMapper;

    @Resource
    private IconCategoryMapper iconCategoryMapper;

    public List<Icon> getIconCategoryMapList() {
        return iconMapper.getIconCategoryMapList();
    }


    public Map<String, Object> deleteById(Long id) {

        Long count = iconCategoryMapper.selectCount(
                new QueryWrapper<IconCategory>().eq("icon_id", id)
        );
        if (count > 0) {
            return BaseApi.error("This upper-level category has lower-level categories. Please delete all lower-level categories and try deleting again.");
        }
        super.removeById(id);
        return BaseApi.success();
    }
}
