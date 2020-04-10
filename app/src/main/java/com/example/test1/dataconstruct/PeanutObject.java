package com.example.test1.dataconstruct;

import java.util.HashMap;
import java.util.Map;

public class PeanutObject {
    private HashMap<String,String> values;

    public PeanutObject(){
        values=new HashMap<>();
    }

    public String GetValue(String fieldName){
        return values.get(fieldName);
    }

    public void SetValue(String fieldName,String content){
        values.put(fieldName,content);
    }
}
