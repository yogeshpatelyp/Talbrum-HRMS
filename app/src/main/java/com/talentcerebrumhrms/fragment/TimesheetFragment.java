package com.talentcerebrumhrms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.talentcerebrumhrms.activity.UploadTimesheetActivity;
import com.talentcerebrumhrms.adapter.TimesheetPagerAdapter;
import com.talentcerebrumhrms.datatype.UploadTimesheetDataType;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.DatePickerEditText;
import com.talentcerebrumhrms.utils.Utility;

import static com.talentcerebrumhrms.utils.AppController.timesheet_upload_array;

/**
 * Created by Harshit on 10-May-17.
 */

public class TimesheetFragment extends Fragment {
    View rootview;
    Button create;
    EditText edittest_date_from, edittest_date_to, edittext_duration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_timesheet, null);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0.0f);
        }

        getActivity().invalidateOptionsMenu();
        TimesheetPagerAdapter mTimesheetPagerAdapter = new TimesheetPagerAdapter(getChildFragmentManager());
        ViewPager mViewPager = (ViewPager) rootview.findViewById(R.id.container);
        mViewPager.setAdapter(mTimesheetPagerAdapter);

        TabLayout tabLayout = (TabLayout) rootview.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = inflater.inflate(R.layout.dialog_upload_timesheet, null);
                alertDialog.setView(convertView);
                alertDialog.show();

                edittest_date_from = (EditText) convertView.findViewById(R.id.date_from);
                edittest_date_to = (EditText) convertView.findViewById(R.id.date_to);
                edittext_duration = (EditText) convertView.findViewById(R.id.duration_edittext);
                create = (Button) convertView.findViewById(R.id.create);

                final DatePickerEditText fromdatepickertext = new DatePickerEditText(alertDialog.getContext(), getActivity().getFragmentManager(), edittest_date_from);
                final DatePickerEditText todatepickertext = new DatePickerEditText(alertDialog.getContext(), getActivity().getFragmentManager(), edittest_date_to);
                fromdatepickertext.setMaxDateToPresent();
                todatepickertext.setMaxDateToPresent();

                edittest_date_from.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
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
                        if (!edittest_date_from.getText().toString().trim().equalsIgnoreCase(""))
                            todatepickertext.setMinPickerDate(Utility.dateToMilisec(edittest_date_from.getText().toString()));
                        if (!edittest_date_to.getText().toString().trim().equalsIgnoreCase(""))
                            fromdatepickertext.setMaxPickerDate(Utility.dateToMilisec(edittest_date_to.getText().toString()));
                    }
                });

                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edittest_date_from.getText().toString().length() == 0)
                            Toast.makeText(getActivity(), "Enter From Date !!", Toast.LENGTH_SHORT).show();
                        else if (edittest_date_to.getText().toString().length() == 0)
                            Toast.makeText(getActivity(), "Enter To Date !!", Toast.LENGTH_SHORT).show();
                        else {
                            String start_date = edittest_date_from.getText().toString();
                            int no_of_days = Utility.milisecToDays(Utility.dateToMilisec(edittest_date_to.getText().toString()) - Utility.dateToMilisec(edittest_date_from.getText().toString()));
                            String default_time = edittext_duration.getText().toString();

                            timesheetUploadData(start_date, no_of_days, default_time);

                            Intent intent = new Intent(getActivity(), UploadTimesheetActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        return rootview;
    }

    private void timesheetUploadData(String start_date, int no_of_days, String default_time) {
        timesheet_upload_array.clear();
        long date_to_milis = Utility.dateToMilisec(start_date);
        for (int i = 0; i <= no_of_days; i++) {
            String milis_to_date = Utility.milisecToDate(date_to_milis);
            addRow(milis_to_date, "Present", default_time, "", "");
            date_to_milis += 86400000;
        }
    }

    private void addRow(String working_date, String working_status, String working_hour, String leave_type_id, String remark) {
        UploadTimesheetDataType a = new UploadTimesheetDataType();
        a.setWorkingDate(working_date);
        a.setWorkingStatus(working_status);
        a.setWorkingHour(working_hour);
        a.setLeaveTypeId(leave_type_id);
        a.setRemark(remark);
        timesheet_upload_array.add(a);
    }

}
