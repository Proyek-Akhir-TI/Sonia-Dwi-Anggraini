package com.example.dolibarr.ui.pesanan;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PesananModelList {

    @SerializedName("pesananList")
    private ArrayList<PesananModel> arrayList;

    public ArrayList<PesananModel> getArrayList() {
        return arrayList;
    }

    public void setArraylList(ArrayList<PesananModel> arrayList) {
        this.arrayList = arrayList;
    }


}
