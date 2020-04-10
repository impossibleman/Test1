package com.example.test1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.dataconstruct.ChatContact;
import com.example.test1.dataconstruct.ChatContactClassify;

import java.util.List;

public class ChatContactAdapter extends BaseExpandableListAdapter {

    Context mContext;
    List<ChatContactClassify> classifies;

    public ChatContactAdapter(Context context, List<ChatContactClassify> classifies) {
        mContext=context;
        this.classifies=classifies;
    }

    @Override
    public int getGroupCount() {
        return classifies.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return classifies.get(i).getContact().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return classifies.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return classifies.get(groupPosition).getContact().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean b, View view, ViewGroup viewGroup) {
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.chat_classify_item,viewGroup,false);
        }
        ChatContactClassify currentClassify= classifies.get(position);
        TextView tvName=view.findViewById(R.id.tv_classifyname);
        tvName.setText(currentClassify.getClassifyName());
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.chat_contact_item,viewGroup,false);
        }
        ChatContact currentContact= classifies.get(groupPosition).getContact().get(childPosition);
        ImageView ivHead=view.findViewById(R.id.image_head);
        TextView tvName=view.findViewById(R.id.tv_username);
        TextView tvNewMessage=view.findViewById(R.id.tv_newmessage);
        tvName.setText(currentContact.getUserName());
        int count=currentContact.getNewMessageCount();
        if(count>0){
            tvNewMessage.setVisibility(View.VISIBLE);
            tvNewMessage.setText(String.valueOf(count));
        }
        else{
            tvNewMessage.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
