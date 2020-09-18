package com.example.dolibarr.ui.pesanan;

import android.content.Context;
import android.os.Bundle;
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
import com.example.dolibarr.ui.pesanan_detail.PesananRekapFragment;
import com.example.dolibarr.ui.produk.ProdukFragment;

import java.util.ArrayList;
import java.util.Locale;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananViewHolder> {

    OnBindCallBack onBindCallBack;
    Context mContext;
    private ArrayList<PesananModel> arrayList;


    public PesananAdapter(ArrayList<PesananModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PesananViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_pesanan_list, parent, false);
        return new PesananAdapter.PesananViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PesananViewHolder holder, int position) {
        String tanggal = arrayList.get(position).getTanggal();
        double nominal = arrayList.get(position).getNominal();
        int status = arrayList.get(position).getStatus();
        String[] arr_status = {"Draft","Sudah divalidasi","Dalam Proses","Sudah diantar"};

        holder.pesanan_id = arrayList.get(position).getPesanan_id();
        holder.tvTanggal.setText(tanggal);
        holder.tvNominal.setText(String.format(Locale.US, "%s", UtilsApi.formatRupiah(nominal)));
        holder.tvStatus.setText(String.format(Locale.US, "%s", arr_status[status]));

        if(status > 0){
            holder.btnDetail.setText("Detail");
            holder.btnDetail.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.holo_green_dark));
            holder.btnDetail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("pesanan_id", arrayList.get(position).getPesanan_id());

                    PesananRekapFragment fragment = new PesananRekapFragment();
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();

                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, "TAG_PESANAN_DETAIL")
                            .addToBackStack(null)
                            .commit();
                }
            });
        }else{
            holder.btnDetail.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.holo_yellow_dark));
            holder.btnDetail.setText("Edit");

            holder.btnDetail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("so_id", arrayList.get(position).getPesanan_id());

                    ProdukFragment fragment = new ProdukFragment();
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();

                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, "TAG_PESANAN_FRAGMENT")
                            .addToBackStack(null)
                            .commit();
                }
            });

        }

        holder.btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBindCallBack != null) {
                    onBindCallBack.OnViewBind("btnBatalOnClick", holder, position);
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

    public class PesananViewHolder extends RecyclerView.ViewHolder {
        int pesanan_id;
        TextView tvTanggal;
        TextView tvNominal;
        TextView tvStatus;

        Button btnBatal;
        Button btnDetail;
        ImageView imgViewFoto;

        public PesananViewHolder(View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.pesanan_tvTanggal);
            tvNominal = itemView.findViewById(R.id.pesanan_tvNominal);
            tvStatus = itemView.findViewById(R.id.pesanan_tvStatus);

            btnBatal = itemView.findViewById(R.id.pesanan_btnBatal);
            btnDetail = itemView.findViewById(R.id.pesanan_btnDetail);
            imgViewFoto = itemView.findViewById(R.id.toko_imgViewfoto);
        }
    }
}
