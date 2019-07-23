package com.talentcerebrumhrms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.talentcerebrumhrms.R;

import java.util.ArrayList;

/**
 * Created by Harshit on 13-Jul-17.
 */

public class FeedbackAdapter extends BaseAdapter {
    Context context;
    private ArrayList<String> feedback_titles;
    private ArrayList<Integer> feedback_icons;

    public FeedbackAdapter(Context context, ArrayList<String> feedback_titles, ArrayList<Integer> feedback_icons) {
        this.context = context;
        this.feedback_titles = feedback_titles;
        this.feedback_icons = feedback_icons;
    }

    @Override
    public int getCount() {
        return feedback_titles.size();
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
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            grid = new View(context);
            grid = inflater.inflate(R.layout.listcell_feedback, null);
            ImageView icon = (ImageView) grid.findViewById(R.id.icon);
            TextView text = (TextView) grid.findViewById(R.id.text);
            icon.setImageResource(feedback_icons.get(position));
            text.setText(feedback_titles.get(position));
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}
