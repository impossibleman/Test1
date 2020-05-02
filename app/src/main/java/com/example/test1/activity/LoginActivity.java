package com.example.test1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test1.R;
import com.example.test1.commontool.ConstantAssemble;
import com.example.test1.commontool.NetWorkTool;
import com.example.test1.dataconstruct.PeanutObject;
import com.example.test1.dataconstruct.PersonalInfo;
import com.example.test1.fragment.ChatOperationFragment;
import com.jaeger.library.StatusBarUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOptions;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    PrintWriter writer;
    BufferedReader reader;
    Toolbar tbLogin;
    EditText etUserName;
    EditText etPassword;
    TextView tvLoading;
    Button btConfirm;
    Button btCancel;
    String accountId;
    String password;
    PersonalInfo personalInfo;
    boolean isFirstConnect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserName = findViewById(R.id.et_user_name);
        etPassword = findViewById(R.id.et_password);
        tbLogin = findViewById(R.id.tb_login);
        btConfirm = findViewById(R.id.bt_confirm);
        btCancel = findViewById(R.id.bt_cancel);
        tvLoading = findViewById(R.id.tv_loading);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetWorkTool.CheckNetConnect(LoginActivity.this)) {
                    return;
                }
                accountId = etUserName.getText().toString();
                password = etPassword.getText().toString();
                if (accountId.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please input id or password!", Toast.LENGTH_LONG).show();
                    return;
                }
//                if(accountId.length()>14||accountId.length()<6){
//                    Toast.makeText(LoginActivity.this,"ID's length must be less than 14 and more than 6!",Toast.LENGTH_LONG).show();
//                    return;
//                }
//                if(password.length()>14||password.length()<6){
//                    Toast.makeText(LoginActivity.this,"password's length must be less than 14 and more than 6!",Toast.LENGTH_LONG).show();
//                    return;
//                }
                Login loginThread = new Login();
                loginThread.start();
                SetLoginState(false);
            }
        });
        tvLoading.setVisibility(View.INVISIBLE);
        tbLogin.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(ConstantAssemble.ACTIVITY_RESULT_CODE_NEGATIVE, intent);
                finish();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(ConstantAssemble.ACTIVITY_RESULT_CODE_NEGATIVE, intent);
                finish();
            }
        });
        StatusBarUtil.setColor(this, getResources().getColor(R.color.grayblue));

        personalInfo = new PersonalInfo();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putInt("resourcetype", ConstantAssemble.ACTIVITY_START_RESOURCE_LOGIN);
                    bundle.putInt("userid", personalInfo.getUesrId());
                    bundle.putString("accountid", accountId);
                    bundle.putString("password", password);
                    intent.putExtras(bundle);
                    setResult(ConstantAssemble.ACTIVITY_RESULT_CODE_CONFIRM, intent);
                    finish();
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this, "Server has problems!", Toast.LENGTH_LONG).show();
                    CreateLocalAccount();
                    break;
                case 2:
                    Toast.makeText(LoginActivity.this, "ID or password is wrong!", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    SetLoginState(true);
                    break;
            }
            ;
        }
    };

    private void SetLoginState(boolean canEdit) {
        etUserName.setEnabled(canEdit);
        etPassword.setEnabled(canEdit);
        btConfirm.setClickable(canEdit);
        btCancel.setClickable(canEdit);
        tvLoading.setVisibility(canEdit ? View.INVISIBLE : View.VISIBLE);
    }

    class Login extends Thread {
        @Override
        public void run() {
            try {
                Socket socket = NetWorkTool.GetSocket();
                if (socket == null) {
                    isFirstConnect = true;
//                    socket=new Socket("192.168.3.46",8888);
                    socket = new Socket();
                    InetSocketAddress address = new InetSocketAddress("192.168.3.46", 8888);
                    socket.connect(address, 2500);
                    NetWorkTool.CreateSocket(socket);
                }
                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                String requestMessage = "{requesttype:5:" + ConstantAssemble.SERVER_REQUEST_TYPE_LOGIN + "}{username:" + accountId.length() + ":" + accountId + "password:" + password.length() + ":" + password + "}";
                writer.println(requestMessage);
                String recieveMessage;
                if (isFirstConnect) {
                    recieveMessage = reader.readLine();
                    Log.d("TAG", "recieveMessage: " + recieveMessage);
                    isFirstConnect = false;
                }
                recieveMessage = reader.readLine();
                Log.d("TAG", "recieveMessage: " + recieveMessage);
                if (recieveMessage == null || recieveMessage.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Net connection error!", Toast.LENGTH_LONG).show();
                    return;
                }
                ParseResult(recieveMessage);
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(1);
//                mHandler.sendEmptyMessage(3);
            }
        }
    }

    private void ParseResult(String recieveMessage) {
        List<PeanutObject> objs = NetWorkTool.ParseNetResult(recieveMessage);
        if (objs.size() > 0) {
            PeanutObject headPeanut = objs.get(0);
            PeanutObject bodyPeanut = objs.get(1);
            if (headPeanut.GetValue(ConstantAssemble.SERVER_REQUEST_TYPE_PREFIX).equals(ConstantAssemble.SERVER_REQUEST_TYPE_LOGIN)) {
                String AuthenaticateResult = bodyPeanut.GetValue(ConstantAssemble.SERVER_REQUEST_RESULT_LOGIN_PREFIX);
                if (AuthenaticateResult == null || AuthenaticateResult.isEmpty()) {
                    mHandler.sendEmptyMessage(1);
                    return;
                }
                if (AuthenaticateResult.equals(ConstantAssemble.SERVER_REQUEST_RESULT_NEGATIVE)) {
                    mHandler.sendEmptyMessage(2);
                    return;
                } else {
                    int userId = Integer.parseInt(bodyPeanut.GetValue("userid"));
                    personalInfo.setUesrId(userId);
                    personalInfo.setAccountId(accountId);
                    personalInfo.setPassword(password);
                    mHandler.sendEmptyMessage(0);
                }
            }
        }
    }

    void CreateLocalAccount() {
        personalInfo.setUesrId(0);
        personalInfo.setAccountId(accountId);
        personalInfo.setPassword(password);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}
