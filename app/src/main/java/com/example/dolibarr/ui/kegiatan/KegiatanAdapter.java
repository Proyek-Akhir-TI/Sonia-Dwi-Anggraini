package com.example.dolibarr.ui.kegiatan;

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
import com.example.dolibarr.ui.kegiatan_detail.KegiatanDetailFragment;

import java.util.ArrayList;
import java.util.Locale;

public class KegiatanAdapter extends RecyclerView.Adapter<KegiatanAdapter.KegiatanViewHolder> {


    Context mContext;
    private ArrayList<KegiatanModel> arrayList;


    public KegiatanAdapter(ArrayList<KegiatanModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public KegiatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_kegiatan_list, parent, false);
        return new KegiatanAdapter.KegiatanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KegiatanViewHolder holder, int position) {
        String tanggal = arrayList.get(position).getTgl();
        int jmlKunjungan = arrayList.get(position).getJml_kunjungan();

        holder.tvTanggal.setText(tanggal);
        holder.tvJumlahKunjungan.setText(String.format(Locale.US, "%d Tempat dikunjungi", jmlKunjungan));


        holder.itemView.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            bundle.putString("tanggal", arrayList.get(position).getTgl());

            KegiatanDetailFragment fragment = new KegiatanDetailFragment();
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

    
    public class KegiatanViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal;
        TextView tvJumlahKunjungan;

        public KegiatanViewHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.kegiatan_tvTanggal);
            tvJumlahKunjungan = itemView.findViewById(R.id.kegiatan_tvJmlKunjungan);
        }
    }
}
