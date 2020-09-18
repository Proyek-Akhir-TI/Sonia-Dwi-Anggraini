package com.example.dolibarr.ui.kegiatan_detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dolibarr.R;

import java.util.ArrayList;

public class KegiatanDetailAdapter extends RecyclerView.Adapter<KegiatanDetailAdapter.KegiatanViewHolder> {


    Context mContext;
    private ArrayList<KegiatanDetailModel> arrayList;


    public KegiatanDetailAdapter(ArrayList<KegiatanDetailModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public KegiatanDetailAdapter.KegiatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_kegiatan_detail_list, parent, false);
        return new KegiatanDetailAdapter.KegiatanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KegiatanDetailAdapter.KegiatanViewHolder holder, int position) {
        String tanggal = arrayList.get(position).getTgl();
        String latitude = arrayList.get(position).getLatitude();
        String longitude = arrayList.get(position).getLongitude();
        String foto = arrayList.get(position).getFoto();
        String keterangan = arrayList.get(position).getKeterangan();

        holder.tvTanggal.setText(tanggal);
        holder.tvKeterangan.setText(keterangan);

        holder.btnLokasi.setOnClickListener(view -> {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            view.getContext().startActivity(mapIntent);
        });

        holder.itemView.setOnClickListener(view -> {

        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class KegiatanViewHolder extends RecyclerView.ViewHolder {

        TextView tvTanggal;
        TextView tvKeterangan;
        Button btnLokasi;

        public KegiatanViewHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.kegiatan_detail_tvTanggal);
            tvKeterangan = itemView.findViewById(R.id.kegiatan_detail_tvKeterangan);
            btnLokasi  = itemView.findViewById(R.id.kegiatan_detail_btnLokasi);
        }
    }
}
