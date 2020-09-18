package com.example.dolibarr.ui.produk;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProdukModelList {

    @SerializedName("productList")
    private ArrayList<ProdukModel> arrayList;

    public ArrayList<ProdukModel> getArrayList() {
        return arrayList;
    }

    public void setArraylList(ArrayList<ProdukModel> arrayList) {
        this.arrayList = arrayList;
    }

}
