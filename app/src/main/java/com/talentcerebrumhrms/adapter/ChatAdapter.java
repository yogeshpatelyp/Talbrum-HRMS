package com.talentcerebrumhrms.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.talentcerebrumhrms.datatype.ChatDataType;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.Utility;

import java.util.ArrayList;

/**
 * Created by saransh on 21-11-2016.
 * chatadapter with chat receive and reply textview editing.
 */

public class ChatAdapter extends BaseAdapter {
    Context context;
    ArrayList<ChatDataType> data, templist;

    public ChatAdapter(Context mContext, ArrayList<ChatDataType> list) {
        this.context = mContext;
        this.data = list;
    }

    @Override
    public int getCount() {
        if (data == null) {
            Log.e("chat adapter", "0");
            return 0;
        } else {
            Log.e("chat adapter", String.valueOf(data.size()));
            return data.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        LayoutInflater inflater;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.listcell_chat_reply, parent, false);
            holder = new ViewHolder();
            holder.chat_text = (TextView) convertView.findViewById(R.id.chatTextReply);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.layout_reply);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (data.get(i).getSenderType() == 1) {
            holder.chat_text.setText(data.get(i).getChat());
            holder.chat_text.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_chat_reply));
            holder.layout.setPadding(Math.round(Utility.dpToPx(context, 72)), 0, Math.round(Utility.dpToPx(context, 16)), 0);
            holder.layout.setGravity(Gravity.END);


        } else if(data.get(i).getSenderType() == 2){
            holder.chat_text.setText(data.get(i).getChat());
            holder.chat_text.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_chat_receive));
            holder.layout.setPadding(Math.round(Utility.dpToPx(context, 16)), 0, Math.round(Utility.dpToPx(context, 72)), 0);
            holder.layout.setGravity(Gravity.START);
        } else if(data.get(i).getSenderType() == 9)
        {
            holder.chat_text.setText(data.get(i).getChat());
           // holder.chat_text.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
            holder.chat_text.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_chat_default));
            holder.layout.setPadding(Math.round(Utility.dpToPx(context, 24)), 0, Math.round(Utility.dpToPx(context, 24)), 0);
            holder.layout.setGravity(Gravity.CENTER);
        }
        return convertView;


    }

    private static class ViewHolder {
        TextView chat_text;
        LinearLayout layout;
    }


    @Override
    public int getItemViewType(int position) {
        return data.get(position).getSenderType();
    }

    public void updateAdapter(ArrayList<ChatDataType> a) {

        data = a;
        this.templist = new ArrayList<>();
        templist.clear();
        templist.addAll(data);
        this.notifyDataSetChanged();
    }
}
