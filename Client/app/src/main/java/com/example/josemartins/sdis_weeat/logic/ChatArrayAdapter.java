package com.example.josemartins.sdis_weeat.logic;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.josemartins.sdis_weeat.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;




public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {


    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;


    public ChatArrayAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public void add(@Nullable ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    @Override
    public void clear() {
        chatMessageList.clear();
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (chatMessageObj.getMessageType() == MessageType.SENT)
            row = inflater.inflate(R.layout.right_message, parent, false);
        else
            row = inflater.inflate(R.layout.left_message, parent, false);


        TextView chatText = (TextView) row.findViewById(R.id.msg);
        TextView sender = (TextView) row.findViewById(R.id.sender_name);
        TextView date = (TextView) row.findViewById(R.id.message_time);

        Glide.with(context).load(chatMessageObj.getImage_url()).into((ImageView) row.findViewById(R.id.picture));

        //content
        chatText.setText(chatMessageObj.getMessage());
        //sender
        sender.setText(chatMessageObj.getName());
        //date
        Calendar cal = Calendar.getInstance();
      //  cal.setTimeInMillis(Long.valueOf(chatMessageObj.get()));
        date.setText(DateFormat.format("HH:mm", cal).toString());





        return row;
    }

}
