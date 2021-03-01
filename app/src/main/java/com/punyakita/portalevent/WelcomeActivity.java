package com.punyakita.portalevent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

public class WelcomeActivity extends AppCompatActivity {

    String token, email, password;

    Context mContext;
    BaseApiService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mContext = this;
        mApiService = ConfigApi.getAPIService();

        getLoginData();

        if (email == null && password == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(mContext, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);
        } else {
            requestLogin();
        }

    }

    private void requestLogin() {
        mApiService.loginRequest(email, password)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                if (jsonResult.has(ConfigApi.JSON_SUCCESS)) {
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
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                                requestLogin();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                                requestLogin();
                            }
                        } else {
                            Toast.makeText(mContext, getString(R.string.incorrectData), Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(mContext, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(mContext, getString(R.string.badConnection), Toast.LENGTH_SHORT).show();
                        requestLogin();
                    }
                });
    }

    private void getLoginData() {
        SharedPreferences sp = mContext.getSharedPreferences("loginData", Context.MODE_PRIVATE);
        email = sp.getString(ConfigApi.KEY_USERNAME, null);
        password = sp.getString(ConfigApi.KEY_PASSWORD, null);
    }
}
