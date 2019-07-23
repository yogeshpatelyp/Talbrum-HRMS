package com.talentcerebrumhrms.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

//import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.activity.LoginActivity;
import com.talentcerebrumhrms.adapter.ReimburesementAdapter;
import com.talentcerebrumhrms.datatype.RembursementDatatype;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;
import static com.talentcerebrumhrms.utils.AppController.reimbursement_array;

/**
 * Created by saransh on 15-10-2016.
 */

public class ReimbursementsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootview;
    ListView reimbursement_listview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout mEmptyViewContainer;
    private static ReimburesementAdapter reimburesementAdapter;
    //  TextView emptytext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragmentreimbursement, null);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10.0f);
        }

        getActivity().invalidateOptionsMenu();
        reimbursement_listview = (ListView) rootview.findViewById(R.id.reimbursement_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.reimbursement_swipe_refresh_layout);
        mEmptyViewContainer = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout_emptyView);
        reimburesementAdapter = new ReimburesementAdapter(getActivity(), reimbursement_array);
        reimbursement_listview.setEmptyView(mEmptyViewContainer);
        reimbursement_listview.setAdapter(reimburesementAdapter);


        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        mEmptyViewContainer.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        //    mEmptyViewContainer.setRefreshing(false);
                                        reimbursementListApiCall();
                                        //fetch data
                                    }
                                }
        );


        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.add_reimbursement);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "asdj", Toast.LENGTH_SHORT).show();
            }
        });


        return rootview;
    }

    @Override
    public void onRefresh() {
        //do what you want on refreash
        Log.e("swiping", "");
        swipeRefreshLayout.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        refreshContent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

 /*   private void onCreateSwipeToRefresh(SwipeRefreshLayout refreshLayout) {

        refreshLayout.setOnRefreshListener(this);

        refreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_light),
                getResources().getColor(android.R.color.holo_orange_light)
        );

    }
*/

    private void refreshContent() {
        //   new Handler().postDelayed(new Runnable() {
        //     @Override
        //   public void run() {
        swipeRefreshLayout.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        reimbursementListApiCall();
        //    }
        //   }, 3000);
    }

    public static void updateadapter() {
        reimburesementAdapter.updateAdapter(reimbursement_array);
    }

    @Override
    public void onResume() {
        super.onResume();

        //for alert and approve leave fragments.

        Log.e("on resume", "ReimbFragment");
        getActivity().invalidateOptionsMenu();
    }

   /* private void reimbursementListApiCall() {

        AppController.list_reimbursement(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    reimbursement_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            RembursementDatatype a = new RembursementDatatype();
                            a.setDetails(jsonObject.getJSONArray("data").getJSONObject(i).getString("detail"));
                            a.setAction(jsonObject.getJSONArray("data").getJSONObject(i).getString("action"));
                            a.setAmount("\u20B9 " + jsonObject.getJSONArray("data").getJSONObject(i).getString("amount"));
                            a.setIssued_by(jsonObject.getJSONArray("data").getJSONObject(i).getString("bill_issued_by"));
                            a.setAuthorised_by(jsonObject.getJSONArray("data").getJSONObject(i).getString("authorized_by"));
                            a.setBill_no(jsonObject.getJSONArray("data").getJSONObject(i).getString("bill_no"));
                            reimbursement_array.add(a);
                        }

                        Log.e("message reimbursement", message);
                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "reimbursement");
                    } else {
                        // swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""));
                    }
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    //    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //   editor.putString("token", jsonObject.getString("token"));
                    //  editor.apply();
                    // Log.e("call check","check appcontroller");
                    ReimbursementsFragment.updateadapter();
                } catch (Exception e) {
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
                errorFunction(getActivity(), volleyError);
            }
        });

    }*/
    private void reimbursementListApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().list_reimbursement();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    reimbursement_array.clear();
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            RembursementDatatype a = new RembursementDatatype();
                            a.setDetails(jsonObject.getJSONArray("data").getJSONObject(i).getString("detail"));
                            a.setAction(jsonObject.getJSONArray("data").getJSONObject(i).getString("action"));
                            a.setAmount("\u20B9 " + jsonObject.getJSONArray("data").getJSONObject(i).getString("amount"));
                            a.setIssued_by(jsonObject.getJSONArray("data").getJSONObject(i).getString("bill_issued_by"));
                            a.setAuthorised_by(jsonObject.getJSONArray("data").getJSONObject(i).getString("authorized_by"));
                            a.setBill_no(jsonObject.getJSONArray("data").getJSONObject(i).getString("bill_no"));
                            reimbursement_array.add(a);
                        }

                        Log.e("message reimbursement", message);
                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "reimbursement");
                    } else {
                        // swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""));
                    }
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    //    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //   editor.putString("token", jsonObject.getString("token"));
                    //  editor.apply();
                    // Log.e("call check","check appcontroller");
                    ReimbursementsFragment.updateadapter();
                } catch (Exception e) {
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
              //  errorFunction(getActivity(), volleyError);
            }
        });

    }


}

