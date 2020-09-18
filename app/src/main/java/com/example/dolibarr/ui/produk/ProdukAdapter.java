package com.example.dolibarr.ui.produk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dolibarr.R;
import com.example.dolibarr.api.UtilsApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    OnBindCallBack onBindCallBack;
    private ArrayList<ProdukModel> arrayList;
    Context mContext;

    public ProdukAdapter(ArrayList<ProdukModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ProdukAdapter.ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_produk_list, parent, false);
        return new ProdukAdapter.ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukAdapter.ProdukViewHolder holder, int position) {


        String nama = arrayList.get(position).getNama();
        String deskripsi = arrayList.get(position).getDeskripsi();
        double harga = arrayList.get(position).getHarga();
        int qty = arrayList.get(position).getQty();
        double subtotal = harga * qty;

        holder.produk_id = arrayList.get(position).getProduk_id();

        holder.tvNama.setText(String.format(Locale.US, "%s", nama));
        holder.tvDeskripsi.setText(String.format(Locale.US, "%s", deskripsi));
        holder.tvHarga.setText(String.format(Locale.US, "%s", UtilsApi.formatRupiah(harga)));
        holder.etQty.setText(String.valueOf(qty));
        holder.tvSubtotal.setText(String.format(Locale.US, "%s", UtilsApi.formatRupiah(subtotal)));

        if(qty > 0){
            holder.btnPesan.setText("Update");
            holder.btnPesan.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.holo_yellow_dark));

        }else{
            holder.btnPesan.setText("Pesan");
            holder.btnPesan.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.holo_green_dark));

        }

        holder.btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBindCallBack != null) {
                    onBindCallBack.OnViewBind("btnUpdatePesanan", holder, position);
                }
            }
        });


        //TODO: dapatkan path logo toko
        Picasso.get().invalidate(UtilsApi.BASE_URL_API + "get-file?imagefile=" + arrayList.get(position).getFoto());
        Picasso.get().load(UtilsApi.BASE_URL_API + "get-file?imagefile=" + arrayList.get(position).getFoto()).into(holder.imgViewfoto);


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

    public class ProdukViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        TextView tvDeskripsi;
        TextView tvHarga;
        TextView tvSubtotal;
        int produk_id;

        EditText etQty;
        Button btnPesan;
        ImageView imgViewfoto;

        public ProdukViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.produk_tvNama);
            tvDeskripsi = itemView.findViewById(R.id.produk_tvDeskripsi);
            tvHarga = itemView.findViewById(R.id.produk_tvHarga);
            tvSubtotal = itemView.findViewById(R.id.produk_tvSubtotal);
            etQty = itemView.findViewById(R.id.produk_etQty);
            btnPesan = itemView.findViewById(R.id.produk_btnPesan);
            imgViewfoto = itemView.findViewById(R.id.produk_imgViewfoto);
        }
    }


}
