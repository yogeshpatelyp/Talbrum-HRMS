package com.talentcerebrumhrms.fragment;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
//import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.activity.AlertActivity;
import com.talentcerebrumhrms.activity.ApproveLeavesActivity;
import com.talentcerebrumhrms.activity.LoginActivity;
import com.talentcerebrumhrms.adapter.SectionsPagerAdapter;
import com.talentcerebrumhrms.adapter.SelectDialogAdapter;
import com.talentcerebrumhrms.datatype.AlertDataType;
import com.talentcerebrumhrms.datatype.LeaveApproveDatatype;
import com.talentcerebrumhrms.datatype.LeaveTypeDatatype;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;
import com.talentcerebrumhrms.utils.DatePickerEditText;
import com.talentcerebrumhrms.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.TAG;
import static com.talentcerebrumhrms.utils.AppController.alert_data_array;
import static com.talentcerebrumhrms.utils.AppController.alert_size;
import static com.talentcerebrumhrms.utils.AppController.approve_size;
import static com.talentcerebrumhrms.utils.AppController.leave_approve_array;
import static com.talentcerebrumhrms.utils.AppController.leave_type_array;

/**
 * Created by saransh on 29-09-2016.
 */

public class LeaveFragment extends Fragment {
    View rootview;
    SectionsPagerAdapter mSectionsPagerAdapter;
    private static SelectDialogAdapter selectDialogAdapter;
    ViewPager mViewPager;
    Button apply_button;
    EditText edittest_date_from, edittest_date_to, reason_edittext;
    TextView working_days_textview;
    Dialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.fragment_leave, null);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0.0f);
        }
      /*  if (AppController.approve_size != AppController.leave_approve_array.size()) {
            AppController.approve_size = AppController.leave_approve_array.size();
            Log.e("approve size AEF", String.valueOf(AppController.approve_size));
            Log.e("approve array size AEF", String.valueOf(AppController.leave_approve_array.size()));
            getActivity().invalidateOptionsMenu();
        }

        if (AppController.alert_size != AppController.alert_data_array.size()) {
            AppController.alert_size = AppController.alert_data_array.size();
            Log.e("alert size AEF", String.valueOf(AppController.alert_size));
            Log.e("alert array size AEF", String.valueOf(AppController.alert_data_array.size()));
            getActivity().invalidateOptionsMenu();
        }*/
        getActivity().invalidateOptionsMenu();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        selectDialogAdapter = new SelectDialogAdapter(getActivity(), leave_type_array);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootview.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) rootview.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Answers.getInstance().logCustom(new CustomEvent("Leaves Fab Clicked"));
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = inflater.inflate(R.layout.dialog_apply_leave_select, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("Select Leave");
                ListView lv = (ListView) convertView.findViewById(R.id.select_listview);
                Log.e("setting adapter", "set");
                lv.setAdapter(selectDialogAdapter);
                // alertDialog.show();

                final AlertDialog close_dialog = alertDialog.create();
                close_dialog.show();

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        //apply leave dialog here.
                        Answers.getInstance().logCustom(new CustomEvent("Leaves " + leave_type_array.get(position).getType() + " Clicked"));
                        final TextView name = (TextView) view.findViewById(R.id.name);
                        close_dialog.cancel();
                        dialog = new Dialog(getActivity(), R.style.theme_dialog);


                        //This makes the dialog take up the full width
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        Window window = dialog.getWindow();
                        lp.copyFrom(window.getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        lp.dimAmount = 0.5f;
                        //  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                        dialog.setTitle("Applying for " + name.getText().toString());
                        dialog.setContentView(R.layout.dialog_apply_leave);

                        ArrayList<String> reason_list = new ArrayList<>();
                        try {
                            reason_list.add("-Select Reason-");
                            for (int i = 0; i < AppController.leave_reason_array.size(); i++) {
                                reason_list.add(AppController.leave_reason_array.get(i).getType());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            reason_list.add("-Select Reason-");
                        }
                        final Spinner s = (Spinner) dialog.findViewById(R.id.spinner_reason);
                        final Spinner spinner_from = (Spinner) dialog.findViewById(R.id.spinner_from);
                        final Spinner spinner_to = (Spinner) dialog.findViewById(R.id.spinner_to);


                        apply_button = (Button) dialog.findViewById(R.id.apply_button);
                        working_days_textview = (TextView) dialog.findViewById(R.id.working_days_textview);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_spinner_dropdown_item, reason_list);
                        s.setAdapter(adapter);

                        edittest_date_from = (EditText) dialog.findViewById(R.id.date_from);
                        edittest_date_to = (EditText) dialog.findViewById(R.id.date_to);
                        reason_edittext = (EditText) dialog.findViewById(R.id.reason_edittext);
                        final DatePickerEditText fromdatepickertext = new DatePickerEditText(dialog.getContext(), getActivity().getFragmentManager(), edittest_date_from);
                        final DatePickerEditText todatepickertext = new DatePickerEditText(dialog.getContext(), getActivity().getFragmentManager(), edittest_date_to);
                        fromdatepickertext.setMinDateToPresent();
                        todatepickertext.setMinDateToPresent();

                        //textchange listeners to calculate number of days
                        edittest_date_from.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                cal_working_days(edittest_date_from.getText().toString(), edittest_date_to.getText().toString(), spinner_from.getSelectedItem().toString(), spinner_to.getSelectedItem().toString(), leave_type_array.get(position).getId());
                                if (!edittest_date_from.getText().toString().trim().equalsIgnoreCase(""))
                                    todatepickertext.setMinPickerDate(Utility.dateToMilisec(edittest_date_from.getText().toString()));
                                if (!edittest_date_to.getText().toString().trim().equalsIgnoreCase(""))
                                    fromdatepickertext.setMaxPickerDate(Utility.dateToMilisec(edittest_date_to.getText().toString()));
                            }
                        });
                        edittest_date_to.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                cal_working_days(edittest_date_from.getText().toString(), edittest_date_to.getText().toString(), spinner_from.getSelectedItem().toString(), spinner_to.getSelectedItem().toString(), leave_type_array.get(position).getId());
                                if (!edittest_date_from.getText().toString().trim().equalsIgnoreCase(""))
                                    todatepickertext.setMinPickerDate(Utility.dateToMilisec(edittest_date_from.getText().toString()));
                                if (!edittest_date_to.getText().toString().trim().equalsIgnoreCase(""))
                                    fromdatepickertext.setMaxPickerDate(Utility.dateToMilisec(edittest_date_to.getText().toString()));
                            }
                        });


                        spinner_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                Log.e("selected_item from", spinner_from.getSelectedItem().toString());
                                cal_working_days(edittest_date_from.getText().toString(), edittest_date_to.getText().toString(), spinner_from.getSelectedItem().toString(), spinner_to.getSelectedItem().toString(), leave_type_array.get(position).getId());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        spinner_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                Log.e("selected_item to", spinner_to.getSelectedItem().toString());

                                cal_working_days(edittest_date_from.getText().toString(), edittest_date_to.getText().toString(), spinner_from.getSelectedItem().toString(), spinner_to.getSelectedItem().toString(), leave_type_array.get(position).getId());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        apply_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Answers.getInstance().logCustom(new CustomEvent("Leave " + leave_type_array.get(position).getType() + " Applied"));
                                if (edittest_date_from.getText().toString().trim().equalsIgnoreCase("")) {
                                    Toast.makeText(getActivity(), R.string.from_date, Toast.LENGTH_SHORT).show();

                                } else if (edittest_date_to.getText().toString().trim().equalsIgnoreCase("")) {
                                    Toast.makeText(getActivity(), R.string.to_date, Toast.LENGTH_SHORT).show();
                                } else if (reason_edittext.getText().toString().trim().equalsIgnoreCase("")) {
                                    Toast.makeText(getActivity(), R.string.enter_reason, Toast.LENGTH_SHORT).show();

                                } else {
                                    //call api
                                    applyLeaveApiCall(reason_edittext.getText().toString(), edittest_date_from.getText().toString(), edittest_date_to.getText().toString(), new Utility().day_type(spinner_from.getSelectedItem().toString()), new Utility().day_type(spinner_to.getSelectedItem().toString()), working_days_textview.getText().toString(), leave_type_array.get(position).getId(), indexof(AppController.leave_reason_array, s.getSelectedItem().toString()));

                                }

                            }
                        });

                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();

                        //window attributes after showing dialogbox
                        window.setAttributes(lp);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        window.setBackgroundDrawableResource(android.R.color.transparent);
                    }
                });
            }
        });


        //calucalting number of working days
        //edittest_date_from.
        return rootview;
    }

    public static void notifychange() {
        if (selectDialogAdapter != null)
            selectDialogAdapter.updateAdapter(leave_type_array);
    }

    private void cal_working_days(String from_date, String to_date, String from_duration, String to_duration, String leave_type) {

        if (from_date.length() > 6 && to_date.length() > 6 && !from_date.equalsIgnoreCase("") && !to_date.equalsIgnoreCase("")) {

            Log.e("cal_working_days", from_date + "\t" + to_date + " " + from_duration + " " + to_duration + " " + leave_type);

            workingDaysApiCall(leave_type, from_date, to_date, new Utility().day_type(to_duration), new Utility().day_type(from_duration));
        }


    }

    String indexof(ArrayList<LeaveTypeDatatype> a, String value) {
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getType().equalsIgnoreCase(value)) {
                return a.get(i).getId();
            }
        }
        Log.e("error indexof", "leave fragment");
        return "";
    }

    @Override
    public void onResume() {
        super.onResume();
        //for alert and approve leave fragments.
        Log.e("on resume", "leaveFragment");
        getActivity().invalidateOptionsMenu();
    }

   /* private void applyLeaveApiCall(final String reason, final String from, final String to, final String from_duration, final String to_duration, final String working_days, final String leave_type, final String leave_reason) {

        AppController.apply_leave(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        dialog.cancel();
                        Toast.makeText(getActivity(), R.string.applied_succesfully, Toast.LENGTH_SHORT).show();
                        approveLeaveApiCall();
                        getAlertsApiCall();
                    } else if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        applyLeaveApiCall(reason, from, to, from_duration, to_duration, working_days, leave_type, leave_reason);

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }, reason, from, to, from_duration, to_duration, working_days, leave_type, leave_reason);

    }*/

    private void applyLeaveApiCall(final String reason, final String from, final String to, final String from_duration, final String to_duration, final String working_days, final String leave_type, final String leave_reason) {
        JSONObject leave_json = new JSONObject();
        try {

            JSONObject period = new JSONObject();
            period.put("from", from);
            period.put("from_duration", from_duration);
            period.put("to_duration", to_duration);
            period.put("to", to);
            leave_json.put("reason", new JSONObject().put("leave", reason));
            leave_json.put("period", period);
            leave_json.put("working", new JSONObject().put("days", working_days));
            leave_json.put("type", new JSONObject().put("leave", leave_type));
            leave_json.put("leave_reason", new JSONObject().put("id", leave_reason));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //dialogapply.cancel();
        Log.e(TAG + "apply_leave", String.valueOf(leave_json));
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(leave_json))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "attendance_details", String.valueOf(leave_json));
        Call<JsonObject> call = ApiUtil.getServiceClass().apply_leave(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        dialog.cancel();
                        Toast.makeText(getActivity(), R.string.applied_succesfully, Toast.LENGTH_SHORT).show();
                        approveLeaveApiCall();
                        getAlertsApiCall();
                    } else if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        applyLeaveApiCall(reason, from, to, from_duration, to_duration, working_days, leave_type, leave_reason);

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    /*private void approveLeaveApiCall() {

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

                    try {
                        Log.d("invalidate options", "leave_approve");
                        getActivity().invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    ApproveLeavesActivity.updateadapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                errorFunction(getActivity(), volleyError);
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

                    try {
                        Log.d("invalidate options", "leave_approve");
                        getActivity().invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    ApproveLeavesActivity.updateadapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

              //  errorFunction(getActivity(), volleyError);
            }
        });

    }

    /*private void getAlertsApiCall() {

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
                                a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
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
                    try {
                        Log.e("alert invalidate", "true");
                        getActivity().invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    AlertActivity.updateadapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                errorFunction(getActivity(), volleyError);
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
                                a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
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
                    try {
                        Log.e("alert invalidate", "true");
                        getActivity().invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    AlertActivity.updateadapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

               // errorFunction(getActivity(), volleyError);
            }
        });

    }

    /*private void workingDaysApiCall(final String leave_type, final String from, final String to, final String to_duration, final String from_duration) {

        AppController.working_days(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        if (working_days_textview != null) {
                            working_days_textview.setText(jsonObject.getString("data"));
                        }


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

                Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        workingDaysApiCall(leave_type, from, to, to_duration, from_duration);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }, leave_type, from, to, to_duration, from_duration);

    }*/

    private void workingDaysApiCall(final String leave_type, final String from, final String to, final String to_duration, final String from_duration) {
        JSONObject work_days = new JSONObject();
        try {
            work_days.put("typeleave", leave_type);
            work_days.put("from", from);
            work_days.put("to", to);
            work_days.put("to_duration", to_duration);
            work_days.put("from_duration", from_duration);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(work_days))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "work_days", String.valueOf(work_days));
        Call<JsonObject> call = ApiUtil.getServiceClass().working_days(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                    if(jObject!=null) {
                       if (jObject.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(jObject.body().toString());

                                String message = jsonObject.getString("message");
                                if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                                    if (working_days_textview != null) {
                                        working_days_textview.setText(jsonObject.getString("data"));
                                    }


                                }
                                if (message.equalsIgnoreCase("User not found")) {

                                    userNotFound();
                                    // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                   }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        workingDaysApiCall(leave_type, from, to, to_duration, from_duration);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
