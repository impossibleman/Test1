package com.example.test1.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.commoninterface.HoverStateController;
import com.example.test1.commontool.ConstantAssemble;
import com.example.test1.customview.HoverExpandableListView;
import com.example.test1.dataconstruct.ChatContact;
import com.example.test1.dataconstruct.ChatContactClassify;

import java.util.List;

public class ChatContactAdapter extends BaseExpandableListAdapter implements HoverStateController {

    Context mContext;
    HoverExpandableListView listView;
    List<ChatContactClassify> classifies;

    public ChatContactAdapter(Context context, List<ChatContactClassify> classifies) {
        mContext = context;
        this.classifies = classifies;
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
    public View getGroupView(int position, boolean isExpanded, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_classify_item, viewGroup, false);
        }
        TextView tvName = view.findViewById(R.id.tv_classifyname);
        TextView tvMemberNumbers = view.findViewById(R.id.tv_number);
        ImageView ivArrow = view.findViewById(R.id.iv_expand);
        ChatContactClassify currentClassify = classifies.get(position);
        tvName.setText(currentClassify.getClassifyName());
        int numbers=currentClassify.getContact().size();
        tvMemberNumbers.setText(numbers+"/"+numbers);
        ivArrow.setImageResource(isExpanded ? R.drawable.ic_keyboard_arrow_down_24dp : R.drawable.ic_keyboard_arrow_up_24dp);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_contact_item, viewGroup, false);
        }
        ChatContact currentContact = classifies.get(groupPosition).getContact().get(childPosition);
        ImageView ivHead = view.findViewById(R.id.image_head);
        TextView tvName = view.findViewById(R.id.tv_username);
        TextView tvNewMessage = view.findViewById(R.id.tv_newmessage);
        tvName.setText(currentContact.getUserName());
        ivHead.setImageResource(Integer.parseInt(currentContact.getHeadImage()));
        int count = currentContact.getNewMessageCount();
        if (count > 0) {
            tvNewMessage.setVisibility(View.VISIBLE);
            tvNewMessage.setText(String.valueOf(count));
        } else {
            tvNewMessage.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    //must call
    public void SetAttachedListView(HoverExpandableListView listView){
        this.listView=listView;
    }

    @Override
    public int GetHoverState(int groupPosition,int childPosition) {
        final int childCount=getChildrenCount(groupPosition);
        if(childPosition==childCount-1){
            return ConstantAssemble.HOVER_STATE_MOVING;
        }
        else if(childPosition==-1&&!listView.isGroupExpanded(groupPosition)){
            return ConstantAssemble.HOVER_STATE_GONE;
        }
        else{
            return ConstantAssemble.HOVER_STATE_HOVER;
        }
    }

    @Override
    public void SetHoverViewContent(View hoverView, int GroupPosition) {
        TextView tvName = hoverView.findViewById(R.id.tv_classifyname);
        TextView tvMemberNumbers = hoverView.findViewById(R.id.tv_number);
        ImageView ivArrow = hoverView.findViewById(R.id.iv_expand);
        ChatContactClassify currentClassify = classifies.get(GroupPosition);
        tvName.setText(currentClassify.getClassifyName());
        int numbers=currentClassify.getContact().size();
        tvMemberNumbers.setText(numbers+"/"+numbers);
        ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_24dp);
    }
}
