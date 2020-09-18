package com.example.dolibarr.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String app_name = "dolibarr";
    public static final String sudah_login = "sudah_login";
    public static final String user_id = "user_id";
    public static final String user_username = "user_username";
    public static final String user_namalengkap = "user_namalengkap";
    public static final String user_email = "user_email";
    public static final String user_level = "user_level";
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    public SharedPrefManager(Context context) {
        sp = context.getSharedPreferences(app_name, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public String getUser_level() {
        return sp.getString(user_level, "");
    }

    public Boolean getSudahLogin() {
        return sp.getBoolean(sudah_login, false);
    }

    public Integer getUserId() {
        return sp.getInt(user_id, 0);
    }

    public String getUsername() {
        return sp.getString(user_username, "");
    }

    public String getNamaLengkap() {
        return sp.getString(user_namalengkap, "");
    }

    public String getEmail() {
        return sp.getString(user_email, "");
    }

    public void saveString(String keySP, String value) {
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveInt(String keySP, int value) {
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveBoolean(String keySP, boolean value) {
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

}
