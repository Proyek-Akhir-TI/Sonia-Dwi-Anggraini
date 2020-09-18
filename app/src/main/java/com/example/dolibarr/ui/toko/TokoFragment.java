package com.example.dolibarr.ui.toko;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokoFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SharedPrefManager sharedPrefManager;
    ProgressDialog loading;
    @BindView(R.id.toko_etFilter)
    EditText tokoEtFilter;
    @BindView(R.id.toko_btnHapusFilter)
    Button tokoBtnHapusFilter;
    @BindView(R.id.toko_btnCariFilter)
    Button tokoBtnCariFilter;
    @BindView(R.id.toko_recyclerview_list)
    RecyclerView tokoRecyclerviewList;
    @BindView(R.id.toko_swipeContainer)
    SwipeRefreshLayout tokoSwipeContainer;

    private TokoAdapter tokoAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_toko, container, false);
        ButterKnife.bind(this, root);

        FloatingActionButton floatingActionButton = ((MainActivity) requireActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.show();

            floatingActionButton.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putInt("toko_id", 0);

                TokoFormFragment fragment = new TokoFormFragment();
                fragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment, "TAG_PROFILE")
                        .addToBackStack(null)
                        .commit();

            });

        }


        swipe = root.findViewById(R.id.toko_swipeContainer);

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
        mBaseApiService.getTokoList(tokoEtFilter.getText().toString())
                .enqueue(new Callback<TokoModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<TokoModelList> call, @NotNull Response<TokoModelList> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            generateList(Objects.requireNonNull(response.body()).getArrayList());

                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<TokoModelList> call, Throwable t) {
                        Toast.makeText(mContext, "Presensi berhasil dikirimkan", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }

    private void generateList(ArrayList<TokoModel> arrayList) {

        recyclerView = requireView().findViewById(R.id.toko_recyclerview_list);
        tokoAdapter = new TokoAdapter(arrayList);

        tokoAdapter.onBindCallBack = (jenis, viewHolder, position) -> {

            if ("btHapusTokoOnClick".equals(jenis)) {

                new AlertDialog.Builder(this.mContext)
                        .setTitle("Nonaktifkan Status Toko")
                        .setMessage("Apakah anda yakin ingin Menonaktifkan status toko ini?\n **Anda harus menghubungi admin web jika ingin mengaktifkan kembali")
                        .setPositiveButton("YA !", (dialog, which) -> {
                            loading = ProgressDialog.show(mContext, null, "Mengambil data ...", true, false);
                            mBaseApiService.deleteToko(viewHolder.rowid)
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

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        };


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tokoAdapter);

    }

    @OnClick(R.id.toko_btnHapusFilter)
    public void onTokoBtnHapusFilterClicked() {
        tokoEtFilter.setText("");
        loadData();

    }

    @OnClick(R.id.toko_btnCariFilter)
    public void onTokoBtnCariFilterClicked() {
        loadData();
    }
}
