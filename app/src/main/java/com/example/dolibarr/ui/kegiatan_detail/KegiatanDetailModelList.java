package com.example.dolibarr.ui.kegiatan_detail;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KegiatanDetailModelList {
    @SerializedName("kegiatanDetailList")
    private ArrayList<KegiatanDetailModel> arrayList;
    public ArrayList<KegiatanDetailModel> getArrayList() {
        return arrayList;
    }
    public void setArraylList(ArrayList<KegiatanDetailModel> arrayList) {
        this.arrayList = arrayList;
    }
}
