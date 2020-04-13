package com.example.test1.service;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.test1.R;
import com.example.test1.activity.ConversationActivity;
import com.example.test1.commontool.ConstantAssemble;
import com.example.test1.commontool.NetWorkTool;
import com.example.test1.commoninterface.OnSocketReadListener;
import com.example.test1.dataconstruct.PeanutObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoticeService extends Service {

    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    ExecutorService executorService;
    OnSocketReadListener onSocketReadListener;
    SingleBinder binder=new SingleBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class SingleBinder extends Binder {
        public NoticeService GetService(){
            return NoticeService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d("TAG", "Service begin to run!");
    }

    class NetConnectService implements Runnable {

        @Override
        public void run() {
            try{
                socket=new Socket("192.168.3.46",8886);
                NetWorkTool.CreateSocket(socket);
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                String recieveMessage;
                while((recieveMessage=reader.readLine())!=null){
                    Log.d("TAG", "recieveMessage: "+recieveMessage);
//                    onSocketReadListener.OnSocketReadSuccess(recieveMessage);
                    List<PeanutObject> objs=NetWorkTool.ParseNetResult(recieveMessage);
                    if(objs.size()>0){
                        PeanutObject headPeanut=objs.get(0);
                        PeanutObject bodyPeanut=objs.get(1);
                        if(headPeanut.GetValue("requesttype").equals(ConstantAssemble.SERVER_REQUEST_TYPE_MESSAGE_ARRIVE)){
                            int uerId=Integer.getInteger(bodyPeanut.GetValue("userid"));
                            int targetUerId=Integer.getInteger(bodyPeanut.GetValue("targetuerid"));
                            String userName=bodyPeanut.GetValue("username");
                            ShowNotification(uerId,targetUerId,userName);
                        }
                    }
                    Thread.sleep(5000);
                }
            }
            catch (Exception e){

            }
        }
    }

    private void ShowNotification(int userId,int targetUserId,String targetName) {
        Intent intent=new Intent(getApplicationContext(), ConversationActivity.class);
        Bundle bundle=new Bundle();
        bundle.putInt("userid",userId);
        bundle.putInt("targetuserid",targetUserId);
        bundle.putInt("resourcetype",ConstantAssemble.ACTIVITY_START_RESOURCE_NOTIFICATION);
        bundle.putString("targetusername",targetName);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            String channelId="channel_1";
            NotificationChannel notificationChannel=new NotificationChannel(channelId,"what",NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            Notification notification=new Notification.Builder(getApplicationContext(),channelId).setContentTitle("Test").setContentText("Text content!").setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).build();
            notificationManager.notify(1,notification);
        }
    }

    public void StopCurrentThread(){
        if(executorService!=null){
            executorService.shutdownNow();
        }
    }

    public void StartNewThread(){
        executorService= Executors.newCachedThreadPool();
        executorService.execute(new NetConnectService());
    }

    public void setOnSocketReadListener(OnSocketReadListener onSocketReadListener){
        this.onSocketReadListener=onSocketReadListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service log:","Stop service!");
    }
}
