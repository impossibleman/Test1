package com.example.test1.commontool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.test1.dataconstruct.PeanutObject;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetWorkTool {

    public static Socket mSocket;

    public static void CreateSocket(Socket socket){
        mSocket=socket;
    }

    public static Socket GetSocket(){
        return mSocket;
    }

    public static List<PeanutObject> ParseNetResult(String result){
        List<PeanutObject> peanutObjects=new ArrayList<>();
        while(!result.equals("")){
//            int fieldNamePosition=result.indexOf(":");
//            String fieldName=result.substring(0,fieldNamePosition);
//            result=result.substring(fieldNamePosition+1);
//            int contentPosition=result.indexOf(":");
//            int contentLength=Integer.parseInt(result.substring(0,contentPosition));
//            String content=result.substring(contentPosition+1);
//            result=result.substring(contentPosition+1+contentLength);
//            obj.SetValue(fieldName,content);
            String head=result.substring(0,1);
            if(head.equals("{")){
                result=result.substring(1);
                PeanutObject obj=new PeanutObject();
                while(true){
                    if(result.substring(0,1).equals("}")){
                        if(result.length()>1){
                            result=result.substring(1);
                        }
                        else{
                            result="";
                        }
                        break;
                    }
                    int fieldNamePosition=result.indexOf(":");
                    String fieldName=result.substring(0,fieldNamePosition);
                    result=result.substring(fieldNamePosition+1);
                    int contentPosition=result.indexOf(":");
                    int contentLength=Integer.parseInt(result.substring(0,contentPosition));
                    String content=result.substring(contentPosition+1,contentPosition+1+contentLength);
                    result=result.substring(contentPosition+1+contentLength);
                    obj.SetValue(fieldName,content);
                }
                peanutObjects.add(obj);
            }
            else{
            }
        }
        return peanutObjects;
    }

    public static boolean CheckNetConnect(Context context){
        ConnectivityManager connectivityManager =(ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info =connectivityManager.getActiveNetworkInfo();
        if(info!=null){
            return true;
        }
        Toast.makeText(context,"Net connect failed!",Toast.LENGTH_LONG);
        return false;
    }
}
