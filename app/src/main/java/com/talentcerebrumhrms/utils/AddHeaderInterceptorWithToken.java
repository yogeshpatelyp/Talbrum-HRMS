package com.talentcerebrumhrms.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.talentcerebrumhrms.utils.AppController.apiVersion;
import static com.talentcerebrumhrms.utils.AppController.appVersion;
import static com.talentcerebrumhrms.utils.AppController.sharedpreferences;

public class AddHeaderInterceptorWithToken implements Interceptor{
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
//System.out.println("TOKEN "+sharedpreferences.getString("token", ""));
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("token", sharedpreferences.getString("token", ""));
        builder.addHeader("Accept", "application/json");
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("appVersion",appVersion);
        builder.addHeader("apiVersion", apiVersion);
        return chain.proceed(builder.build());
    }
}
