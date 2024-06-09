package com.cookandroid.myproject;

import com.google.gson.annotations.SerializedName;

public class data_model {
    @SerializedName("id")
    private int id;

    @SerializedName("arsno")
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

    public int getArsno() {
        return arsno;
    }

    public int getBstopid() {
        return bstopid;
    }

    public String getLineno() {
        return lineno;
    }

    public void setLineno(String lineno) {
        this.lineno = lineno;
    }

    public String getNodenm() {
        return nodenm;
    }

    public void setNodenm(String nodenm) {
        this.nodenm = nodenm;
    }

    public double getGpsx() {
        return gpsx;
    }

    public double getGpsy() {
        return gpsy;
    }

    public void setId(int id) {
        this.id = id;
    }
}
