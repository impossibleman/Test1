package com.example.test1.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.adapter.ChatContentAdapter;
import com.example.test1.commontool.ConstantAssemble;
import com.example.test1.commontool.NetWorkTool;
import com.example.test1.dataconstruct.ChatContact;
import com.example.test1.dataconstruct.ChatContent;
import com.example.test1.dataconstruct.PeanutObject;
import com.jaeger.library.StatusBarUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConversationActivity extends AppCompatActivity {

    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    Toolbar tbConversation;
    EditText etInputMessage;
    ImageButton ibSend;
    ListView lvMessages;
    ChatContentAdapter adapter;
    int userId;
    int targetUserId;
    int resourceType;
    String targetUserName;
    String inputMessage;
    ExecutorService executorService;
    List<ChatContent> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Bundle bundle=getIntent().getExtras();
        userId=bundle.getInt("userid");
        targetUserId=bundle.getInt("targetuserid");
        targetUserName=bundle.getString("targetusername");
        resourceType=bundle.getInt("resourcetype");
        socket= NetWorkTool.GetSocket();

        tbConversation=findViewById(R.id.tb_login);
        etInputMessage=findViewById(R.id.et_input_message);
        ibSend=findViewById(R.id.ib_send);
        lvMessages=findViewById(R.id.lv_messages);
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!NetWorkTool.CheckNetConnect(ConversationActivity.this)){
                    return;
                }
                inputMessage=etInputMessage.getText().toString();
                ChatContent createdContent=new ChatContent();
                createdContent.setContent(inputMessage);
                createdContent.setUserId(userId);
                messages.add(createdContent);
                if(messages.size()-1>0){
                    adapter.notifyDataSetChanged();
                }
                else{
                    adapter=new ChatContentAdapter(ConversationActivity.this,R.layout.chat_content_item,userId,messages);
                    lvMessages.setAdapter(adapter);
                }
                executorService.execute(new SendMessage());
            }
        });
        tbConversation.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resourceType==ConstantAssemble.ACTIVITY_START_RESOURCE_CONTACT_LIST){
                    finish();
                }
                else{
                    Intent intent=new Intent(ConversationActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        StatusBarUtil.setColor(this, getResources().getColor(R.color.grayblue));

        messages=new ArrayList<>();
        executorService= Executors.newCachedThreadPool();
        executorService.execute(new GetServerMessage());
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    adapter=new ChatContentAdapter(ConversationActivity.this,R.layout.chat_content_item,userId,messages);
                    lvMessages.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    public class GetServerMessage implements Runnable{

        @Override
        public void run() {
            try{
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                String messageForSend="{"+ConstantAssemble.SERVER_REQUEST_TYPE_PREFIX+":10:"+ConstantAssemble.SERVER_REQUEST_TYPE_GET_MESSAGE+"}{userid:"+String.valueOf(userId).length()+":"+userId+"targetuserid:"+String.valueOf(targetUserId).length()+":"+targetUserId+"}";
                writer.println(messageForSend);
                String recieveMessage="";
                while (true){
                    if((recieveMessage=reader.readLine())!=null){
                        Log.d("TAG", recieveMessage);
                        ParseResult(recieveMessage);
                    }
                    Thread.sleep(200);
                }
            }
            catch (Exception e){

            }
        }
    }

    public class SendMessage implements Runnable{

        @Override
        public void run() {
            String sendMessage="{"+ConstantAssemble.SERVER_REQUEST_TYPE_PREFIX+":11:"+ConstantAssemble.SERVER_REQUEST_TYPE_SEND_MESSAGE+"userid:"+String.valueOf(userId).length()+":"+userId+"targetuserid:"+String.valueOf(targetUserId).length()+":"+targetUserId+"message:"+String.valueOf(inputMessage).length()+":"+inputMessage+"}";
            writer.println(sendMessage);
            writer.flush();
        }
    }

    private void ParseResult(String recieveMessage) {
        List<PeanutObject> peanutObjects=NetWorkTool.ParseNetResult(recieveMessage);
        PeanutObject headPeanutObj=peanutObjects.get(0);
        String requestType=headPeanutObj.GetValue(ConstantAssemble.SERVER_REQUEST_TYPE_PREFIX);
        peanutObjects.remove(0);
        if(requestType.equals(ConstantAssemble.SERVER_REQUEST_TYPE_GET_MESSAGE)){
            if(peanutObjects.size()==0){
                return;
            }
            PeanutObject peanutObj=peanutObjects.get(0);
            ChatContent singleMessage=new ChatContent();
            singleMessage.setUserId(Integer.parseInt(peanutObj.GetValue("userid")));
            singleMessage.setContent(peanutObj.GetValue("content"));
            singleMessage.setOccurDate(peanutObj.GetValue("occurDate"));
            messages.add(singleMessage);
        }
        else if(requestType.equals(ConstantAssemble.SERVER_REQUEST_TYPE_SEND_MESSAGE)){
        }
        else if(requestType.equals(ConstantAssemble.SERVER_REQUEST_TYPE_END)){
            mHandler.sendEmptyMessage(0);
        }
        else{
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(getCurrentFocus()!=null){
            InputMethodManager manager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            return manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
        return super.onTouchEvent(event);
    }
}
