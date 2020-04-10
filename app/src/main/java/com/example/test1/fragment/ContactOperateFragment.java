package com.example.test1.fragment;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.test1.commontool.NetWorkTool;
import com.example.test1.dataconstruct.CellContact;
import com.example.test1.adapter.ContactListAdapter;
import com.example.test1.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactOperateFragment extends Fragment implements View.OnClickListener {

    ListView lvContact;
    Button btReadContact;
    Button btUpload;
    Button btGetServerContact;
    Button btStoreContact;
    Activity activity;
    ExecutorService executorService;
    Socket socket;
    PrintWriter writer;
    BufferedReader reader;
    ArrayList<CellContact> serverContact;
    ArrayList<CellContact> phoneContact;
    boolean isFirstConnect=false,isVisible=false,isInitFinished=false,isFirstLoad=true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity=(Activity) context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            isVisible=true;
            if(isVisible&&isInitFinished&&isFirstLoad){
//                executorService.execute(new ConnectService());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contact_operate,container,false);
        lvContact = view.findViewById(R.id.lv_Contact);
        btReadContact = view.findViewById(R.id.bt_ReadContact);
        btUpload = view.findViewById(R.id.bt_Upload);
        btGetServerContact = view.findViewById(R.id.bt_GetSeverContact);
        btStoreContact = view.findViewById(R.id.bt_StoreContact);
        btReadContact.setOnClickListener(this);
        btUpload.setOnClickListener(this);
        btGetServerContact.setOnClickListener(this);
        btStoreContact.setOnClickListener(this);

        serverContact = new ArrayList<>();
        phoneContact = new ArrayList<CellContact>();

        executorService = Executors.newCachedThreadPool();
        isInitFinished=true;
        if(isVisible&&isInitFinished&&isFirstLoad){
//            executorService.execute(new ConnectService());
        }
        Log.d("TAG","Init finished!");
        isFirstLoad=false;
        return view;
    }

    private class ConnectService implements Runnable {
        @Override
        public void run() {
            try {
                socket= NetWorkTool.GetSocket();
                if(socket==null){
                    isFirstConnect=true;
                    socket = new Socket("192.168.3.46", 8888);
                }
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                String recieveMessage="";
                if(isFirstConnect){
                    recieveMessage=reader.readLine();
                    Log.d("TAG", recieveMessage);
                    isFirstConnect=false;
                }
            } catch (Exception e) {
                Log.e("TAG", "Socket connect failed!");
                e.printStackTrace();
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ContactListAdapter adapter = new ContactListAdapter(activity, R.layout.contact_item, serverContact);
                    lvContact.setAdapter(adapter);
                    break;
            }
            ;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_ReadContact:
                GetLocalContact();
                break;
            case R.id.bt_GetSeverContact:
                GetSeverContact();
                break;
            case R.id.bt_Upload:
                SendToServer();
                break;
            case R.id.bt_StoreContact:
                StoreContact();
                break;
        }
    }

    private void GetLocalContact() {
        phoneContact.clear();
        Cursor cursor = activity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            CellContact singleContact = new CellContact();
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            singleContact.setId(id);
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            singleContact.setName(name);

            Cursor phoneCursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                singleContact.setPhoneNumber(phoneNumber);
            }
            phoneContact.add(singleContact);
        }
        ContactListAdapter adapter = new ContactListAdapter(activity, R.layout.contact_item, phoneContact);
        lvContact.setAdapter(adapter);
    }

    private void GetSeverContact() {
        executorService.execute(new RecieveService());
    }

    class RecieveService implements Runnable {
        @Override
        public void run() {
            try {
                if (socket != null && socket.isConnected()) {
                    serverContact.clear();
                    String sendMessage = "download:";
                    writer.println(sendMessage);
                    writer.flush();
                    String recieveMessage = "";
                    Log.d("TAG", "Waiting for server message!");
                    while (!recieveMessage.equals("end")) {
                        if (socket == null || !socket.isConnected()){
//                            ConnectToServer();
                        }
                        recieveMessage = reader.readLine();
                        Log.d("TAG", recieveMessage);
                        int headPosition = recieveMessage.indexOf(":");
                        if (headPosition != -1) {
                            String head = recieveMessage.substring(0, recieveMessage.indexOf(":"));
                            recieveMessage = recieveMessage.substring(recieveMessage.indexOf(":") + 1);
                            if (head.equals("download")) {
                                AnalysisServerData(recieveMessage);
                            } else {
                                Log.e("TAG", "Wrong format from server!");
                                break;
                            }
                            Thread.sleep(300);
                            Log.d("TAG", "Get more information");
                            sendMessage = "next:";
                            writer.println(sendMessage);
                            writer.flush();
                        }
                    }
                    mHandler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                Log.e("TAG", "Analysis failed!");
                e.printStackTrace();
            }
        }
    }

    private void AnalysisServerData(String serverMessage) {
        CellContact singleContact = new CellContact();
        int length = Integer.parseInt(serverMessage.substring(0, serverMessage.indexOf(":")));
        serverMessage = serverMessage.substring(serverMessage.indexOf(":") + 1);
        while (!serverMessage.equals("")) {
            Log.d("TAG", "Analysis loop!");
            int titlePosition=serverMessage.indexOf(":");
            String title = serverMessage.substring(0, titlePosition);
            serverMessage = serverMessage.substring(titlePosition + 1);
            int lengthPosition=serverMessage.indexOf(":");
            int contentLength = Integer.parseInt(serverMessage.substring(0, lengthPosition));
            String content = serverMessage.substring(lengthPosition+1, lengthPosition+1+contentLength);
            Log.d("TAG", content);
            if (title.equals("name")) {
                singleContact.setName(content);
            } else if (title.equals("phonenumber")) {
                singleContact.setPhoneNumber(content);
            } else {
                Log.e("TAG", "Unknow field!");
            }
            if(contentLength+1>serverMessage.length()){
                break;
            }
            serverMessage = serverMessage.substring(lengthPosition+1+contentLength+1);
            Log.d("TAG", "Remaining message:  "+serverMessage);
        }
        Log.d("TAG", "Analysis over!");
        serverContact.add(singleContact);
    }

    //Send info to server
    private void SendToServer() {
        executorService.execute(new SendService());
    }

    class SendService implements Runnable {

        @Override
        public void run() {
            try {
                for (int index = 0; index != phoneContact.size(); index++) {
                    String messageForSend = "upload:";
                    String content = "";
                    CellContact contact = phoneContact.get(index);
                    String name = contact.getName();
                    String phoneNumber = contact.getPhoneNumber();
                    content = "name:" + name.length() + ":" + name + ":phoneNumber:" + phoneNumber.length() + ":" + phoneNumber + ":";
                    messageForSend += content.length() + ":" + content;
                    writer.println(messageForSend);
                    writer.flush();
                    Thread.sleep(300);
                }
            } catch (Exception e) {

            }
        }
    }

    private void StoreContact() {
        if (!serverContact.isEmpty()) {
            ArrayList<ContentProviderOperation> Opreations = new ArrayList<>();
            int index = 0;
            for (CellContact singleContact : serverContact) {
                index = Opreations.size();
                Opreations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).build());
                Opreations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, singleContact.getName()).withYieldAllowed(true).build());
                Opreations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, index).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, singleContact.getPhoneNumber()).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).withValue(ContactsContract.CommonDataKinds.Phone.LABEL, "").withYieldAllowed(true).build());
            }
            if (Opreations != null && Opreations.size() != 0) {
                try {
                    activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, Opreations);
                } catch (Exception e) {

                }
            }
        } else {
            Toast.makeText(activity, "没有需要存储的联系人！", Toast.LENGTH_SHORT).show();
        }
    }
}
