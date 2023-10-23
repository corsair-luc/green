package com.example.green.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.green.annotation.Authority;
import com.example.green.constants.Constants;
import com.example.green.common.Result;
import com.example.green.entity.AuthorityType;
import com.example.green.entity.LoginForm;
import com.example.green.entity.User;
import com.example.green.entity.dto.UserDTO;
import com.example.green.service.UserService;
import com.example.green.utils.TokenUtils;
import com.sun.xml.internal.fastinfoset.stax.events.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public Result login(@RequestBody LoginForm loginForm) {
        UserDTO dto = userService.login(loginForm);
        return Result.success(dto);
    }

    @PostMapping("/register")
    public Result register(@RequestBody LoginForm loginForm) {
        User user = userService.register(loginForm);
        return Result.success(user);
    }

    @GetMapping("/userinfo/{username}")
    public Result getUserInfoByName(@PathVariable String username) {
        User one = userService.getOne(username);
        return Result.success(one);
    }

    @GetMapping("/userid")
    public long getUserId() {
        return TokenUtils.getCurrentUser().getId();
    }

    @GetMapping("/user/")
    public Result findAll() {
        List<User> list = userService.list();
        return Result.success(list);
    }

    @PostMapping("/user")
    public Result save(@RequestBody User user) {

        return userService.saveUpdate(user);
    }

    @Authority(AuthorityType.requireAuthority)
    @DeleteMapping("/user/{id}")
    public Result deleteById(@PathVariable int id) {
        boolean isSuccessful = userService.removeById(id);
        if (isSuccessful) {
            return Result.success();
        } else {
            return Result.error(Constants.CODE_500, "fail to delete!");
        }
    }

    @Authority(AuthorityType.requireAuthority)
    @PostMapping("/user/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        boolean isSuccessful = userService.removeBatchByIds(ids);
        if (isSuccessful) {
            return Result.success();
        } else {
            return Result.error(Constants.CODE_500, "fail to delete!");
        }
    }

    @GetMapping("/user/page")
    public Result findPage(@RequestParam int pageNum,
                           @RequestParam int pageSize,
                           String id,
                           String username,
                           String nickname) {
        IPage<User> userPage = new Page<>(pageNum, pageSize);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (!Util.isEmptyString(id)) {
            userQueryWrapper.like("id", id);
        }
        if (!Util.isEmptyString(username)) {
            userQueryWrapper.like("username", username);
        }
        if (!Util.isEmptyString(nickname)) {
            userQueryWrapper.like("nickname", nickname);
        }
        userQueryWrapper.orderByDesc("id");
        System.out.println("============" + TokenUtils.getCurrentUser());
        return Result.success(userService.page(userPage, userQueryWrapper));
    }


    @GetMapping("/user/resetPassword")
    public Result resetPassword(@RequestParam String id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
}
