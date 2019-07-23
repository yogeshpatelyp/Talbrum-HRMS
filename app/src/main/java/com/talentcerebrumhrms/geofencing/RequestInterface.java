package com.talentcerebrumhrms.geofencing;

import com.google.gson.JsonObject;
import com.talentcerebrumhrms.models.JSONResponse;

import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestInterface {



    @FormUrlEncoded
    @POST("otpsend")
    Call<JsonObject> sendOTP(@Field("username") String first, @Field("newPassword") String last);

    @FormUrlEncoded
    @POST("varifyotp")
    Call<JsonObject> verifyOTP(@Field("otp") String first);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("api/users/reset")
    Call<JSONObject> changePassword(@Body RequestBody jsonObject);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })

    @POST("api/users")
    Call<JSONObject> signup(@Body RequestBody jsonObject);

   /* @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("api/jobs/{id}/apply?")
    Call<JSONObject> applyJobs(@Query("access_token") String token,@Body RequestBody jsonObject);*/


    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @PATCH("api/users/{id}/")
    Call<JSONObject> updateProfile(@Path("id") String id, @Query("access_token") String token, @Body RequestBody jsonObject);

    @FormUrlEncoded
    @POST("applyforjob")
    Call<JsonObject> applyJobs(@Field("jobid") String first, @Field("userid") String userid);


    @Headers({
            "apiVersion: 1",
            "email: jayashree.ghosh@village.net.in",
            "password: 12345678",
            "appVersion: 1.3"
    })
    @GET("/api/v2/companies/location_coordinates?")
    Call<JSONResponse> fetchlatlon(@Query("apiVersion") String apiVersion, @Query("appVersion") String appVersion, @Query("email") String email, @Query("password") String password);

    @GET("/api/v2/companies/location_coordinates?")
    Call<JSONObject> fetchlatlonJ(@Query("apiVersion") String apiVersion, @Query("appVersion") String appVersion, @Query("email") String email, @Query("password") String password);

}
