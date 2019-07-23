package com.talentcerebrumhrms.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.talentcerebrumhrms.datatype.HolidayDatatype;
import com.talentcerebrumhrms.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by saransh on 17-10-2016.
 */

public class ListHolidayAdapter extends BaseAdapter {

    private Context mcontext;
    private ArrayList<HolidayDatatype> holidayDatatype;

    public ListHolidayAdapter(Context context, ArrayList<HolidayDatatype> holiday) {
        this.mcontext = context;
        this.holidayDatatype = holiday;
    }

    @Override
    public int getCount() {

        if (holidayDatatype == null) {
            return 0;
        } else {
            return holidayDatatype.size();
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
            convertView = inflater.inflate(R.layout.listcell_holiday, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.day = (ImageView) convertView.findViewById(R.id.day);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Log.e("call check", "check adapter");
        try {
            if (holidayDatatype.get(position).getName() != null) {
                holder.name.setText(holidayDatatype.get(position).getName());
                holder.date.setText(holidayDatatype.get(position).getDate());
                holder.day.setImageDrawable(iconGenerator(holidayDatatype.get(position)));

                String s[] = holidayDatatype.get(position).getDate().split(" ");

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
                String month_name = dateFormat.format(cal.getTime());

                if (s[1].equalsIgnoreCase(month_name))
                    convertView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.holiday_select_color));
                else
                    convertView.setBackgroundColor(Color.WHITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView name, date;
        ImageView day;

    }

    public void updateAdapter(ArrayList<HolidayDatatype> a) {
        //  holidayDatatype.clear();
        holidayDatatype = a;
        //data.addAll(newlist);
        // details = newlist;
        this.notifyDataSetChanged();
    }

    private TextDrawable iconGenerator(HolidayDatatype item) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color1 = generator.getRandomColor();
        // generate color based on a key (same key returns the same color), useful for list/grid views
        //random image generator . emails cannot be same so always generate unique colours.
        int color2 = generator.getColor(item.getDate());

        // declare the builder object once.
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .fontSize(60)
                .endConfig()
                .rect();

        // reuse the builder specs to create multiple drawables
        TextDrawable ic1 = builder.build(item.getDay().substring(0, 3).toUpperCase(), color2);
        return ic1;
    }
}
