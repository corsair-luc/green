package com.example.green.controller;

import com.auth0.jwt.JWT;
import com.example.green.annotation.Authority;
import com.example.green.common.Result;
import com.example.green.entity.AuthorityType;
import com.example.green.entity.Good;
import com.example.green.service.GoodService;
import com.example.green.service.UserService;
import com.example.green.entity.Carousel;
import com.example.green.service.CarouselService;
import com.example.green.entity.User;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/carousel")
public class CarouselController {
    @Resource
    private CarouselService carouselService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserService userService;
    @Resource
    private GoodService goodService;

    public User getUser() {
        String token = request.getHeader("token");
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }


    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return Result.success(carouselService.getById(id));
    }

    @GetMapping
    public Result findAll() {
        List<Carousel> list = carouselService.getAllCarousel();
        return Result.success(list);
    }


    @Authority(AuthorityType.requireAuthority)
    @PostMapping
    public Result save(@RequestBody Carousel carousel) {
        Good good = goodService.getById(carousel.getGoodId());
        if(good == null) {
            return Result.error("400", "The product id is wrong and the product id was not found. = " + carousel.getGoodId());
        }
        carouselService.saveOrUpdate(carousel);
        return Result.success();
    }
    @Authority(AuthorityType.requireAuthority)
    @PutMapping
    public Result update(@RequestBody Carousel carousel) {
        Good good = goodService.getById(carousel.getGoodId());
        if(good == null) {
            return Result.error("400", "The product id is wrong and the product id was not found. = " + carousel.getGoodId());
        }
        carouselService.updateById(carousel);
        return Result.success();
    }


    @Authority(AuthorityType.requireAuthority)
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        carouselService.removeById(id);
        return Result.success();
    }





}
