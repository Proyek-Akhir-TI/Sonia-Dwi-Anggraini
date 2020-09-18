package com.example.dolibarr.ui.pengantaran;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dolibarr.R;
import com.example.dolibarr.api.UtilsApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class PengantaranAdapter extends RecyclerView.Adapter<PengantaranAdapter.PengantaranViewHolder>{

    Context mContext;
    private ArrayList<PengantaranModel> arrayList;
    OnBindCallBack onBindCallBack;

    public PengantaranAdapter(ArrayList<PengantaranModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PengantaranAdapter.PengantaranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_pengantaran_list, parent, false);
        return new PengantaranAdapter.PengantaranViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PengantaranAdapter.PengantaranViewHolder holder, int position) {
        String toko = arrayList.get(position).getNama();
        String ref = arrayList.get(position).getRef();

        double nominal = arrayList.get(position).getNominal();
        int status = arrayList.get(position).getStatus();
        String[] arr_status = {"Draft","Sudah divalidasi","Dalam Proses","Sudah diantar"};

        holder.tvToko.setText(toko);
        holder.pesanan_id = arrayList.get(position).getPesanan_id();
        holder.tvRef.setText(ref);
        holder.tvNominal.setText(String.format(Locale.US, "%s", UtilsApi.formatRupiah(nominal)));

        Picasso.get().invalidate(UtilsApi.BASE_URL + arrayList.get(position).getFoto());
        Picasso.get().load(UtilsApi.BASE_URL + arrayList.get(position).getFoto()).into(holder.imgViewFoto);


        holder.btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri navigationIntentUri = Uri.parse("google.navigation:q=" + arrayList.get(position).getLatitude() + "," + arrayList.get(position).getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                view.getContext().startActivity(mapIntent);
            }
        });

        holder.btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBindCallBack != null) {
                    onBindCallBack.OnViewBind("btnFotoOnClick", holder, position);
                }
            }
        });

        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBindCallBack != null) {
                    onBindCallBack.OnViewBind("btnStatusOnClick", holder, position);
                }
            }
        });

        holder.itemView.setOnClickListener(view -> {
            //
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class PengantaranViewHolder extends RecyclerView.ViewHolder {
        int pesanan_id;
        TextView tvToko;
        TextView tvRef;
        TextView tvNominal;

        Button btnLokasi;
        Button btnStatus;
        Button btnFoto;
        ImageView imgViewFoto;

        public PengantaranViewHolder(View itemView) {
            super(itemView);
            tvToko = itemView.findViewById(R.id.pengantaran_tvToko);
            tvRef = itemView.findViewById(R.id.pengantaran_tvRef);
            tvNominal = itemView.findViewById(R.id.pengantaran_tvNominal);

            btnLokasi = itemView.findViewById(R.id.pengantaran_btnLokasi);
            btnStatus = itemView.findViewById(R.id.pengantaran_btnStatus);
            btnFoto = itemView.findViewById(R.id.pengantaran_btnFoto);
            imgViewFoto = itemView.findViewById(R.id.pengantaran_imgViewfoto);
        }
    }
}
