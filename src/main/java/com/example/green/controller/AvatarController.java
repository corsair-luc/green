package com.example.green.controller;

import com.example.green.annotation.Authority;
import com.example.green.constants.Constants;
import com.example.green.common.Result;
import com.example.green.entity.AuthorityType;
import com.example.green.entity.Avatar;
import com.example.green.service.AvatarService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    @Resource
    private AvatarService avatarService;

    @PostMapping()
    public Result uploadAvatar(@RequestParam MultipartFile file){
        System.out.println("uploadAvatar====>");
        String url = avatarService.upload(file);
        return Result.success(url);
    }

    @GetMapping("/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response){
        avatarService.download(fileName,response);
    }

    @Authority(AuthorityType.requireAuthority)
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable int id){
        int i = avatarService.delete(id);
        if(i == 1){
            return Result.success();
        }else{
            return Result.error(Constants.CODE_500,"Fail to delete!");
        }
    }

    @GetMapping("/page")
    public Result selectPage(@RequestParam int pageNum,
                             @RequestParam int pageSize){
        int index = (pageNum - 1) * pageSize;
        List<Avatar> avatars = avatarService.selectPage(index, pageSize);
        int total = avatarService.getTotal();
        HashMap<String, Object> map = new HashMap<>();
        map.put("records",avatars);
        map.put("total",total);
        return Result.success(map);
    }
}
