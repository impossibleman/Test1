package com.example.test1.commontool;

public class ConstantAssemble {
    public static String SERVER_REQUEST_TYPE_PREFIX="requesttype";
    public static String SERVER_REQUEST_TYPE_LOGIN="login";
    public static String SERVER_REQUEST_TYPE_GET_CONTACT="getcontact";
    public static String SERVER_REQUEST_TYPE_GET_MESSAGE="getmessage";
    public static String SERVER_REQUEST_TYPE_MESSAGE_ARRIVE="messagearrive";
    public static String SERVER_REQUEST_TYPE_SEND_MESSAGE="sendmessage";
    public static String SERVER_REQUEST_TYPE_END="end";
    public static String SERVER_REQUEST_RESULT_LOGIN_PREFIX="loginresult";
    public static String SERVER_REQUEST_RESULT_CONFIRM="confirm";
    public static String SERVER_REQUEST_RESULT_NEGATIVE="negative";

    public static String SERVICE_NAME="com.example.test1.NoticeService";
    public static String PACKAGE_NAME="com.example.test1";

    public static int ACTIVITY_START_RESOURCE_CONTACT_LIST=0;
    public static int ACTIVITY_START_RESOURCE_NOTIFICATION=1;
    public static int ACTIVITY_START_RESOURCE_LOGIN=2;
    public static int ACTIVITY_START_RESOURCE_MAINACTIVITY=3;

    public static String DATABASE_NAME="androidtoolpersonalinfo";
    public static String DATABASE_TABLE_NAME_USERINFO="userinfo";
    public static String DATABASE_TABLE_NAME_USERCONTACT="usercontact";

    public static int ACTIVITY_REQUEST_CODE_CHAT_OPERATION=0;

    public static int ACTIVITY_RESULT_CODE_CONFIRM=10;
    public static int ACTIVITY_RESULT_CODE_NEGATIVE=11;
}
