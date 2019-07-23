package com.talentcerebrumhrms.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.talentcerebrumhrms.adapter.AlertAdapter;
import com.talentcerebrumhrms.datatype.AlertDataType;
import com.talentcerebrumhrms.R;
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

/**
 * Created by saransh on 28-10-2016.
 */

public class AlertActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ListView list;
    SwipeRefreshLayout swipe_refresh;
    static AlertAdapter alertAdapter;
    static EditText search_edittext;
    LinearLayout searchbar;
    Toolbar toolbar;
    static ArrayList<AlertDataType> dataArray;
    ImageButton search_back_button, search_remove_button;
    private SwipeRefreshLayout mEmptyViewContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        list = (ListView) findViewById(R.id.alert_listview);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.alert_swipe);
        mEmptyViewContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);
        search_back_button = (ImageButton) findViewById(R.id.search_back_button);
        search_remove_button = (ImageButton) findViewById(R.id.search_remove_button);
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        searchbar = (LinearLayout) findViewById(R.id.search_layout);

        dataArray = new ArrayList<>(alert_data_array);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10.0f);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.alerts);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        search_back_button.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        search_remove_button.setColorFilter(ContextCompat.getColor(this, R.color.grey), PorterDuff.Mode.SRC_ATOP);
        int ab=0;
        for ( ab = 0; ab<dataArray.size(); ab++)
        {
            Log.e("alert activity", String.valueOf(dataArray.get(ab).getSender_image()));
        }

        alertAdapter = new AlertAdapter(this, dataArray, new AlertAdapter.AlertAdapterListener() {
            @Override
            public void onReadClickListener(int position) {
                markReadAlertApiCall(dataArray.get(position).getId());
            }
        });
        list.setEmptyView(mEmptyViewContainer);
        list.setAdapter(alertAdapter);

        mEmptyViewContainer.setOnRefreshListener(this);
        swipe_refresh.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(AlertActivity.this, R.color.colorAccent)
        );
        swipe_refresh.setColorSchemeColors(

                ContextCompat.getColor(AlertActivity.this, R.color.colorAccent)
        );


        swipe_refresh.post(new Runnable() {
                               @Override
                               public void run() {
                                   swipe_refresh.setRefreshing(false);
                                   mEmptyViewContainer.setRefreshing(false);
                                   getAlertsApiCall();

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
                Log.e("AlertActivity", "after text changed ");
                alertAdapter.filter(text);
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

                Log.e("ontouch", "true");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_edittext.getWindowToken(), 0);
                return false;
            }
        });

    }

    @Override
    public void onRefresh() {
        swipe_refresh.setRefreshing(false);
        mEmptyViewContainer.setRefreshing(false);
        getAlertsApiCall();

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

    public static void updateadapter() {
        dataArray = new ArrayList<>(alert_data_array);
        if (alertAdapter != null) {
            alertAdapter.updateAdapter(dataArray);
            if (!search_edittext.getText().toString().equalsIgnoreCase("")) {
                String text = search_edittext.getText().toString().toLowerCase(Locale.getDefault());
                alertAdapter.filter(text);
            }
        }
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
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.e("animation sh listener", "start");

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("animation sh listener", "end");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.e("animation sh listener", "repeat");
            }
        });

        toolbar.setVisibility(View.VISIBLE);
        searchbar.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(search_edittext.getWindowToken(), 0);
        }
    }

    private void removeToolbar(final MenuItem menuItem) {
        //animation up and fade out
        AnimationSet anim = new AnimationSet(true);
        Animation animate = new TranslateAnimation(0, 0, 0, -toolbar.getHeight());
        animate.setDuration(200);
        // animate.setFillAfter(true);
        anim.addAnimation(animate);
        Animation fade = new AlphaAnimation(1.0f, 0.0f);
        fade.setDuration(200);


        anim.addAnimation(fade);
        toolbar.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //  Log.e("animation rm listener","start");
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
        // toolbar.animate().alpha(0.0f).setDuration(300);
        searchbar.setVisibility(View.VISIBLE);
        search_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(search_edittext, InputMethodManager.SHOW_IMPLICIT);
        }
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
                                a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
                                a.setAlert_type(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("alert_type"));
                                a.setFlag(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("flag"));
                                a.setDescription(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("description"));
                                a.setId(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("id"));
                                alert_data_array.add(a);
                            }
                        }
                        alert_size = alert_data_array.size();
                        Log.e("message leave approve", message);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        alert_size = 0;
                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(AlertActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(AlertActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
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
                errorFunction(AlertActivity.this, volleyError);
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
                                a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
                                a.setAlert_type(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("alert_type"));
                                a.setFlag(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("flag"));
                                a.setDescription(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("description"));
                                a.setId(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("id"));
                                alert_data_array.add(a);
                            }
                        }
                        alert_size = alert_data_array.size();
                        Log.e("message leave approve", message);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        alert_size = 0;
                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(AlertActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(AlertActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
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
               // errorFunction(AlertActivity.this, volleyError);
            }
        });

    }
    /*private void markReadAlertApiCall(final String id) {

        AppController.mark_read_alert(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        Toast.makeText(AlertActivity.this, R.string.alert_read, Toast.LENGTH_SHORT).show();
                        //TODO refresh should be there in swipe to refresh to show progress
                        getAlertsApiCall();

                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(AlertActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlertActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        markReadAlertApiCall(id);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }, id);

    }*/

    private void markReadAlertApiCall(final String id) {
        JSONObject work_days = new JSONObject();
        try {
            work_days.put("alert_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG + "mark_read_id", String.valueOf(work_days));
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(work_days))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<JsonObject> call = ApiUtil.getServiceClass().mark_read_alert(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        Toast.makeText(AlertActivity.this, R.string.alert_read, Toast.LENGTH_SHORT).show();
                        //TODO refresh should be there in swipe to refresh to show progress
                        getAlertsApiCall();

                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(AlertActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlertActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        markReadAlertApiCall(id);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    private void userNotFound() {

        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(this, R.anim.anim_out, R.anim.anim_in).toBundle();
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i, bndlanimation);
        Log.e("tag value", TAG);
        AppController.getInstance().cancelPendingRequests(TAG);

    }
}
