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
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.talentcerebrumhrms.activity.LoginActivity;
import com.talentcerebrumhrms.adapter.ListHolidayAdapter;
import com.talentcerebrumhrms.datatype.HolidayDatatype;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;
import static com.talentcerebrumhrms.utils.AppController.holidays;

/**
 * Created by saransh on 15-10-2016.
 */

public class List_Holidays_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootview;
    ListView list_holidays_listview;
    private SwipeRefreshLayout list_holidays_swipe;
    private SwipeRefreshLayout mEmptyViewContainer;
    private static ListHolidayAdapter listHolidayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragmentlistholidays, null);
        list_holidays_listview = (ListView) rootview.findViewById(R.id.list_holidays_listview);
        list_holidays_swipe = (SwipeRefreshLayout) rootview.findViewById(R.id.list_holidays_swipe);
        mEmptyViewContainer = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout_emptyView);
        listHolidayAdapter = new ListHolidayAdapter(getActivity(), holidays);
        // AppController.list_holidays(getActivity(),list_holidays_swipe);
        Log.e("call check", "check fragment");
        list_holidays_listview.setEmptyView(mEmptyViewContainer);
        list_holidays_listview.setAdapter(listHolidayAdapter);
       /* if (AppController.approve_size != AppController.leave_approve_array.size()) {
            AppController.approve_size = AppController.leave_approve_array.size();
            Log.e("approve size HF", String.valueOf(AppController.approve_size));
            Log.e("approve array size HF", String.valueOf(AppController.leave_approve_array.size()));
            getActivity().invalidateOptionsMenu();
        }*/
        getActivity().invalidateOptionsMenu();
        list_holidays_swipe.setOnRefreshListener(this);
        list_holidays_swipe.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        mEmptyViewContainer.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        list_holidays_swipe.post(new Runnable() {
                                     @Override
                                     public void run() {
                                         list_holidays_swipe.setRefreshing(true);
                                         holidayListApiCall();
                                         //fetch data
                                     }
                                 }
        );
        return rootview;
    }

    @Override
    public void onRefresh() {
        list_holidays_swipe.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        holidayListApiCall();
        //refer reimbursement fragment.
    }

    public static void notifychange() {
        if (listHolidayAdapter != null)
            listHolidayAdapter.updateAdapter(holidays);
    }

    @Override
    public void onResume() {
        super.onResume();

        //for alert and approve leave fragments.

        Log.e("on resume", "ListHoliFragment");
        getActivity().invalidateOptionsMenu();
    }

    /*private void holidayListApiCall() {

        AppController.list_holidays(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    holidays.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            HolidayDatatype a = new HolidayDatatype();
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("name"));
                            a.setDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("date"));
                            a.setDay(jsonObject.getJSONArray("data").getJSONObject(i).getString("day"));
                            holidays.add(a);
                        }

                        Log.e("message list_holidays", message);
                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "list_holidays");
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    }
                    if (list_holidays_swipe != null)
                        list_holidays_swipe.setRefreshing(false);

                    //    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //   editor.putString("token", jsonObject.getString("token"));
                    //   editor.apply();
                    Log.e("call check", "check appcontroller");
                    List_Holidays_Fragment.notifychange();
                } catch (Exception e) {
                    if (list_holidays_swipe != null)
                        list_holidays_swipe.setRefreshing(false);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                if (list_holidays_swipe != null)
                    list_holidays_swipe.setRefreshing(false);
                errorFunction(getActivity(), volleyError);

            }
        });
    }*/
    private void holidayListApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().list_holidays();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    holidays.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            HolidayDatatype a = new HolidayDatatype();
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("name"));
                            a.setDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("date"));
                            a.setDay(jsonObject.getJSONArray("data").getJSONObject(i).getString("day"));
                            holidays.add(a);
                        }

                        Log.e("message list_holidays", message);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "list_holidays");
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    }
                    if (list_holidays_swipe != null)
                        list_holidays_swipe.setRefreshing(false);

                    //    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //   editor.putString("token", jsonObject.getString("token"));
                    //   editor.apply();
                    Log.e("call check", "check appcontroller");
                    List_Holidays_Fragment.notifychange();
                } catch (Exception e) {
                    if (list_holidays_swipe != null)
                        list_holidays_swipe.setRefreshing(false);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                if (list_holidays_swipe != null)
                    list_holidays_swipe.setRefreshing(false);
              //  errorFunction(getActivity(), volleyError);

            }
        });
    }
}
