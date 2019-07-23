package com.talentcerebrumhrms.adapter;

import android.content.Context;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.talentcerebrumhrms.datatype.PeopleDataType;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.SlidePanelListener;
import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by saransh on 10-11-2016.
 * SuperSlim library for material style header recycler view in people.
 */

public class PeopleAdapter extends RecyclerView.Adapter<PeopleViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0x01;

    private static final int VIEW_TYPE_CONTENT = 0x00;

    private final ArrayList<LineItem> mItems;

    private int mHeaderDisplay;

    private boolean mMarginsFixed;

    private final Context mContext;
    private ArrayList<PeopleDataType> data, templist;

    private SlidePanelListener listener;

    public PeopleAdapter(Context context, int headerMode, ArrayList<PeopleDataType> list, SlidePanelListener listener) {
        mContext = context;
        this.data = list;
        this.listener = listener;
        // countryNames = context.getResources().getStringArray(R.array.country_names);
        mHeaderDisplay = headerMode;

        mItems = new ArrayList<>();
        this.templist = new ArrayList<>();
        templist.addAll(data);

        //Insert headers into list of items.
        String lastHeader = "";
        int headerCount = 0;
        int sectionFirstPosition = 0;
        for (int i = 0; i < data.size(); i++) {
            Log.e("check search", String.valueOf(data.size()));
            String header = data.get(i).getName().substring(0, 1);
            if (!TextUtils.equals(lastHeader, header)) {
                sectionFirstPosition = i + headerCount;
                lastHeader = header;
                headerCount += 1;
                //header part
                mItems.add(new LineItem(data.get(i), true, sectionFirstPosition));
            }
            mItems.add(new LineItem(data.get(i), false, sectionFirstPosition));
        }
    }

    public boolean isItemHeader(int position) {
        return mItems.get(position).isHeader;
    }

    public String itemToString(int position) {
        return mItems.get(position).value.getName();
    }

    @Override
    public PeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.headerfragmentpeople, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_peoplefragment, parent, false);
        }
        return new PeopleViewHolder(view, viewType, listener, mContext);
    }

    @Override
    public void onBindViewHolder(PeopleViewHolder holder, int position) {
        final LineItem item = mItems.get(position);
        final View itemView = holder.itemView;
        Log.e("position", "" + position);
        Log.e("url pic", "" + item.value.getPhoto());
      /*  TextDrawable drawable = TextDrawable.builder()
                .buildRound(item.text.substring(0, 1), Color.RED);*/
        //   int resID = mContext.getResources().getIdentifier(String.valueOf(iconGenerator(item)), String.valueOf(iconGenerator(item)), mContext.getPackageName());
        //   Log.e("check drawable name", String.valueOf(resID));
        holder.bindItem(item.value, iconGenerator(item), getItemViewType(position));


        final LayoutManager.LayoutParams lp = LayoutManager.LayoutParams.from(itemView.getLayoutParams());
        // Overrides xml attrs, could use different layouts too.
        if (item.isHeader) {
            lp.headerDisplay = mHeaderDisplay;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.headerEndMarginIsAuto = !mMarginsFixed;
            lp.headerStartMarginIsAuto = !mMarginsFixed;
        }

        lp.setSlm(LinearSLM.ID);
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setHeaderDisplay(int headerDisplay) {
        mHeaderDisplay = headerDisplay;
        notifyHeaderChanges();
    }

    public void setMarginsFixed(boolean marginsFixed) {
        mMarginsFixed = marginsFixed;
        notifyHeaderChanges();
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < mItems.size(); i++) {
            LineItem item = mItems.get(i);
            if (item.isHeader) {
                notifyItemChanged(i);
            }
        }
    }

    private class LineItem {

        public int sectionFirstPosition;

        public boolean isHeader;

        public PeopleDataType value;

        public LineItem(PeopleDataType text, boolean isHeader,
                        int sectionFirstPosition) {
            this.isHeader = isHeader;
            this.value = text;
            this.sectionFirstPosition = sectionFirstPosition;
        }
    }

    private TextDrawable iconGenerator(LineItem item) {
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color1 = generator.getRandomColor();
        // generate color based on a key (same key returns the same color), useful for list/grid views
        //random image generator . emails cannot be same so always generate unique colours.
        int color2 = generator.getColor(item.value.getEmail());

        // declare the builder object once.
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .endConfig()
                .round();

        // reuse the builder specs to create multiple drawables
        TextDrawable ic1 = builder.build(item.value.getName().substring(0, 1).toUpperCase(), color2);
        return ic1;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Log.e("length list 1", String.valueOf(data.size()));
        Log.e("length templist", String.valueOf(templist.size()));
        data.clear();
        mItems.clear();

        if (charText.length() == 0) {
            data.addAll(templist);
        } else {
            for (PeopleDataType wp : templist) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText) || wp.getDesignation().toLowerCase(Locale.getDefault()).contains(charText) || wp.getDivision().toLowerCase(Locale.getDefault()).contains(charText) || wp.getPhone().toLowerCase(Locale.getDefault()).contains(charText) || wp.getEmail().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(wp);
                    Log.e("length list", String.valueOf(data.size()));
                }
            }
        }
        String lastHeader = "";
        int headerCount = 0;
        int sectionFirstPosition = 0;
        for (int i = 0; i < data.size(); i++) {
            Log.e("check search", String.valueOf(data.size()));
            String header = data.get(i).getName().substring(0, 1);
            if (!TextUtils.equals(lastHeader, header)) {
                sectionFirstPosition = i + headerCount;
                lastHeader = header;
                headerCount += 1;
                //header part
                mItems.add(new LineItem(data.get(i), true, sectionFirstPosition));
            }
            mItems.add(new LineItem(data.get(i), false, sectionFirstPosition));
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<PeopleDataType> a) {
        data = a;
        this.templist = new ArrayList<>();
        templist.clear();
        templist.addAll(data);
        String lastHeader = "";
        int headerCount = 0;
        int sectionFirstPosition = 0;
        for (int i = 0; i < data.size(); i++) {
            Log.e("check search", String.valueOf(data.size()));
            String header = data.get(i).getName().substring(0, 1);
            if (!TextUtils.equals(lastHeader, header)) {
                sectionFirstPosition = i + headerCount;
                lastHeader = header;
                headerCount += 1;
                //header part
                mItems.add(new LineItem(data.get(i), true, sectionFirstPosition));
            }
            mItems.add(new LineItem(data.get(i), false, sectionFirstPosition));
        }
        this.notifyDataSetChanged();
    }
}
