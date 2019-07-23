package com.talentcerebrumhrms.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.github.clans.fab.FloatingActionButton;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.activity.LoginActivity;
import com.talentcerebrumhrms.activity.MainActivity;
import com.talentcerebrumhrms.datatype.AttendanceFragmentDataType;
import com.talentcerebrumhrms.datatype.ChartValueFormatter;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;
import com.talentcerebrumhrms.utils.FragmentCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;
import static com.talentcerebrumhrms.utils.AppController.attendanceFragmentDataType;

/**
 * Created by saransh on 15-10-2016.
 */

public class AttendanceFragment extends Fragment implements FragmentCallback, SwipeRefreshLayout.OnRefreshListener {
    private static final int LOCATION_REQUEST = 1338;
    static AttendanceFragment attendanceFragment;
    View rootview;
    TextView leave_days;
    TextView absent_days;
    //FloatingActionButton chat_fab;
    TextView in_time, out_time, income_tax, professional_tax, net_deduction, net_credit, text_location, date_range;
    ImageButton toggle_arrow;
    LinearLayout slip;
    BarChart chart;
    ScrollView scrollView;
    SharedPreferences sharedpreferences;
    private SwipeRefreshLayout attendance_swipe;

    public static void refresh() {
        attendanceFragment.updateUI();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_attendance, null);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10.0f);
        }
        Log.e("onCreateView", "Attendance Fragment");
        sharedpreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        attendanceFragment = AttendanceFragment.this;
        attendance_swipe = (SwipeRefreshLayout) rootview.findViewById(R.id.swipe_attendance);
        scrollView = (ScrollView) rootview.findViewById(R.id.scrollView);
        text_location = (TextView) rootview.findViewById(R.id.text_location);
        leave_days = (TextView) rootview.findViewById(R.id.leave_days);
        absent_days = (TextView) rootview.findViewById(R.id.absent_days);
        //all_working_days = (TextView) rootview.findViewById(R.id.outdoor_days);
        in_time = (TextView) rootview.findViewById(R.id.in_time);
        out_time = (TextView) rootview.findViewById(R.id.out_time);
        toggle_arrow = (ImageButton) rootview.findViewById(R.id.toggle_arrow);
        slip = (LinearLayout) rootview.findViewById(R.id.slip);
        income_tax = (TextView) rootview.findViewById(R.id.income_tax);
        date_range = (TextView) rootview.findViewById(R.id.date_range);
        professional_tax = (TextView) rootview.findViewById(R.id.professional_tax);
        net_deduction = (TextView) rootview.findViewById(R.id.total_deduction);
        net_credit = (TextView) rootview.findViewById(R.id.net_credit);
       // chat_fab = (FloatingActionButton) rootview.findViewById(R.id.fab_chat);
        chart = (BarChart) rootview.findViewById(R.id.chart);
        chart.setNoDataText("Loading Chart...");
        chart.setNoDataTextColor(ContextCompat.getColor(getActivity(), R.color.graph_text_colour));
        getActivity().invalidateOptionsMenu();

        attendance_swipe.setOnRefreshListener(this);
        attendance_swipe.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );


        attendance_swipe.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      Log.d("attendance swipe", "running");
                                      attendance_swipe.setRefreshing(true);
                                      getAllLeaveDetailsApiCall();
                                      //fetch data
                                  }
                              }
        );


        /*chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent i = new Intent(getActivity(), ChatActivity.class);
                // startActivity(i);
            }
        });*/


        toggle_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Answers.getInstance().logCustom(new CustomEvent("Salary Slip Toggled"));
                if (slip.getVisibility() == View.VISIBLE) {
                    toggle_arrow.setBackgroundResource(R.drawable.toggle_arrow_down);
                    collapse(slip);
                } else {
                    toggle_arrow.setBackgroundResource(R.drawable.toggle_arrow_up);
                    expand(slip);
                }
                scrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 200);

            }
        });

        return rootview;

    }

    private void drawChart() {

        chart.setDescription(null);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        String months[] = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

        ArrayList<String> xvals = new ArrayList();
        ArrayList<BarEntry> yVals1 = new ArrayList();
        ArrayList<BarEntry> yVals2 = new ArrayList();
        ArrayList<BarEntry> yVals3 = new ArrayList();
        int startMonth=0;
        if(attendanceFragmentDataType!=null) {
            if (attendanceFragmentDataType.getStartMonth() != null) {
                startMonth = Integer.parseInt(attendanceFragmentDataType.getStartMonth());
            }
            String mPresentDays[] = attendanceFragmentDataType.getMonthlyPresentDays().substring(1, attendanceFragmentDataType.getMonthlyPresentDays().length() - 1).split(",");
            String mAbsentDays[] = attendanceFragmentDataType.getMonthlyAbsentDays().substring(1, attendanceFragmentDataType.getMonthlyAbsentDays().length() - 1).split(",");
            String mLeaveDays[] = attendanceFragmentDataType.getMonthlyLeaveDays().substring(1, attendanceFragmentDataType.getMonthlyLeaveDays().length() - 1).split(",");

            for (int i = startMonth; i < months.length + startMonth; i++) {
                if (i > 12)
                    xvals.add(months[i - months.length - 1]);
                else
                    xvals.add(months[i - 1]);

                yVals1.add(new BarEntry(i, Float.parseFloat(mPresentDays[i - startMonth])));
                yVals2.add(new BarEntry(i, Float.parseFloat(mAbsentDays[i - startMonth])));
                yVals3.add(new BarEntry(i, Float.parseFloat(mLeaveDays[i - startMonth])));
            }

            float groupSpace = 0.4f;
            float barSpace = 0.0f;
            float barWidth = 0.2f;


            BarDataSet set1, set2, set3;
            set1 = new BarDataSet(yVals1, "PRESENTS");
            set1.setColor(Color.parseColor("#19b393"));
            set1.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.graph_text_colour));
            set2 = new BarDataSet(yVals2, "ABSENTS");
            set2.setColor(Color.parseColor("#ec5665"));
            set2.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.graph_text_colour));
            set3 = new BarDataSet(yVals3, "LEAVES");
            set3.setColor(Color.parseColor("#f9ac59"));
            set3.setValueTextColor(ContextCompat.getColor(getActivity(), R.color.graph_text_colour));

            BarData data = new BarData(set1, set2, set3);
            data.setValueFormatter(new ChartValueFormatter());
            chart.setData(data);
            chart.getBarData().setBarWidth(barWidth);
            //chart.getXAxis().setAxisMinimum(chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
            chart.groupBars(0, groupSpace, barSpace);
            chart.setExtraBottomOffset(10f);
            chart.invalidate();

            Legend l = chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setTextColor(ContextCompat.getColor(getActivity(), R.color.graph_axis_colour));
            l.setDrawInside(true);
            l.setXOffset(0f);
            l.setYOffset(20f);
            l.setYEntrySpace(0f);
            l.setTextSize(8f);

            XAxis xAxis = chart.getXAxis();
            xAxis.setAxisLineColor(ContextCompat.getColor(getActivity(), R.color.graph_axis_colour));
            xAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.graph_axis_colour));
            xAxis.setGranularity(1f);
            xAxis.setGranularityEnabled(true);
            xAxis.setCenterAxisLabels(true);
            xAxis.setDrawGridLines(false);
            xAxis.setAxisMinimum(0);
            xAxis.setAxisMaximum(xvals.size());
            xAxis.setLabelCount(xvals.size());
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(xvals));

            chart.getAxisRight().setEnabled(false);
            YAxis yAxis = chart.getAxisLeft();
            yAxis.setAxisLineColor(ContextCompat.getColor(getActivity(), R.color.graph_axis_colour));
            yAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.graph_axis_colour));
            yAxis.setGranularity(1f);
            yAxis.setValueFormatter(new LargeValueFormatter());
            yAxis.setDrawGridLines(false);
            yAxis.setSpaceTop(35f);
            yAxis.setAxisMinimum(0f);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("onViewCreated", "Attendance Fragment");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e("onSaveInstanceState", "Attendance Fragment");
        try {
            outState.putString("absent_days", attendanceFragmentDataType.getAllAbsentDays());
            outState.putString("leave_days", attendanceFragmentDataType.getAllLeaveDays());
            outState.putString("all_working_days", attendanceFragmentDataType.getAllWorkingDays());
            outState.putString("in_time", attendanceFragmentDataType.getInTime());
            outState.putString("out_time", attendanceFragmentDataType.getOutTime());
            outState.putString("income_tax", attendanceFragmentDataType.getIncome_tax());
            outState.putString("date_range", attendanceFragmentDataType.getSalaryRange());
            outState.putString("professional_tax", attendanceFragmentDataType.getProfessional_tax());
            outState.putString("net_deduction", attendanceFragmentDataType.getTotal_deduction());
            outState.putString("net_credit", attendanceFragmentDataType.getNet_credit());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
        Log.e("onViewStateRestored", "Attendance Fragment");
        if (savedInstanceState != null) {
            Log.e("restoring state", "Attendance Fragment");
            try {
                absent_days.setText(savedInstanceState.getString("absent_days"));
                leave_days.setText(savedInstanceState.getString("leave_days"));
                //all_working_days.setText(savedInstanceState.getString("all_working_days"));
                in_time.setText(savedInstanceState.getString("in_time"));
                out_time.setText(savedInstanceState.getString("out_time"));
                income_tax.setText(savedInstanceState.getString("income_tax"));
                date_range.setText(savedInstanceState.getString("date_range"));
                professional_tax.setText(savedInstanceState.getString("professional_tax"));
                net_deduction.setText(savedInstanceState.getString("net_deduction"));
                net_credit.setText(savedInstanceState.getString("net_credit"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("Attendance Fragment", "onstart");
    }

    @Override
    public void onResume() {
        super.onResume();
        //for alert and approve leave fragments.
        Log.e("on resume", "attendanceFragment");
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("on stop", "attendanceFragment");
    }

    @Override
    public void updateUI() {
        Log.e("updateUI", "called");
        if (this.isAdded()) {
            Log.e("attendancefragment", "is added");
            drawChart();

            if(attendanceFragmentDataType!=null) {
                absent_days.setText(attendanceFragmentDataType.getAllAbsentDays());

                leave_days.setText(attendanceFragmentDataType.getAllLeaveDays());
                //all_working_days.setText(attendanceFragmentDataType.getAllWorkingDays());
                in_time.setText(attendanceFragmentDataType.getInTime());
                out_time.setText(attendanceFragmentDataType.getOutTime());
                income_tax.setText(getString(R.string.rupee) + " " + attendanceFragmentDataType.getIncome_tax());
                date_range.setText(attendanceFragmentDataType.getSalaryRange());
                professional_tax.setText(getString(R.string.rupee) + " " + attendanceFragmentDataType.getProfessional_tax());
                net_deduction.setText(getString(R.string.rupee) + " " + attendanceFragmentDataType.getTotal_deduction());
                net_credit.setText(getString(R.string.rupee) + " " + attendanceFragmentDataType.getNet_credit());
            }
        } else {
            Log.e("attendancefragment", "not added");
        }
    }

    @Override
    public void onRefresh() {
        attendance_swipe.setRefreshing(true);
        getAllLeaveDetailsApiCall();
    }

    public void expand(final View v) {
        v.measure(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? CoordinatorLayout.LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

   /* private void getAllLeaveDetailsApiCall() {

        AppController.get_all_leave_details(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        //TODO refresh should be there in swipe to refresh to show progress

                        attendanceFragmentDataType = new AttendanceFragmentDataType();
                        attendanceFragmentDataType.setAllLeaveDays(jsonObject.getJSONObject("data").getString("all_leaves"));
                        attendanceFragmentDataType.setAllAbsentDays(jsonObject.getJSONObject("data").getString("all_absents"));
                        attendanceFragmentDataType.setStartMonth(jsonObject.getJSONObject("data").getString("fin_month"));
                        attendanceFragmentDataType.setMonthlyLeaveDays(jsonObject.getJSONObject("data").getString("monthly_leaves"));
                        attendanceFragmentDataType.setMonthlyAbsentDays(jsonObject.getJSONObject("data").getString("monthly_absents"));
                        attendanceFragmentDataType.setMonthlyPresentDays(jsonObject.getJSONObject("data").getString("monthly_presents"));
                        //attendanceFragmentDataType.setAllWorkingDays(jsonObject.getJSONObject("data").getString("allworkingdays"));
                        //Log.e("start_month", "" + start_month);
                        Log.e("Leave Details", "" + attendanceFragmentDataType);

                        attendanceFragment.updateUI();

                        inTimeApiCall();
                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        //  Toast.makeText(context, R.string.please_wait, Toast.LENGTH_SHORT).show();
                        //  login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "get_details_attendance");
                    } else {
                        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    if (attendance_swipe != null)
                        attendance_swipe.setRefreshing(false);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                if (attendance_swipe != null)
                    attendance_swipe.setRefreshing(false);
                errorFunction(getActivity(), volleyError);
            }
        });
    }*/

    private void getAllLeaveDetailsApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().get_all_leave_details();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null) {
                    if (jObject.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());
                            System.out.println(" LEAVE DETAIL "+jsonObject.toString());
                            String message = jsonObject.getString("message");
                            if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                                //TODO refresh should be there in swipe to refresh to show progress

                                attendanceFragmentDataType = new AttendanceFragmentDataType();
                                attendanceFragmentDataType.setAllLeaveDays(jsonObject.getJSONObject("data").getString("all_leaves"));
                                attendanceFragmentDataType.setAllAbsentDays(jsonObject.getJSONObject("data").getString("all_absents"));
                                attendanceFragmentDataType.setStartMonth(jsonObject.getJSONObject("data").getString("fin_month"));
                                attendanceFragmentDataType.setMonthlyLeaveDays(jsonObject.getJSONObject("data").getString("monthly_leaves"));
                                attendanceFragmentDataType.setMonthlyAbsentDays(jsonObject.getJSONObject("data").getString("monthly_absents"));
                                attendanceFragmentDataType.setMonthlyPresentDays(jsonObject.getJSONObject("data").getString("monthly_presents"));
                                //attendanceFragmentDataType.setAllWorkingDays(jsonObject.getJSONObject("data").getString("allworkingdays"));
                                //Log.e("start_month", "" + start_month);
                                Log.e("Leave Details", "" + attendanceFragmentDataType);

                                attendanceFragment.updateUI();

                                inTimeApiCall();
                            }
                            if (message.equalsIgnoreCase("User not found")) {

                                userNotFound();
                                //  Toast.makeText(context, R.string.please_wait, Toast.LENGTH_SHORT).show();
                                //  login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "get_details_attendance");
                            } else {
                                // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            if (attendance_swipe != null)
                                attendance_swipe.setRefreshing(false);
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                if (attendance_swipe != null)
                    attendance_swipe.setRefreshing(false);
               // errorFunction(getActivity(), volleyError);
            }
        });
    }


   /* private void inTimeApiCall() {

        AppController.in_time(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        attendanceFragmentDataType.setInTime(jsonObject.getJSONObject("data").getString("in_time"));
                        attendanceFragmentDataType.setOutTime(jsonObject.getJSONObject("data").getString("out_time"));
                        Log.e("updateui", "appcontroller");
                        //null when calling from mark attendance
                        updateUI();

                        //null when calling from mark attendance
                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    } else {

                        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                    //attendanceFragment null when calling from mark attendance no need to call salary slip then.
                    salarySlipApiCall();

                } catch (JSONException e) {
                    attendance_swipe.setRefreshing(false);
                    salarySlipApiCall();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                if (attendance_swipe != null)
                    attendance_swipe.setRefreshing(false);
                errorFunction(getActivity(), volleyError);
            }
        });
    }*/

    private void inTimeApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().in_time();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());
System.out.println("IN TIME API CALL : "+jsonObject.toString());
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        attendanceFragmentDataType.setInTime(jsonObject.optJSONObject("data").optString("in_time"));
                        attendanceFragmentDataType.setOutTime(jsonObject.optJSONObject("data").optString("out_time"));
                        Log.e("updateui", "appcontroller");
                        //null when calling from mark attendance
                        updateUI();
                        MainActivity.isintimemarked = true;
                        //null when calling from mark attendance
                        if(jsonObject.optJSONObject("data").has("out_time")){
                            MainActivity.isouttimemarked = true;
                            System.out.println("ISOUTTIMEMARKED : "+jsonObject.toString());
                        }
                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    } else {

                        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                    //attendanceFragment null when calling from mark attendance no need to call salary slip then.
                    salarySlipApiCall();

                } catch (JSONException e) {
                    attendance_swipe.setRefreshing(false);
                    salarySlipApiCall();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                if (attendance_swipe != null)
                    attendance_swipe.setRefreshing(false);
              //  errorFunction(getActivity(), volleyError);
            }
        });
    }


   /* private void salarySlipApiCall() {

        AppController.salary_slip(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        attendanceFragmentDataType.setIncome_tax(jsonObject.getJSONObject("data").getString("income_tax"));
                        attendanceFragmentDataType.setProfessional_tax(jsonObject.getJSONObject("data").getString("professional_tax"));
                        attendanceFragmentDataType.setTotal_deduction(jsonObject.getJSONObject("data").getString("total_deduction_amount"));
                        attendanceFragmentDataType.setNet_credit(jsonObject.getJSONObject("data").getString("net_credit"));
                        attendanceFragmentDataType.setSalaryRange(jsonObject.getJSONObject("data").getString("from_date") + " - " + jsonObject.getJSONObject("data").getString("to_date"));

                        Log.e("updateui", "appcontroller");
                        updateUI();

                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                    if (attendance_swipe != null)
                        attendance_swipe.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (attendance_swipe != null)
                        attendance_swipe.setRefreshing(false);
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                if (attendance_swipe != null)
                    attendance_swipe.setRefreshing(false);
                errorFunction(getActivity(), volleyError);
            }
        });
    }*/

   private void salarySlipApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().salary_slip();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());


                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        attendanceFragmentDataType.setIncome_tax(jsonObject.getJSONObject("data").getString("income_tax"));
                        attendanceFragmentDataType.setProfessional_tax(jsonObject.getJSONObject("data").getString("professional_tax"));
                        attendanceFragmentDataType.setTotal_deduction(jsonObject.getJSONObject("data").getString("total_deduction_amount"));
                        attendanceFragmentDataType.setNet_credit(jsonObject.getJSONObject("data").getString("net_credit"));
                        attendanceFragmentDataType.setSalaryRange(jsonObject.getJSONObject("data").getString("from_date") + " - " + jsonObject.getJSONObject("data").getString("to_date"));

                        Log.e("updateui", "appcontroller");
                        updateUI();

                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                    if (attendance_swipe != null)
                        attendance_swipe.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (attendance_swipe != null)
                        attendance_swipe.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                if (attendance_swipe != null)
                    attendance_swipe.setRefreshing(false);
               // errorFunction(getActivity(), volleyError);
            }
        });
    }
    private void userNotFound() {

        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anim_out, R.anim.anim_in).toBundle();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i, bndlanimation);
        Log.e("tag value", TAG);
        AppController.getInstance().cancelPendingRequests(TAG);

    }


}
