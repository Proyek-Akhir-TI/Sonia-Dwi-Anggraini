package com.example.dolibarr.ui.pengantaran;

import com.example.dolibarr.ui.pesanan.PesananModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PengantaranModelList {

    @SerializedName("pengantaranList")
    private ArrayList<PengantaranModel> arrayList;

    public ArrayList<PengantaranModel> getArrayList() {
        return arrayList;
    }

    public void setArraylList(ArrayList<PengantaranModel> arrayList) {
        this.arrayList = arrayList;
    }
}
