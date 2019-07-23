package com.talentcerebrumhrms.utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAPI {

    public static Retrofit getRetrofit(String url) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addNetworkInterceptor(new AddHeaderInterceptorWithToken());
      // httpClient.connectTimeout(60, TimeUnit.MINUTES);
      //  httpClient.readTimeout(60, TimeUnit.SECONDS);
      //  httpClient.writeTimeout(60, TimeUnit.SECONDS);
        Gson gson = new GsonBuilder().setLenient().create();

        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
    }


}
