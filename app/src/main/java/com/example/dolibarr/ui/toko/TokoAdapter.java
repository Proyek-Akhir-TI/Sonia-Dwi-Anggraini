package com.example.dolibarr.ui.toko;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dolibarr.R;
import com.example.dolibarr.api.UtilsApi;
import com.example.dolibarr.ui.pesanan.PesananFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class TokoAdapter extends RecyclerView.Adapter<TokoAdapter.TokoViewHolder> {

    private ArrayList<TokoModel> arrayList;
    OnBindCallBack onBindCallBack;

    public TokoAdapter(ArrayList<TokoModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public TokoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_toko_list, parent, false);
        return new TokoAdapter.TokoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TokoViewHolder holder, int position) {

        String nama = arrayList.get(position).getNama();
        String alamat = arrayList.get(position).getAlamat();

        holder.rowid = arrayList.get(position).getToko_id();
        holder.tvNama.setText(String.format(Locale.US, "%s", nama));
        holder.tvAlamat.setText(String.format(Locale.US, "%s", alamat));

        //TODO: dapatkan path logo toko
        Picasso.get().invalidate(UtilsApi.BASE_URL + arrayList.get(position).getFoto_toko());
        Picasso.get().load(UtilsApi.BASE_URL + arrayList.get(position).getFoto_toko()).into(holder.imgViewFoto);

        holder.btnProfile.setOnClickListener(view -> {
            Log.i("OnClick", String.valueOf(arrayList.get(position).getToko_id()));
            Bundle bundle = new Bundle();
            bundle.putInt("toko_id", arrayList.get(position).getToko_id());

            TokoFormFragment fragment = new TokoFormFragment();
            fragment.setArguments(bundle);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, "TAG_TOKO_PROFILE")
                    .addToBackStack(null)
                    .commit();
        });

        holder.btnPesanan.setOnClickListener(view -> {

            Log.i("OnClick", String.valueOf(arrayList.get(position).getToko_id()));
            Bundle bundle = new Bundle();
            bundle.putInt("toko_id", arrayList.get(position).getToko_id());

            PesananFragment fragment = new PesananFragment();
            fragment.setArguments(bundle);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment, "TAG_PESANAN")
                    .addToBackStack(null)
                    .commit();
        });

        holder.imgViewHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBindCallBack != null) {
                    onBindCallBack.OnViewBind("btHapusTokoOnClick", holder, position);
                }
            }
        });

        holder.btnLokasi.setOnClickListener(view -> {

            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + arrayList.get(position).getLatitude() + "," + arrayList.get(position).getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            view.getContext().startActivity(mapIntent);

//            Log.i("OnClick", String.valueOf(arrayList.get(position).getToko_id()));
//            Bundle bundle = new Bundle();
//            bundle.putString("nama_toko", arrayList.get(position).getNama());
//            bundle.putString("longitude", arrayList.get(position).getLongitude());
//            bundle.putString("latitude", arrayList.get(position).getLatitude());
//
//            LokasiFragment fragment = new LokasiFragment();
//            fragment.setArguments(bundle);
//            AppCompatActivity activity = (AppCompatActivity) view.getContext();
//
//            activity.getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.nav_host_fragment, fragment, "TAG_LOKASI")
//                    .addToBackStack(null)
//                    .commit();
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class TokoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        TextView tvAlamat;
        Button btnProfile;
        Button btnPesanan;
        Button btnLokasi;
        ImageView imgViewFoto;
        ImageView imgViewHapus;
        int rowid;

        public TokoViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.toko_tvNama);
            tvAlamat = itemView.findViewById(R.id.toko_tvAlamat);
            btnProfile = itemView.findViewById(R.id.toko_btnProfile);
            btnPesanan = itemView.findViewById(R.id.toko_btnPesanan);
            btnLokasi = itemView.findViewById(R.id.toko_btnLokasi);
            imgViewFoto = itemView.findViewById(R.id.toko_imgViewfoto);
            imgViewHapus = itemView.findViewById(R.id.toko_imgViewHapus);
        }
    }


}
