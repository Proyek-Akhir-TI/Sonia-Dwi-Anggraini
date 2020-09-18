package com.example.dolibarr.ui.toko;

import com.google.gson.annotations.SerializedName;

public class TokoModel {

    @SerializedName("id")
    private int toko_id;
    @SerializedName("nama")
    private String nama;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("foto")
    private String foto_toko;

    public int getToko_id() {
        return toko_id;
    }

    public void setToko_id(int toko_id) {
        this.toko_id = toko_id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getFoto_toko() {
        return foto_toko;
    }

    public void setFoto_toko(String foto_toko) {
        this.foto_toko = foto_toko;
    }




}
