package com.cookandroid.myproject;

import com.google.gson.annotations.SerializedName;

public class NaverMapData {
    @SerializedName("id")
    private int id;
    @SerializedName("arsno") // php 파일과 이름이 같아야 함.
    private int arsno;
    @SerializedName("bstopid")
    private int bstopid;
    @SerializedName("lineno")
    private String lineno;
    @SerializedName("nodenm")
    private String nodenm;
    @SerializedName("gpsx")
    private double gpsx;
    @SerializedName("gpsy")
    private double gpsy;


    public int getid() {
        return id;
    }

    public int getarsno() {
        return arsno;
    }

    public int getbstopid() {
        return bstopid;
    }

    public String getlineno() {
        return lineno;
    }

    public String getnodenm() {
        return nodenm;
    }

    public Double getgpsx() {
        return gpsx;
    }

    public Double getgpsy() {
        return gpsy;
    }
}
