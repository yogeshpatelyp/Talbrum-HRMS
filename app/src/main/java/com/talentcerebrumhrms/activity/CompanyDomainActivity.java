package com.talentcerebrumhrms.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.AddHeaderInterceptor;
import com.talentcerebrumhrms.utils.ApiInterface;
import com.talentcerebrumhrms.utils.AppController;
import com.talentcerebrumhrms.utils.CustomLoadingDialog;
import com.talentcerebrumhrms.utils.FragmentCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import retrofit2.converter.gson.GsonConverterFactory;


public class CompanyDomainActivity extends AppCompatActivity implements FragmentCallback {




    EditText company_url;
    Button next;
    TextInputLayout inputtexturl;
    CustomLoadingDialog dialoglogin;
    LinearLayoutCompat layout;
    RadioGroup rgdomain;
    RadioButton rbdomain,rb1,rb2;
    String strdomain="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_domain);

        company_url = (EditText) findViewById(R.id.company_url);
        next = (Button) findViewById(R.id.next);
        inputtexturl = (TextInputLayout) findViewById(R.id.input_layout_url);
        layout = (LinearLayoutCompat) findViewById(R.id.layout);
        rb1 = (RadioButton) findViewById(R.id.radioButton);
        rb2 = (RadioButton) findViewById(R.id.radioButton2);
        /*rgdomain = (RadioGroup) findViewById(R.id.radioGroup);
        rgdomain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                // int selectedId=rgYear.getCheckedRadioButtonId();
                rbdomain=(RadioButton)findViewById(checkedId);
                //  Toast.makeText(ActivityHolidays.this,"checked "+rbyear.getText().toString(),Toast.LENGTH_SHORT).show();

                if(rbdomain.getText().toString().equalsIgnoreCase("https")){
                    strdomain = "https://";
                }
                else{
                    strdomain = "http://";
                }

            }
        });*/

        company_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                inputtexturl.setError(null);
                company_url.setError(null);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (company_url.getText().toString().matches("")) {
                    inputtexturl.setError(getString(R.string.enter_company_domain));
                }else {

                    if(rb1.isChecked()){
                        strdomain="http://";
                        CompanyDomainApiCallRetrofit(company_url.getText().toString());
                    }else{
                        if(rb2.isChecked()){
                            strdomain="https://";
                            CompanyDomainApiCallRetrofit(company_url.getText().toString());
                        }
                        else{
                            Toast.makeText(CompanyDomainActivity.this,"Please select Domain",Toast.LENGTH_SHORT).show();
                        }
                    }

                    //CompanyDomainApiCall();

                }
            }
        });

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

   /* private void CompanyDomainApiCall() {

        dialoglogin = new CustomLoadingDialog(this, R.style.dialog_theme_custom, getString(R.string.proceeding));
        dialoglogin.show();
        //*******set background dim lvl
        dialoglogin.getWindow().setDimAmount(0.7f);
        dialoglogin.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialoglogin.setCancelable(false);

        AppController.company_domain(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    dialoglogin.dismiss();
                    Log.e("message", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        AppController.serverUrl = "http://" + company_url.getText().toString().trim();

                        SharedPreferences sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("company_url", AppController.serverUrl);
                        editor.putString("company_logo_url", jsonObject.getString("logo_url"));
                        editor.putString("company_name", jsonObject.getString("company_name"));
                        editor.apply();

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(CompanyDomainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(CompanyDomainActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);

                    } else if (message.equalsIgnoreCase("Update your application")) {

                        updateUI();

                    } else {

                        Toast.makeText(CompanyDomainActivity.this, message, Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(CompanyDomainActivity.this, R.string.check_internet_and_server, Toast.LENGTH_SHORT).show();
                dialoglogin.dismiss();

            }
        }, company_url.getText().toString().trim());
    }*/

    private void CompanyDomainApiCallRetrofit(String companyURL) {
        System.out.println("DOMAIN : "+strdomain);
        dialoglogin = new CustomLoadingDialog(this, R.style.dialog_theme_custom, getString(R.string.proceeding));
        dialoglogin.show();
        //*******set background dim lvl
        dialoglogin.getWindow().setDimAmount(0.7f);
        dialoglogin.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        dialoglogin.setCancelable(false);
        JSONObject company_details = new JSONObject();
        try {
            company_details.put("company_url", companyURL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addNetworkInterceptor(new AddHeaderInterceptor());
        //httpClient.addInterceptor(new LogJsonInterceptor());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(strdomain + companyURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        ApiInterface request = retrofit.create(ApiInterface.class);
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(company_details))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<JsonObject> call = request.company_domain(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null){
                    if(jObject.code()==200){

                        try {
                            // System.out.println("jObject.body().toString() "+jObject.body().toString());
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());
                            String message = jsonObject.getString("message");
                            dialoglogin.dismiss();
                            Log.e("message RETROFIT", message);
                            Log.e("jObj message RETROFIT", jsonObject.toString());
                            if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                                AppController.serverUrl = strdomain + company_url.getText().toString().trim();

                                SharedPreferences sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString("company_url", AppController.serverUrl);
                                editor.putString("company_logo_url", jsonObject.getString("logo_url"));
                                editor.putString("company_name", jsonObject.getString("company_name"));
                                editor.apply();

                                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(CompanyDomainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                                Intent i = new Intent(CompanyDomainActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i, bndlanimation);

                            } else if (message.equalsIgnoreCase("Update your application")) {

                                updateUI();

                            } else {

                                Toast.makeText(CompanyDomainActivity.this, message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                else{
                    dialoglogin.dismiss();
                    Toast.makeText(CompanyDomainActivity.this, "something went wrong please try again", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error RETROFIT",t.getMessage());
                Toast.makeText(CompanyDomainActivity.this, R.string.check_internet_and_server, Toast.LENGTH_SHORT).show();
                dialoglogin.dismiss();

            }
        });
    }
}
