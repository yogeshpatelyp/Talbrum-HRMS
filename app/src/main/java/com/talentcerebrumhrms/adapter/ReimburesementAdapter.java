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
import com.talentcerebrumhrms.datatype.RembursementDatatype;

import java.util.ArrayList;

/**
 * Created by saransh on 17-10-2016.
 */

public class ReimburesementAdapter extends BaseAdapter {
    private Context mcontext;
    private ArrayList<RembursementDatatype> data;

    public ReimburesementAdapter(Context context, ArrayList<RembursementDatatype> arraydata) {
        this.mcontext = context;
        this.data = arraydata;

    }

    @Override
    public int getCount() {
        if (data == null) {
            Log.e("reimbursement adapter", "0");
            return 0;
        } else {
            Log.e("reimbursement adapter", String.valueOf(data.size()));
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

        inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listcell_reimbursement, parent, false);
            holder = new ViewHolder();
            holder.head = (TextView) convertView.findViewById(R.id.reim_head);
            holder.date = (TextView) convertView.findViewById(R.id.reimb_date);
            holder.amount = (TextView) convertView.findViewById(R.id.reimb_amount);
            holder.status = (TextView) convertView.findViewById(R.id.reimb_status);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (data != null) {
                holder.head.setText(data.get(position).getDetails());
                holder.amount.setText(data.get(position).getAmount());
                holder.status.setText(data.get(position).getAction());
                if (data.get(position).getAction().equalsIgnoreCase("pending")) {
                    holder.status.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.pending_color));
                } else if (data.get(position).getAction().equalsIgnoreCase("approved")) {
                    holder.status.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.colorAccent));
                } else {
                    holder.status.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.blue));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView head, date, status, amount;
    }

    public void updateAdapter(ArrayList<RembursementDatatype> newlist) {
        //data.clear();
        //data.addAll(newlist);
        data = newlist;
        this.notifyDataSetChanged();
    }
}
