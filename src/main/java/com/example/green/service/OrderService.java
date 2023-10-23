package com.example.green.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.green.constants.Constants;
import com.example.green.constants.RedisConstants;
import com.example.green.entity.Good;
import com.example.green.entity.Order;
import com.example.green.entity.OrderGoods;
import com.example.green.entity.OrderItem;
import com.example.green.exception.ServiceException;
import com.example.green.mapper.GoodMapper;
import com.example.green.mapper.OrderGoodsMapper;
import com.example.green.mapper.OrderMapper;
import com.example.green.mapper.StandardMapper;
import com.example.green.utils.TokenUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderGoodsMapper orderGoodsMapper;
    @Resource
    private StandardMapper standardMapper;
    @Resource
    private GoodMapper goodMapper;
    @Resource
    private CartService cartService;
    @Resource
    private RedisTemplate<String, Good> redisTemplate;

    @Transactional
    public String saveOrder(Order order) {
        order.setUserId(TokenUtils.getCurrentUser().getId());
        String orderNo = DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(6);
        order.setOrderNo(orderNo);
        order.setCreateTime(DateUtil.now());
        orderMapper.insert(order);

        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setOrderId(order.getId());

        String goods = order.getGoods();
        List<OrderItem> orderItems = JSON.parseArray(goods, OrderItem.class);
        for (OrderItem orderItem : orderItems) {
            long good_id = orderItem.getId();
            String standard = orderItem.getStandard();
            int num = orderItem.getNum();
            orderGoods.setGoodId(good_id);
            orderGoods.setCount(num);
            orderGoods.setStandard(standard);

            orderGoodsMapper.insert(orderGoods);
        }

        cartService.removeById(order.getCartId());
        return orderNo;
    }


    @Transactional
    public void payOrder(String orderNo) {

        orderMapper.payOrder(orderNo);

        Map<String, Object> orderMap = orderMapper.selectByOrderNo(orderNo);
        int count = (int) orderMap.get("count");
        Object goodIdObj = orderMap.get("goodId");
        Long goodId = null;
        if(goodIdObj instanceof Long) {
            goodId = (Long) goodIdObj;
        } else if(goodIdObj != null) {
            try {
                goodId = Long.parseLong(goodIdObj.toString());
            } catch (NumberFormatException e) {
                throw new ServiceException(Constants.CODE_500, "Product ID is incorrect");
            }
        }

        if(goodId == null) {
            throw new ServiceException(Constants.CODE_500, "Product ID does not exist");
        }
        String standard = (String) orderMap.get("standard");
        int store = standardMapper.getStore(goodId, standard);
        if (store < count) {
            throw new ServiceException(Constants.CODE_500, "Inventory shortage");
        }
        standardMapper.deductStore(goodId, standard, store - count);


        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderLambdaQueryWrapper.eq(Order::getOrderNo, orderNo);
        Order one = getOne(orderLambdaQueryWrapper);
        BigDecimal totalPrice = one.getTotalPrice();
        goodMapper.saleGood(goodId, count, totalPrice);


        String redisKey = RedisConstants.GOOD_TOKEN_KEY + goodId;
        ValueOperations<String, Good> valueOperations = redisTemplate.opsForValue();
        Good good = valueOperations.get(redisKey);
        if(!ObjectUtils.isEmpty(good)) {
            good.setSales(good.getSales() + count);
            valueOperations.set(redisKey, good);
        }
    }

    public List<Map<String, Object>> selectByUserId(int userId) {
        return orderMapper.selectByUserId(userId);
    }

    public boolean receiveOrder(String orderNo) {
        return orderMapper.receiveOrder(orderNo);
    }

    public Map<String, Object> selectByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    public void delivery(String orderNo) {
        LambdaUpdateWrapper<Order> orderLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        orderLambdaUpdateWrapper.eq(Order::getOrderNo, orderNo)
                .set(Order::getState, "Shipped");
        update(orderLambdaUpdateWrapper);
    }
}
