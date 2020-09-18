package com.example.dolibarr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dolibarr.api.BaseApiService;
import com.example.dolibarr.api.UtilsApi;
import com.example.dolibarr.util.SharedPrefManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;

    ProgressDialog loading;
    Context context;
    BaseApiService baseApiService;
    SharedPrefManager sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        context = this;
        baseApiService = UtilsApi.getAPIService();
        sharedPref = new SharedPrefManager(this);

        if (Boolean.TRUE.equals(sharedPref.getSudahLogin())) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    @OnClick(R.id.btnSignIn)
    public void requestLogin() {
        loading = ProgressDialog.show(context, "Mengambil data", "Mohon tunggu...", true, false);
        Log.e("LOGIN", "Mulai login");
        baseApiService.postLogin(etUsername.getText().toString(), etPassword.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                Log.i("LOGIN", "JSONObject :" + jsonObject.toString());
                                if (jsonObject.getString("error").equals("false")) {

                                    Log.i("LOGIN", "Login SUCCESS");
                                    sharedPref.saveBoolean(SharedPrefManager.sudah_login, true);

                                    int id = jsonObject.getJSONObject("user").getInt("id");
                                    sharedPref.saveInt(SharedPrefManager.user_id, id);

                                    String username = jsonObject.getJSONObject("user").getString("username");
                                    sharedPref.saveString(SharedPrefManager.user_username, username);

                                    String nama_lengkap = jsonObject.getJSONObject("user").getString("nama_lengkap");
                                    sharedPref.saveString(SharedPrefManager.user_namalengkap, nama_lengkap);

                                    String email = jsonObject.getJSONObject("user").getString("email");
                                    sharedPref.saveString(SharedPrefManager.user_email, email);

                                    String level = jsonObject.getJSONObject("user").getString("level");
                                    sharedPref.saveString(SharedPrefManager.user_level, level);


                                    startActivity(new Intent(context, MainActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();

                                } else {
                                    String error_message = jsonObject.getString("error_msg");
                                    Toasty.error(context, error_message, Toasty.LENGTH_LONG).show();
                                    Log.i("LOGIN", "Login GAGAL : " + error_message);
                                }
                            } catch (IOException | JSONException e) {
                                Log.i("LOGIN", "Login GAGAL " + e.getMessage());
                            }
                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toasty.error(context, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });

    }

    @Override
    public void onBackPressed() {

    }

}
