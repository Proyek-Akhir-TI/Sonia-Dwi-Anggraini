package com.example.dolibarr.ui.pesanan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SharedPrefManager sharedPrefManager;
    ProgressDialog loading;
    private int toko_id;

    private PesananAdapter pesananAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;
    private BaseApiService baseApiService;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_pesanan, container, false);
        baseApiService = UtilsApi.getAPIService();
        Bundle arguments = getArguments();

        if (arguments == null)
            Toast.makeText(getActivity(), "Arguments is NULL", Toast.LENGTH_LONG).show();
        else {
            toko_id = getArguments().getInt("toko_id", 0);
        }

        FloatingActionButton floatingActionButton = ((MainActivity) requireActivity()).getFloatingActionButton();
        floatingActionButton.setOnClickListener(view -> {
            this.createNewSO();
        });

        swipe = root.findViewById(R.id.pesanan_swipeContainer);

        mBaseApiService = UtilsApi.getAPIService();
        sharedPrefManager = new SharedPrefManager(mContext);

        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(false);
            loadData();
        });

        loadData();
        return root;

    }

    private void createNewSO() {

        loading = ProgressDialog.show(mContext, null, "Membuat data pesanan baru ...", true, false);
        mBaseApiService.postPesananBaru(sharedPrefManager.getUserId(), this.toko_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {

                        loading.dismiss();
//                        if (response.isSuccessful()) {
//                            loading.dismiss();
//
//                        } else {
//                            loading.dismiss();
//                        }

                        swipe.setRefreshing(false);
                        loadData();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(mContext, "Pesanan gagal dibuat", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }

    private void loadData() {
        loading = ProgressDialog.show(mContext, null, "Mengambil data ...", true, false);
        mBaseApiService.getPesananTokoList(this.toko_id)
                .enqueue(new Callback<PesananModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<PesananModelList> call, @NotNull Response<PesananModelList> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            generateList(Objects.requireNonNull(response.body()).getArrayList());

                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<PesananModelList> call, Throwable t) {
                        Toast.makeText(mContext, "Presensi berhasil dikirimkan", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }

    private void generateList(ArrayList<PesananModel> arrayList) {

        recyclerView = requireView().findViewById(R.id.pesanan_recyclerview_list);
        pesananAdapter = new PesananAdapter(arrayList);

        pesananAdapter.onBindCallBack = (jenis, viewHolder, position) -> {

            if ("btnBatalOnClick".equals(jenis)) {

                new AlertDialog.Builder(this.mContext)
                        .setTitle("Batalkan Pesanan")
                        .setMessage("Apakah anda yakin ingin membatalkan pesanan ini?")
                        .setPositiveButton("YA !", (dialog, which) -> {
                            loading = ProgressDialog.show(mContext, null, "Mengambil data ...", true, false);
                            mBaseApiService.deletePesananToko(viewHolder.pesanan_id)
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
                        }).setNegativeButton("Tidak", null).show();
            }
        };


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pesananAdapter);

    }

}
