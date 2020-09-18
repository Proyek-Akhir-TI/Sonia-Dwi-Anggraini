package com.example.dolibarr.ui.kegiatan_detail;

import com.google.gson.annotations.SerializedName;

public class KegiatanDetailModel {

    @SerializedName("id")
    private int kegiatan_id;
    @SerializedName("tgl")
    private String tgl;
    @SerializedName("latitude")
    private String latitude;
    @SerializedName("longitude")
    private String longitude;
    @SerializedName("foto")
    private String foto;
    @SerializedName("keterangan")
    private String keterangan;

    public int getKegiatan_id() {
        return kegiatan_id;
    }

    public void setKegiatan_id(int kegiatan_id) {
        this.kegiatan_id = kegiatan_id;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }


}
