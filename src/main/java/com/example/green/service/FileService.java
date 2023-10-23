package com.example.green.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.green.constants.Constants;
import com.example.green.entity.MyFile;
import com.example.green.exception.ServiceException;
import com.example.green.mapper.FileMapper;
import com.sun.xml.fastinfoset.stax.events.Util;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

@Service
public class FileService extends ServiceImpl<FileMapper, MyFile> {
    @Resource
    private FileMapper fileMapper;

    public String upload(MultipartFile uploadFile){
        String originalFilename = uploadFile.getOriginalFilename();
        String type = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        long size = uploadFile.getSize() / 1024;
        String url;
        MyFile myFile = new MyFile();
        myFile.setName(originalFilename);
        myFile.setSize(size);
        myFile.setType(type);


        InputStream inputStream = null;
        try {
            inputStream = uploadFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String md5 = SecureUtil.md5(inputStream);
        QueryWrapper<MyFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        List<MyFile> dbMyFileList = fileMapper.selectList(queryWrapper);
        if(dbMyFileList.size() != 0){

            url = dbMyFileList.get(0) .getUrl();
            myFile.setUrl(url);
        }else{

            File folder = new File(Constants.fileFolderPath);
            if(!folder.exists()){
                folder.mkdir();
            }
            String folderPath = folder.getAbsolutePath()+"/";
            System.out.println("File storage address"+folderPath);

            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            String finalFileName = uuid+"."+type;
            File targetFile = new File(folderPath + finalFileName);
            try {
                uploadFile.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            url = "/file/"+finalFileName;
            myFile.setUrl(url);
        }
        myFile.setMd5(md5);

        fileMapper.insert(myFile);
        System.out.println("file"+originalFilename+" "+url);
        return url;
    }


    public void download(String fileName, HttpServletResponse response){
        File file = new File(Constants.fileFolderPath+fileName);
        if(!file.exists()){
            throw new ServiceException(Constants.CODE_500,"file does not exist");
        }
        try {
            ServletOutputStream os = response.getOutputStream();
            response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileName,"UTF-8"));
            response.setContentType("application/octet-stream");
            os.write(FileUtil.readBytes(file));
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int fakeDelete(int id){
        UpdateWrapper<MyFile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",id).set("is_delete",true);
        return fileMapper.update(null,updateWrapper);
    }

    public IPage<MyFile> selectPage(int pageNum,int pageSize,String fileName) {
        IPage<MyFile> filesPage = new Page<>(pageNum, pageSize);
        QueryWrapper<MyFile> filesQueryWrapper = new QueryWrapper<>();
        if(!Util.isEmptyString(fileName)){
            filesQueryWrapper.like("name",fileName);
        }
        filesQueryWrapper.eq("is_delete",false);
        filesQueryWrapper.orderByDesc("id");
        return page(filesPage, filesQueryWrapper);
    }


    public int changeEnable(int id, boolean enable) {
        UpdateWrapper<MyFile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",id).set("enable",enable);
        return fileMapper.update(null, updateWrapper);
    }
}
