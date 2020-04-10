package com.example.test1.commontool;

import java.util.HashMap;

public class FileMimeType {
    static final String MIME_TYPE_ALL="*/*";
    static final String MIME_TYPE_APK="application/vnd.android.package-archive";
    static final String MIME_TYPE_VIDEO="video/*";
    static final String MIME_TYPE_AUDIO="audio/*";
    static final String MIME_TYPE_IMAGE="image/*";
    static final String MIME_TYPE_WORD="application/msword";
    static final String MIME_TYPE_PPT="application/vnd.ms-powerpoint";
    static final String MIME_TYPE_PDF="application/pdf";
    static final String MIME_TYPE_TXT="text/plain";
    static final String MIME_TYPE_HTML="text/html";
    static final String MIME_TYPE_XML="text/xml";

    static final HashMap<String,String> mapTable=new HashMap<>();

    static {
        InitMapTable();
    }

    private static void InitMapTable() {
        mapTable.put("apk",MIME_TYPE_APK);
        mapTable.put("mp4",MIME_TYPE_VIDEO);
        mapTable.put("3gp",MIME_TYPE_VIDEO);
        mapTable.put("avi",MIME_TYPE_VIDEO);
        mapTable.put("rmvb",MIME_TYPE_VIDEO);
        mapTable.put("mp3",MIME_TYPE_AUDIO);
        mapTable.put("jpg",MIME_TYPE_IMAGE);
        mapTable.put("bmp",MIME_TYPE_IMAGE);
        mapTable.put("png",MIME_TYPE_IMAGE);
        mapTable.put("doc",MIME_TYPE_WORD);
        mapTable.put("docx",MIME_TYPE_WORD);
        mapTable.put("ppt",MIME_TYPE_PPT);
        mapTable.put("pdf",MIME_TYPE_PDF);
        mapTable.put("txt",MIME_TYPE_TXT);
        mapTable.put("html",MIME_TYPE_HTML);
        mapTable.put("xml",MIME_TYPE_XML);
    }
}
