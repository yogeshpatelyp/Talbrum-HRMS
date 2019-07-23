package com.talentcerebrumhrms.utils;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {


    @POST("/api/v2/companies/check_domain")
    Call<JsonObject> company_domain(@Body RequestBody jsonObject);


    @POST("/api/v2/api_sessions/login")
    Call<JsonObject> login(@Body RequestBody jsonObject);

    @DELETE("/api/v2/api_sessions/logout")
    Call<JsonObject> logout();


    @GET("/api/v2/other_details/holidays")
    Call<JsonObject> list_holidays();

    @GET("/api/v2/other_details/non_ctc_reimbursement")
    Call<JsonObject> list_reimbursement();

    @GET("/api/v2/leaves/remaining")
    Call<JsonObject> leave_remaining();

    @GET("/api/v2/leaves/leave_dropdown")
    Call<JsonObject> select_leave();

    @GET("/api/v2/leaves/leave_request_list")
    Call<JsonObject> leave_approve();

    @GET("/api/v2/attendances/check_attendance")
    Call<JsonObject> mark_attendance_check();

    @POST("/api/v2/attendances/mark_attendance")
    Call<JsonObject> mark_attendance(@Body RequestBody jsonObject);

    @POST("/api/v2/leaves/working_days")
    Call<JsonObject> working_days(@Body RequestBody jsonObject);

    @POST("/api/v2/leaves/leave_request")
    Call<JsonObject> apply_leave(@Body RequestBody jsonObject);

    @POST("/api/v2/leaves/approve_leave")
    Call<JsonObject> approve_reject_leave(@Body RequestBody jsonObject);

    @GET("/api/v2/other_details/alerts")
    Call<JsonObject> get_alerts();

    @POST("/api/v2/other_details/read_alert")
    Call<JsonObject> mark_read_alert(@Body RequestBody jsonObject);

    @GET("/api/v2/attendances/get_details")
    Call<JsonObject> get_all_leave_details();

    @GET("/api/v2/attendances/get_in_time")
    Call<JsonObject> in_time();

    @GET("/api/v2/other_details/salary_slip")
    Call<JsonObject> salary_slip();

    @GET("/api/v2/api_sessions/check_token")
    Call<JsonObject> emer_Check_token();

    @POST("/api/v2/leaves/all_records")
    Call<JsonObject> leave_details(@Body RequestBody jsonObject);

    @GET("/api/v2/other_details/employee_directory")
    Call<JsonObject> employee_directory();

    @POST("/api/v2/timesheets/get_records")
    Call<JsonObject> get_timesheet_records(@Body RequestBody jsonObject);

    @POST("/api/v2/timesheets/get_detail")
    Call<JsonObject> get_detailed_timesheet_record(@Body RequestBody jsonObject);

    @POST("/api/v2/timesheets/save_timesheet")
    Call<JsonObject> upload_timesheet_record(@Body RequestBody jsonObject);

    @GET("/api/v2/")
    Call<JsonObject> list_vacancy();

    @POST("/api/v2/")
    Call<JsonObject> list_candidate(@Body RequestBody jsonObject);

    @POST("/api/v2/attendances/attendance_time_out")
    Call<JsonObject> outTime(@Body RequestBody jsonObject);

    @POST("/api/v2/beat_logs")
    Call<JsonObject> beatlog(@Body RequestBody jsonObject);

    @GET("/api/v2/beat_logs?")
    Call<JsonObject> beat_logs_detail(@Query("employee_id") String employee_id, @Query("to_date") String to_date, @Query("from_date") String from_date);

    @GET("/api/v2/companies/getDarpanUrl")
    Call<JsonObject> getPDFurl();




}
