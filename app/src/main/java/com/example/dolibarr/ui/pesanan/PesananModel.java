package com.example.dolibarr.ui.pesanan;

import com.google.gson.annotations.SerializedName;

public class PesananModel {

    @SerializedName("id")
    private int pesanan_id;
    @SerializedName("ref")
    private String ref;
    @SerializedName("tanggal")
    private String tanggal;
    @SerializedName("nominal")
    private double nominal;
    @SerializedName("status")
    private int status;

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public int getPesanan_id() {
        return pesanan_id;
    }

    public void setPesanan_id(int pesanan_id) {
        this.pesanan_id = pesanan_id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
