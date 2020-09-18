package com.example.dolibarr.ui.pengantaran;

import com.google.gson.annotations.SerializedName;

public class PengantaranModel {

    @SerializedName("id")
    private int pesanan_id;
    @SerializedName("nom")
    private String nama;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("ref")
    private String ref;
    @SerializedName("nominal")
    private double nominal;
    @SerializedName("status")
    private int status;

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @SerializedName("foto")
    private String foto;

    public int getPesanan_id() {
        return pesanan_id;
    }

    public void setPesanan_id(int pesanan_id) {
        this.pesanan_id = pesanan_id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
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
