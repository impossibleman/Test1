package com.example.test1.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.activity.ConversationActivity;
import com.example.test1.activity.LoginActivity;
import com.example.test1.adapter.ChatContactAdapter;
import com.example.test1.commontool.ConstantAssemble;
import com.example.test1.commontool.NetWorkTool;
import com.example.test1.database.DBOperater;
import com.example.test1.dataconstruct.CellContact;
import com.example.test1.dataconstruct.ChatContact;
import com.example.test1.dataconstruct.ChatContactClassify;
import com.example.test1.dataconstruct.PeanutObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatOperationFragment extends Fragment {

    TextView tvHead;
    Button btLogin;
    ExpandableListView lvChatContact;
    Activity activity;
    ExecutorService executorService;
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    ChatContactAdapter adapter;
    List<ChatContactClassify> contactClassifies;
    int userId;
    boolean isFirstConnect=false,isVisible=false,isInitFinished=false,isFirstLoad=true,isNetRequestSuccess=false;

    @Override
    public void onAttach(Context context) {
        activity= (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            isVisible=true;
            if(isVisible&&isInitFinished&&isFirstLoad){
                AsyncCheckLoginState();
                isFirstLoad=false;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chat_operation,container,false);
        lvChatContact=view.findViewById(R.id.lv_Chat_Contact);
        tvHead=view.findViewById(R.id.tv_head);
        btLogin=view.findViewById(R.id.bt_login);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, LoginActivity.class);
                startActivityForResult(intent,ConstantAssemble.ACTIVITY_REQUEST_CODE_CHAT_OPERATION);
            }
        });
        lvChatContact.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int position, long l) {
                ChatContact currentContact= contactClassifies.get(groupPosition).getContact().get(position);
                Intent intent=new Intent(activity, ConversationActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("resourcetype",ConstantAssemble.ACTIVITY_START_RESOURCE_CONTACT_LIST);
                bundle.putInt("userid",userId);
                bundle.putInt("targetuserid",currentContact.getUserId());
                bundle.putString("targetusername",currentContact.getUserName());
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });
        contactClassifies=new ArrayList<>();
        executorService= Executors.newCachedThreadPool();
        isInitFinished=true;
        if(isVisible&&isInitFinished&&isFirstLoad){
            AsyncCheckLoginState();
            isFirstLoad=false;
        }

        return view;
    }

    private void AsyncCheckLoginState(){
        new CheckLoginState().execute();
    }

    private void AsyncStoreLoginInfo(String accountId, String password) {
        new StoreLocalAccount().execute(String.valueOf(userId),accountId,password);
    }

    private void AsyncStoreContact(){
        new StoreLocalContact().execute();
    }

    private void AsyncGetLocalContact(){
        new GetLocalContact().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ConstantAssemble.ACTIVITY_REQUEST_CODE_CHAT_OPERATION){
            if(resultCode==ConstantAssemble.ACTIVITY_RESULT_CODE_CONFIRM){
                Bundle bundle=data.getExtras();
                userId=bundle.getInt("userid");
                String accountId=bundle.getString("accountid");
                String password=bundle.getString("password");
                AsyncStoreLoginInfo(accountId,password);
                mHandler.sendEmptyMessage(2);
            }
        }
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    ChatContactAdapter mAdapter=new ChatContactAdapter(activity,contactClassifies);
                    lvChatContact.setAdapter(mAdapter);
                    if(isNetRequestSuccess){
                        AsyncStoreContact();
                    }
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    executorService.execute(new GetContact());
                    break;
                case 3:
                    ProcessConnectionFailure();
                    break;
            }
        }
    };

    class StoreLocalAccount extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try{
                DBOperater operater=new DBOperater(activity,ConstantAssemble.DATABASE_NAME,null,1);
                SQLiteDatabase database =operater.getWritableDatabase();
                ContentValues values=new ContentValues();
                values.put("userid",params[0]);
                values.put("accountid",params[1]);
                values.put("password",params[2]);
                database.insert(ConstantAssemble.DATABASE_TABLE_NAME_USERINFO,null,values);
                return true;
            }
            catch (Exception e){
                return false;
            }
        }
    }

    class StoreLocalContact extends AsyncTask<List<ChatContact>,Void,Boolean>{
        @Override
        protected Boolean doInBackground(List<ChatContact>... params) {
            try{
                DBOperater operater=new DBOperater(activity,ConstantAssemble.DATABASE_NAME,null,1);
                SQLiteDatabase database =operater.getWritableDatabase();
                List<ChatContact> contact=params[0];
                ContentValues values=new ContentValues();
                for(ChatContact currentContact:contact){
                    values.put("userid",currentContact.getUserId());
                    values.put("username",currentContact.getUserName());
                    values.put("headimage",currentContact.getHeadImage());
                    values.put("classifyid",currentContact.getClassifyId());
                    values.put("classifyname",currentContact.getClassifyName());
                    values.put("newmessagecount",currentContact.getNewMessageCount());
                    database.insert(ConstantAssemble.DATABASE_TABLE_NAME_USERCONTACT,null,values);
                }
                return true;
            }
            catch (Exception e){
                return false;
            }
        }
    }

    class CheckLoginState extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                DBOperater operater=new DBOperater(activity,ConstantAssemble.DATABASE_NAME,null,1);
                SQLiteDatabase database =operater.getWritableDatabase();
                Cursor cursor=database.query(ConstantAssemble.DATABASE_TABLE_NAME_USERINFO,null,null,null,null,null,null);
                if(cursor.moveToNext()){
                    userId=cursor.getInt(cursor.getColumnIndex("userid"));
                    btLogin.setVisibility(View.INVISIBLE);
                    lvChatContact.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(2);
                }
                else{
                    btLogin.setVisibility(View.VISIBLE);
                    lvChatContact.setVisibility(View.INVISIBLE);
                }
            }
            catch (Exception e){

            }
            return null;
        }
    }

    private class GetLocalContact extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                DBOperater operater=new DBOperater(activity,ConstantAssemble.DATABASE_NAME,null,1);
                SQLiteDatabase database =operater.getWritableDatabase();
                Cursor cursor=database.query(ConstantAssemble.DATABASE_TABLE_NAME_USERCONTACT,null,null,null,null,null,null);
                if(cursor.moveToNext()){
                    int userId=cursor.getInt(cursor.getColumnIndex("userid"));
                    String userName=cursor.getString(cursor.getColumnIndex("username"));
                    String headImage=cursor.getString(cursor.getColumnIndex("headimage"));
                    int classifyId=cursor.getInt(cursor.getColumnIndex("classifyid"));
                    String classifyName=cursor.getString(cursor.getColumnIndex("classifyname"));
                    int newMessageCount=cursor.getInt(cursor.getColumnIndex("newmessagecount"));
                    CreateContactList(userId,userName,headImage,newMessageCount,classifyId,classifyName);
                }
            }
            catch (Exception e){

            }
            return null;
        }
    }

    private class GetContact implements Runnable{

        @Override
        public void run() {
            try{
                socket=NetWorkTool.GetSocket();
                if(socket==null){
                    isFirstConnect=true;
                    socket=new Socket("192.168.3.46",8888);
                    NetWorkTool.CreateSocket(socket);
                }
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                String recieveMessage;
                if(isFirstConnect){
                    recieveMessage=reader.readLine();
                    Log.d("TAG", recieveMessage);
                    isFirstConnect=false;
                }
                String requetMessage = "{"+ConstantAssemble.SERVER_REQUEST_TYPE_PREFIX + ":10:" + ConstantAssemble.SERVER_REQUEST_TYPE_GET_CONTACT+"}{userid:"+String.valueOf(userId).length()+":"+userId+"}";
                Log.d("TAG", requetMessage);
                writer.println(requetMessage);
                writer.flush();
                while((recieveMessage=reader.readLine())!=null){
                    Log.d("TAG", "Begin to recieve loop!");
                    Log.d("TAG", recieveMessage);
                    ParseResult(recieveMessage);
                }
            }
            catch (Exception e){
                Log.e("TAG", "Socket connect failed!");
                e.printStackTrace();
                mHandler.sendEmptyMessage(3);
            }
        }
    }

    private void ParseResult(String recieveMessage) {
        List<PeanutObject> Objs= NetWorkTool.ParseNetResult(recieveMessage);
        if(Objs.size()!=0){
            PeanutObject headPeanutObject=Objs.get(0);
            String requestType=headPeanutObject.GetValue(ConstantAssemble.SERVER_REQUEST_TYPE_PREFIX);
            Objs.remove(0);
            if(requestType.equals(ConstantAssemble.SERVER_REQUEST_TYPE_GET_CONTACT)){
                if(Objs.size()==0){
                    return;
                }
                for (PeanutObject peanutObj:Objs){
                    CreateContactList(Integer.parseInt(peanutObj.GetValue("userid")),peanutObj.GetValue("username"),"",0,Integer.parseInt(peanutObj.GetValue("classifyid")),peanutObj.GetValue("classifyname"));
                }
            }
            else if(requestType.equals(ConstantAssemble.SERVER_REQUEST_TYPE_MESSAGE_ARRIVE)){
                if(Objs.size()==0){
                    return;
                }
                PeanutObject peanutObj=Objs.get(0);
                int userId=Integer.parseInt(peanutObj.GetValue("userId"));
                for(ChatContactClassify currentClassify:contactClassifies){
                    List<ChatContact> allContact=currentClassify.getContact();
                    for(ChatContact currentContact:allContact){
                        if(currentContact.getUserId()==userId){
                            currentContact.addNewMessageCount();
                        }
                    }
                }
                mHandler.sendEmptyMessage(1);
            }
            else if(requestType.equals(ConstantAssemble.SERVER_REQUEST_TYPE_END)){
                isNetRequestSuccess=true;
                mHandler.sendEmptyMessage(0);
            }
            else{

            }
        }
    }

    private void CreateContactList(int userId,String userName,String imageUrl,int newMessageCount,int classifyId,String classifyName) {
        ChatContact singleContact=new ChatContact();
        singleContact.setUserId(userId);
        singleContact.setUserName(userName);
        singleContact.setHeadImage(imageUrl);
        singleContact.setClassifyId(classifyId);
        singleContact.setClassifyName(classifyName);
        if(contactClassifies.size()==0){
            ChatContactClassify classify=new ChatContactClassify();
            classify.setClassifyId(classifyId);
            classify.setClassifyName(classifyName);
            classify.addSingleContact(singleContact);
            contactClassifies.add(classify);
        }
        else{
            boolean hasIdBeenFound=false;
            for(ChatContactClassify currentClassify:contactClassifies){
                if(currentClassify.getClassifyId()==classifyId){
                    currentClassify.addSingleContact(singleContact);
                    hasIdBeenFound=true;
                }
            }
            if(!hasIdBeenFound){
                ChatContactClassify classify=new ChatContactClassify();
                classify.setClassifyId(classifyId);
                classify.setClassifyName(classifyName);
                classify.addSingleContact(singleContact);
                contactClassifies.add(classify);
            }
        }
    }

    void ProcessConnectionFailure(){
        AsyncGetLocalContact();
    }
}
