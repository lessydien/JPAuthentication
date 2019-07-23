package com.jadaperkasa.mifaredesfire.service.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class responseUpload {
    @Expose
    @SerializedName("pesan")
    private Integer pesan;

    @Expose
    @SerializedName("nama")
    private String nama;

    @Expose
    @SerializedName("akses")
    private String akses;

    @Expose
    @SerializedName("no_akses")
    private Integer no_akses;

    @Expose
    @SerializedName("ektp")
    private String ektp;


    public responseUpload() {
    }


    public Integer getPesan() {
        return pesan;
    }

    public void setPesan(Integer pesan) {
        this.pesan = pesan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAkses() {
        return akses;
    }

    public void setAkses(String akses) {
        this.akses = akses;
    }

    public Integer getNo_akses() {
        return no_akses;
    }

    public void setNo_akses(Integer no_akses) {
        this.no_akses = no_akses;
    }


    public String getEktp() {
        return ektp;
    }

    public void setEktp(String ektp) {
        this.ektp = ektp;
    }
}
