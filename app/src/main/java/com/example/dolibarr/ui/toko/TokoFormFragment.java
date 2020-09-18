package com.example.dolibarr.ui.toko;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.dolibarr.MainActivity;
import com.example.dolibarr.R;
import com.example.dolibarr.api.BaseApiService;
import com.example.dolibarr.api.UtilsApi;
import com.example.dolibarr.ui.presensi.PresensiFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class TokoFormFragment extends Fragment {

    private static final String TAG = "TAG_FORM_TOKO";
    public final int REQUEST_CAMERA = 0;
    @BindView(R.id.profile_imgView)
    ImageView profileImgView;
    @BindView(R.id.profile_btnPilihGambar)
    Button profileBtnPilihGambar;

    @BindView(R.id.profileToko_tvNama)
    TextView profileTokoTvNama;
    @BindView(R.id.profileToko_tvAlamat)
    TextView profileTokoTvAlamat;
    @BindView(R.id.profileToko_tvKota)
    TextView profileTokoTvKota;
    @BindView(R.id.profileToko_tvLongitude)
    TextView tvLongitude;
    @BindView(R.id.profileToko_tvLatitude)
    TextView tvLatitude;

    @BindView(R.id.profile_btnSimpan)
    Button profileBtnSimpan;

    ProgressDialog loading;
    int bitmap_size = 60; // range 1 - 100
    Bitmap bitmap, decoded;

    @BindView(R.id.profile_btnUpdateLokasi)
    Button profileBtnUpdateLokasi;
    @BindView(R.id.profile_btnKembali)
    Button profileBtnKembali;

    private Context mContext;
    private int toko_id;
    private BaseApiService mBaseApiService;
    private String mCurrentPhotoPath;
    private FusedLocationProviderClient sFusedLocationClient;
    private LocationCallback sLocationCallback;
    private LocationRequest sLocationRequest;
    private GoogleApiClient sGoogleApiClient;
    private boolean apiConnectionStatus = false;
    private int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private double Latitude = 0.0, Longitude = 0.0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_form_toko, container, false);
        ButterKnife.bind(this, root);

        mBaseApiService = UtilsApi.getAPIService();

        Bundle arguments = getArguments();
        if (arguments == null)
            Toast.makeText(getActivity(), "Arguments is NULL", Toast.LENGTH_LONG).show();
        else {
            toko_id = getArguments().getInt("toko_id", 0);
        }

        if (toko_id > 0) {
            loadData();
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

        return root;
    }

    private void loadData() {
        loading = ProgressDialog.show(mContext, "Mengambil data", "Mohon tunggu...", true, false);

        Log.e("TAG_PROFILE", "Ambil Data");
        mBaseApiService.getTokoProfile(this.toko_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                Log.i("TAG_PROFILE", "JSONObject :" + jsonObject.toString());
                                if (jsonObject.getString("error").equals("false")) {

                                    Log.i("TAG_PROFILE", "Data berhasil diambil");

                                    String nama = jsonObject.getJSONObject("toko").getString("nama");
                                    profileTokoTvNama.setText(nama.toString());

                                    String alamat = jsonObject.getJSONObject("toko").getString("alamat");
                                    profileTokoTvAlamat.setText(alamat.toString());

                                    String kota = jsonObject.getJSONObject("toko").getString("kota");
                                    profileTokoTvKota.setText(kota.toString());

                                    String longitude = jsonObject.getJSONObject("toko").getString("longitude");
                                    tvLongitude.setText(longitude.toString());

                                    String latitude = jsonObject.getJSONObject("toko").getString("latitude");
                                    tvLatitude.setText(latitude.toString());

                                    String foto = jsonObject.getJSONObject("toko").getString("foto");

                                    Picasso.get().invalidate(UtilsApi.BASE_URL + foto);
                                    Picasso.get().load(UtilsApi.BASE_URL + foto).into(profileImgView);


                                } else {
                                    String error_message = jsonObject.getString("error_msg");
                                    Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();
                                    Log.i("TAG_PROFILE", "Data gagal diambil : " + error_message);
                                }
                            } catch (IOException | JSONException e) {
                                Log.i("TAG_PROFILE", "Data gagal diambil " + e.getMessage());
                            }
                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("TAG_PROFILE", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });
    }


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
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, "com.example.dolibarr.fileprovider", f));
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        // imageView.setImageBitmap(decoded);
        profileImgView.setImageBitmap(decoded);
    }

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

    // Handle results of enable GPS Dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 214) {
            switch (resultCode) {
                case RESULT_OK: {
                    // User enabled GPS start fusedlocation
                    requestLocationUpdate();
                    break;
                }
                case RESULT_CANCELED: {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(mContext, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                    break;
                }
                default: {
                    break;
                }
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    //Uri uri = data.getData();
                    //Log.e("CAMERA", Objects.requireNonNull(uri.getPath()));

//                    bitmap = rotateImage(BitmapFactory.decodeFile(mCurrentPhotoPath), 90);
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    setToImageView(getResizedBitmap(bitmap, 512));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getStringImage(@NotNull Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void saveProfile() {
        loading = ProgressDialog.show(mContext, null, "Menyimpan data ...", true, false);

        if (this.decoded != null) {

            mBaseApiService.postTokoProfile(
                    this.toko_id,
                    profileTokoTvNama.getText().toString(),
                    profileTokoTvAlamat.getText().toString(),
                    profileTokoTvKota.getText().toString(),
                    getStringImage(decoded),
                    tvLatitude.getText().toString(),
                    tvLongitude.getText().toString()
            ).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        loading.dismiss();
                        try {
                            assert response.body() != null;
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getString("error").equals("false")) {
                                Toast.makeText(mContext, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();

                                TokoFragment fragment = new TokoFragment();
                                AppCompatActivity activity = (AppCompatActivity) getView().getContext();
                                activity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.nav_host_fragment, fragment, "TAG_PRESENSI_FORM")
                                        .addToBackStack(null)
                                        .commit();

                            } else {
                                String error_message = jsonObject.getString("error_msg");
                                Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        loading.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                    loading.dismiss();
                }
            });

        } else {

            mBaseApiService.postTokoProfileTanpaFoto(
                    this.toko_id,
                    profileTokoTvNama.getText().toString(),
                    profileTokoTvAlamat.getText().toString(),
                    profileTokoTvKota.getText().toString(),
                    tvLatitude.getText().toString(),
                    tvLongitude.getText().toString()
            ).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        loading.dismiss();
                        try {
                            assert response.body() != null;
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getString("error").equals("false")) {
                                Toast.makeText(mContext, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                            } else {
                                String error_message = jsonObject.getString("error_msg");
                                Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        loading.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                    loading.dismiss();
                }
            });

        }


    }


    /**
     * Function to connect googleapiclient
     */
    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(mContext);
        if (resultCode == ConnectionResult.SUCCESS) {
            sGoogleApiClient.connect();
        } else {
            int REQUEST_GOOGLE_PLAY_SERVICE = 988;
            googleAPI.getErrorDialog(getActivity(), resultCode, REQUEST_GOOGLE_PLAY_SERVICE);
        }
    }

    public void requestLocationUpdate() {

        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(mContext, "LOCATION ACCESS GRANTED", Toast.LENGTH_SHORT).show();

                            tvLatitude.setText(getString(R.string.loading));
                            tvLongitude.setText(getString(R.string.loading));
                            sFusedLocationClient.requestLocationUpdates(sLocationRequest, sLocationCallback, Looper.myLooper());
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(mContext, String.format("Some Error! %s", error.toString()), Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();


    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private synchronized void buildGoogleApiClient() {
        sFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        sGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        // Creating a location request
                        sLocationRequest = new LocationRequest();
                        sLocationRequest.setPriority(priority);
                        sLocationRequest.setSmallestDisplacement(0);
                        sLocationRequest.setNumUpdates(1);

                        // FusedLocation callback
                        sLocationCallback = new LocationCallback() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onLocationResult(final LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                Latitude = locationResult.getLastLocation().getLatitude();
                                Longitude = locationResult.getLastLocation().getLongitude();

                                if (Latitude == 0.0 && Longitude == 0.0) {
                                    requestLocationUpdate();
                                } else {
                                    // Update Textview
                                    tvLatitude.setText(Double.toString(Latitude));
                                    tvLongitude.setText(Double.toString(Longitude));
                                }
                            }
                        };

                        // Simple api status check
                        apiConnectionStatus = true;
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        connectGoogleClient();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();

        // Connect googleapiclient after build
        connectGoogleClient();
    }

    private void locationSettingsRequest() {
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(mContext);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(sLocationRequest);
        builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    // Start FusedLocation if GPS is enabled
                    requestLocationUpdate();
                })
                .addOnFailureListener(e -> {
                    // Show enable GPS Dialog and handle dialog buttons
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                int REQUEST_CHECK_SETTINGS = 214;
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                showLog("Unable to Execute Request");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            showLog("Location Settings are Inadequate, and Cannot be fixed here. Fix in Settings");
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        showLog("Canceled No Thanks");
                    }
                });
    }

    private void showLog(String message) {
        Log.e(TAG, "" + message);
    }

    @Override
    public void onResume() {
        super.onResume();
        buildGoogleApiClient();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sFusedLocationClient.removeLocationUpdates(sLocationCallback);
    }

    @OnClick(R.id.profile_btnUpdateLokasi)
    public void onProfileBtnUpdateLokasiClicked() {
        new AlertDialog.Builder(this.mContext)
                .setTitle("Update Lokasi")
                .setMessage("Apakah anda yakin ingin update lokasi toko ini?")
                .setPositiveButton("Update", (dialog, which) -> {

                    if (apiConnectionStatus) {
                        locationSettingsRequest();
                    }
                }).setNegativeButton("Batal", null).show();

    }

    @OnClick(R.id.profile_btnKembali)
    public void onProfileBtnKembaliClicked() {

        TokoFragment fragment = new TokoFragment();
        AppCompatActivity activity = (AppCompatActivity) getView().getContext();

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment, "TAG_FORM_TOKO")
                .addToBackStack(null)
                .commit();

    }

    @OnClick(R.id.profile_btnSimpan)
    public void onProfileBtnSimpanClicked() {

        //if(this.decoded != null){
        new AlertDialog.Builder(this.mContext)
                .setTitle("Kirim Data")
                .setMessage("Apakah anda yakin ingin mengirimkan data ini?")
                .setPositiveButton("Kirim", (dialog, which) -> {

                    saveProfile();

                }).setNegativeButton("Batal", null).show();

//                }else{
//                    Toasty.error(requireActivity(), "Foto masih belum dibuat", Toast.LENGTH_LONG).show();
//                }

    }

    @OnClick(R.id.profile_btnPilihGambar)
    public void onBtnPilihGambarOnClick() {
        pickImage();
    }
}
