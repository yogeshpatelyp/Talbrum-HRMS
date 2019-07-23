package com.talentcerebrumhrms.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.talentcerebrumhrms.adapter.VacancyAdapter;
import com.talentcerebrumhrms.datatype.CandidateDataType;
import com.talentcerebrumhrms.datatype.VacancyDataType;
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
import static com.talentcerebrumhrms.utils.AppController.candidate_array;
import static com.talentcerebrumhrms.utils.AppController.vacancy_array;

/**
 * Created by Harshit on 06-Jul-17.
 */

public class VacancyActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ListView vacancy_listview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout mEmptyViewContainer;
    VacancyAdapter vacancyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy);
        Log.e("VacancyActivity", "onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.new_vacancies));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vacancy_listview = (ListView) findViewById(R.id.vacancy_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.vacancy_swipe_refresh_layout);
        mEmptyViewContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);
        vacancyAdapter = new VacancyAdapter(this, vacancy_array);
        vacancy_listview.setEmptyView(mEmptyViewContainer);
        vacancy_listview.setAdapter(vacancyAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(

                ContextCompat.getColor(this, R.color.colorAccent)
        );

        mEmptyViewContainer.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(this, R.color.colorAccent)
        );
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        //    mEmptyViewContainer.setRefreshing(false);
                                        getVacancyListApiCall();
                                        //fetch data
                                    }
                                }
        );

        vacancy_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("vacancy_array_ID", vacancy_array.get(i).getId());
                getCandidateListApiCall(vacancy_array.get(i).getId());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onRefresh() {
        Log.e("swiping", "");
        swipeRefreshLayout.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        refreshContent();
    }

    private void refreshContent() {
        //   new Handler().postDelayed(new Runnable() {
        //     @Override
        //   public void run() {
        swipeRefreshLayout.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        getVacancyListApiCall();
        //    }
        //   }, 3000);
    }

   /* private void getVacancyListApiCall() {

        AppController.list_vacancy(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {
                try {
                    vacancy_array.clear();
                    String message = jsonObject.getString("message");
                    Log.e("message vacancy", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            VacancyDataType a = new VacancyDataType();
                            a.setRole(jsonObject.getJSONArray("data").getJSONObject(i).getString("role"));
                            a.setLocation(jsonObject.getJSONArray("data").getJSONObject(i).getString("location"));
                            a.setDesignation(jsonObject.getJSONArray("data").getJSONObject(i).getString("designation"));
                            a.setPostedOn(jsonObject.getJSONArray("data").getJSONObject(i).getString("posted_on"));
                            a.setId(jsonObject.getJSONArray("data").getJSONObject(i).getString("id"));
                            a.setNoOfPosts(jsonObject.getJSONArray("data").getJSONObject(i).getString("no_of_posts"));
                            vacancy_array.add(a);
                        }
                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(VacancyActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(VacancyActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                    } else {
                        // swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(VacancyActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    //    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //   editor.putString("token", jsonObject.getString("token"));
                    //  editor.apply();
                    // Log.e("call check","check appcontroller");
                    vacancyAdapter.updateAdapter(vacancy_array);
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
                errorFunction(VacancyActivity.this, volleyError);
            }
        });
    }*/

    private void getVacancyListApiCall() {
        Call<JsonObject> call = ApiUtil.getServiceClass().list_vacancy();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());
                    vacancy_array.clear();
                    String message = jsonObject.getString("message");
                    Log.e("message vacancy", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            VacancyDataType a = new VacancyDataType();
                            a.setRole(jsonObject.getJSONArray("data").getJSONObject(i).getString("role"));
                            a.setLocation(jsonObject.getJSONArray("data").getJSONObject(i).getString("location"));
                            a.setDesignation(jsonObject.getJSONArray("data").getJSONObject(i).getString("designation"));
                            a.setPostedOn(jsonObject.getJSONArray("data").getJSONObject(i).getString("posted_on"));
                            a.setId(jsonObject.getJSONArray("data").getJSONObject(i).getString("id"));
                            a.setNoOfPosts(jsonObject.getJSONArray("data").getJSONObject(i).getString("no_of_posts"));
                            vacancy_array.add(a);
                        }
                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(VacancyActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(VacancyActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                    } else {
                        // swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(VacancyActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
                    //    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //   editor.putString("token", jsonObject.getString("token"));
                    //  editor.apply();
                    // Log.e("call check","check appcontroller");
                    vacancyAdapter.updateAdapter(vacancy_array);
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
              //  errorFunction(VacancyActivity.this, volleyError);
            }
        });
    }


    /*private void getCandidateListApiCall(String id) {

        AppController.list_candidate(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {
                try {
                    candidate_array.clear();
                    String message = jsonObject.getString("message");
                    Log.e("message vacancy", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            CandidateDataType a = new CandidateDataType();
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("name"));
                            a.setContact(jsonObject.getJSONArray("data").getJSONObject(i).getString("phone"));
                            a.setEmailId(jsonObject.getJSONArray("data").getJSONObject(i).getString("email"));
                            a.setTotalExp(jsonObject.getJSONArray("data").getJSONObject(i).getString("exp"));
                            a.setPrevOrg(jsonObject.getJSONArray("data").getJSONObject(i).getString("org"));
                            a.setCurrLoc(jsonObject.getJSONArray("data").getJSONObject(i).getString("location"));
                            a.setCurrCtc(jsonObject.getJSONArray("data").getJSONObject(i).getString("ctc"));
                            a.setAge(jsonObject.getJSONArray("data").getJSONObject(i).getString("age"));
                            a.setPic(jsonObject.getJSONArray("data").getJSONObject(i).getString("pic"));
                            candidate_array.add(a);
                        }

                        Intent intent = new Intent(VacancyActivity.this, CandidateActivity.class);
                        startActivity(intent);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(VacancyActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(VacancyActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                    } else {
                        // swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(VacancyActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);

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
                errorFunction(VacancyActivity.this, volleyError);
            }
        }, id);
    }*/

   private void getCandidateListApiCall(String id) {
        JSONObject candidate = new JSONObject();
        try {
            candidate.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(candidate))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "candidate", String.valueOf(candidate));
        Call<JsonObject> call = ApiUtil.getServiceClass().list_candidate(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    candidate_array.clear();
                    String message = jsonObject.getString("message");
                    Log.e("message vacancy", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            CandidateDataType a = new CandidateDataType();
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("name"));
                            a.setContact(jsonObject.getJSONArray("data").getJSONObject(i).getString("phone"));
                            a.setEmailId(jsonObject.getJSONArray("data").getJSONObject(i).getString("email"));
                            a.setTotalExp(jsonObject.getJSONArray("data").getJSONObject(i).getString("exp"));
                            a.setPrevOrg(jsonObject.getJSONArray("data").getJSONObject(i).getString("org"));
                            a.setCurrLoc(jsonObject.getJSONArray("data").getJSONObject(i).getString("location"));
                            a.setCurrCtc(jsonObject.getJSONArray("data").getJSONObject(i).getString("ctc"));
                            a.setAge(jsonObject.getJSONArray("data").getJSONObject(i).getString("age"));
                            a.setPic(jsonObject.getJSONArray("data").getJSONObject(i).getString("pic"));
                            candidate_array.add(a);
                        }

                        Intent intent = new Intent(VacancyActivity.this, CandidateActivity.class);
                        startActivity(intent);

                    } else if (message.equalsIgnoreCase("User not found")) {

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(VacancyActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(VacancyActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);
                        Log.e("tag value", TAG);
                        AppController.getInstance().cancelPendingRequests(TAG);
                    } else {
                        // swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(VacancyActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);

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
              //  errorFunction(VacancyActivity.this, volleyError);
            }
        });
    }
}

