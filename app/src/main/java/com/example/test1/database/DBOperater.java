package com.example.test1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOperater extends SQLiteOpenHelper {

    public DBOperater(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createFirstTable="create table userinfo(userid int,accountid varchar(20),password varchar(20));";
        sqLiteDatabase.execSQL(createFirstTable);
        String createSecondTable="create table usercontact(userid int,username varchar(20),headimage varchar(300),classifyid int,classifyname varchar(100),newmessagecount int);";
        sqLiteDatabase.execSQL(createSecondTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
