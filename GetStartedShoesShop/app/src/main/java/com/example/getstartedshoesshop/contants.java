package com.example.getstartedshoesshop;

public class contants {

    public static String localhost = "api-mobile.onrender.com";
    //login và register API
    private static final String ROOT_URL = "http://" + localhost + "/";
    public static final String URL_REGISTER = ROOT_URL + "/shoppingapp/registrationapi.php?apicall=signup";
    public static final String URL_LOGIN= ROOT_URL + "api/v1/auth/login";

    public static final String URL_GETME= ROOT_URL + "api/v1/auth/me";

    public static final String URL_CATEGORY= ROOT_URL + "appfoods/";
    //    public static final String URL_CATEGORY= "http://app.iotstar.vn/appfoods/categories.php";
    private static final String URL_LAST_PRODUCT = "http://app.iotstar.vn/appfoods/lastproduct.php";
}

