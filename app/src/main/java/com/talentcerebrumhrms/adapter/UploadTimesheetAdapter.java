package com.talentcerebrumhrms.adapter;

import android.app.Activity;
import android.content.Context;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.DatePickerEditText;

import static com.talentcerebrumhrms.activity.UploadTimesheetActivity.printUploadTimesheetArray;
import static com.talentcerebrumhrms.utils.AppController.leave_type_array;
import static com.talentcerebrumhrms.utils.AppController.timesheet_upload_array;

/**
 * Created by Harshit on 19-Jun-17.
 */

public class UploadTimesheetAdapter extends RecyclerView.Adapter<UploadTimesheetAdapter.MyViewHolder> {

    Context context;
    private String working_status_array[] = {"Present", "Leave", "Holiday", "Absent", "Half Day"};

    public UploadTimesheetAdapter(Context context) {
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener, View.OnClickListener, TextWatcher {
        EditText working_date, hours, remark;
        Spinner working_status, type;
        TextView hours_type;
        ImageButton cross;
        LinearLayout hours_type_layout;

        SelectSpinnerAdapter selectSpinnerAdapter;

        MyViewHolder(View itemView) {
            super(itemView);
            working_date = (EditText) itemView.findViewById(R.id.working_date);
            hours_type = (TextView) itemView.findViewById(R.id.hours_type);
            hours = (EditText) itemView.findViewById(R.id.hours);
            remark = (EditText) itemView.findViewById(R.id.remark);
            working_status = (Spinner) itemView.findViewById(R.id.working_status);
            type = (Spinner) itemView.findViewById(R.id.type);
            hours_type_layout = (LinearLayout) itemView.findViewById(R.id.hours_type_layout);
            cross = (ImageButton) itemView.findViewById(R.id.cross);

            DatePickerEditText datepickertext = new DatePickerEditText(context, ((Activity) context).getFragmentManager(), working_date);
            datepickertext.setMaxDateToPresent();
            working_date.addTextChangedListener(this);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, working_status_array);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            working_status.setAdapter(adapter);
            working_status.setOnItemSelectedListener(this);

            selectSpinnerAdapter = new SelectSpinnerAdapter(context, leave_type_array);
            type.setAdapter(selectSpinnerAdapter);
            type.setOnItemSelectedListener(this);

            cross.setOnClickListener(this);
            hours.addTextChangedListener(this);
            remark.addTextChangedListener(this);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            Spinner spinner = (Spinner) parent;
            switch (spinner.getId()) {

                case R.id.working_status:

                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    timesheet_upload_array.get(getAdapterPosition()).setWorkingStatus(working_status_array[position].toLowerCase());
                    switch (working_status_array[position]) {
                        case "Present":
                            hours_type_layout.setVisibility(View.VISIBLE);
                            hours.setVisibility(View.VISIBLE);
                            type.setVisibility(View.GONE);
                            hours_type.setText(R.string.hours);
                            timesheet_upload_array.get(getAdapterPosition()).setWorkingHour(hours.getText().toString());
                            timesheet_upload_array.get(getAdapterPosition()).setLeaveTypeId("");
                            break;
                        case "Leave":
                            hours_type_layout.setVisibility(View.VISIBLE);
                            hours.setVisibility(View.GONE);
                            type.setVisibility(View.VISIBLE);
                            hours_type.setText(R.string.type);
                            timesheet_upload_array.get(getAdapterPosition()).setWorkingHour("");
                            timesheet_upload_array.get(getAdapterPosition()).setLeaveTypeId(leave_type_array.get(type.getSelectedItemPosition()).getId());
                            break;
                        case "Holiday":
                            hours_type_layout.setVisibility(View.GONE);
                            hours.setVisibility(View.GONE);
                            type.setVisibility(View.GONE);
                            timesheet_upload_array.get(getAdapterPosition()).setWorkingHour("");
                            timesheet_upload_array.get(getAdapterPosition()).setLeaveTypeId("");
                            break;
                        case "Absent":
                            hours_type_layout.setVisibility(View.GONE);
                            hours.setVisibility(View.GONE);
                            type.setVisibility(View.GONE);
                            timesheet_upload_array.get(getAdapterPosition()).setWorkingHour("");
                            timesheet_upload_array.get(getAdapterPosition()).setLeaveTypeId("");
                            break;
                        case "Half Day":
                            hours_type_layout.setVisibility(View.VISIBLE);
                            hours.setVisibility(View.GONE);
                            type.setVisibility(View.VISIBLE);
                            hours_type.setText(R.string.type);
                            timesheet_upload_array.get(getAdapterPosition()).setWorkingHour("");
                            timesheet_upload_array.get(getAdapterPosition()).setLeaveTypeId(leave_type_array.get(type.getSelectedItemPosition()).getId());
                            break;
                        default:
                            hours_type_layout.setVisibility(View.VISIBLE);
                            hours.setVisibility(View.VISIBLE);
                            type.setVisibility(View.GONE);
                            hours_type.setText(R.string.hours);
                            timesheet_upload_array.get(getAdapterPosition()).setWorkingHour(hours.getText().toString());
                            timesheet_upload_array.get(getAdapterPosition()).setLeaveTypeId("");
                            break;
                    }
                    break;

                case R.id.type:

                    timesheet_upload_array.get(getAdapterPosition()).setLeaveTypeId(leave_type_array.get(position).getId());
                    break;
            }
            printUploadTimesheetArray();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        public void onClick(View v) {
            Log.e("Child Position", "" + getAdapterPosition());
            timesheet_upload_array.remove(getAdapterPosition());
            printUploadTimesheetArray();
            notifyItemRemoved(getAdapterPosition());
            notifyItemRangeChanged(getAdapterPosition(), timesheet_upload_array.size());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (working_date.getText().hashCode() == s.hashCode())
                timesheet_upload_array.get(getAdapterPosition()).setWorkingDate(working_date.getText().toString());
            else if (hours.getText().hashCode() == s.hashCode())
                timesheet_upload_array.get(getAdapterPosition()).setWorkingHour(hours.getText().toString());
            else if (remark.getText().hashCode() == s.hashCode())
                timesheet_upload_array.get(getAdapterPosition()).setRemark(remark.getText().toString());
            printUploadTimesheetArray();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listcell_upload_timesheet, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.working_date.setText(timesheet_upload_array.get(position).getWorkingDate());
        holder.hours.setText(timesheet_upload_array.get(position).getWorkingHour());
        if (!timesheet_upload_array.get(position).getLeaveTypeId().equalsIgnoreCase("")) {
            holder.working_status.setSelection(getIndexOfWorkingArray(timesheet_upload_array.get(position).getWorkingStatus()));
            holder.type.setSelection(getIndexOfLeaveType(timesheet_upload_array.get(position).getLeaveTypeId()));
        }
        holder.remark.setText(timesheet_upload_array.get(position).getRemark());
    }

    private int getIndexOfWorkingArray(String workingStatus) {
        for (int i = 0; i < working_status_array.length; i++) {
            if (working_status_array[i].equalsIgnoreCase(workingStatus))
                return i;
        }
        return 0;
    }

    private int getIndexOfLeaveType(String leaveTypeId) {
        for (int i = 0; i < leave_type_array.size(); i++) {
            if (leave_type_array.get(i).getId().equalsIgnoreCase(leaveTypeId))
                return i;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return timesheet_upload_array.size();
    }
}
