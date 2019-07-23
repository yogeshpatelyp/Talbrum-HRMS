package com.talentcerebrumhrms.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.datatype.LeaveAppliedDataType;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by saransh on 05-11-2016.
 */

public class LeaveDetailAdapter extends BaseAdapter {
    Context context;
    private ArrayList<LeaveAppliedDataType> data, templist;

    public LeaveDetailAdapter(Context mcontext, ArrayList<LeaveAppliedDataType> list) {
        this.context = mcontext;
        data = list;
        this.templist = new ArrayList<>();
        templist.addAll(data);
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
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater inflater;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listcell_leave_detail, parent, false);
            holder = new ViewHolder();
            holder.from = (TextView) convertView.findViewById(R.id.from);
            holder.to = (TextView) convertView.findViewById(R.id.to);
            holder.days = (TextView) convertView.findViewById(R.id.leave_day);
            holder.approved_by = (TextView) convertView.findViewById(R.id.approved_by);
            holder.status = (TextView) convertView.findViewById(R.id.leave_status);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {

            holder.from.setText(data.get(position).getFrom_date());
            holder.to.setText(data.get(position).getTo_date());
            //  holder.total_leaevs_available.setText(data.get(position).getEligible().substring(0, data.get(position).getEligible().indexOf('.')));

            if (Float.parseFloat(data.get(position).getNumber_of_days()) <= 1) {
                holder.days.setText(data.get(position).getNumber_of_days() + " Day");
            } else
                holder.days.setText(data.get(position).getNumber_of_days() + " Days");
            holder.approved_by.setText(data.get(position).getAction_taken_by());
            holder.status.setText(data.get(position).getStatus());
            if (data.get(position).getStatus().trim().equalsIgnoreCase("approved")) {
                holder.status.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorAccent));
            } else if (data.get(position).getStatus().trim().equalsIgnoreCase("Rejected"))
                holder.status.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.pending_color));
            else if (data.get(position).getStatus().trim().equalsIgnoreCase("pending"))
                holder.status.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.blue));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView from, to, approved_by, status, days;

    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Log.e("length list 1", String.valueOf(data.size()));
        Log.e("length templist", String.valueOf(templist.size()));
        data.clear();

        if (charText.length() == 0) {
            data.addAll(templist);
        } else {
            for (LeaveAppliedDataType wp : templist) {
                if (wp.getStatus().toLowerCase(Locale.getDefault()).contains(charText) || wp.getFrom_date().toLowerCase(Locale.getDefault()).contains(charText) || wp.getTo_date().toLowerCase(Locale.getDefault()).contains(charText) || wp.getNumber_of_days().toLowerCase(Locale.getDefault()).contains(charText) || wp.getAction_taken_by().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(wp);
                    Log.e("length list", String.valueOf(data.size()));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<LeaveAppliedDataType> a) {

        data = a;
        this.templist = new ArrayList<>();
        templist.clear();
        templist.addAll(data);

        this.notifyDataSetChanged();
    }
}
