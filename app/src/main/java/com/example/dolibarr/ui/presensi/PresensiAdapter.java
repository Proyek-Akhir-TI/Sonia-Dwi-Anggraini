package com.example.dolibarr.ui.presensi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dolibarr.R;
import com.example.dolibarr.ui.presensi_detail.PresensiDetailFragment;

import java.util.ArrayList;
import java.util.Locale;

public class PresensiAdapter extends RecyclerView.Adapter<PresensiAdapter.PresensiViewHolder> {

    Context mContext;
    private ArrayList<PresensiModel> arrayList;

    public PresensiAdapter(ArrayList<PresensiModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PresensiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_presensi_list, parent, false);
        return new PresensiAdapter.PresensiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PresensiViewHolder holder, int position) {
        String tanggal = arrayList.get(position).getTanggal();
        String jamMasuk = arrayList.get(position).getJam_masuk();
        String jamPulang = arrayList.get(position).getJam_pulang();

        holder.tvTanggal.setText(tanggal);
        holder.tvJamMasuk.setText(String.format(Locale.US, "Presensi Masuk: %s", jamMasuk));
        holder.tvJamPulang.setText(String.format(Locale.US, "Presensi Pulang: %s", jamPulang));


        holder.itemView.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            bundle.putString("tanggal", arrayList.get(position).getTanggal());

            PresensiDetailFragment fragment = new PresensiDetailFragment();
            fragment.setArguments(bundle);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, "TAG_KEGIATAN")
                    .addToBackStack(null)
                    .commit();

        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class PresensiViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal;
        TextView tvJamMasuk;
        TextView tvJamPulang;

        public PresensiViewHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.presensi_tvTanggal);
             tvJamMasuk = itemView.findViewById(R.id.presensi_tvJamMasuk);
            tvJamPulang = itemView.findViewById(R.id.presensi_tvJamPulang);
        }
    }
}
