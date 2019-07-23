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
import com.talentcerebrumhrms.adapter.ApproveLeaveAdapter;
import com.talentcerebrumhrms.datatype.LeaveApproveDatatype;
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
import static com.talentcerebrumhrms.utils.AppController.approve_size;
import static com.talentcerebrumhrms.utils.AppController.leave_approve_array;

/**
 * Created by saransh on 28-10-2016.
 */

public class ApproveLeavesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ListView list;
    SwipeRefreshLayout swipe_refresh;
    Toolbar toolbar;
    static ArrayList<LeaveApproveDatatype> dataArray;
    static ApproveLeaveAdapter approveLeaveAdapter;
    private SwipeRefreshLayout mEmptyViewContainer;
    ImageButton search_back_button, search_remove_button;
    static EditText search_edittext;
    LinearLayout searchbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_leave);

        list = (ListView) findViewById(R.id.approve_leave_listview);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.approve_leave_swipe);
        mEmptyViewContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        searchbar = (LinearLayout) findViewById(R.id.search_layout);
        search_back_button = (ImageButton) findViewById(R.id.search_back_button);
        search_remove_button = (ImageButton) findViewById(R.id.search_remove_button);
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        dataArray = new ArrayList<>(leave_approve_array);

        search_back_button.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        search_remove_button.setColorFilter(ContextCompat.getColor(this, R.color.grey), PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10.0f);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.approve_leaves);

        getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        approveLeaveAdapter = new ApproveLeaveAdapter(this, dataArray, new ApproveLeaveAdapter.ApproveAdapterListener() {
            @Override
            public void onApproveClickListener(int position) {
                approveRejectLeaveApiCall(dataArray.get(position).getLeave_id(), "true");
            }

            @Override
            public void onRejectClickListener(int position) {
                approveRejectLeaveApiCall(dataArray.get(position).getLeave_id(), "false");
            }
        });
        list.setEmptyView(mEmptyViewContainer);
        list.setAdapter(approveLeaveAdapter);

        mEmptyViewContainer.setOnRefreshListener(this);
        swipe_refresh.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(ApproveLeavesActivity.this, R.color.colorAccent)
        );
        swipe_refresh.setColorSchemeColors(

                ContextCompat.getColor(ApproveLeavesActivity.this, R.color.colorAccent)
        );

        swipe_refresh.post(new Runnable() {
                               @Override
                               public void run() {
                                   swipe_refresh.setRefreshing(true);
                                   mEmptyViewContainer.setRefreshing(false);
                                   approveLeaveApiCall();
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
                Log.e("jobsActivity", "after text changed ");
                approveLeaveAdapter.filter(text);
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
        swipe_refresh.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        approveLeaveApiCall();
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
        dataArray = new ArrayList<>(leave_approve_array);
        if (approveLeaveAdapter != null) {
            approveLeaveAdapter.updateAdapter(dataArray);
            if (!search_edittext.getText().toString().equalsIgnoreCase("")) {
                String text = search_edittext.getText().toString().toLowerCase(Locale.getDefault());
                approveLeaveAdapter.filter(text);
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
                //Log.e("animation rm listener","end");
                menuItem.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //Log.e("animation rm listener","repeat");
            }
        });
        toolbar.setVisibility(View.GONE);
        searchbar.setVisibility(View.VISIBLE);
        search_edittext.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(search_edittext, InputMethodManager.SHOW_IMPLICIT);
    }

   /* private void approveLeaveApiCall() {

        AppController.leave_approve(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    leave_approve_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            LeaveApproveDatatype a = new LeaveApproveDatatype();
                            a.setLeave_id(jsonObject.getJSONArray("data").getJSONObject(i).getString("leave_id"));
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("user_name"));
                            a.setReason(jsonObject.getJSONArray("data").getJSONObject(i).getString("reason"));
                            a.setDate_from(jsonObject.getJSONArray("data").getJSONObject(i).getString("date_from"));
                            a.setDate_to(jsonObject.getJSONArray("data").getJSONObject(i).getString("date_to"));
                            a.setProfile(jsonObject.getJSONArray("data").getJSONObject(i).getString("passport_photo"));
                            leave_approve_array.add(a);
                        }
                        approve_size = jsonObject.getJSONArray("data").length();
                        Log.e("message leave approve", message);
                        Log.d("aaaprove size", String.valueOf(approve_size));

                    } else if (message.equalsIgnoreCase("User not found")) {

                        approve_size = 0;
                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leave_approve");

                    } else {

                        approve_size = 0;


                    }
                    if (swipe_refresh != null)
                        swipe_refresh.setRefreshing(false);


                    try {
                        Log.d("invalidate options", "leave_approve");
                        invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    ApproveLeavesActivity.updateadapter();
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
                errorFunction(ApproveLeavesActivity.this, volleyError);
            }
        });

    }*/

   private void approveLeaveApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().leave_approve();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    leave_approve_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            LeaveApproveDatatype a = new LeaveApproveDatatype();
                            a.setLeave_id(jsonObject.getJSONArray("data").getJSONObject(i).getString("leave_id"));
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("user_name"));
                            a.setReason(jsonObject.getJSONArray("data").getJSONObject(i).getString("reason"));
                            a.setDate_from(jsonObject.getJSONArray("data").getJSONObject(i).getString("date_from"));
                            a.setDate_to(jsonObject.getJSONArray("data").getJSONObject(i).getString("date_to"));
                            a.setProfile(jsonObject.getJSONArray("data").getJSONObject(i).getString("passport_photo"));
                            leave_approve_array.add(a);
                        }
                        approve_size = jsonObject.getJSONArray("data").length();
                        Log.e("message leave approve", message);
                        Log.d("aaaprove size", String.valueOf(approve_size));

                    } else if (message.equalsIgnoreCase("User not found")) {

                        approve_size = 0;
                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leave_approve");

                    } else {

                        approve_size = 0;


                    }
                    if (swipe_refresh != null)
                        swipe_refresh.setRefreshing(false);


                    try {
                        Log.d("invalidate options", "leave_approve");
                        invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    ApproveLeavesActivity.updateadapter();
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
               // errorFunction(ApproveLeavesActivity.this, volleyError);
            }
        });

    }

   /* private void approveRejectLeaveApiCall(final String id, final String approve) {

        AppController.approve_reject_leave(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        Toast.makeText(ApproveLeavesActivity.this, message, Toast.LENGTH_SHORT).show();
                        //TODO refresh should be there in swipe to refresh to show progress
                        approveLeaveApiCall();

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

                Toast.makeText(ApproveLeavesActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ApproveLeavesActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        approveRejectLeaveApiCall(id, approve);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                if (alertDialog != null)
                    alertDialog.show();
            }
        }, id, approve);

    }*/

   private void approveRejectLeaveApiCall(final String id, final String approve) {
        JSONObject work_days = new JSONObject();
        try {
            work_days.put("leave_id", id);
            work_days.put("approve", approve);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG + "approve_reject_leave", String.valueOf(work_days));
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(work_days))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // Log.e(TAG + "attendance_details", String.valueOf(work_days));
        Call<JsonObject> call = ApiUtil.getServiceClass().approve_reject_leave(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        Toast.makeText(ApproveLeavesActivity.this, message, Toast.LENGTH_SHORT).show();
                        //TODO refresh should be there in swipe to refresh to show progress
                        approveLeaveApiCall();

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

                Toast.makeText(ApproveLeavesActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ApproveLeavesActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        approveRejectLeaveApiCall(id, approve);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                if (alertDialog != null)
                    alertDialog.show();
            }
        });

    }

    private void userNotFound() {

        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(ApproveLeavesActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
        Intent i = new Intent(ApproveLeavesActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ApproveLeavesActivity.this.startActivity(i, bndlanimation);
        Log.e("tag value", TAG);
        AppController.getInstance().cancelPendingRequests(TAG);

    }
}
