package com.example.test1.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.adapter.FileInfomationAdapter;
import com.example.test1.dataconstruct.FileInformation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListFragment extends Fragment {

    TextView tvFlieDirectory;
    Button btBegin;
    ListView lvContentList;
    LinearLayout llDirecctory;
    Activity activity;
    String currentPath="";
    List<FileInformation> files;
            ;

    @Override
    public void onAttach(Context context) {
        activity= (Activity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_file_list,container,false);
        lvContentList=view.findViewById(R.id.lv_Content_list);
        tvFlieDirectory=view.findViewById(R.id.tv_file_directory);
        btBegin=view.findViewById(R.id.bt_begin_to_search);
        llDirecctory=view.findViewById(R.id.ll_directory);
        lvContentList.setVisibility(View.INVISIBLE);
        llDirecctory.setVisibility(View.INVISIBLE);
        btBegin.setVisibility(View.VISIBLE);

        files=new ArrayList<>();
        btBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvContentList.setVisibility(View.VISIBLE);
                llDirecctory.setVisibility(View.VISIBLE);
                btBegin.setVisibility(View.INVISIBLE);
                GetItems();
            }
        });
        lvContentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInformation selectedInfo=files.get(position);
                String path=selectedInfo.getPath();
                if(selectedInfo.isDirectory()){
                    currentPath=path;
                    GetItems();
                }
                else{
                    OpenFile(path);
                }
            }
        });
        return view;
    }

    private void GetItems() {
        File file;
        if(currentPath.equals("")){
            file =Environment.getExternalStorageDirectory();
            if(file.listFiles()==null||file.listFiles().length==0){
                file =Environment.getRootDirectory();
            }
            currentPath=file.getAbsolutePath();
        }
        else{
            file=new File(currentPath);
        }
        tvFlieDirectory.setText(currentPath);
        Log.d("tag",currentPath);
        File[] filesArray=file.listFiles();
        if(filesArray!=null&&filesArray.length!=0){
            files.clear();
            for(int index=0;index!=filesArray.length;index++){
                File currentFile=filesArray[index];
                FileInformation fileInfo=new FileInformation();
                fileInfo.setPath(currentFile.getAbsolutePath());
                fileInfo.setName(currentFile.getName());
                if(!currentFile.isDirectory()){
                    fileInfo.setSize(currentFile.length()+"");
                }
                else{
                    fileInfo.setDirectory(true);
                }
                files.add(fileInfo);
            }
            FileInfomationAdapter mAdapter=new FileInfomationAdapter(activity,R.layout.file_infomation_item,files);
            lvContentList.setAdapter(mAdapter);
        }
    }

    private void OpenFile(String path) {
    }

    public boolean BackToLastLevel(){
        boolean isRootDirectory=false;
        File file=new File(currentPath);
        File rootFile =Environment.getExternalStorageDirectory();
        if(file.compareTo(rootFile)==0){
            isRootDirectory=true;
        }
        else{
            currentPath=file.getParent();
            GetItems();
        }
        return isRootDirectory;
    }
}
