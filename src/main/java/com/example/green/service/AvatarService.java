package com.example.green.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.example.green.constants.Constants;
import com.example.green.entity.Avatar;
import com.example.green.exception.ServiceException;
import com.example.green.mapper.AvatarMapper;
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
public class AvatarService {
    @Resource
    private AvatarMapper avatarMapper;

    public String upload(MultipartFile uploadFile){
        String url = null;



        InputStream inputStream = null;
        try {
            inputStream = uploadFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String md5 = SecureUtil.md5(inputStream);
        Avatar dbAvatar = avatarMapper.selectByMd5(md5);
        if(dbAvatar==null){
            String originalFilename = uploadFile.getOriginalFilename();
            String type = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            System.out.println(originalFilename+"   "+type);
            long size = uploadFile.getSize() / 1024;

            File folder = new File(Constants.avatarFolderPath);
            if(!folder.exists()){
                folder.mkdir();
            }
            String folderPath = folder.getAbsolutePath()+"/";
            System.out.println("文件存储地址"+folderPath);



            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
            String finalFileName = uuid+"."+type;
            File targetFile = new File(folderPath + finalFileName);
            try {
                uploadFile.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            url = "/avatar/"+finalFileName;
            Avatar avatar = new Avatar(type, size, url, md5);
            System.out.println(avatar);
            avatarMapper.save(avatar);
            return url;
        }
        return dbAvatar.getUrl();
    }

    public void download(String fileName, HttpServletResponse response){
        File file = new File(Constants.avatarFolderPath+fileName);
        if(!file.exists()){
            throw new ServiceException(Constants.CODE_500,"File does not exist!");
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

    public int delete(int id) {
        Avatar avatar = avatarMapper.selectById(id);
        int delete = avatarMapper.delete(id);
        System.out.println(delete);
        if(delete==1){
            String fileName = StrUtil.subAfter(avatar.getUrl(),"/",true);
            System.out.println(fileName);
            File file = new File(Constants.avatarFolderPath+fileName);
            System.out.println(file.getAbsolutePath());
            if(file.exists()){

                boolean delete1 = file.delete();
                if(delete1){
                    System.out.println("successfully deleted!");
                }
            }
        }
        return delete;
    }

    public List<Avatar> selectPage(int index, int pageSize) {
        return avatarMapper.selectPage(index,pageSize);
    }

    public int getTotal() {
        return avatarMapper.getTotal();
    }
}
