package com.example.green.controller;

import com.example.green.annotation.Authority;
import com.example.green.constants.Constants;
import com.example.green.common.Result;
import com.example.green.entity.AuthorityType;
import com.example.green.entity.Address;
import com.example.green.service.AddressService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Authority(AuthorityType.requireLogin)
@RestController
@RequestMapping("/api/address")
public class AddressController {
    @Resource
    private AddressService addressService;


    @GetMapping("/{userId}")
    public Result findAllById(@PathVariable Long userId) {
        return Result.success(addressService.findAllById(userId));
    }

    @GetMapping
    public Result findAll() {
        List<Address> list = addressService.list();
        return Result.success(list);
    }



    @PostMapping
    public Result save(@RequestBody Address address) {
        boolean b = addressService.saveOrUpdate(address);
        if(b){
            return Result.success();
        }else{
            return Result.error(Constants.CODE_500,"Fail to save the address!");
        }

    }

    @PutMapping
    public Result update(@RequestBody Address address) {
        addressService.updateById(address);
        return Result.success();
    }


    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        addressService.removeById(id);
        return Result.success();
    }





}
