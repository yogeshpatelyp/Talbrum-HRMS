package com.talentcerebrumhrms.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.datatype.AlertDataType;
import com.talentcerebrumhrms.datatype.AttendanceFragmentDataType;
import com.talentcerebrumhrms.datatype.CandidateDataType;
import com.talentcerebrumhrms.datatype.ChatDataType;
import com.talentcerebrumhrms.datatype.HolidayDatatype;
import com.talentcerebrumhrms.datatype.LeaveAppliedDataType;
import com.talentcerebrumhrms.datatype.LeaveApproveDatatype;
import com.talentcerebrumhrms.datatype.LeaveTypeDatatype;
import com.talentcerebrumhrms.datatype.LeavesDatatype;
import com.talentcerebrumhrms.datatype.PeopleDataType;
import com.talentcerebrumhrms.datatype.RembursementDatatype;
import com.talentcerebrumhrms.datatype.TimesheetDataType;
import com.talentcerebrumhrms.datatype.UploadTimesheetDataType;
import com.talentcerebrumhrms.datatype.VacancyDataType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

/**
 * Created by saransh on 29-09-2016.
 * AppController, has all the api requests and static variables for the project.
 */

public class AppController extends MultiDexApplication {
    public static final String TAG = AppController.class.getSimpleName();
    public static boolean attendance_dialog;
    public static String loc = "";
    public static double lat, lon;
    public static ArrayList<HolidayDatatype> holidays = new ArrayList<>();
    public static ArrayList<RembursementDatatype> reimbursement_array = new ArrayList<>();
    public static ArrayList<LeavesDatatype> leaves_array = new ArrayList<>();
    public static ArrayList<TimesheetDataType> timesheet_approved_array = new ArrayList<>();
    public static ArrayList<TimesheetDataType> timesheet_rejected_array = new ArrayList<>();
    public static ArrayList<UploadTimesheetDataType> timesheet_upload_array = new ArrayList<>();
    public static ArrayList<VacancyDataType> vacancy_array = new ArrayList<>();
    public static ArrayList<CandidateDataType> candidate_array = new ArrayList<>();
    public static ArrayList<LeaveTypeDatatype> leave_type_array = new ArrayList<>();
    public static ArrayList<LeaveTypeDatatype> leave_reason_array = new ArrayList<>();
    public static ArrayList<LeaveApproveDatatype> leave_approve_array = new ArrayList<>();
    public static ArrayList<AlertDataType> alert_data_array = new ArrayList<>();
    public static ArrayList<LeaveAppliedDataType> leave_deatil_array = new ArrayList<>();
    public static ArrayList<PeopleDataType> people_data_array = new ArrayList<>();
    public static ArrayList<ChatDataType> chat_data_array = new ArrayList<>();
    public static AttendanceFragmentDataType attendanceFragmentDataType;
    public static int approve_size = 0;
    public static int alert_size = 0;
    public static String appVersion;
    public static final String apiVersion = "2.0";
    public static String serverUrl;
    static SharedPreferences sharedpreferences;
    static LocationManager manager;
    private static AppController mInstance;
    private static int MY_SOCKET_TIMEOUT_MS = 10000;
    private static Context mContext;
    private RequestQueue mRequestQueue;
    //public static final String chatbotUrl = "http://139.59.8.40:8000";
    //public static final String serverUrl = "http://192.168.4.29:3000";
    private ImageLoader mImageLoader;

    // manage check token and user exipry stuff.
    // outdoor leave and apply and approve reimbursement.
    // offline data


    @Override
    public Context getApplicationContext() {
        MultiDex.install(getContext());
        return super.getApplicationContext();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void company_domain(final ResponseListener responseListener, final String company_url) {

        String url = "http://" + company_url + "/api/v2/companies/check_domain";
        JSONObject company_details = new JSONObject();
        try {
            company_details.put("company_url", company_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, " company_url ======> " + company_url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, company_details, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.e("signin response", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("signin error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");    //accept drawer_header i think fixes prob
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static void login(final ResponseListener responseListener, String username, String password) {
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        String url = serverUrl + "/api/v2/api_sessions/login";

        Log.e("signin url", url);
        JSONObject login_details = new JSONObject();
        try {
            login_details.put("email", username);
            login_details.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "login_details", String.valueOf(login_details));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, login_details, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.e("signin response", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("signin error", String.valueOf(volleyError));
                responseListener.onError(volleyError);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static void logout(final ResponseListener responseListener) {
        String url = serverUrl + "/api/v2/api_sessions/logout";
        Log.e("token_1", sharedpreferences.getString("token", ""));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "logout", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("logout error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));

                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void list_holidays(final ResponseListener responseListener) {
        String url = serverUrl + "/api/v2/other_details/holidays";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "list of holidays", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("list_holidays error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void list_reimbursement(final ResponseListener responseListener) {
        String url = serverUrl + "/api/v2/other_details/non_ctc_reimbursement";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "reimbursement", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("reimbursement error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void leave_remaining(final ResponseListener responseListener) {
        String url = serverUrl + "/api/v2/leaves/remaining";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "leaves_remaining", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("leaves_remaining error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void select_leave(final ResponseListener responseListener) {
        String url = serverUrl + "/api/v2/leaves/leave_dropdown";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "select_leave", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("select_leave", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void leave_approve(final ResponseListener responseListener) {
        String url = serverUrl + "/api/v2/leaves/leave_request_list";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "leave_approve", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("leave_approve error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

    }

    public static void mark_attendance_check(final ResponseListener responseListener) {

        String url = serverUrl + "/api/v2/attendances/check_attendance";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "check_attendance", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("attendance_check error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void mark_attendance(final ResponseListener responseListener, final String value) {

        String url = serverUrl + "/api/v2/attendances/mark_attendance";
        JSONObject attendance_details = new JSONObject();
        try {
            attendance_details.put("status", value);
            //attendance_details.put("Address", loc);
            //attendance_details.put("Latitude", lat);
            //attendance_details.put("Longitude", lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG + "attendance_details", String.valueOf(attendance_details));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, attendance_details, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "mark_attendance", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("mark_attendance error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    public static void working_days(final ResponseListener responseListener, final String leave_type, final String from, final String to, final String to_duration, final String from_duration) {
        String url = serverUrl + "/api/v2/leaves/working_days";
        JSONObject work_days = new JSONObject();
        try {
            work_days.put("typeleave", leave_type);
            work_days.put("from", from);
            work_days.put("to", to);
            work_days.put("to_duration", to_duration);
            work_days.put("from_duration", from_duration);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG + "work_days", String.valueOf(work_days));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, work_days, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "work_days", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("work_days error", String.valueOf(volleyError));
                responseListener.onError(volleyError);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static void apply_leave(final ResponseListener responseListener, final String reason, final String from, final String to, final String from_duration, final String to_duration, final String working_days, final String leave_type, final String leave_reason) {

        String url = serverUrl + "/api/v2/leaves/leave_request";
        JSONObject leave_json = new JSONObject();
        try {

            JSONObject period = new JSONObject();
            period.put("from", from);
            period.put("from_duration", from_duration);
            period.put("to_duration", to_duration);
            period.put("to", to);
            leave_json.put("reason", new JSONObject().put("leave", reason));
            leave_json.put("period", period);
            leave_json.put("working", new JSONObject().put("days", working_days));
            leave_json.put("type", new JSONObject().put("leave", leave_type));
            leave_json.put("leave_reason", new JSONObject().put("id", leave_reason));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //dialogapply.cancel();
        Log.e(TAG + "apply_leave", String.valueOf(leave_json));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, leave_json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "apply_leave", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("apply_leave error", String.valueOf(volleyError));
                responseListener.onError(volleyError);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    public static void approve_reject_leave(final ResponseListener responseListener, final String leave_id, final String approve) {
        String url = serverUrl + "/api/v2/leaves/approve_leave";
        JSONObject work_days = new JSONObject();
        try {
            work_days.put("leave_id", leave_id);
            work_days.put("approve", approve);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG + "approve_reject_leave", String.valueOf(work_days));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, work_days, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "approve_reject_leave", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("appr_rej_leave error", String.valueOf(volleyError));
                responseListener.onError(volleyError);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static void get_alerts(final ResponseListener responseListener) {
        String url = serverUrl + "/api/v2/other_details/alerts";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "get_alerts", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("get_alerts error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void mark_read_alert(final ResponseListener responseListener, final String mark_read_id) {
        String url = serverUrl + "/api/v2/other_details/read_alert";
        JSONObject work_days = new JSONObject();
        try {
            work_days.put("alert_id", mark_read_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG + "mark_read_id", String.valueOf(work_days));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, work_days, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "mark_read_id", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("mark_read_id error", String.valueOf(volleyError));
                responseListener.onError(volleyError);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static void get_all_leave_details(final ResponseListener responseListener) {

        String url = AppController.serverUrl + "/api/v2/attendances/get_details";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "get_all_leave_details", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("getAllLeavDetails error", String.valueOf(volleyError));
                responseListener.onError(volleyError);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void in_time(final ResponseListener responseListener) {

        String url = AppController.serverUrl + "/api/v2/attendances/get_in_time";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "in_time", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("in_time error", String.valueOf(volleyError));
                responseListener.onError(volleyError);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void salary_slip(final ResponseListener responseListener) {

        String url = AppController.serverUrl + "/api/v2/other_details/salary_slip";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + " salary_slip response", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("salary_slip error", String.valueOf(volleyError));
                responseListener.onError(volleyError);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void emer_Check_token(final ResponseListener responseListener) {

        String url = serverUrl + "/api/v2/api_sessions/check_token";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                responseListener.onResponse(jsonObject);
                Log.e(TAG + "check_token", String.valueOf(jsonObject));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                responseListener.onError(volleyError);
                Log.e("check_token error", String.valueOf(volleyError));

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

    }

    public static void leave_details(final ResponseListener responseListener, final String leave_type) {

        String url = serverUrl + "/api/v2/leaves/all_records";
        JSONObject work_days = new JSONObject();
        try {
            work_days.put("typeleave", leave_type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        leave_deatil_array.clear();
        Log.e(TAG + "leave_details", String.valueOf(work_days));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, work_days, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "leave_details", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("leave_details error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static void employee_directory(final ResponseListener responseListener) {

        String url = serverUrl + "/api/v2/other_details/employee_directory";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "employee_directory", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("check_token error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void get_timesheet_records(final ResponseListener responseListener, final String status) {

        String url = serverUrl + "/api/v2/timesheets/get_records";
        JSONObject timesheet = new JSONObject();
        try {
            timesheet.put("type", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, timesheet, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + " " + status + "_timesheet", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG + " " + status + "_timesheet err", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void get_detailed_timesheet_record(final ResponseListener responseListener, final String id) {

        String url = serverUrl + "/api/v2/timesheets/get_detail";
        JSONObject timesheet = new JSONObject();
        try {
            timesheet.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, timesheet, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(" detailed_timesheet", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(" detailed_timesheet err", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void upload_timesheet_record(final ResponseListener responseListener, final String json_list) {

        String url = serverUrl + "/api/v2/timesheets/save_timesheet";
        JSONObject obj = new JSONObject();
        try {
            JSONArray timesheet_array = new JSONArray(json_list);
            obj.put("list", timesheet_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("upload_timesheet_obj", obj.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(" upload_timesheet_resp", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(" upload_timesheet err", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void list_vacancy(final ResponseListener responseListener) {

        String url = serverUrl + "/api/v2/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "vacancy", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("vacancy error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void list_candidate(final ResponseListener responseListener, final String id) {

        String url = serverUrl + "/api/v2/";
        JSONObject candidate = new JSONObject();
        try {
            candidate.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, candidate, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "candidate", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("candidate error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;


            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

    public static void errorFunction(Context context, VolleyError volleyError) {

        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
            message = getContext().getString(R.string.ServerError);
        } else if (volleyError instanceof AuthFailureError) {
            message = getContext().getString(R.string.AuthFailureError);
        } else if (volleyError instanceof ParseError) {
            message = getContext().getString(R.string.ParseError);
        } else if (volleyError instanceof NoConnectionError) {
            message = getContext().getString(R.string.NoConnectionError);
        } else if (volleyError instanceof TimeoutError) {
            message = getContext().getString(R.string.TimeoutError);
        }
        if (message != null)
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void outTime(final ResponseListener responseListener) {

        String url = serverUrl + "/api/v2/attendances/attendance_time_out";
        JSONObject timeout_details = new JSONObject();
        try {
            timeout_details.put("Address", loc);
            timeout_details.put("Latitude", lat);
            timeout_details.put("Longitude", lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, timeout_details, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "out_time", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("out_time error", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }

    public static void beatlog(final ResponseListener responseListener, float latitude, float longitude) {
        String url = serverUrl + "/api/v2/beat_logs";
        JSONObject request = new JSONObject();
        try {
            //request.put("user_id", "140110");
            request.put("lat", latitude);
            request.put("long", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("create beat log values", String.valueOf(request));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e(TAG + "Beat Log", String.valueOf(jsonObject));
                responseListener.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Beat Log", String.valueOf(volleyError));
                responseListener.onError(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public static void getbeatdata(final ResponseListener responseListener, String employee_id, String to_date, String from_date) {
        // String  REQUEST_TAG = "getBeatLogsRequest";
        String url = serverUrl + "/api/v2/beat_logs?employee_id=" + employee_id + "&to_date=" + to_date + "&from_date=" + from_date;
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        responseListener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(getClass().getName(), "Error: " + error.getMessage());
                        responseListener.onError(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", sharedpreferences.getString("token", ""));
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                headers.put("appVersion", appVersion);
                headers.put("apiVersion", apiVersion);
                return headers;
            }
        };
        jsonObjectReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectReq);

        //  VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
        mContext = this;
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        FontsOverride.setDefaultFont(this, "DEFAULT", "OpenSans-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "OpenSans-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "OpenSans-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "OpenSans-Regular.ttf");
        Log.e("test", "test");
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    //    public static void chat_bot(final Context context, final ChatActivity chatActivity, final String text) {
//        String url = null;
//        try {
//            url = chatbotUrl + "/bot?params=" + java.net.URLEncoder.encode(text, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        Log.e("chat url", url);
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                Log.e(TAG + "employee_directory", String.valueOf(jsonObject));
//                try {
//                    //String message = jsonObject.getString("message");
//                    if (jsonObject.getString("status").trim().equalsIgnoreCase("true")) {
//                        ChatDataType data = new ChatDataType();
//                        data.setChat(jsonObject.getJSONObject("data").getString("bot_response"));
//                        data.setSenderType(2);
//                        chat_data_array.add(data);
//                        chatActivity.updateadapter();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Log.e("chat_bot_error error", String.valueOf(volleyError));
//                errorFunction(context, volleyError);
//
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("token", sharedpreferences.getString("token", ""));

//                headers.put("appVersion", appVersion);
//                headers.put("apiVersion", apiVersion);
//                return headers;
//
//
//            }
//        };
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        AppController.getInstance().addToRequestQueue(request);
//    }
}