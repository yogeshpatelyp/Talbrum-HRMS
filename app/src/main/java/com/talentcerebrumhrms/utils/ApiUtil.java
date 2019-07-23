package com.talentcerebrumhrms.utils;
public class ApiUtil {


    public static ApiInterface getServiceClass(){
        return RetrofitAPI.getRetrofit(AppController.serverUrl).create(ApiInterface.class);
    }
}
