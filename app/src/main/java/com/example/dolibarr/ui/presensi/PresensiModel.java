package com.example.dolibarr.ui.presensi;

import com.google.gson.annotations.SerializedName;

public class PresensiModel {

    @SerializedName("tanggal")
    private String tanggal;
    @SerializedName("jam_masuk")
    private String jam_masuk;
    @SerializedName("jam_pulang")
    private String jam_pulang;

    public String getTanggal() {
        return tanggal;
    }
    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
    public String getJam_masuk() {
        return jam_masuk;
    }
    public void setJam_masuk(String jam_masuk) {
        this.jam_masuk = jam_masuk;
    }
    public String getJam_pulang() {
        return jam_pulang;
    }
    public void setJam_pulang(String jam_pulang) {
        this.jam_pulang = jam_pulang;
    }


}
