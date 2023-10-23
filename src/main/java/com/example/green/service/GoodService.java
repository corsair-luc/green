package com.example.green.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.green.constants.Constants;
import com.example.green.constants.RedisConstants;
import com.example.green.entity.Good;
import com.example.green.entity.GoodStandard;
import com.example.green.entity.dto.GoodDTO;
import com.example.green.exception.ServiceException;
import com.example.green.mapper.GoodMapper;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GoodService extends ServiceImpl<GoodMapper, Good> {

    @Resource
    private GoodMapper goodMapper;
    @Resource
    private RedisTemplate<String, Good> redisTemplate;


    public Good getGoodById(Long id) {
        String redisKey = RedisConstants.GOOD_TOKEN_KEY + id;

        ValueOperations<String, Good> valueOperations = redisTemplate.opsForValue();
        Good redisGood = valueOperations.get(redisKey);
        if(redisGood!=null){
            redisTemplate.expire(redisKey, RedisConstants.GOOD_TOKEN_TTL, TimeUnit.MINUTES);
            return redisGood;
        }

        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getIsDelete,false);
        queryWrapper.eq(Good::getId,id);
        Good dbGood = getOne(queryWrapper);
        if(dbGood!=null){

            valueOperations.set(redisKey,dbGood);
            redisTemplate.expire(redisKey, RedisConstants.GOOD_TOKEN_TTL, TimeUnit.MINUTES);
            return dbGood;
        }

        throw new ServiceException(Constants.NO_RESULT,"no results");

    }

    public String getStandard(int id){
        List<GoodStandard> standards = goodMapper.getStandardById(id);
        if(standards.size()==0){
            throw new ServiceException(Constants.NO_RESULT,"no results");
        }
        return JSON.toJSONString(standards);
    }

    public BigDecimal getMinPrice(Long id){
        return goodMapper.getMinPrice(id);
    }

    public List<GoodDTO> findFrontGoods() {
        return goodMapper.findFrontGoods();
    }



    public void deleteGood(Long id) {
        redisTemplate.delete(RedisConstants.GOOD_TOKEN_KEY+id);
        goodMapper.fakeDelete(id);
    }

    public Long saveOrUpdateGood(Good good) {
        System.out.println(good);
        if(good.getId()==null){
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            good.setCreateTime(df.format(LocalDateTime.now()));
            goodMapper.insertGood(good);
        }else{
            saveOrUpdate(good);
            redisTemplate.delete(RedisConstants.GOOD_TOKEN_KEY + good.getId());
        }
        return good.getId();
    }

    public boolean setRecommend(Long id,Boolean isRecommend) {
        LambdaUpdateWrapper<Good> goodsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        goodsLambdaUpdateWrapper.eq(Good::getId,id)
                .set(Good::getRecommend,isRecommend);
        return update(goodsLambdaUpdateWrapper);
    }

    public List<Good> getSaleRank(int num) {
        return goodMapper.getSaleRank(num);
    }


    public void update(Good good) {
        updateById(good);
        redisTemplate.delete(RedisConstants.GOOD_TOKEN_KEY + good.getId());
    }

    public IPage<GoodDTO> findPage(Integer pageNum, Integer pageSize, String searchText, Integer categoryId) {
        LambdaQueryWrapper<Good> query = Wrappers.<Good>lambdaQuery().orderByDesc(Good::getId);

        if (StrUtil.isNotBlank(searchText)) {
            query.like(Good::getName, searchText).or().like(Good::getDescription,searchText).or().eq(Good::getId,searchText);
        }
        if(categoryId != null){
            query.eq(Good::getCategoryId,categoryId);
        }

        query.eq(Good::getIsDelete,false);
        IPage<Good> page = this.page(new Page<>(pageNum, pageSize), query);

        IPage<GoodDTO> goodDTOPage = page.convert(good -> {
            GoodDTO goodDTO = new GoodDTO();
            BeanUtil.copyProperties(good, goodDTO);
            return goodDTO;
        });
        for (GoodDTO good : goodDTOPage.getRecords()) {

            good.setPrice(getMinPrice(good.getId()));
        }
        return goodDTOPage;
    }
    public IPage<Good> findFullPage(Integer pageNum, Integer pageSize, String searchText, Integer categoryId) {
        LambdaQueryWrapper<Good> query = Wrappers.<Good>lambdaQuery().orderByDesc(Good::getId);

        if (StrUtil.isNotBlank(searchText)) {
            query.like(Good::getName, searchText).or().like(Good::getDescription,searchText).or().eq(Good::getId,searchText);
        }
        if(categoryId != null){
            query.eq(Good::getCategoryId,categoryId);
        }

        query.eq(Good::getIsDelete,false);
        IPage<Good> page = this.page(new Page<>(pageNum, pageSize), query);
        for (Good good : page.getRecords()) {

            good.setPrice(getMinPrice(good.getId()));
        }
        return page;
    }
}
