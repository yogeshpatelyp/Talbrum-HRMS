package com.talentcerebrumhrms.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.talentcerebrumhrms.datatype.TimesheetDataType;
import com.talentcerebrumhrms.R;

import java.util.ArrayList;

/**
 * Created by Harshit on 15-Jun-17.
 */

public class TimesheetAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TimesheetDataType> data;
    private String status;

    public TimesheetAdapter(Context mcontext, ArrayList<TimesheetDataType> list, String status) {
        this.context = mcontext;
        this.data = list;
        this.status = status;
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        } else {
            return data.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listcell_app_rej_timesheet, parent, false);
            holder = new ViewHolder();
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.from_date = (TextView) convertView.findViewById(R.id.from_date);
            holder.to_date = (TextView) convertView.findViewById(R.id.to_date);
            holder.submitted_date = (TextView) convertView.findViewById(R.id.submitted_date);
            holder.total_days = (TextView) convertView.findViewById(R.id.total_days);
            holder.signed_by = (TextView) convertView.findViewById(R.id.signed_by);
            holder.signed_on = (TextView) convertView.findViewById(R.id.signed_on);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            holder.status.setText(status.toUpperCase());
            if (status.equalsIgnoreCase(context.getResources().getString(R.string.approved)))
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            else
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.opaque_red));
            holder.from_date.setText(data.get(position).getFromDate());
            holder.to_date.setText(data.get(position).getToDate());
            holder.submitted_date.setText(data.get(position).getSubmittedDate());
            holder.total_days.setText(data.get(position).getTotalDays());
            holder.signed_by.setText(data.get(position).getSignedBy().toUpperCase());
            holder.signed_on.setText(data.get(position).getSignedOn());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView status, from_date, to_date, submitted_date, total_days, signed_by, signed_on;

    }

    public void updateAdapter(ArrayList<TimesheetDataType> a) {
        data = a;
        this.notifyDataSetChanged();
    }
}
