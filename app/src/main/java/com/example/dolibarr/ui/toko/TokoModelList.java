package com.example.dolibarr.ui.toko;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TokoModelList {

    @SerializedName("tokoList")
    private ArrayList<TokoModel> arrayList;

    public ArrayList<TokoModel> getArrayList() {
        return arrayList;
    }

    public void setArraylList(ArrayList<TokoModel> arrayList) {
        this.arrayList = arrayList;
    }


}
