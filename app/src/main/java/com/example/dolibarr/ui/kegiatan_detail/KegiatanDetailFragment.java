package com.example.dolibarr.ui.kegiatan_detail;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dolibarr.MainActivity;
import com.example.dolibarr.R;
import com.example.dolibarr.api.BaseApiService;
import com.example.dolibarr.api.UtilsApi;
import com.example.dolibarr.ui.kegiatan.KegiatanFormFragment;
import com.example.dolibarr.util.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KegiatanDetailFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SharedPrefManager sharedPrefManager;
    ProgressDialog loading;

    private KegiatanDetailAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;
    private BaseApiService baseApiService;
    private String tanggal;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail_kegiatan, container, false);
        baseApiService = UtilsApi.getAPIService();

        Bundle arguments = getArguments();
        if (arguments == null)
            Toast.makeText(getActivity(), "Arguments is NULL", Toast.LENGTH_LONG).show();
        else {
            tanggal = getArguments().getString("tanggal", "");
        }

        FloatingActionButton floatingActionButton = ((MainActivity) requireActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.show();

            floatingActionButton.setOnClickListener(view -> {
                KegiatanFormFragment fragment = new KegiatanFormFragment();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment, "TAG_KEGIATAN")
                        .addToBackStack(null)
                        .commit();


            });
        }


        swipe = root.findViewById(R.id.kegiatan_detail_swipeContainer);

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
        mBaseApiService.getKegiatanDetailList(this.sharedPrefManager.getUserId(), this.tanggal)
                .enqueue(new Callback<KegiatanDetailModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<KegiatanDetailModelList> call, @NotNull Response<KegiatanDetailModelList> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            generateList(Objects.requireNonNull(response.body()).getArrayList());

                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<KegiatanDetailModelList> call, Throwable t) {
                        Toast.makeText(mContext, "Kegiatan berhasil diambil", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }

    private void generateList(ArrayList<KegiatanDetailModel> arrayList) {

        recyclerView = requireView().findViewById(R.id.kegiatan_detail_recyclerview_list);
        adapter = new KegiatanDetailAdapter(arrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
