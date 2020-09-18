package com.example.dolibarr.ui.kegiatan;

import com.google.gson.annotations.SerializedName;

public class KegiatanModel {


    @SerializedName("tgl")
    private String tgl;
    @SerializedName("jml_kunjungan")
    private int jml_kunjungan;

    public int getJml_kunjungan() {
        return jml_kunjungan;
    }

    public void setJml_kunjungan(int jml_kunjungan) {
        this.jml_kunjungan = jml_kunjungan;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }


}
