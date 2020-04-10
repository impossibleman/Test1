package com.example.test1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.dataconstruct.ChatContent;

import java.util.List;

public class ChatContentAdapter extends ArrayAdapter<ChatContent> {

    int userId;

    public ChatContentAdapter(Context context, int resource,int ownerUserId,List<ChatContent> objects) {
        super(context, resource, objects);
        userId=ownerUserId;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.chat_content_item,null);
        }
        LinearLayout llMine=convertView.findViewById(R.id.ll_mine);
        LinearLayout llTarget=convertView.findViewById(R.id.ll_target);
        ChatContent currentContent=getItem(position);
        if(currentContent.getUserId()==userId){
            llMine.setVisibility(View.VISIBLE);
            llTarget.setVisibility(View.INVISIBLE);
            TextView tvContent=convertView.findViewById(R.id.tv_my_message);
            tvContent.setText(currentContent.getContent());
        }
        else{
            llMine.setVisibility(View.INVISIBLE);
            llTarget.setVisibility(View.VISIBLE);
            TextView tvContent=convertView.findViewById(R.id.tv_target_message);
            tvContent.setText(currentContent.getContent());
        }
        return convertView;
    }
}
