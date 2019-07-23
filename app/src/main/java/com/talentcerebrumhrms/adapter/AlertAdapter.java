package com.talentcerebrumhrms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.datatype.AlertDataType;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saransh on 28-10-2016.
 * AlertAdapter for setting listview in AlertActivity and for searching text in alert.
 */

public class AlertAdapter extends BaseAdapter {

    Context context;
    private ArrayList<AlertDataType> data, templist;
    private AlertAdapterListener onClickListener;

    public AlertAdapter(Context mcontext, ArrayList<AlertDataType> list, AlertAdapterListener listener) {
        // super();
        this.context = mcontext;
        this.data = list;
        this.templist = new ArrayList<>();
        this.onClickListener = listener;
        templist.addAll(data);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listcell_alert, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.mark_read = (LinearLayout) convertView.findViewById(R.id.mark_read);
            holder.profile_image = (CircleImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.name.setText(data.get(position).getSender_name());
        String date_text = data.get(position).getCreated_date().substring(0, 3) + data.get(position).getCreated_date().substring(data.get(position).getCreated_date().indexOf(" "), data.get(position).getCreated_date().indexOf("("));
        Log.e("alert adapter text", date_text);
        String time_text = data.get(position).getCreated_date().substring(data.get(position).getCreated_date().indexOf("(") + 1, data.get(position).getCreated_date().indexOf(")"));
        holder.date.setText(date_text);
        holder.time.setText(time_text);
        String text = data.get(position).getDescription().replace("\n", "").replace("   ", "");
        holder.description.setText(text);
        final ViewHolder finalHolder = holder;

        if (!data.get(position).getSender_image().trim().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(data.get(position).getSender_image())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.profile)
                            .error(R.drawable.warning).signature(new ObjectKey(data.get(position).getSender_image())))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            finalHolder.profile_image.setBackground(null);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.profile_image);

        } else
            holder.profile_image.setImageResource(R.drawable.profile);


        holder.mark_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "mark read", Toast.LENGTH_SHORT).show();
                onClickListener.onReadClickListener(position);
                // markReadAlertApiCall(position);
            }
        });

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Log.e("length list 1", String.valueOf(data.size()));
        Log.e("length templist", String.valueOf(templist.size()));
        data.clear();

        if (charText.length() == 0) {
            data.addAll(templist);
        } else {
            for (AlertDataType wp : templist) {
                if (wp.getSender_name().toLowerCase(Locale.getDefault()).contains(charText) || wp.getCreated_date().toLowerCase(Locale.getDefault()).contains(charText) || wp.getDescription().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(wp);
                    Log.e("length list", String.valueOf(data.size()));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<AlertDataType> a) {

        data = a;
        this.templist = new ArrayList<>();
        templist.clear();
        templist.addAll(data);
        this.notifyDataSetChanged();
    }

    public interface AlertAdapterListener {
        void onReadClickListener(int position);
    }

    private class ViewHolder {
        TextView name, date, description, time;
        LinearLayout mark_read;
        CircleImageView profile_image;


    }


}
