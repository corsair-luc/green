package com.example.green.controller;



import com.example.green.annotation.Authority;
import com.example.green.entity.AuthorityType;
import com.example.green.service.IncomeService;
import com.example.green.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Authority(AuthorityType.requireAuthority)
@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Resource
    private IncomeService incomeService;

    @GetMapping("/chart")
    public Result getChart(){
        return Result.success(incomeService.getChart());
    }
    @GetMapping("/week")
    public Result getWeekIncome(){
        return Result.success(incomeService.getWeekIncome());
    }

    @GetMapping("/month")
    public Result getMonthIncome(){
        return Result.success(incomeService.getMonthIncome());
    }
}
