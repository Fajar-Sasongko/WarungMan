package com.punyakita.portalevent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.punyakita.portalevent.Activity.MainActivity;
import com.punyakita.portalevent.Service.BaseApiService;
import com.punyakita.portalevent.Service.ConfigApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    ProgressDialog progressDialog;
    String token;
    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        mApiService = ConfigApi.getAPIService();

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        SharedPreferences sp = mContext.getSharedPreferences(ConfigApi.KEY_LOGIN_DATA, Context.MODE_PRIVATE);
        etEmail.setText(sp.getString(ConfigApi.KEY_USERNAME, null));
        etPassword.setText(sp.getString(ConfigApi.KEY_PASSWORD, null));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();
                requestLogin();
            }
        });
    }

    private void loading() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

    private void setLoginData() {
        SharedPreferences sp = mContext.getSharedPreferences("loginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed=sp.edit();
        ed.putString("username", etEmail.getText().toString());
        ed.putString("password", etPassword.getText().toString());
        ed.commit();
    }

    private void requestLogin() {
        mApiService.loginRequest(etEmail.getText().toString(), etPassword.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                if (jsonResult.has(ConfigApi.JSON_SUCCESS)) {
                                    setLoginData();

                                    JSONObject jsonSuccess = jsonResult.getJSONObject(ConfigApi.JSON_SUCCESS);
                                        token = (String) jsonSuccess.get(ConfigApi.TAG_TOKEN);

                                    JSONArray jsonData = jsonResult.getJSONArray(ConfigApi.JSON_DATA);
                                    JSONObject jData = jsonData.getJSONObject(0);
                                        String name = jData.getString(ConfigApi.TAG_NAME);
                                        String email = jData.getString(ConfigApi.TAG_EMAIL);
                                        String phone_number = jData.getString(ConfigApi.TAG_PHONE_NUMBER);
                                        String balance = jData.getString(ConfigApi.TAG_BALANCE);

                                    Intent i = new Intent(mContext, MainActivity.class);
                                        i.putExtra(ConfigApi.TAG_TOKEN, token);
                                        i.putExtra(ConfigApi.TAG_NAME, name);
                                        i.putExtra(ConfigApi.TAG_EMAIL, email);
                                        i.putExtra(ConfigApi.TAG_PHONE_NUMBER, phone_number);
                                        i.putExtra(ConfigApi.TAG_BALANCE, balance);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(mContext, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(mContext, getString(R.string.incorrectData), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, getString(R.string.badConnection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(mContext, getString(R.string.exitConfirm), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
