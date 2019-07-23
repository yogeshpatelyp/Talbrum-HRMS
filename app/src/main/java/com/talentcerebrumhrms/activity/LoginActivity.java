package com.talentcerebrumhrms.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.datatype.PeopleDataType;
import com.talentcerebrumhrms.fragment.PeopleFragment;
import com.talentcerebrumhrms.utils.AddHeaderInterceptor;
import com.talentcerebrumhrms.utils.ApiInterface;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;
import com.talentcerebrumhrms.utils.CustomLoadingDialog;
import com.talentcerebrumhrms.utils.SessionManager;
import com.talentcerebrumhrms.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.talentcerebrumhrms.utils.AppController.people_data_array;
import static com.talentcerebrumhrms.utils.AppController.serverUrl;

/**
 * Created by saransh on 29-09-2016.
 */

public class LoginActivity extends AppCompatActivity {

    private static Boolean isRunning = false;
    ImageView company_logo;
    Button login;
    EditText username, password;
    ImageButton visibility;
    TextView terms, company_name;
    CustomLoadingDialog dialoglogin;
    TextInputLayout textinputlayputemail, textinputlayputpassword;
    SharedPreferences sharedpreferences;
    SessionManager session;
    String pdfURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(LoginActivity.this);

        sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        company_logo = (ImageView) findViewById(R.id.company_logo);
        company_name = (TextView) findViewById(R.id.company_name);
        company_name.setText(sharedpreferences.getString("company_name", ""));
        login = (Button) findViewById(R.id.login);
        textinputlayputemail = (TextInputLayout) findViewById(R.id.input_layout_email);
        textinputlayputpassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        visibility = (ImageButton) findViewById(R.id.visibility_image);
        terms = (TextView) findViewById(R.id.terms);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        setCompanyLogo();

        visibility.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setSelection(password.getText().length());
                    visibility.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.visibility_full));
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setSelection(password.getText().length());
                    visibility.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.visibility_off));
                }
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Answers.getInstance().logCustom(new CustomEvent("Terms and Conditions Clicked"));
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(AppController.serverUrl + "/static/terms"));
                if (Utility.isAvailable(LoginActivity.this, i))
                    startActivity(i);
                else
                    Toast.makeText(LoginActivity.this, R.string.web_error, Toast.LENGTH_SHORT).show();
            }
        });


        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textinputlayputemail.setError(null);
                username.setError(null);
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textinputlayputpassword.setError(null);
                password.setError(null);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Answers.getInstance().logCustom(new CustomEvent("Login Clicked"));
                if (username.getText().toString().matches(""))
                    textinputlayputemail.setError(getString(R.string.enter_username));
                else if (password.getText().toString().matches(""))
                    textinputlayputpassword.setError(getString(R.string.enter_password));
                else
                    //loginApiCall();
               // for(int i = 0;i<=100;i++){
                    loginApiCallRetrofit();
               // }

            }
        });

        if(session.isLoggedIn())
        {
            session.checkLogin();

            HashMap<String, String> userlogin = session.getSaveCredentialSession();

            String email = userlogin.get(SessionManager.KEY_USER_EMAIL);
            String strpassword = userlogin.get(SessionManager.KEY_USER_PASS);

            username.setText(""+email);
            password.setText(""+strpassword);

        }
    }

    private void setCompanyLogo() {

        String company_logo_url = sharedpreferences.getString("company_logo_url", "");
        if (company_logo_url.length() > 0) {
            Glide.with(this)
                    .asBitmap()
                    .load(company_logo_url)
                    .apply(new RequestOptions().placeholder(R.drawable.talbrum_icon).error(R.drawable.talbrum_icon)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).signature(new ObjectKey(company_logo_url)))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            company_logo.setImageBitmap(Utility.resizeBitmap(resource, Utility.dpToPx(LoginActivity.this, 150)));
                        }
                    });
        } else {
            company_logo.setImageResource(R.drawable.talbrum_icon);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }

   /* private void loginApiCall() {

        dialoglogin = new CustomLoadingDialog(this, R.style.dialog_theme_custom, getString(R.string.logging_in));
        dialoglogin.show();
        //*******set background dim lvl
        dialoglogin.getWindow().setDimAmount(0.7f);
        dialoglogin.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialoglogin.setCancelable(false);

        AppController.login(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    Log.e("login_message", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("loggedin", true);
                        //editor.putString("username", username.getText().toString().trim());
                        //editor.putString("password", password.getText().toString().trim());
                        editor.putString("email", jsonObject.getJSONObject("data").getString("email"));
                        editor.putString("role", jsonObject.getJSONObject("data").getString("role"));
                        editor.putString("name", jsonObject.getJSONObject("data").getString("name"));
                        editor.putString("token", jsonObject.getString("token"));
                        editor.putString("passport_image", jsonObject.getJSONObject("data").getString("passport_photo"));
                        editor.putString("allow_attendance", jsonObject.getJSONObject("data").getString("allow_attendance"));
                        editor.putString("allow_employee_directory", jsonObject.getJSONObject("data").getString("allow_employee_directory"));
                        editor.putString("allow_timesheet", jsonObject.getJSONObject("data").getString("allow_timesheet"));
                        //editor.putString("last_updated_time", jsonObject.getJSONObject("data").getString("last_updated"));
                        editor.apply();

                        if (jsonObject.getJSONObject("data").getString("allow_employee_directory").equalsIgnoreCase("true"))
                            employeeDirectoryApiCall();
                        else {
                            dialoglogin.dismiss();
                            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i, bndlanimation);
                        }

                        //logging user information to crashlytics
                        Crashlytics.setUserIdentifier(jsonObject.getString("token"));
                        Crashlytics.setUserName(username.getText().toString().trim());
                    } else {
                        dialoglogin.dismiss();

                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(LoginActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        loginApiCall();
                    }
                });
                dialoglogin.dismiss();

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        }, username.getText().toString().trim(), password.getText().toString().trim());

    }*/


    private void loginApiCallRetrofit() {

        dialoglogin = new CustomLoadingDialog(this, R.style.dialog_theme_custom, getString(R.string.logging_in));
        dialoglogin.show();
        //*******set background dim lvl
        dialoglogin.getWindow().setDimAmount(0.7f);
        dialoglogin.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialoglogin.setCancelable(false);

        JSONObject login_details = new JSONObject();
        try {
            login_details.put("email", username.getText().toString().trim());
            login_details.put("password", password.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(login_details))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addNetworkInterceptor(new AddHeaderInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        ApiInterface request = retrofit.create(ApiInterface.class);

       // Call<JsonObject> call = ApiUtil.getServiceClass().login(myreqbody);
        Call<JsonObject> call = request.login(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());
                    String message = jsonObject.getString("message");
                    Log.e("login_message", message);
                    System.out.println("Login response "+jsonObject.toString());
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("loggedin", true);
                        //editor.putString("username", username.getText().toString().trim());
                        //editor.putString("password", password.getText().toString().trim());
                        editor.putString("email", jsonObject.getJSONObject("data").getString("email"));
                        editor.putString("role", jsonObject.getJSONObject("data").getString("role"));
                        editor.putString("name", jsonObject.getJSONObject("data").getString("name"));
                        editor.putString("token", jsonObject.getString("token"));
                        editor.putString("passport_image", jsonObject.getJSONObject("data").getString("passport_photo"));
                        editor.putString("allow_attendance", jsonObject.getJSONObject("data").getString("allow_attendance"));
                        editor.putString("allow_employee_directory", jsonObject.getJSONObject("data").getString("allow_employee_directory"));
                        editor.putString("allow_timesheet", jsonObject.getJSONObject("data").getString("allow_timesheet"));
                        editor.putString("latitude", jsonObject.optJSONObject("data").optString("latitude"));
                       editor.putString("longitude", jsonObject.optJSONObject("data").optString("longitude"));
                        if(jsonObject.getJSONObject("data").has("darpan_url")){
                            pdfURL = jsonObject.getJSONObject("data").optString("darpan_url");
                            editor.putString("darpan_url", jsonObject.getJSONObject("data").optString("darpan_url"));

                        }

                       editor.apply();

                        session.createSaveCredentialSession(username.getText().toString().trim(),password.getText().toString().trim());

                        if (jsonObject.getJSONObject("data").getString("allow_employee_directory").equalsIgnoreCase("true"))
                            employeeDirectoryApiCall();
                        else {
                            dialoglogin.dismiss();

                            if(pdfURL!=null){
                                if(pdfURL.contains("pdf")){
                                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                                    Intent i = new Intent(LoginActivity.this, ActivityPDFView.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i, bndlanimation);
                                }else{
                                    Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i, bndlanimation);
                                }
                            }else{
                                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i, bndlanimation);
                            }




                        }

                        //logging user information to crashlytics
                        Crashlytics.setUserIdentifier(jsonObject.getString("token"));
                        Crashlytics.setUserName(username.getText().toString().trim());
                    } else {
                        dialoglogin.dismiss();

                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            Log.d("Error",t.getMessage());
                Toast.makeText(LoginActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        loginApiCallRetrofit();
                    }
                });
                dialoglogin.dismiss();

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

    }

    /*private void employeeDirectoryApiCall() {

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

                        if (dialoglogin.isShowing())
                            dialoglogin.dismiss();
                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);

                    } else if (message.equalsIgnoreCase("Update your application")) {
                        if (dialoglogin.isShowing())
                            dialoglogin.dismiss();

                    } else {
                        if (dialoglogin.isShowing())
                            dialoglogin.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(LoginActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        employeeDirectoryApiCall();
                    }
                });
                if (dialoglogin.isShowing())
                    dialoglogin.dismiss();
                AlertDialog alertDialog = alertDialogBuilder.create();
                if (isRunning)
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

                        if (dialoglogin.isShowing())
                            dialoglogin.dismiss();

                        if(pdfURL!=null){
                            if(pdfURL.contains("pdf")){
                                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                                Intent i = new Intent(LoginActivity.this, ActivityPDFView.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i, bndlanimation);
                            }else{
                                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i, bndlanimation);
                            }
                        }else{
                            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LoginActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i, bndlanimation);
                        }


                    } else if (message.equalsIgnoreCase("Update your application")) {
                        if (dialoglogin.isShowing())
                            dialoglogin.dismiss();

                    } else {
                        if (dialoglogin.isShowing())
                            dialoglogin.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(LoginActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        employeeDirectoryApiCall();
                    }
                });
                if (dialoglogin.isShowing())
                    dialoglogin.dismiss();
                AlertDialog alertDialog = alertDialogBuilder.create();
                if (isRunning)
                    alertDialog.show();

            }
        });

    }
    private void userNotFound() {

        /*Bundle bndlanimation = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i, bndlanimation);
        AppController.getInstance().cancelPendingRequests(TAG);*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }
}
