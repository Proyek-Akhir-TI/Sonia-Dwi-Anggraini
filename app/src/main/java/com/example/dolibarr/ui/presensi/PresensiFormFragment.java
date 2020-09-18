package com.example.dolibarr.ui.presensi;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.dolibarr.R;
import com.example.dolibarr.api.BaseApiService;
import com.example.dolibarr.api.UtilsApi;
import com.example.dolibarr.util.SharedPrefManager;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

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
import java.util.Locale;
import java.util.Objects;

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

public class PresensiFormFragment extends Fragment {

    private static final String TAG = "PresensiFragment";
    public final int REQUEST_CAMERA = 0;

    @BindView(R.id.latitude)
    TextView latitude;
    @BindView(R.id.verticalLine)
    View verticalLine;
    @BindView(R.id.category_line_layout)
    RelativeLayout categoryLineLayout;
    @BindView(R.id.longitude)
    TextView longitude;
    @BindView(R.id.list_cardview)
    CardView listCardview;

    @BindView(R.id.presensi_imageView)
    ImageView presensiImageView;
    @BindView(R.id.presensi_btnAmbilFoto)
    Button btnAmbilFoto;

    Bitmap bitmap, decoded;
    int bitmap_size = 60; // range 1 - 100
    BaseApiService mBaseApiService;
    ProgressDialog loading;
    Context mContext;
    SharedPrefManager sharedPref;
    String mCurrentPhotoPath;
    @BindView(R.id.presensi_btnKirim)
    Button presensiBtnKirim;
    @BindView(R.id.presensi_tvJenis)
    TextView presensiTvJenis;
    @BindView(R.id.presensi_etKeterangan)
    EditText presensiEtKeterangan;
    private FusedLocationProviderClient sFusedLocationClient;
    private LocationCallback sLocationCallback;
    private LocationRequest sLocationRequest;
    private GoogleApiClient sGoogleApiClient;
    private boolean apiConnectionStatus = false;
    private int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    private double Latitude = 0.0, Longitude = 0.0;
    String status_presensi;


    private File createImageFile() throws IOException {
        // Create an image file name
        //sdcard/Android/data/com.example.dolibarr/files/Pictures/....
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
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
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, "com.example.dolibarr.fileprovider", Objects.requireNonNull(f)));
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(takePictureIntent, REQUEST_CAMERA);
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        // imageView.setImageBitmap(decoded);
        presensiImageView.setImageBitmap(decoded);

        //kirimPresensi();
    }

    private String getStringImage(@NotNull Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_presensi, container, false);
        ButterKnife.bind(this, root);

        mBaseApiService = UtilsApi.getAPIService();
        sharedPref = new SharedPrefManager(mContext);

        loadStatusPresensi();

        //TODO: hilangkan permanen tombol ambil foto
        btnAmbilFoto.setVisibility(View.GONE);
        return root;
    }

    private void loadStatusPresensi() {

        loading = ProgressDialog.show(mContext, "Mengambil status presensi", "Mohon tunggu...", true, false);

        mBaseApiService.getStatusPresensi(sharedPref.getUserId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());

                                if (jsonObject.getString("error").equals("false")) {

                                    String status = jsonObject.getJSONObject("presensi").getString("status");
                                    if(status.equals("MASUK")){
                                        presensiTvJenis.setText("PRESENSI MASUK");
                                        status_presensi = "MASUK";
                                    }else if(status.equals("PULANG")){
                                        presensiTvJenis.setText("PRESENSI PULANG");
                                        status_presensi = "PULANG";
                                    }else{
                                        presensiTvJenis.setText("PRESENSI SELESAI");
                                        status_presensi = "SELESAI";
                                        presensiBtnKirim.setEnabled(false);
                                    }

                                } else {
                                    String error_message = jsonObject.getString("error_msg");
                                    Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();
                                }
                            } catch (IOException | JSONException e) {
                                Log.e("debug", "onFailure: ERROR > " + e.getMessage());
                            }
                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });


    }

    @OnClick({R.id.presensi_btnAmbilFoto})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.presensi_btnAmbilFoto) {
            if (apiConnectionStatus) {
                locationSettingsRequest();
            }
            pickImage();
        } else {
            throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }


    /**
     *
     */
    private void kirimPresensi() {

        loading = ProgressDialog.show(mContext, null, "Mengirim data ...", true, false);
        mBaseApiService.postPresensi(
                sharedPref.getUserId(),
                getStringImage(decoded),
                longitude.getText().toString(),
                latitude.getText().toString(),
                presensiEtKeterangan.getText().toString(),
                status_presensi
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("error").equals("false")) {
                            Toast.makeText(mContext, "Presensi berhasil dikirimkan", Toast.LENGTH_SHORT).show();

                            PresensiFragment fragment = new PresensiFragment();
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


                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

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

    /**
     * Function to start FusedLocation updates
     */
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

                            latitude.setText(getString(R.string.loading));
                            longitude.setText(getString(R.string.loading));
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

//    public void requestLocationUpdate() {
//        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            latitude.setText(getString(R.string.loading));
//            longitude.setText(getString(R.string.loading));
//            sFusedLocationClient.requestLocationUpdates(sLocationRequest, sLocationCallback, Looper.myLooper());
//        }
//    }

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

    /**
     * Build GoogleApiClient and connect
     */
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
                            @Override
                            public void onLocationResult(final LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                Latitude = locationResult.getLastLocation().getLatitude();
                                Longitude = locationResult.getLastLocation().getLongitude();

                                if (Latitude == 0.0 && Longitude == 0.0) {
                                    requestLocationUpdate();
                                } else {
                                    // Update Textview
                                    latitude.setText(Double.toString(Latitude));
                                    longitude.setText(Double.toString(Longitude));
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

    /**
     * Function to request Location permission and enable GPS Dialog
     */
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


    @OnClick(R.id.presensi_imageView)
    public void onImageViewClicked() {
        if (apiConnectionStatus) {
            locationSettingsRequest();
        }
        pickImage();

    }

    @OnClick(R.id.presensi_btnKirim)
    public void onBtnKirimClicked() {

        if (this.decoded != null) {
            new AlertDialog.Builder(this.mContext)
                    .setTitle("Kirim presensi")
                    .setMessage("Apakah anda yakin ingin mengirimkan data ini?")
                    .setPositiveButton("Kirim", (dialog, which) -> {

                        kirimPresensi();

                    }).setNegativeButton("Batal", null).show();

        } else {
            Toasty.error(getActivity(), "Foto masih belum dibuat", Toast.LENGTH_LONG).show();
        }
    }
}
