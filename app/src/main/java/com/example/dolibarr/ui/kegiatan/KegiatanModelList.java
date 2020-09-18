package com.example.dolibarr.ui.kegiatan;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KegiatanModelList {
    @SerializedName("kegiatanList")
    private ArrayList<KegiatanModel> arrayList;
    public ArrayList<KegiatanModel> getArrayList() {
        return arrayList;
    }
    public void setArraylList(ArrayList<KegiatanModel> arrayList) {
        this.arrayList = arrayList;
    }
}
