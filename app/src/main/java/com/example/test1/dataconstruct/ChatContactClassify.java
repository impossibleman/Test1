package com.example.test1.dataconstruct;

import java.util.ArrayList;
import java.util.List;

public class ChatContactClassify {
    int classifyId;
    String classifyName;
    List<ChatContact> contact;

    public ChatContactClassify(){
        contact=new ArrayList<>();
    }

    public int getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public List<ChatContact> getContact() {
        return contact;
    }

    public void setContact(List<ChatContact> contact) {
        this.contact = contact;
    }

    public void addSingleContact(ChatContact singleContact) {
        contact.add(singleContact);
    }
}
