package com.talentcerebrumhrms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.datatype.LeaveApproveDatatype;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by saransh on 28-10-2016.
 */
public class ApproveLeaveAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<LeaveApproveDatatype> data, templist;
    private ApproveAdapterListener onClickListener;

    public ApproveLeaveAdapter(Context mcontext, ArrayList<LeaveApproveDatatype> list, ApproveAdapterListener listener) {
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
        LayoutInflater inflater;
        Log.e("check filter", "1");
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listcell_approve_leave, parent, false);
            holder = new ViewHolder();
            Log.e("check filter", "2");
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.from_date = (TextView) convertView.findViewById(R.id.from_date);
            holder.to_date = (TextView) convertView.findViewById(R.id.to_date);
            holder.reason = (TextView) convertView.findViewById(R.id.reason);
            holder.approve = (ImageView) convertView.findViewById(R.id.approve);
            holder.reject = (ImageView) convertView.findViewById(R.id.disapprove);
            holder.profile = (CircleImageView) convertView.findViewById(R.id.profile_image);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        try {
            Log.e("check filter", "3");
            holder.name.setText(data.get(position).getName());
            holder.reason.setText(data.get(position).getReason());
            holder.from_date.setText(data.get(position).getDate_from());
            holder.to_date.setText(data.get(position).getDate_to());
            ImageLoader mImageLoader;

            Log.e("imageloader path", data.get(position).getProfile());
            if (!data.get(position).getProfile().trim().equalsIgnoreCase("")) {
                final ViewHolder finalHolder = holder;
                Glide.with(context).load(data.get(position).getProfile()).apply(new RequestOptions()
                        .signature(new ObjectKey(data.get(position).getProfile())).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.profile).error(R.drawable.warning))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                finalHolder.profile.setBackground(null);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(holder.profile);

            } else {
                holder.profile.setImageResource(R.drawable.profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onApproveClickListener(position);

                Log.e(context.getPackageName().getClass().toString(), "approved");
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onRejectClickListener(position);

                Log.e(context.getPackageName().getClass().toString(), "rejected");
                // Toast.makeText(context, "rejected", Toast.LENGTH_SHORT).show();

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
            for (LeaveApproveDatatype wp : templist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText) || wp.getReason().toLowerCase(Locale.getDefault()).contains(charText) || wp.getDate_from().toLowerCase(Locale.getDefault()).contains(charText) || wp.getDate_to().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(wp);
                    Log.e("length list", String.valueOf(data.size()));
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<LeaveApproveDatatype> a) {

        data = a;
        this.templist = new ArrayList<>();
        templist.clear();
        templist.addAll(data);
        this.notifyDataSetChanged();
    }

    public interface ApproveAdapterListener {
        void onApproveClickListener(int position);

        void onRejectClickListener(int position);
    }

    private class ViewHolder {
        TextView name, from_date, to_date, reason;
        CircleImageView profile;
        ImageView approve, reject;

    }
}
