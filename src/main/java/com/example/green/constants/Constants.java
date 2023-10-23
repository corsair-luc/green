package com.example.green.constants;

import com.example.green.utils.PathUtils;

public class Constants {
    public static final String CODE_200 = "200";//sucess
    public static final String CODE_500 = "500";//system wrong
    public static final String NO_RESULT = "510";//no results
    public static final String CODE_401 = "401";//no permission
    public static final String TOKEN_ERROR = "401";//token invalid
    public static final String CODE_403 = "403";//reject to run

    public static final String fileFolderPath = PathUtils.getClassLoadRootPath() + "/file/";
    public static final String avatarFolderPath =  PathUtils.getClassLoadRootPath() + "/avatar/";
}
