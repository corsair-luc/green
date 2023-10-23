package com.example.green.controller;

import com.example.green.annotation.Authority;
import com.example.green.constants.Constants;
import com.example.green.common.Result;
import com.example.green.entity.AuthorityType;
import com.example.green.entity.Good;
import com.example.green.entity.Standard;
import com.example.green.service.GoodService;
import com.example.green.service.StandardService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/good")
public class GoodController {
    @Resource
    private GoodService goodService;

    @Resource
    private StandardService standardService;



    @Authority(AuthorityType.requireAuthority)
    @PostMapping
    public Result save(@RequestBody Good good) {
        System.out.println(good);
        return Result.success(goodService.saveOrUpdateGood(good));
    }

    @Authority(AuthorityType.requireAuthority)
    @PutMapping
    public Result update(@RequestBody Good good) {
        goodService.update(good);
        return Result.success();
    }

    @Authority(AuthorityType.requireAuthority)
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        goodService.deleteGood(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        return Result.success(goodService.getGoodById(id));
    }


    @GetMapping("/standard/{id}")
    public Result getStandard(@PathVariable int id) {
        return Result.success(goodService.getStandard(id));
    }

    @GetMapping
    public Result findAll() {

        return Result.success(goodService.findFrontGoods());
    }

    @GetMapping("/rank")
    public Result getSaleRank(@RequestParam int num){
        return Result.success(goodService.getSaleRank(num));
    }

    @PostMapping("/standard")
    public Result saveStandard(@RequestBody List<Standard> standards, @RequestParam int goodId) {

        standardService.deleteAll(goodId);

        for (Standard standard : standards) {
            standard.setGoodId(goodId);
            if(!standardService.save(standard)){
                return Result.error(Constants.CODE_500,"Fail to save!");
            }
        }
        return Result.success();
    }


    @Authority(AuthorityType.requireAuthority)
    @DeleteMapping("/standard")
    public Result delStandard(@RequestBody Standard standard) {
        boolean delete = standardService.delete(standard);
        if(delete) {
            return Result.success();
        }else {
            return Result.error(Constants.CODE_500,"删除失败");
        }
    }


    @Authority(AuthorityType.requireAuthority)
    @GetMapping("/recommend")
    public Result setRecommend(@RequestParam Long id,@RequestParam Boolean isRecommend){
        return Result.success(goodService.setRecommend(id,isRecommend));
    }

    @GetMapping("/page")
    public Result findPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) Integer categoryId) {

        return Result.success(goodService.findPage(pageNum,pageSize,searchText,categoryId));
    }
    @GetMapping("/fullPage")
    public Result findFullPage(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(required = false) Integer categoryId) {

        return Result.success(goodService.findFullPage(pageNum,pageSize,searchText,categoryId));
    }

}
