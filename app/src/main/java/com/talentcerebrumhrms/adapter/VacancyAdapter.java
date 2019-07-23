package com.talentcerebrumhrms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.talentcerebrumhrms.datatype.VacancyDataType;
import com.talentcerebrumhrms.R;

import java.util.ArrayList;

/**
 * Created by Harshit on 06-Jul-17.
 */

public class VacancyAdapter extends BaseAdapter {
    private Context mcontext;
    private ArrayList<VacancyDataType> data;

    public VacancyAdapter(Context context, ArrayList<VacancyDataType> arraydata) {
        this.mcontext = context;
        this.data = arraydata;
    }

    @Override
    public int getCount() {
        if (data == null) {
            Log.e("vacancy adapter", "0");
            return 0;
        } else {
            Log.e("vacancy adapter", String.valueOf(data.size()));
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
            convertView = inflater.inflate(R.layout.listcell_vacancy, parent, false);
            holder = new ViewHolder();
            holder.role = (TextView) convertView.findViewById(R.id.role);
            holder.location = (TextView) convertView.findViewById(R.id.location);
            holder.designation = (TextView) convertView.findViewById(R.id.designation);
            holder.posted_on = (TextView) convertView.findViewById(R.id.posted_on);
            holder.id = (TextView) convertView.findViewById(R.id.id);
            holder.no_of_posts = (TextView) convertView.findViewById(R.id.no_of_posts);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            if (data != null) {
                holder.role.setText(data.get(position).getRole());
                holder.location.setText(data.get(position).getLocation());
                holder.designation.setText(data.get(position).getDesignation());
                holder.posted_on.setText(data.get(position).getPostedOn());
                holder.id.setText(data.get(position).getId());
                holder.no_of_posts.setText(data.get(position).getNoOfPosts());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView role, location, designation, posted_on, id, no_of_posts;
    }

    public void updateAdapter(ArrayList<VacancyDataType> newlist) {
        data = newlist;
        this.notifyDataSetChanged();
    }
}
