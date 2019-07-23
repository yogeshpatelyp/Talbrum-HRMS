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
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.activity.LeaveDetailsActivity;
import com.talentcerebrumhrms.activity.LoginActivity;
import com.talentcerebrumhrms.adapter.LeaveBalanceAdapter;
import com.talentcerebrumhrms.datatype.LeavesDatatype;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;
import static com.talentcerebrumhrms.utils.AppController.leaves_array;

/**

 * Created by saransh on 14-10-2016.
 */

public class LeaveBalanceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootview;
    StaggeredGridView leave_gridview;
    SwipeRefreshLayout leave_swipe;
    private SwipeRefreshLayout mEmptyViewContainer;

    private static LeaveBalanceAdapter leaveBalanceAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.fragement_leave_balance, null);

        leave_swipe = (SwipeRefreshLayout) rootview.findViewById(R.id.leaves_swipe);
        mEmptyViewContainer = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout_emptyView);
        leave_gridview = (StaggeredGridView) rootview.findViewById(R.id.leave_gridview);
        leaveBalanceAdapter = new LeaveBalanceAdapter(getActivity(), leaves_array);

        leave_gridview.setEmptyView(mEmptyViewContainer);
        leave_gridview.setAdapter(leaveBalanceAdapter);
      /*  if (AppController.approve_size != AppController.leave_approve_array.size()) {
            AppController.approve_size = AppController.leave_approve_array.size();
            Log.e("approve size LF", String.valueOf(AppController.approve_size));
            Log.e("approve array size LF", String.valueOf(AppController.leave_approve_array.size()));
            getActivity().invalidateOptionsMenu();
        }*/
        getActivity().invalidateOptionsMenu();
        leave_swipe.setOnRefreshListener(this);
        leave_swipe.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        mEmptyViewContainer.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        leave_swipe.post(new Runnable() {
                             @Override
                             public void run() {
                                 leave_swipe.setRefreshing(true);
                                 remainingLeaveApiCall();
                                 //fetch data
                             }
                         }
        );


        leave_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("leave_id : "+leaves_array.get(i).getId());
                if(leaves_array.get(i).getId()!=null) {
                    if(leaves_array.get(i).getId().isEmpty()){

                    }else {
                        Intent a = new Intent(getActivity(), LeaveDetailsActivity.class);
                        a.putExtra("leave_id", leaves_array.get(i).getId());
                        a.putExtra("leave_type", leaves_array.get(i).getType_of_leave());
                        startActivity(a);
                    }
                }
            }
        });
        return rootview;
    }

    @Override
    public void onRefresh() {
        leave_swipe.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        remainingLeaveApiCall();

    }

    public static void notifychange() {
        leaveBalanceAdapter.updateAdapter(leaves_array);
    }

    @Override
    public void onResume() {
        super.onResume();

        //for alert and approve leave fragments.

        Log.e("on resume", "LeavebalFragment");
        getActivity().invalidateOptionsMenu();
    }

    /*private void remainingLeaveApiCall() {

        AppController.leave_remaining(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    leaves_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            LeavesDatatype a = new LeavesDatatype();
                            a.setEligible(jsonObject.getJSONArray("data").getJSONObject(i).getString("Eligible"));
                            a.setUsed(jsonObject.getJSONArray("data").getJSONObject(i).getString("Used"));
                            a.setId(jsonObject.getJSONArray("data").getJSONObject(i).getString("leave_type_id"));
                            a.setType_of_leave(jsonObject.getJSONArray("data").getJSONObject(i).getString("Type_of_leave"));
                            a.setRemaining(jsonObject.getJSONArray("data").getJSONObject(i).getString("Remaining"));
                            a.setAdmissible(jsonObject.getJSONArray("data").getJSONObject(i).getString("Admissible"));
                            leaves_array.add(a);
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
                    if (leave_swipe != null)
                        leave_swipe.setRefreshing(false);
                    LeaveBalanceFragment.notifychange();
                } catch (Exception e) {
                    if (leave_swipe != null)
                        leave_swipe.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                if (leave_swipe != null)
                    leave_swipe.setRefreshing(false);
                errorFunction(getActivity(), volleyError);
            }
        });
    }*/
    private void remainingLeaveApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().leave_remaining();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    leaves_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            LeavesDatatype a = new LeavesDatatype();
                            a.setEligible(jsonObject.getJSONArray("data").getJSONObject(i).getString("Eligible"));
                            a.setUsed(jsonObject.getJSONArray("data").getJSONObject(i).getString("Used"));
                            a.setId(jsonObject.getJSONArray("data").getJSONObject(i).getString("leave_type_id"));
                            a.setType_of_leave(jsonObject.getJSONArray("data").getJSONObject(i).getString("Type_of_leave"));
                            a.setRemaining(jsonObject.getJSONArray("data").getJSONObject(i).getString("Remaining"));
                            a.setAdmissible(jsonObject.getJSONArray("data").getJSONObject(i).getString("Admissible"));
                            leaves_array.add(a);
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
                    if (leave_swipe != null)
                        leave_swipe.setRefreshing(false);
                    LeaveBalanceFragment.notifychange();
                } catch (Exception e) {
                    if (leave_swipe != null)
                        leave_swipe.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                if (leave_swipe != null)
                    leave_swipe.setRefreshing(false);
               // errorFunction(getActivity(), volleyError);
            }
        });
    }
}
