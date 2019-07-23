package com.talentcerebrumhrms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.talentcerebrumhrms.datatype.LeaveTypeDatatype;
import com.talentcerebrumhrms.R;

import java.util.ArrayList;

/**
 * Created by Harshit on 23-Jun-17.
 */

class SelectSpinnerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LeaveTypeDatatype> data;

    SelectSpinnerAdapter(Context mcontext, ArrayList<LeaveTypeDatatype> list) {
        this.context = mcontext;
        this.data = list;
    }

    @Override
    public int getCount() {
        return data.size();
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
            convertView = inflater.inflate(R.layout.listcell_spinner_select, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            holder.name.setText(data.get(position).getType());
            // Log.e("type", data.get(position).getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;

    }

    private class ViewHolder {
        TextView name;
    }

    public void updateAdapter(ArrayList<LeaveTypeDatatype> newlist) {
        //data.clear();
        //data.addAll(newlist);
        data = newlist;
        this.notifyDataSetChanged();
    }
}
