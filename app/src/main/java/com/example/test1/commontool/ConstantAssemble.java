package com.example.test1.commontool;

import com.example.test1.R;

import java.util.List;

public class ConstantAssemble {
    public static String SERVER_REQUEST_TYPE_PREFIX = "requesttype";
    public static String SERVER_REQUEST_TYPE_LOGIN = "login";
    public static String SERVER_REQUEST_TYPE_GET_CONTACT = "getcontact";
    public static String SERVER_REQUEST_TYPE_GET_MESSAGE = "getmessage";
    public static String SERVER_REQUEST_TYPE_MESSAGE_ARRIVE = "messagearrive";
    public static String SERVER_REQUEST_TYPE_SEND_MESSAGE = "sendmessage";
    public static String SERVER_REQUEST_TYPE_END = "end";
    public static String SERVER_REQUEST_RESULT_LOGIN_PREFIX = "loginresult";
    public static String SERVER_REQUEST_RESULT_CONFIRM = "confirm";
    public static String SERVER_REQUEST_RESULT_NEGATIVE = "negative";

    public static String SERVICE_NAME = "com.example.test1.NoticeService";
    public static String PACKAGE_NAME = "com.example.test1";

    public static int ACTIVITY_START_RESOURCE_CONTACT_LIST = 0;
    public static int ACTIVITY_START_RESOURCE_NOTIFICATION = 1;
    public static int ACTIVITY_START_RESOURCE_LOGIN = 2;
    public static int ACTIVITY_START_RESOURCE_MAINACTIVITY = 3;

    public static String DATABASE_NAME = "androidtoolpersonalinfo";
    public static String DATABASE_TABLE_NAME_USERINFO = "userinfo";
    public static String DATABASE_TABLE_NAME_USERCONTACT = "usercontact";

    public static int ACTIVITY_REQUEST_CODE_CHAT_OPERATION = 0;

    public static int ACTIVITY_RESULT_CODE_CONFIRM = 10;
    public static int ACTIVITY_RESULT_CODE_NEGATIVE = 11;

    public static final int HOVER_STATE_GONE = 0;
    public static final int HOVER_STATE_HOVER = 1;
    public static final int HOVER_STATE_MOVING = 2;

    public static String[] names = {
            "Luke",
            "Camero",
            "Alex",
            "Gavin",
            "Sam",
            "Notorn",
            "Josh",
            "Larry",
            "Forbes",
            "Kevin",
            "Bob",
            "Ash",
            "Macree",
    };

    public static int[] headImgs = {
            R.drawable.headimage01,
            R.drawable.headimage02,
            R.drawable.headimage03,
            R.drawable.headimage04,
            R.drawable.headimage05,
            R.drawable.headimage06,
            R.drawable.headimage07,
            R.drawable.headimage08,
            R.drawable.headimage09,
    };

    public static int[] membersCount = {
            2,
            4,
            7,
            5,
            3
    };

    public static String[] classifyNames = {
            "家庭",
            "朋友",
            "亲戚",
            "同学",
            "同事",
    };

    public static float[] linePTS={
      0,0,100,100
    };
}
