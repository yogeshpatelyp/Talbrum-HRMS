package com.talentcerebrumhrms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.talentcerebrumhrms.datatype.LeavesDatatype;
import com.talentcerebrumhrms.R;

import java.util.ArrayList;

/**
 * Created by saransh on 19-10-2016.
 */

public class LeaveBalanceAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LeavesDatatype> data;

    public LeaveBalanceAdapter(Context mcontext, ArrayList<LeavesDatatype> list) {
        this.context = mcontext;
        this.data = list;
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
            convertView = inflater.inflate(R.layout.listcell_leave_balance, parent, false);
            holder = new ViewHolder();
            holder.total_leaevs_available = (TextView) convertView.findViewById(R.id.leaves_available);
            holder.leaves_used = (TextView) convertView.findViewById(R.id.leaves_used);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.leaves_currently_available = (TextView) convertView.findViewById(R.id.currently_available);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (data.get(position).getType_of_leave() != null && holder.name != null) {
                holder.name.setText(data.get(position).getType_of_leave());
                //  holder.total_leaevs_available.setText(data.get(position).getEligible().substring(0, data.get(position).getEligible().indexOf('.')));
                holder.total_leaevs_available.setText(data.get(position).getEligible());
                holder.leaves_used.setText(data.get(position).getUsed());
                holder.leaves_currently_available.setText(data.get(position).getRemaining());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView total_leaevs_available, leaves_used, leaves_currently_available, name;

    }

    public void updateAdapter(ArrayList<LeavesDatatype> a) {
        //  holidayDatatype.clear();
        data = a;
        //data.addAll(newlist);
        // details = newlist;
        this.notifyDataSetChanged();
    }
}
