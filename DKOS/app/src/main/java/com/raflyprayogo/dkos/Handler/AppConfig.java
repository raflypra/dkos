package com.raflyprayogo.dkos.Handler;

/**
 * Created by raflyprayogo on 2/23/2016.
 */
public class AppConfig {
    
    //Base Link
//    public static String BASE_URL                       = "http://128.199.193.234:3131/iform/api/";
    public static String BASE_URL                       = "http://192.168.0.29/dkos/api/";

    // Server user login url
    public static String URL_LOGIN                      = BASE_URL + "login/login_api";
    public static String URL_GetImageUser               = BASE_URL + "assets/user_pict/";
    public static String URL_SENT_TOKEN                 = BASE_URL + "login/member_token";
    public static String URL_CHECK_TOKEN                = BASE_URL + "login/member_token_check";

//    //Expense
    public static String URL_GET_EXPENSE                = BASE_URL + "expense";
//
//    //Upload Image
//    public static String URL_UPLOAD_IMG                 = BASE_URL + "assignment/upload_image";
}
