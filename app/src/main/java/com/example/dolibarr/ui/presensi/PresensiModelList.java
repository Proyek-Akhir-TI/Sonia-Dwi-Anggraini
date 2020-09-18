package com.example.dolibarr.ui.presensi;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PresensiModelList {
    @SerializedName("presensiList")
    private ArrayList<PresensiModel> arrayList;
    public ArrayList<PresensiModel> getArrayList() {
        return arrayList;
    }
    public void setArraylList(ArrayList<PresensiModel> arrayList) {
        this.arrayList = arrayList;
    }
}
