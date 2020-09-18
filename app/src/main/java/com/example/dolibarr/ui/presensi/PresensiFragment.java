package com.example.dolibarr.ui.presensi;

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
import com.example.dolibarr.util.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PresensiFragment extends Fragment {

    Context mContext;
    SharedPrefManager sharedPrefManager;
    ProgressDialog loading;

    private PresensiAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;
    private BaseApiService mBaseApiService;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_presensi, container, false);
        mBaseApiService = UtilsApi.getAPIService();

        FloatingActionButton floatingActionButton = ((MainActivity) requireActivity()).getFloatingActionButton();

        if (floatingActionButton != null) {
            floatingActionButton.show();

            floatingActionButton.setOnClickListener(view -> {
                PresensiFormFragment fragment = new PresensiFormFragment();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment, "TAG_KEGIATAN")
                        .addToBackStack(null)
                        .commit();


            });
        }

        swipe = root.findViewById(R.id.presensi_swipeContainer);
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
        mBaseApiService.getPresensiList(this.sharedPrefManager.getUserId())
                .enqueue(new Callback<PresensiModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<PresensiModelList> call, @NotNull Response<PresensiModelList> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            generateList(Objects.requireNonNull(response.body()).getArrayList());

                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<PresensiModelList> call, Throwable t) {
                        Toasty.error(mContext, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }

    private void generateList(ArrayList<PresensiModel> arrayList) {

        recyclerView = requireView().findViewById(R.id.presensi_recyclerview_list);
        adapter = new PresensiAdapter(arrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
