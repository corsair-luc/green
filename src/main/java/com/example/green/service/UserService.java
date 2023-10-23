package com.example.green.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.green.constants.Constants;
import com.example.green.constants.RedisConstants;
import com.example.green.entity.User;
import com.example.green.exception.ServiceException;
import com.example.green.mapper.UserMapper;
import com.example.green.utils.TokenUtils;
import com.example.green.common.Result;
import com.example.green.entity.LoginForm;
import com.example.green.entity.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;


@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Resource
    RedisTemplate<String,User> redisTemplate;

    public UserDTO login(LoginForm loginForm) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",loginForm.getUsername());
        queryWrapper.eq("password",loginForm.getPassword());
        User user = getOne(queryWrapper);
        if(user == null) {
            throw new ServiceException(Constants.CODE_403,"wrong user name or password");
        }
        String token = TokenUtils.genToken(user.getId().toString(), user.getUsername());

        redisTemplate.opsForValue().set(RedisConstants.USER_TOKEN_KEY + token,user);

        redisTemplate.expire(RedisConstants.USER_TOKEN_KEY +token, RedisConstants.USER_TOKEN_TTL, TimeUnit.MINUTES);

        UserDTO userDTO = BeanUtil.copyProperties(user,UserDTO.class);

        userDTO.setToken(token);
        return userDTO;

    }

    public User register(LoginForm loginForm) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",loginForm.getUsername());
        User user = getOne(queryWrapper);
        if(user!=null){
            throw new ServiceException(Constants.CODE_403,"Username is already taken");
        }else{
            user = new User();
            BeanUtils.copyProperties(loginForm,user);
            user.setNickname("new user");
            user.setRole("user");
            save(user);
            return user;
        }
    }

    public User getOne(String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        return getOne(queryWrapper);
    }

    public Result saveUpdate(User user) {
        if(user.getId() != null) {

            User old = this.baseMapper.selectById(user.getId());
            old.setNickname(ObjectUtils.isEmpty(user.getNickname()) ? old.getNickname() : user.getNickname());
            old.setAvatarUrl(ObjectUtils.isEmpty(user.getAvatarUrl()) ? old.getAvatarUrl() : user.getAvatarUrl());
            old.setRole(ObjectUtils.isEmpty(user.getRole()) ? old.getRole() : user.getRole());
            old.setPhone(ObjectUtils.isEmpty(user.getPhone()) ? old.getPhone() : user.getPhone());
            old.setEmail(ObjectUtils.isEmpty(user.getEmail()) ? old.getEmail() : user.getEmail());
            old.setAddress(ObjectUtils.isEmpty(user.getAddress()) ? old.getAddress() : user.getAddress());
            super.updateById(old);
            return Result.success("Successfully modified");
        } else {

            if(!ObjectUtils.isEmpty(this.getOne(user.getUsername()))) {
                return Result.error("400", "Username already exists");
            }
            user.setPassword(user.getNewPassword());
            super.save(user);
            return Result.success("added successfully");
        }
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }


    public void resetPassword(String id, String newPassword) {
        User user = this.getById(id);
        if(user == null) {
            return;
        }
        user.setPassword(newPassword);
        this.updateById(user);
    }
}
