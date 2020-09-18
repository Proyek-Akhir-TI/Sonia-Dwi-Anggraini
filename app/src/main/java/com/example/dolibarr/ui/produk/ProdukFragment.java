package com.example.dolibarr.ui.produk;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dolibarr.MainActivity;
import com.example.dolibarr.R;
import com.example.dolibarr.api.BaseApiService;
import com.example.dolibarr.api.UtilsApi;
import com.example.dolibarr.util.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdukFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SharedPrefManager sharedPrefManager;
    ProgressDialog loading;
    @BindView(R.id.produk_recyclerview_list)
    RecyclerView produkRecyclerviewList;
    @BindView(R.id.produk_swipeContainer)
    SwipeRefreshLayout produkSwipeContainer;
    @BindView(R.id.produk_tvSubtotal)
    TextView produkTvSubtotal;
    @BindView(R.id.produk_etFilter)
    EditText produkEtFilter;
    @BindView(R.id.produk_btnHapusFilter)
    Button produkBtnHapusFilter;
    @BindView(R.id.produk_btnCariFilter)
    Button produkBtnCariFilter;


    private ProdukAdapter produkAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;
    private int so_id;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_produk, container, false);
        ButterKnife.bind(this, root);

        Bundle arguments = getArguments();

        if (arguments == null)
            Toast.makeText(getActivity(), "Arguments is NULL", Toast.LENGTH_LONG).show();
        else {
            so_id = getArguments().getInt("so_id", 0);
        }

        FloatingActionButton floatingActionButton = ((MainActivity) requireActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.hide();

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }


        swipe = root.findViewById(R.id.produk_swipeContainer);

        mBaseApiService = UtilsApi.getAPIService();
        sharedPrefManager = new SharedPrefManager(mContext);

        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(false);
            loadData();
        });

        loadData();
        return root;

    }

    private void loadData() {
        loading = ProgressDialog.show(mContext, null, "Mengambil data ...", true, false);
        mBaseApiService.getProdukList(this.so_id,produkEtFilter.getText().toString())
                .enqueue(new Callback<ProdukModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<ProdukModelList> call, @NotNull Response<ProdukModelList> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            generateList(Objects.requireNonNull(response.body()).getArrayList());

                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ProdukModelList> call, Throwable t) {
                        Toast.makeText(mContext, "ERROR" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }

    private void generateList(ArrayList<ProdukModel> arrayList) {

        recyclerView = requireView().findViewById(R.id.produk_recyclerview_list);
        produkAdapter = new ProdukAdapter(arrayList);

        produkAdapter.onBindCallBack = (jenis, viewHolder, position) -> {

            if ("btnUpdatePesanan".equals(jenis)) {
                loading = ProgressDialog.show(mContext, null, "Mengupdate data ...", true, false);
                String qty = viewHolder.etQty.getText().toString().equals("") ? "0" : viewHolder.etQty.getText().toString();

                mBaseApiService.updatePesanan(this.so_id, viewHolder.produk_id, Integer.parseInt(qty))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    loading.dismiss();
                                    swipe.setRefreshing(false);
                                    loadData();
                                } else {
                                    loading.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toasty.error(mContext, "Ada kesalahan!\n" + t.toString(), Toast.LENGTH_LONG, true).show();
                                loading.dismiss();
                            }
                        });

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        };

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(produkAdapter);

        double total = 0.0;
//        for (int i = 0; i < arrayList.size(); i++) {
//            total += (arrayList.get(i).getQty() * arrayList.get(i).getHarga() );
//        }

        for (ProdukModel pm : arrayList) {
            total += (pm.getQty() * pm.getHarga());
        }

        produkTvSubtotal.setText(String.format("%s", UtilsApi.formatRupiah(total)));
    }

    @OnClick(R.id.produk_btnHapusFilter)
    public void onProdukBtnHapusFilterClicked() {
        produkEtFilter.setText("");
        loadData();
    }

    @OnClick(R.id.produk_btnCariFilter)
    public void onProdukBtnCariFilterClicked() {
        loadData();
    }
}
