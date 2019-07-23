package com.talentcerebrumhrms.utils;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.activity.LoginActivity;
import com.talentcerebrumhrms.activity.MainActivity;
import com.talentcerebrumhrms.fragment.AttendanceFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;

/**
 * Created by Belal on 1/27/2017.
 */

public class NetworkStateCheckerOutTime extends BroadcastReceiver {

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
                Cursor cursor = db.getUnsyncedOuttime();
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
                        DATE =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_OUTTIME));

                        if(DATE.equalsIgnoreCase(mdate)) {
                            System.out.println("DATE if : "+DATE);
                            ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_OUTTIME));
                            //ATDSTATUS =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTANDANCE_STATUS));
                            ADDRESS =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Address_OUTTIME));
                            LAT =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Latitude_OUTTIME));
                            LON =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_Longitude_OUTTIME));
                            TIME =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME_OUTTIME));
                            TOKEN =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TOKEN_OUTTIME));
                            //IMAGEDATA =cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));
                            DATE =cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_OUTTIME));

                            if(TIME.equalsIgnoreCase("null")){
                                // System.out.println("TIME IFF: "+TIME);
                                // System.out.println("DATE : IFF: "+DATE);
                            }else{
                                outTimeApiCall();
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
                            System.out.println("NETWORK STATE CHECKER CALLED :: "+cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS_OUTTIME)) );

                        }
                        else{
                            ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_OUTTIME));
                            System.out.println("DATE else : "+DATE);
                            db.deleteouttime(ID);

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

    private void outTimeApiCall() {
        JSONObject timeout_details = new JSONObject();
        try {
            timeout_details.put("Address", ADDRESS);
            timeout_details.put("Latitude", LAT);
            timeout_details.put("Longitude", LON);
            timeout_details.put("date", DATE);
            timeout_details.put("time", TIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(timeout_details))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "timeout_details", String.valueOf(timeout_details));
        Call<JsonObject> call = ApiUtil.getServiceClass().outTime(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null) {
                    if (jObject.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());
                            String message = jsonObject.getString("message");
                            System.out.println("OUT TIME API CALL  " + jsonObject.toString());
                            //Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();

                            if (jsonObject.getBoolean("status")) {
                                if (AppController.attendanceFragmentDataType != null)
                                    AppController.attendanceFragmentDataType.setOutTime(jsonObject.getJSONObject("data").getString("out_time"));
                                    MainActivity.isouttimemarked = true;
                                 AttendanceFragment.refresh();
                                System.out.println("NETWORK :: ATTANDANCE SYNCED "+ID);
                                db.updateOuttimeStatus(ID, MainActivity.OUT_TIME_SYNCED_WITH_SERVER);
                                System.out.println("NETWORK :: UPDATED "+ID);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST_OUTTIME));
                                //todo add when API ready
                                //askForFeedback();
                      /* if (isServicesOK()) {

                            Intent locationIntent = new Intent(MainActivity.this, ActivityGeoFenceMap.class);
                            startActivity(locationIntent);
                        }*/

                            }
                            if (message.equalsIgnoreCase("User not found")) {
                                userNotFound();
                                //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "attendance_check");
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

                //Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
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
