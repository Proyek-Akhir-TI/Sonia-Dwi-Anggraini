package com.example.dolibarr.ui.keluar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.dolibarr.LoginActivity;
import com.example.dolibarr.MainActivity;
import com.example.dolibarr.R;
import com.example.dolibarr.util.SharedPrefManager;

public class KeluarFragment extends Fragment {


    private Context mContext;
    SharedPrefManager sharedPrefManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        sharedPrefManager = new SharedPrefManager(mContext);

//        if (Boolean.FALSE.equals(sharedPrefManager.getSudahLogin())) {
//            Intent myIntent = new Intent(getActivity(), LoginActivity.class);
//            getActivity().startActivity(myIntent);
//        }

        new AlertDialog.Builder(this.mContext)
                .setTitle("Keluar")
                .setMessage("Apakah anda yakin ingin keluar aplikasi?")
                .setPositiveButton("YA !", (dialog, which) -> {

                    sharedPrefManager.saveBoolean(SharedPrefManager.sudah_login, false);

                    Intent myIntent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivity(myIntent);

                }).setNegativeButton("Tidak", (dialog, which) -> {

        }).show();


        return root;
    }
}
