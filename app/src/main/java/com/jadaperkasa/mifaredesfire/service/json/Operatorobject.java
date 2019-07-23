package com.jadaperkasa.mifaredesfire.service.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Operatorobject {
    @Expose
    @SerializedName("idktp")
    private String idktp;

    @Expose
    @SerializedName("pass")
    private String pass;

    @Expose
    @SerializedName("authtype")
    private Integer authtype;

    @Expose
    @SerializedName("created_by")
    private String created_by;

    @Expose
    @SerializedName("nama")
    private String nama;

    @Expose
    @SerializedName("ektp")
    private String ektp;

    @Expose
    @SerializedName("akses")
    private Integer akses;


    public Operatorobject(String idktp, String pass, Integer authtype, String created_by,
                          String nama, String ektp,int akses) {
        this.idktp = idktp;
        this.pass = pass;
        this.authtype = authtype;
        this.created_by = created_by;
        this.nama = nama;
        this.ektp = ektp;
        this.akses = akses;
    }



    public String getId_ktp() {
        return idktp;
    }

    public void setId_ktp(String id_ktp1) {
        this.idktp = idktp;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Integer getAuthtype() {
        return authtype;
    }

    public void setAuthtype(Integer authtype) {
        this.authtype = authtype;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEktp() {
        return ektp;
    }

    public void setEktp(String ektp) {
        this.ektp = ektp;
    }

    public int getAkses() {
        return akses;
    }

    public void setAkses(int akses) {
        this.akses = akses;
    }
}


