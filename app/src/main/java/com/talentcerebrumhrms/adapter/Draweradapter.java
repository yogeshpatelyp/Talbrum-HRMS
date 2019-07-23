package com.talentcerebrumhrms.adapter;

/**
 * Created by saransh on 14-06-2015.
 */

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.talentcerebrumhrms.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Draweradapter extends RecyclerView.Adapter<Draweradapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    private static final int TYPE_ITEM = 1;
    public static String mSelectedPositionTitle = "Overview";
    Context context;
    private ArrayList<String> titles;
    private ArrayList<Integer> icons;
    private String name, email, role, company_name, profile_pic_url, comapny_logo_url;

    public Draweradapter(Context context, ArrayList<String> titles, ArrayList<Integer> icons, String name, String email, String role, String company_name, String profile_pic_url, String company_logo_url) {
        this.titles = titles;
        this.icons = icons;
        this.name = name;
        this.email = email;
        this.profile_pic_url = profile_pic_url;
        this.role = role;
        this.company_name = company_name;
        this.comapny_logo_url = company_logo_url;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_row, parent, false);
            v.setBackgroundColor(ContextCompat.getColor(parent.getContext(), R.color.drawer_below));
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem; // Returning the created object

        } else if (viewType == TYPE_HEADER) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false); //Inflating the layout
            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhHeader; //returning the object created
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (holder.holderID == 1) {
            holder.textView.setText(titles.get(position - 1));
            holder.imageView.setImageResource(icons.get(position - 1));

            if (mSelectedPositionTitle.equalsIgnoreCase(titles.get(position - 1))) {
                Log.e("Selected position", "" + mSelectedPositionTitle);
                final int selectedColor = ContextCompat.getColor(context, R.color.drawer_item_selected);
                //Color.parseColor("#EE7E1B");//red #C43425  blue #33B5E5
                holder.textView.setTextColor(selectedColor);
                holder.imageView.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP);
            } else {
                final int normalColor = ContextCompat.getColor(context, R.color.drawer_items_colour);
                holder.textView.setTextColor(normalColor);
                holder.imageView.setColorFilter(normalColor, PorterDuff.Mode.SRC_ATOP);
            }
        } else {

            // Similarly we set the resources for drawer_header view
            holder.name.setText(name);
            holder.email.setText(email);
            holder.role.setText(role);
            holder.comapany_name.setText(company_name);

            // Log.e("image profile ", profile_pic_url + " header");
            if (!profile_pic_url.equalsIgnoreCase("")) {
                Glide.with(context).load(profile_pic_url).apply(new RequestOptions().placeholder(R.drawable.profile)
                        .signature(new ObjectKey(profile_pic_url)).error(R.drawable.warning)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)).into(holder.profile_image);
            }
            // Log.e("image profile ", comapny_logo_url + " company header");
            if (!comapny_logo_url.equalsIgnoreCase("")) {
                Glide.with(context).load(comapny_logo_url).apply(new RequestOptions().placeholder(R.drawable.talbrum)
                        .signature(new ObjectKey(comapny_logo_url)).error(R.drawable.talbrum)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)).into(holder.company_image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return titles.size() + 1; // the number of items in the list will be +1 the titles including the drawer_header view.
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        int holderID;

        TextView textView;
        ImageView imageView;
        ImageView company_image;
        CircleImageView profile_image;
        TextView name;
        TextView email, role, comapany_name;

        public ViewHolder(final View itemView, int ViewType) {
            super(itemView);

            itemView.setClickable(true);
            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if (ViewType == TYPE_ITEM) {
                textView = (TextView) itemView.findViewById(R.id.rowText);
                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                holderID = 1;      // setting holder id as 1 as the object being populated are of type item row
            } else {
                name = (TextView) itemView.findViewById(R.id.name);
                email = (TextView) itemView.findViewById(R.id.email);
                profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
                company_image = (ImageView) itemView.findViewById(R.id.company_image);
                role = (TextView) itemView.findViewById(R.id.role);
                comapany_name = (TextView) itemView.findViewById(R.id.comapany_name);
                holderID = 0;      // Setting holder id = 0 as the object being populated are of type drawer_header view
            }
        }
    }
}
