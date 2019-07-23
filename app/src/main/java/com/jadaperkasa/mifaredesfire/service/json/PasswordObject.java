package com.jadaperkasa.mifaredesfire.service.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PasswordObject {
    @Expose
    @SerializedName("pass_lama")
    private String pass_lama;

    @Expose
    @SerializedName("pass_baru")
    private String pass_baru;

    @Expose
    @SerializedName("idktp")
    private String idktp;

    @Expose
    @SerializedName("no_akses")
    private Integer no_akses;



    public PasswordObject(String pass_lama, String pass_baru, String idktp, Integer no_akses) {
        this.pass_lama = pass_lama;
        this.pass_baru = pass_baru;
        this.idktp = idktp;
        this.no_akses = no_akses;
    }


    public String getPass_lama() {
        return pass_lama;
    }

    public void setPass_lama(String pass_lama) {
        this.pass_lama = pass_lama;
    }

    public String getPass_baru() {
        return pass_baru;
    }

    public void setPass_baru(String pass_baru) {
        this.pass_baru = pass_baru;
    }

    public String getIdktp() {
        return idktp;
    }

    public void setIdktp(String idktp) {
        this.idktp = idktp;
    }

    public Integer getNo_akses() {
        return no_akses;
    }

    public void setNo_akses(Integer no_akses) {
        this.no_akses = no_akses;
    }
}
