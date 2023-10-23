package com.example.green.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.green.annotation.Authority;
import com.example.green.constants.Constants;
import com.example.green.common.Result;
import com.example.green.entity.AuthorityType;
import com.example.green.entity.MyFile;
import com.example.green.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file){
        String url = fileService.upload(file);
        return Result.success(url);
    }


    @GetMapping("/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response){
        fileService.download(fileName,response);
    }

    @Authority(AuthorityType.requireAuthority)
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable int id){
        int i = fileService.fakeDelete(id);
        if(i == 1){
            return Result.success();
        }else{
            return Result.error(Constants.CODE_500,"Fail to delete!");
        }
    }

    @Authority(AuthorityType.requireAuthority)
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        for (Integer id : ids) {
            int i = fileService.fakeDelete(id);
            if(i != 1){
                return Result.error(Constants.CODE_500,"Delete file："+fileService.getById(id).getName()+" stop ");
            }
        }
        return Result.success();
    }

    @Authority(AuthorityType.requireAuthority)
    @GetMapping("/enable")
    public Result changeEnable(@RequestParam int id,@RequestParam boolean enable){
        int i = fileService.changeEnable(id, enable);
        if(i == 0){
            return Result.error(Constants.CODE_500,"Fail to modify!");
        }else {
            return Result.success();
        }

    }
    //查询
    @GetMapping("/page")
    public Result selectPage(@RequestParam int pageNum,
                             @RequestParam int pageSize,
                             @RequestParam(required = false) String fileName){

        IPage<MyFile> myFileIPage = fileService.selectPage(pageNum, pageSize, fileName);
        return Result.success(myFileIPage);
    }
}
