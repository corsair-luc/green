package com.example.green.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.green.mapper.CategoryMapper;
import com.example.green.utils.BaseApi;
import com.example.green.entity.Category;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.green.entity.IconCategory;
import com.example.green.mapper.IconCategoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private IconCategoryMapper iconCategoryMapper;


    public void add(Category category) {
        save(category);
        IconCategory iconCategory = new IconCategory();
        iconCategory.setCategoryId(category.getId());
        iconCategory.setIconId(category.getIconId());
        iconCategoryMapper.insert(iconCategory);
    }

    public Map<String, Object> delete(Long id) {

        iconCategoryMapper.delete(
                new QueryWrapper<IconCategory>().eq("category_id", id)
        );

        removeById(id);
        return BaseApi.success();
    }
}
