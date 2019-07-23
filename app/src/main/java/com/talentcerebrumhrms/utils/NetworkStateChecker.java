package com.talentcerebrumhrms.utils;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.activity.LoginActivity;
import com.talentcerebrumhrms.activity.MainActivity;
import com.talentcerebrumhrms.fragment.AttendanceFragment;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;
import static com.talentcerebrumhrms.utils.AppController.apiVersion;
import static com.talentcerebrumhrms.utils.AppController.appVersion;
import static com.talentcerebrumhrms.utils.AppController.attendanceFragmentDataType;
import static com.talentcerebrumhrms.utils.AppController.serverUrl;
import static com.talentcerebrumhrms.utils.AppController.sharedpreferences;

/**
 * Created by Belal on 1/27/2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    String ATDSTATUS,ADDRESS,LAT,LON,TIME="null",DATE,TOKEN;
    int ID;
   //byte [] IMAGEDATA  =Base64.decode("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", Base64.DEFAULT);
   byte [] IMAGEDATA  =null;
    // Bitmap bm = null;
   // SharedPreferences sharedpreferences;
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        System.out.println("NETWORK STATE CHECKER CALLED 1 :: ");
        db = new DatabaseHelper(context);
     //   sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                System.out.println("NETWORK STATE CHECKER CALLED 2 :: ");
                //getting all the unsynced names
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String datetime = dateformat.format(c.getTime());
                StringTokenizer tk = new StringTokenizer(datetime);

                final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
                final String mtime = tk.nextToken();
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        System.out.println("NETWORK STATE CHECKER CALLED 3 :: ");
                        //calling the method to save the unsynced name to MySQL
                       /* saveName(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTANDANCE_STATUS)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Address)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Latitude)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Longitude)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME)),
                                cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE))
                        );*/
                                 DATE =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));

                          if(DATE.equalsIgnoreCase(mdate)) {
                            System.out.println("DATE if : "+DATE);
                            ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                            ATDSTATUS =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTANDANCE_STATUS));
                            ADDRESS =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Address));
                            LAT =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Latitude));
                            LON =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Longitude));
                            TIME =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME));
                            TOKEN =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN));
                            IMAGEDATA =cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));
                              DATE =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));

                              if(TIME.equalsIgnoreCase("null")){
                                 // System.out.println("TIME IFF: "+TIME);
                                 // System.out.println("DATE : IFF: "+DATE);
                            }else{
                                new UploadTask().execute();
                                 // System.out.println("TIME : ELSEE "+TIME);
                                 // System.out.println("DATE : ELSEE "+DATE);
                            }
                              System.out.println("ID : "+ID);
                              System.out.println("TIME : "+TIME);
                              System.out.println("DATE : "+DATE);
                              System.out.println("TOKEN : "+TOKEN);
                              System.out.println("ADDRESS : "+ADDRESS);
                              System.out.println("LAT : "+LAT);
                              System.out.println("LON : "+LON);
                              System.out.println("NETWORK STATE CHECKER CALLED :: "+cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)) );

                          }
                        else{
                              ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                            System.out.println("DATE else : "+DATE);
                              db.delete(ID);

                        }

                    } while (cursor.moveToNext());
                }
            }
        }
    }

    /*
    * method taking two arguments
    * name that is to be saved and id of the name from SQLite
    * if the name is successfully sent
    * we will update the status as synced in SQLite
    * */
  /*  private void saveName(final int id, final String attandancestatus, final String address, final String lat, final String lon, final String time,final  byte[] image) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
               // params.put("name", name);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }*/

         class UploadTask extends AsyncTask<Bitmap, Integer, Void> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMax(100);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            protected Void doInBackground(Bitmap... bitmaps) {
                Calendar c = Calendar.getInstance();

                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String datetime = dateformat.format(c.getTime());
                StringTokenizer tk = new StringTokenizer(datetime);

                final String mdate = tk.nextToken();  // <---  yyyy-mm-dd

                /*if (bitmaps[0] == null)
                    return null;
                Bitmap bitmap = bitmaps[0];
                Log.v("pic size before", "" + bitmap.getByteCount() + " B");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
                byte[] data = stream.toByteArray();*/
                System.out.println("NETWORK STATE CHECKER CALLED 4:: ");
                Log.v("pic size after", "" + IMAGEDATA.length + " B");
                ByteArrayInputStream input = new ByteArrayInputStream(IMAGEDATA);

                try {
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    ByteArrayBody bab = new ByteArrayBody(IMAGEDATA, System.currentTimeMillis() + ".png");
                    builder.addPart("myFile", bab);
                    builder.addPart("status", new StringBody("present", ContentType.TEXT_PLAIN));
                    // Log.v("location GPS",loc);
                    builder.addPart("Address", new StringBody(ADDRESS, ContentType.TEXT_PLAIN));
                    builder.addPart("Latitude", new StringBody(String.valueOf(LAT), ContentType.TEXT_PLAIN));
                    builder.addPart("Longitude", new StringBody(String.valueOf(LON), ContentType.TEXT_PLAIN));
                    builder.addPart("date", new StringBody(String.valueOf(mdate), ContentType.TEXT_PLAIN));
                    builder.addPart("time", new StringBody(String.valueOf(TIME), ContentType.TEXT_PLAIN));
                    HttpEntity entity = builder.build();
                    Log.v("http_entity", "" + entity);

                    URL url = new URL(serverUrl + "/api/v2/attendances/mark_attendance");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("token", TOKEN);
                    conn.setRequestProperty("appVersion", appVersion);
                    conn.setRequestProperty("apiVersion", apiVersion);
                    conn.setRequestProperty("Accept", "application/json");
//                conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Length", "" + reqEntity.getContentLength());
//                conn.setRequestProperty("Content-Language", "en-US");
                    conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    //conn.connect();

                    long len = entity.getContentLength();
                    Log.v("Full Data Size", "" + len + " B");
                    long total = 0;
                    int count;

                    while ((count = input.read(IMAGEDATA)) != -1) {
                        total += count;
                        Log.v("total", "" + total);
                        publishProgress((int) ((total * 100) / len));
                    }

                    entity.writeTo(conn.getOutputStream());

                    final int responseCode = conn.getResponseCode();
                    Log.v("response Code", "" + responseCode);
                    Log.v("response error", "" + conn.getErrorStream());
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder("");
                        String line;
                        while ((line = in.readLine()) != null)
                            sb.append(line);
                        in.close();
                        final JSONObject response_server = new JSONObject(sb.toString());

                        Log.v("response String", "" + sb);

                                try {
                                    if (response_server.getString("status").equalsIgnoreCase("true")) {
                                       // Toast.makeText(context, R.string.attendance_marked, Toast.LENGTH_SHORT).show();
                                        Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " present successful"));

                                       System.out.println("NETWORK :: ATTANDANCE SYNCED "+ID);
                                        db.updateNameStatus(ID, MainActivity.IN_TIME_SYNCED_WITH_SERVER);
                                        System.out.println("NETWORK :: UPDATED "+ID);

                                        //sending the broadcast to refresh the list
                                        context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));

                                    } else {
                                       // db.delete(ID);
                                        System.out.println("NETWORK :: ATTANDANCE NOT SYNCED");
                                       // Toast.makeText(context, R.string.attendance_mark_error, Toast.LENGTH_SHORT).show();

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

               /* try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                progressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Void result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);

                if (progressDialog != null)
                    if (progressDialog.isShowing())
                        progressDialog.cancel();
            if (MainActivity.dialog != null) {
                if (MainActivity.dialog.isShowing())
                    MainActivity.dialog.cancel();
            }
            inTimeApiCall();
            }


        }

    private void inTimeApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().in_time();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null) {
                    if (jObject.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());

                            String message = jsonObject.getString("message");
                            if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                                if (AppController.attendanceFragmentDataType != null) {
                                    attendanceFragmentDataType.setInTime(jsonObject.getJSONObject("data").getString("in_time"));
                                    attendanceFragmentDataType.setOutTime(jsonObject.optJSONObject("data").optString("out_time"));
                                    if(jsonObject.optJSONObject("data").has("out_time")){
                                        MainActivity.isouttimemarked = true;
                                        System.out.println("ISOUTTIMEMARKED : "+jsonObject.toString());
                                    }
                                    MainActivity.isoffline = false;
                                    MainActivity.isintimemarked = true;
                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String datetime = dateformat.format(c.getTime());
                                    StringTokenizer tk = new StringTokenizer(datetime);

                                    final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
                                    final String mtime = tk.nextToken();
                                    Cursor cursorouttime = db.getUnsyncedOuttime();

                                    if (cursorouttime.moveToFirst()) {
                                        do {
                                            String DATE = cursorouttime.getString(cursorouttime.getColumnIndex(DatabaseHelper.COLUMN_DATE_OUTTIME));
                                            if(DATE.equalsIgnoreCase(mdate)){
                                                Log.v("onResume DATE", "IF");
                                                int  ID = cursorouttime.getInt(cursorouttime.getColumnIndex(DatabaseHelper.COLUMN_ID_OUTTIME));
                                                context.registerReceiver(new NetworkStateCheckerOutTime(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                                                BroadcastReceiver broadcastReceiver_OUTTIME = new BroadcastReceiver() {
                                                    @Override
                                                    public void onReceive(Context context, Intent intent) {

                                                        //loading the names again
                                                        // loadNames();
                                                    }
                                                };
                                                // saveNameToLocalStorage("present","khandwa",String.valueOf("21.8090"),String.valueOf("76.3691"),"12:20",null, NAME_NOT_SYNCED_WITH_SERVER);
                                                context.registerReceiver(broadcastReceiver_OUTTIME, new IntentFilter(MainActivity.DATA_SAVED_BROADCAST_OUTTIME));
                                                //markAttendanceCheckApiCall();

                                            }else{
                                                Log.v("onResume DATE", "ELSE");
                                                int  ID = cursorouttime.getInt(cursorouttime.getColumnIndex(DatabaseHelper.COLUMN_ID_OUTTIME));
                                                db.deleteouttime(ID);
                                                //markAttendanceCheckApiCall();
                                            }

                                        } while (cursorouttime.moveToNext());
                                    }
                                }
                                //null when calling from mark attendance

                                //null when calling from mark attendance
                                Log.v("check in_time", "" + jsonObject.getJSONObject("data").getString("in_time"));
                                Log.v("check out_time", "" + jsonObject.getJSONObject("data").getString("out_time"));
                                AttendanceFragment.refresh();

                                //CALL GEOFENCE HERE
                       /* if (isServicesOK()) {

                            Intent locationIntent = new Intent(context, ActivityGeoFenceMap.class);
                            context.startActivity(locationIntent);

                        }*/
                            }
                            if (message.equalsIgnoreCase("User not found")) {

                                userNotFound();
                                // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                            }
                            //attendanceFragment null when calling from mark attendance no need to call salary slip then.
                        } catch (JSONException e) {
                            salarySlipApiCall();
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
                //  errorFunction(MainActivity.this, volleyError);
            }
        });

    }
    private void salarySlipApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().salary_slip();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null) {
                    if (jObject.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());

                            String message = jsonObject.getString("message");
                            if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                                if (AppController.attendanceFragmentDataType != null) {
                                    attendanceFragmentDataType.setIncome_tax(jsonObject.getJSONObject("data").getString("income_tax"));
                                    attendanceFragmentDataType.setProfessional_tax(jsonObject.getJSONObject("data").getString("professional_tax"));
                                    attendanceFragmentDataType.setTotal_deduction(jsonObject.getJSONObject("data").getString("total_deduction_amount"));
                                    attendanceFragmentDataType.setNet_credit(jsonObject.getJSONObject("data").getString("net_credit"));
                                    attendanceFragmentDataType.setSalaryRange(jsonObject.getJSONObject("data").getString("from_date") + " - " + jsonObject.getJSONObject("data").getString("to_date"));
                                }
                                AttendanceFragment.refresh();
                            }
                            if (message.equalsIgnoreCase("User not found")) {

                                userNotFound();
                                // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                // errorFunction(MainActivity.this, volleyError);
            }
        });
    }
    private void userNotFound() {

        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(context, R.anim.anim_out, R.anim.anim_in).toBundle();
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i, bndlanimation);
        AppController.getInstance().cancelPendingRequests(TAG);

    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
          //  Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(context, available, ERROR_DIALOG_REQUEST);
          //  dialog.show();
        }else{
            Toast.makeText(context, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
