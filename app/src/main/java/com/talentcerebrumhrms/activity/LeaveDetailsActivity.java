package com.talentcerebrumhrms.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.adapter.LeaveDetailAdapter;
import com.talentcerebrumhrms.datatype.AlertDataType;
import com.talentcerebrumhrms.datatype.LeaveAppliedDataType;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;
import static com.talentcerebrumhrms.utils.AppController.alert_data_array;
import static com.talentcerebrumhrms.utils.AppController.alert_size;
import static com.talentcerebrumhrms.utils.AppController.leave_deatil_array;

/**
 * Created by saransh on 05-11-2016.
 */

public class LeaveDetailsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    static LeaveDetailAdapter leaveDetailAdapter;
    static EditText search_edittext;
    static ArrayList<LeaveAppliedDataType> dataArray;
    ListView list;
    SwipeRefreshLayout swipe_refresh;
    String leave_id;
    LinearLayout searchbar;
    Toolbar toolbar;
    ImageButton search_back_button, search_remove_button;
    private SwipeRefreshLayout mEmptyViewContainer;

    public static void updateadapter() {
        dataArray = new ArrayList<>(leave_deatil_array);
        leaveDetailAdapter.updateAdapter(dataArray);
        if (!search_edittext.getText().toString().equalsIgnoreCase("")) {
            String text = search_edittext.getText().toString().toLowerCase(Locale.getDefault());
            leaveDetailAdapter.filter(text);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_detail);
        list = (ListView) findViewById(R.id.leave_detail_listview);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.leave_detail_swipe);
        mEmptyViewContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);
        search_back_button = (ImageButton) findViewById(R.id.search_back_button);
        search_remove_button = (ImageButton) findViewById(R.id.search_remove_button);
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        searchbar = (LinearLayout) findViewById(R.id.search_layout);

        search_back_button.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        search_remove_button.setColorFilter(ContextCompat.getColor(this, R.color.grey), PorterDuff.Mode.SRC_ATOP);


        dataArray = new ArrayList<>(leave_deatil_array);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10.0f);
        }
        Intent i = getIntent();
        Bundle use = i.getExtras();
        leave_id = use.getString("leave_id");
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(use.getString("leave_type"));


        Log.e("leave_id leaDetActi", leave_id);
        getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        leaveDetailAdapter = new LeaveDetailAdapter(this, dataArray);
        list.setEmptyView(mEmptyViewContainer);
        list.setAdapter(leaveDetailAdapter);

        mEmptyViewContainer.setOnRefreshListener(this);
        swipe_refresh.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(LeaveDetailsActivity.this, R.color.colorAccent)
        );
        swipe_refresh.setColorSchemeColors(

                ContextCompat.getColor(LeaveDetailsActivity.this, R.color.colorAccent)
        );

        swipe_refresh.post(new Runnable() {
                               @Override
                               public void run() {
                                   swipe_refresh.setRefreshing(true);
                                   mEmptyViewContainer.setRefreshing(false);
                                   getAlertsApiCall();
                                   leaveDetailsApiCall(leave_id);
                                   //fetch data
                               }
                           }
        );


        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search_edittext.getText().toString().toLowerCase(Locale.getDefault());
                Log.e("Leavedeatailsactivity", "after text changed ");
                leaveDetailAdapter.filter(text);
            }
        });
        search_remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                search_edittext.setText("");

            }
        });

        search_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                // Log.e("ontouch", "true");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_edittext.getWindowToken(), 0);
                return false;
            }
        });

    }

    @Override
    public void onRefresh() {
        swipe_refresh.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        leaveDetailsApiCall(leave_id);
        if (toolbar.getVisibility() == View.GONE) {
            showToolbar();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
            case R.id.search:
                removeToolbar(item);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (toolbar.getVisibility() == View.GONE) {

            showToolbar();

        } else
            super.onBackPressed();
        overridePendingTransition(R.anim.finish_in, R.anim.finish_out);

    }

    @Override
    protected void onStop() {
        super.onStop();
        leave_deatil_array.clear();
    }

    private void showToolbar() {

        //animation fade in and drop down
        AnimationSet anim = new AnimationSet(true);
        Animation animate = new TranslateAnimation(0, 0, 0, toolbar.getTop());
        animate.setDuration(200);
        // animate.setFillAfter(true);
        anim.addAnimation(animate);
        Animation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(200);
        anim.addAnimation(fade);
        search_edittext.setText("");

        toolbar.startAnimation(anim);
        toolbar.setVisibility(View.VISIBLE);
        searchbar.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search_edittext.getWindowToken(), 0);
    }

    private void removeToolbar(final MenuItem menuItem) {
        //animation up and fade out
        AnimationSet anim = new AnimationSet(true);
        Animation animate = new TranslateAnimation(0, 0, 0, -toolbar.getHeight());
        animate.setDuration(200);
        anim.addAnimation(animate);
        Animation fade = new AlphaAnimation(1.0f, 0.0f);
        fade.setDuration(200);


        anim.addAnimation(fade);
        toolbar.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //   Log.e("animation rm listener","start");
                menuItem.setEnabled(false);
                // AlertActivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Log.e("animation rm listener","end");
                menuItem.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Log.e("animation rm listener","repeat");
            }
        });
        toolbar.setVisibility(View.GONE);
        searchbar.setVisibility(View.VISIBLE);
        search_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search_edittext, InputMethodManager.SHOW_IMPLICIT);
    }

   /* private void getAlertsApiCall() {

        AppController.get_alerts(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    alert_data_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("message_alert").length(); i++) {
                            AlertDataType a = new AlertDataType();
                            if (!jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getBoolean("read")) {
                                a.setRead(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("read"));
                                a.setSender_name(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_name"));
                                a.setReceiver_name(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("receiver_name"));
                                a.setCreated_date(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("created_at"));
                                a.setAlert_type(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("alert_type"));
                                a.setFlag(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("flag"));
                                a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
                                a.setDescription(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("description"));
                                a.setId(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("id"));
                                alert_data_array.add(a);
                            }
                        }
                        alert_size = alert_data_array.size();
                        Log.e("message leave approve", message);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        alert_size = 0;
                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leave_approve");

                    } else {

                        alert_size = 0;
                        //   Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    }
                    if (swipe_refresh != null)
                        swipe_refresh.setRefreshing(false);


                    try {
                        Log.e("alert invalidate", "true");
                        invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    AlertActivity.updateadapter();
                } catch (Exception e) {
                    if (swipe_refresh != null)
                        swipe_refresh.setRefreshing(false);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                if (swipe_refresh != null)
                    swipe_refresh.setRefreshing(false);
                errorFunction(LeaveDetailsActivity.this, volleyError);
            }
        });

    }*/

    private void getAlertsApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().get_alerts();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());
                    alert_data_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("message_alert").length(); i++) {
                            AlertDataType a = new AlertDataType();
                            if (!jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getBoolean("read")) {
                                a.setRead(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("read"));
                                a.setSender_name(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_name"));
                                a.setReceiver_name(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("receiver_name"));
                                a.setCreated_date(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("created_at"));
                                a.setAlert_type(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("alert_type"));
                                a.setFlag(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("flag"));
                                a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
                                a.setDescription(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("description"));
                                a.setId(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("id"));
                                alert_data_array.add(a);
                            }
                        }
                        alert_size = alert_data_array.size();
                        Log.e("message leave approve", message);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        alert_size = 0;
                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leave_approve");

                    } else {

                        alert_size = 0;
                        //   Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    }
                    if (swipe_refresh != null)
                        swipe_refresh.setRefreshing(false);


                    try {
                        Log.e("alert invalidate", "true");
                        invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    AlertActivity.updateadapter();
                } catch (Exception e) {
                    if (swipe_refresh != null)
                        swipe_refresh.setRefreshing(false);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                if (swipe_refresh != null)
                    swipe_refresh.setRefreshing(false);
              //  errorFunction(LeaveDetailsActivity.this, volleyError);
            }
        });

    }

    private void leaveDetailsApiCall(final String leave_id) {
        JSONObject work_days = new JSONObject();
        try {
            work_days.put("typeleave", leave_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        leave_deatil_array.clear();
        Log.e(TAG + "leave_details", String.valueOf(work_days));
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(work_days))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.e(TAG + "attendance_details", String.valueOf(attendance_details));
        Call<JsonObject> call = ApiUtil.getServiceClass().leave_details(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {

                    System.out.println("jObject.body().toString() :: "+jObject.body().toString());
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {


                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            LeaveAppliedDataType a = new LeaveAppliedDataType();
                            a.setAction_taken_by(jsonObject.getJSONArray("data").getJSONObject(i).getString("action_taken_by"));
                            a.setAction_taken_on(jsonObject.getJSONArray("data").getJSONObject(i).getString("action_taken_on"));
                            a.setApplied_on(jsonObject.getJSONArray("data").getJSONObject(i).getString("applied_on"));
                            a.setFrom_date(jsonObject.getJSONArray("data").getJSONObject(i).getString("from_date"));
                            a.setTo_date(jsonObject.getJSONArray("data").getJSONObject(i).getString("to_date"));
                            a.setStatus(jsonObject.getJSONArray("data").getJSONObject(i).getString("status"));
                            a.setNumber_of_days(jsonObject.getJSONArray("data").getJSONObject(i).getString("number_of_days"));
                            leave_deatil_array.add(a);
                        }
                        LeaveDetailsActivity.updateadapter();
                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    } else {
                        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                    if (swipe_refresh != null)
                        swipe_refresh.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (swipe_refresh != null)
                        swipe_refresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                if (swipe_refresh != null)
                    swipe_refresh.setRefreshing(false);
               // errorFunction(LeaveDetailsActivity.this, volleyError);
            }
        });
    }

    private void userNotFound() {

        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(LeaveDetailsActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
        Intent i = new Intent(LeaveDetailsActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i, bndlanimation);
        Log.e("tag value", TAG);
        AppController.getInstance().cancelPendingRequests(TAG);

    }

}
