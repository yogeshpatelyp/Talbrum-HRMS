package com.talentcerebrumhrms.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.talentcerebrumhrms.activity.LoginActivity;
import com.talentcerebrumhrms.activity.UploadTimesheetActivity;
import com.talentcerebrumhrms.adapter.TimesheetAdapter;
import com.talentcerebrumhrms.datatype.TimesheetDataType;
import com.talentcerebrumhrms.datatype.UploadTimesheetDataType;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;
import static com.talentcerebrumhrms.utils.AppController.timesheet_rejected_array;
import static com.talentcerebrumhrms.utils.AppController.timesheet_upload_array;

/**
 * Created by Harshit on 15-Jun-17.
 */

public class RejectedTimesheetFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootview;
    TextView emptyView;
    ListView timesheet_view;
    SwipeRefreshLayout timesheet_swipe;
    private SwipeRefreshLayout mEmptyViewContainer;
    private TimesheetAdapter timesheetAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.fragment_app_rej_timesheet, null);

        emptyView = (TextView) rootview.findViewById(R.id.emptyView);
        emptyView.setText(getResources().getString(R.string.no_rejected_timesheet));
        timesheetAdapter = new TimesheetAdapter(getActivity(), timesheet_rejected_array, getResources().getString(R.string.rejected));
        timesheet_swipe = (SwipeRefreshLayout) rootview.findViewById(R.id.timesheet_swipe);
        mEmptyViewContainer = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout_emptyView);
        timesheet_view = (ListView) rootview.findViewById(R.id.timesheet_view);

        timesheet_view.setEmptyView(mEmptyViewContainer);
        timesheet_view.setAdapter(timesheetAdapter);
        getActivity().invalidateOptionsMenu();
        timesheet_swipe.setOnRefreshListener(this);
        timesheet_swipe.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        mEmptyViewContainer.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        timesheet_swipe.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     timesheet_swipe.setRefreshing(true);
                                     getTimesheetRecordsApiCall();
                                 }
                             }
        );

        timesheet_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("rejected_array_ID", timesheet_rejected_array.get(i).getId());
                getDetailedTimesheetRecordApiCall(timesheet_rejected_array.get(i).getId());

            }
        });
        return rootview;
    }

    @Override
    public void onRefresh() {
        Log.e("on refresh", "RejectedTimesheetFragment");
        timesheet_swipe.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        getTimesheetRecordsApiCall();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("on resume", "RejectedTimesheetFragment");
        getActivity().invalidateOptionsMenu();
    }

   /* private void getTimesheetRecordsApiCall() {

        AppController.get_timesheet_records(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {
                try {
                    timesheet_rejected_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            TimesheetDataType a = new TimesheetDataType();
                            a.setId(jsonObject.getJSONArray("data").getJSONObject(i).getString("timesheet_id"));
                            a.setFromDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("min_date"));
                            a.setToDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("max_date"));
                            a.setSubmittedDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("submitted_on"));
                            a.setTotalDays(jsonObject.getJSONArray("data").getJSONObject(i).getString("total_days"));
                            a.setSignedBy(jsonObject.getJSONArray("data").getJSONObject(i).getString("rejected_by"));
                            a.setSignedOn(jsonObject.getJSONArray("data").getJSONObject(i).getString("rejected_on"));
                            timesheet_rejected_array.add(a);
                        }
                        Log.e("message leaves", message);
                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leaves_remaing");
                    } else {
                        //  Toast.makeText(context, R.string.please_wait, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                    if (timesheet_swipe != null)
                        timesheet_swipe.setRefreshing(false);
                    timesheetAdapter.updateAdapter(timesheet_rejected_array);
                } catch (Exception e) {
                    if (timesheet_swipe != null)
                        timesheet_swipe.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                if (timesheet_swipe != null)
                    timesheet_swipe.setRefreshing(false);
                errorFunction(getActivity(), volleyError);
            }
        }, getResources().getString(R.string.rejected));
    }*/

    private void getTimesheetRecordsApiCall() {

        JSONObject timesheet = new JSONObject();
        try {
            timesheet.put("type", getResources().getString(R.string.rejected));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(timesheet))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "timesheet", String.valueOf(timesheet));
        Call<JsonObject> call = ApiUtil.getServiceClass().get_timesheet_records(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    timesheet_rejected_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            TimesheetDataType a = new TimesheetDataType();
                            a.setId(jsonObject.getJSONArray("data").getJSONObject(i).getString("timesheet_id"));
                            a.setFromDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("min_date"));
                            a.setToDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("max_date"));
                            a.setSubmittedDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("submitted_on"));
                            a.setTotalDays(jsonObject.getJSONArray("data").getJSONObject(i).getString("total_days"));
                            a.setSignedBy(jsonObject.getJSONArray("data").getJSONObject(i).getString("rejected_by"));
                            a.setSignedOn(jsonObject.getJSONArray("data").getJSONObject(i).getString("rejected_on"));
                            timesheet_rejected_array.add(a);
                        }
                        Log.e("message leaves", message);
                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leaves_remaing");
                    } else {
                        //  Toast.makeText(context, R.string.please_wait, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                    if (timesheet_swipe != null)
                        timesheet_swipe.setRefreshing(false);
                    timesheetAdapter.updateAdapter(timesheet_rejected_array);
                } catch (Exception e) {
                    if (timesheet_swipe != null)
                        timesheet_swipe.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
                if (timesheet_swipe != null)
                    timesheet_swipe.setRefreshing(false);
              //  errorFunction(getActivity(), volleyError);
            }
        });
    }

    /*private void getDetailedTimesheetRecordApiCall(String id) {

        AppController.get_detailed_timesheet_record(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {
                try {
                    timesheet_upload_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("day_records").length(); i++) {
                            UploadTimesheetDataType a = new UploadTimesheetDataType();
                            a.setWorkingDate(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("date"));
                            a.setLeaveTypeId(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("leave_type_id"));
                            a.setWorkingHour(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("hours"));
                            a.setRemark(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("remark"));
                            a.setWorkingStatus(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("status"));
                            timesheet_upload_array.add(a);
                        }
                        String file_name = jsonObject.getJSONObject("data").getString("file_name");

                        Intent intent = new Intent(getActivity(), UploadTimesheetActivity.class);
                        intent.putExtra("file_name", file_name);
                        startActivity(intent);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leaves_remaing");
                    } else {
                        //  Toast.makeText(context, R.string.please_wait, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                    if (timesheet_swipe != null)
                        timesheet_swipe.setRefreshing(false);

                } catch (Exception e) {
                    if (timesheet_swipe != null)
                        timesheet_swipe.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                if (timesheet_swipe != null)
                    timesheet_swipe.setRefreshing(false);
                errorFunction(getActivity(), volleyError);
            }
        }, id);
    }*/

    private void getDetailedTimesheetRecordApiCall(String id) {
        JSONObject timesheet = new JSONObject();
        try {
            timesheet.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(timesheet))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "timesheet", String.valueOf(timesheet));
        Call<JsonObject> call = ApiUtil.getServiceClass().get_detailed_timesheet_record(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    timesheet_upload_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("day_records").length(); i++) {
                            UploadTimesheetDataType a = new UploadTimesheetDataType();
                            a.setWorkingDate(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("date"));
                            a.setLeaveTypeId(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("leave_type_id"));
                            a.setWorkingHour(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("hours"));
                            a.setRemark(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("remark"));
                            a.setWorkingStatus(jsonObject.getJSONObject("data").getJSONArray("day_records").getJSONObject(i).getString("status"));
                            timesheet_upload_array.add(a);
                        }
                        String file_name = jsonObject.getJSONObject("data").getString("file_name");

                        Intent intent = new Intent(getActivity(), UploadTimesheetActivity.class);
                        intent.putExtra("file_name", file_name);
                        startActivity(intent);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leaves_remaing");
                    } else {
                        //  Toast.makeText(context, R.string.please_wait, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                    if (timesheet_swipe != null)
                        timesheet_swipe.setRefreshing(false);

                } catch (Exception e) {
                    if (timesheet_swipe != null)
                        timesheet_swipe.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
                if (timesheet_swipe != null)
                    timesheet_swipe.setRefreshing(false);
             //   errorFunction(getActivity(), volleyError);
            }
        });
    }
}
