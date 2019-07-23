package com.talentcerebrumhrms.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.datatype.PeopleDataType;
import com.talentcerebrumhrms.utils.SlidePanelListener;

/**
 * Simple view holder for a single text view.
 */
class PeopleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    Context context;
    private TextView mTextView, designation, division, divider, phone, email;
    private ImageView imageCircle;
    private SlidePanelListener listener;
    private int header;

    PeopleViewHolder(View view, int viewType, SlidePanelListener listener, Context context) {
        super(view);
        if (viewType == 0x01) {
            mTextView = (TextView) view.findViewById(R.id.text);
        } else if (viewType == 0x00) {
            mTextView = (TextView) view.findViewById(R.id.text);
            imageCircle = (ImageView) view.findViewById(R.id.image_circle);
            designation = (TextView) view.findViewById(R.id.designation);
            division = (TextView) view.findViewById(R.id.division);
            divider = (TextView) view.findViewById(R.id.divider);
            phone = (TextView) view.findViewById(R.id.phone);
            email = (TextView) view.findViewById(R.id.email);
        }
        this.listener = listener;
        this.context = context;
        view.setOnClickListener(this);
    }

    void bindItem(PeopleDataType value, Drawable drawable, int number) {
        if (number == 0x01) {
            header = number;
            mTextView.setText(value.getName().substring(0, 1).toUpperCase());
        } else if (number == 0x00) {
            header = number;
            imageCircle.setImageResource(0);
            imageCircle.setBackground(drawable);
            if (!value.getPhoto().trim().equalsIgnoreCase("")) {

                imageCircle.setBackground(null);


                Glide.with(context)
                        .load(value.getPhoto())
                        .apply(new RequestOptions().signature(new ObjectKey(value.getPhoto())).diskCacheStrategy(DiskCacheStrategy.ALL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                imageCircle.setBackground(null);
                                imageCircle.setBackgroundResource(R.drawable.warning_black);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(imageCircle);
            }
            mTextView.setText(value.getName());

            designation.setText(value.getDesignation());
            division.setText(value.getDivision());
            phone.setText(value.getPhone());
            email.setText(value.getEmail());
            divider.setText(" | ");
        }

    }

    @Override
    public String toString() {
        return mTextView.getText().toString();
    }

    @Override
    public void onClick(View view) {
        //add stuff here but remember the position includes headers and the body items in the list.
        if (header == 0x00) {
            int position = getAdapterPosition();
            listener.slideUpPanelAction(mTextView.getText().toString(), phone.getText().toString(), division.getText().toString(), designation.getText().toString(), email.getText().toString());
            // TextView division = (TextView) view.findViewById(R.id.division);
            Log.e("position holder 1", phone.getText().toString() + "\t" + email.getText().toString());
            Log.e("position holder 2", String.valueOf(position));
        } else
            Log.e("false click", "header");
    }

}
