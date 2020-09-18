package com.example.dolibarr.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

/*
* cara pake::
* 1. set mContext;
* 2. set mActivity
* 3. if (apiConnectionStatus) { locationSettingsRequest(); }
*
* */


public class LocationUtil {

    Context mContext;
    Activity mActivity;

    private GoogleApiClient sGoogleApiClient;
    private FusedLocationProviderClient sFusedLocationClient;
    private LocationCallback sLocationCallback;
    private LocationRequest sLocationRequest;
    private boolean apiConnectionStatus = false;
    private int priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private double Latitude = 0.0, Longitude = 0.0;

    public boolean isApiConnectionStatus() {
        return apiConnectionStatus;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
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
            googleAPI.getErrorDialog(mActivity, resultCode, REQUEST_GOOGLE_PLAY_SERVICE);
        }
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(){
        sFusedLocationClient.requestLocationUpdates(sLocationRequest, sLocationCallback, Looper.myLooper());
    }

//    public void requestLocationUpdate() {
//
//        Dexter.withActivity(mActivity)
//                .withPermissions(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION)
//                .withListener(new MultiplePermissionsListener() {
//                    @SuppressLint("MissingPermission")
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        if (report.areAllPermissionsGranted()) {
//                            //Toast.makeText(mContext, "LOCATION ACCESS GRANTED", Toast.LENGTH_SHORT).show();
//                            //latitude.setText(getString(R.string.loading));
//                            //longitude.setText(getString(R.string.loading));
//
//                            sFusedLocationClient.requestLocationUpdates(sLocationRequest, sLocationCallback, Looper.myLooper());
//                        }
//
//                        if (report.isAnyPermissionPermanentlyDenied()) {
//                            showSettingsDialog();
//                        }
//                    }
//
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).
//                withErrorListener(error -> Toast.makeText(mContext, String.format("Some Error! %s", error.toString()), Toast.LENGTH_SHORT).show())
//                .onSameThread()
//                .check();
//    }

//    private void showSettingsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("Need Permissions");
//        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
//        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
//            dialog.cancel();
//            openSettings();
//        });
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//        builder.show();
//    }

//    private void openSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
//        intent.setData(uri);
//        startActivityForResult(intent, 101);
//    }

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
                                    requestLocationUpdates();
                                } else {
                                    // Update Textview
                                    //latitude.setText(Double.toString(Latitude));
                                    //longitude.setText(Double.toString(Longitude));

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
                    requestLocationUpdates();
                })
                .addOnFailureListener(e -> {
                    // Show enable GPS Dialog and handle dialog buttons
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                int REQUEST_CHECK_SETTINGS = 214;
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e("Location","Unable to Execute Request");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e("Location","Location Settings are Inadequate, and Cannot be fixed here. Fix in Settings");
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.e("Location","Canceled No Thanks");
                    }
                });
    }

    public void removeLocationUpdate(){
        sFusedLocationClient.removeLocationUpdates(sLocationCallback);
    }

}
