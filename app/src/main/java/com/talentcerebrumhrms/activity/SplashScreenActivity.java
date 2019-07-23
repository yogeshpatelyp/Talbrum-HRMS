package com.talentcerebrumhrms.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.datatype.PeopleDataType;
import com.talentcerebrumhrms.fragment.PeopleFragment;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;
import com.talentcerebrumhrms.utils.FragmentCallback;
import com.talentcerebrumhrms.utils.Utility;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.appVersion;
import static com.talentcerebrumhrms.utils.AppController.people_data_array;
import static com.talentcerebrumhrms.utils.AppController.serverUrl;

/**
 * Created by saransh on 29-09-2016.
 * Splash Screen functioning and token check(api call) to see if user is logged in or not.
 */

public class SplashScreenActivity extends AppCompatActivity implements FragmentCallback {

    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    int SPLASH_TIME_OUT = 2000;
    SharedPreferences sharedpreferences;
    ImageView img;
    Boolean activityRunning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        super.onCreate(savedInstanceState);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
            //appVersion = "1.39";
            Log.e(TAG, "version =====> " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateformat.format(c.getTime());
        StringTokenizer tk = new StringTokenizer(datetime);
        final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
        final String mtime = tk.nextToken();
        System.out.println("DATE : "+mdate);
        System.out.println("TIME : "+mtime);
         activityRunning = true;
        sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        serverUrl = sharedpreferences.getString("company_url", null);

//        ChatDataType a = new ChatDataType();
//        a.setChat(getString(R.string.chat_default));
//        a.setSenderType(9);
//        AppController.chat_data_array.add(a);
        img = (ImageView) findViewById(R.id.load);
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim.setZAdjustment(Animation.ZORDER_TOP);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1000);
        img.setAnimation(anim);
        img.startAnimation(anim);
        service();

    }


    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }
    private void service() {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (serverUrl != null)
                    emerTokenApiCall();
                else {
                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                    Intent i = new Intent(SplashScreenActivity.this, CompanyDomainActivity.class);
                    startActivity(i, bndlanimation);
                    SplashScreenActivity.this.finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void updateUI() {
        final Dialog dialog = new Dialog(this, R.style.theme_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();


        if (window != null) {
            lp.copyFrom(window.getAttributes());
        }

        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.5f;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.dialog_update_application);
        dialog.setCancelable(false);  //false to set cancelable false

        dialog.show();
        if (window != null) {
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("splash_screen", "backpressed");
        AppController.getInstance().cancelPendingRequests(AppController.class.getSimpleName());
    }

   /* private void emerTokenApiCall() {
        AppController.emer_Check_token(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {
                try {
                    String message = jsonObject.getString("message");
                    Log.e("emer_token_check", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("allow_attendance", jsonObject.getString("allow_attendance"));
                        editor.putString("allow_employee_directory", jsonObject.getString("allow_employee_directory"));
                        editor.putString("allow_timesheet", jsonObject.getString("allow_timesheet"));
                        //editor.putString("last_updated_time", jsonObject.getString("last_updated"));
                        editor.apply();

                        String allow_employee_directory = sharedpreferences.getString("allow_employee_directory", "");
                        if (allow_employee_directory.equalsIgnoreCase("true"))
                            employeeDirectoryApiCall();
                        else {
                            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(i, bndlanimation);
                            SplashScreenActivity.this.finish();
                        }
                    } else if (message.equalsIgnoreCase("Update your application")) {

                        updateUI();

                    } else {
                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();

                        String company_url = sharedpreferences.getString("company_url", null);
                        Intent i;
                        if (company_url == null)
                            i = new Intent(SplashScreenActivity.this, CompanyDomainActivity.class);
                        else
                            i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(i, bndlanimation);
                        SplashScreenActivity.this.finish();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                Toast.makeText(SplashScreenActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashScreenActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        emerTokenApiCall();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                if (activityRunning)
                    alertDialog.show();

            }
        });
    }*/

    private void emerTokenApiCall() {
        Call<JsonObject> call = ApiUtil.getServiceClass().emer_Check_token();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    Log.e("emer_token_check", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("allow_attendance", jsonObject.getString("allow_attendance"));
                        editor.putString("allow_employee_directory", jsonObject.getString("allow_employee_directory"));
                        editor.putString("allow_timesheet", jsonObject.getString("allow_timesheet"));
                        //editor.putString("last_updated_time", jsonObject.getString("last_updated"));
                        editor.apply();

                        String allow_employee_directory = sharedpreferences.getString("allow_employee_directory", "");
                        if (allow_employee_directory.equalsIgnoreCase("true"))
                            employeeDirectoryApiCall();
                        else {
                            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(i, bndlanimation);
                            SplashScreenActivity.this.finish();
                        }
                    } else if (message.equalsIgnoreCase("Update your application")) {

                        updateUI();

                    } else {
                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();

                        String company_url = sharedpreferences.getString("company_url", null);
                        Intent i;
                        if (company_url == null)
                            i = new Intent(SplashScreenActivity.this, CompanyDomainActivity.class);
                        else
                            i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(i, bndlanimation);
                        SplashScreenActivity.this.finish();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
                Toast.makeText(SplashScreenActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashScreenActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        emerTokenApiCall();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                if (activityRunning)
                    alertDialog.show();

            }
        });
    }


   /* private void employeeDirectoryApiCall() {

        AppController.employee_directory(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    Log.e("employee_directory", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        people_data_array.clear();
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            PeopleDataType a = new PeopleDataType();
                            a.setPhoto(jsonObject.getJSONArray("data").getJSONObject(i).getString("photo"));
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("name"));
                            a.setPhone(jsonObject.getJSONArray("data").getJSONObject(i).getString("phone"));
                            a.setEmail(jsonObject.getJSONArray("data").getJSONObject(i).getString("email_id"));
                            a.setDesignation(jsonObject.getJSONArray("data").getJSONObject(i).getString("designation"));
                            a.setDivision(jsonObject.getJSONArray("data").getJSONObject(i).getString("division"));
                            people_data_array.add(a);
                        }
                        Utility.getSortedPeople();
                        PeopleFragment.updateadapter();

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(i, bndlanimation);
                        SplashScreenActivity.this.finish();
                    } else if (message.equalsIgnoreCase("Update your application")) {

                        updateUI();

                    } else {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(i, bndlanimation);
                        SplashScreenActivity.this.finish();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(SplashScreenActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashScreenActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        employeeDirectoryApiCall();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                if (activityRunning)
                    alertDialog.show();

            }
        });


    }*/


   private void employeeDirectoryApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().employee_directory();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    Log.e("employee_directory", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        people_data_array.clear();
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            PeopleDataType a = new PeopleDataType();
                            a.setPhoto(jsonObject.getJSONArray("data").getJSONObject(i).getString("photo"));
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("name"));
                            a.setPhone(jsonObject.getJSONArray("data").getJSONObject(i).getString("phone"));
                            a.setEmail(jsonObject.getJSONArray("data").getJSONObject(i).getString("email_id"));
                            a.setDesignation(jsonObject.getJSONArray("data").getJSONObject(i).getString("designation"));
                            a.setDivision(jsonObject.getJSONArray("data").getJSONObject(i).getString("division"));
                            people_data_array.add(a);
                        }
                        Utility.getSortedPeople();
                        PeopleFragment.updateadapter();

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(i, bndlanimation);
                        SplashScreenActivity.this.finish();
                    } else if (message.equalsIgnoreCase("Update your application")) {

                        updateUI();

                    } else {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(SplashScreenActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(i, bndlanimation);
                        SplashScreenActivity.this.finish();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(SplashScreenActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SplashScreenActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        employeeDirectoryApiCall();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                if (activityRunning)
                    alertDialog.show();

            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityRunning = false;
        /*
        if (alertDialog.isShowing() && alertDialog != null) {
            alertDialog.dismiss();
        }

        if (alertDialog1.isShowing() && alertDialog1 != null) {
            alertDialog1.dismiss();
        }
        */
    }
}
