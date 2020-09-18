package com.example.dolibarr.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class UtilsApi {

    public static final String BASE_URL = "http://103.109.210.29:80/~ti17198/lumen/public/";
// public static final String BASE_URL = "http://192.168.8.101/dolibarr_lumen/public/";
    public static final String BASE_URL_API = BASE_URL + "api/";
    public static final String BASE_URL_WEBVIEW = BASE_URL + "webview/";
//    public static final String BASE_DOLIBARR_URL = "c:/dolibar/dolibarr_documents/";

    // Mendeklarasikan Interface BaseApiService
    //@org.jetbrains.annotations.NotNull
    public static BaseApiService getAPIService() {
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }


    public static String formatRupiah(double nominal){
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);

        return kursIndonesia.format(nominal);
    }

}
