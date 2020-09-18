package com.example.dolibarr.ui.pengantaran;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengantaranFragment extends Fragment {

    private static final int REQUEST_CAMERA = 0;
    Context mContext;
    BaseApiService mBaseApiService;
    SharedPrefManager sharedPrefManager;
    ProgressDialog loading;
    private int toko_id;

    private PengantaranAdapter pengantaranAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;
    private BaseApiService baseApiService;

    Bitmap bitmap, decoded;
    int bitmap_size = 60; // range 1 - 100
    private String mCurrentPhotoPath;
    private int pesanan_id;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_pengantaran, container, false);
        baseApiService = UtilsApi.getAPIService();


        FloatingActionButton floatingActionButton = ((MainActivity) requireActivity()).getFloatingActionButton();
        floatingActionButton.setOnClickListener(view -> {
            floatingActionButton.hide();
        });

        swipe = root.findViewById(R.id.pengantaran_swipeContainer);

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
        mBaseApiService.getPengantaranList()
                .enqueue(new Callback<PengantaranModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<PengantaranModelList> call, @NotNull Response<PengantaranModelList> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            generateList(Objects.requireNonNull(response.body()).getArrayList());

                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<PengantaranModelList> call, Throwable t) {
                        Toast.makeText(mContext, "Data Pengantaran berhasil diambil", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });

    }


    /*foto*/

    private File createImageFile() throws IOException {
        // Create an image file name
        //sdcard/Android/data/com.example.dolibarr/files/Pictures/....
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.e("mCurrentPhotoPath", mCurrentPhotoPath);
        return image;
    }

    private void pickImage() {
        File f = null;
        try {
            f = createImageFile();

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, "com.example.dolibarr.fileprovider", f));
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

//    private void setToImageView(Bitmap bmp) {
//        //compress image
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
//        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
//        // imageView.setImageBitmap(decoded);
//        profileImgView.setImageBitmap(decoded);
//    }

    public Bitmap getResizedBitmap(@NotNull Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String getStringImage(@NotNull Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        // imageView.setImageBitmap(decoded);
        Log.e("Foto",getStringImage(decoded));
        mBaseApiService.postFotoPenyerahan(this.pesanan_id,getStringImage(decoded))
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

        return decoded;
        //profileImgView.setImageBitmap(decoded);
    }

    // Handle results of enable GPS Dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            try {
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                //setToImageView(getResizedBitmap(bitmap, 512));
                setToImageView(getResizedBitmap(bitmap, 512));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void generateList(ArrayList<PengantaranModel> arrayList) {

        recyclerView = requireView().findViewById(R.id.pengantaran_recyclerview_list);
        pengantaranAdapter = new PengantaranAdapter(arrayList);

        pengantaranAdapter.onBindCallBack = (jenis, viewHolder, position) -> {
            if ("btnFotoOnClick".equals(jenis)) {

                this.pesanan_id = viewHolder.pesanan_id;
                pickImage();
                // viewHolder.imgViewFoto.setImageBitmap(setToImageView(getResizedBitmap(bitmap, 512)));
                //viewHolder.imgViewFoto.setImageBitmap(decoded);
                //Log.e("Foto",getStringImage(decoded));
            } else if ("btnStatusOnClick".equals(jenis)) {

//                if (this.decoded != null) {
                    new AlertDialog.Builder(this.mContext)
                            .setTitle("Ubah status pemesanan")
                            .setMessage("Apakah anda yakin ingin pemesanan ini selesai ?")
                            .setPositiveButton("YA !", (dialog, which) -> {
                                loading = ProgressDialog.show(mContext, null, "mengupdate data ...", true, false);
                                mBaseApiService.postPesananSelesai(viewHolder.pesanan_id,sharedPrefManager.getUserId())
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
                            }).setNegativeButton("Batal", null).show();
//                } else {
//                    Toast.makeText(mContext, "Foto penyerahan belum dibuat!", Toast.LENGTH_SHORT).show();
////                    loading.dismiss();
//                }


            }
        };

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pengantaranAdapter);

    }

}
