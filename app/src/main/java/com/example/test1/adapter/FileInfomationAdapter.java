package com.example.test1.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.dataconstruct.FileInformation;

import java.util.List;
import java.util.zip.Inflater;

public class FileInfomationAdapter extends ArrayAdapter {
    List<FileInformation> files;

    public FileInfomationAdapter(Context context, int resource, List<FileInformation> files) {
        super(context, resource, files);
        this.files=files;
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView= View.inflate(getContext(), R.layout.file_infomation_item,null);
            viewHolder.ivDirectory=convertView.findViewById(R.id.iv_directory_icon);
            viewHolder.ivFileIcon=convertView.findViewById(R.id.iv_file_icon);
            viewHolder.tvFileName=convertView.findViewById(R.id.tv_file_name);
            viewHolder.tvSize=convertView.findViewById(R.id.tv_size);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        FileInformation currentFile=(FileInformation)getItem(position);
        viewHolder.tvFileName.setText(currentFile.getName());
        if(!currentFile.isDirectory()){
            viewHolder.ivDirectory.setVisibility(View.INVISIBLE);
            viewHolder.ivFileIcon.setVisibility(View.VISIBLE);
            viewHolder.tvSize.setText(currentFile.getSize());
        }
        else{
            viewHolder.ivDirectory.setVisibility(View.VISIBLE);
            viewHolder.ivFileIcon.setVisibility(View.INVISIBLE);
            viewHolder.tvSize.setText("");
        }
        return convertView;
    }

    class ViewHolder{
        ImageView ivDirectory,ivFileIcon;
        TextView tvFileName,tvSize;
    }
}
