package com.punyakita.portalevent.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.punyakita.portalevent.Service.BaseApiService;
import com.punyakita.portalevent.Service.ConfigApi;
import com.punyakita.portalevent.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemDetailActivity extends AppCompatActivity {

    int mAmount = 1;

    ProgressDialog progressDialog;
    String token, seller_id, item_id, stock, amount;
    Context mContext;
    BaseApiService mApiService;
    ImageView ivItem;
    TextView tvName, tvStock, tvDescription, tvSellingPrice, tvAmount;
    Button btnDecrease, btnIncrease, btnTambah;
    ListView lvRating;

//    BottomNavigationView bnvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        token = getIntent().getStringExtra(ConfigApi.TAG_TOKEN);
        item_id = getIntent().getStringExtra(ConfigApi.TAG_ITEM_ID);

        mContext = ItemDetailActivity.this;
        mApiService = ConfigApi.getAPIService();

        lvRating = (ListView) findViewById(R.id.lvRating);

        ivItem = (ImageView) findViewById(R.id.ivItem);
        tvName = (TextView) findViewById(R.id.tvName);
        tvStock = (TextView) findViewById(R.id.tvStock);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvSellingPrice = (TextView) findViewById(R.id.tvSellingPrice);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        btnDecrease = (Button) findViewById(R.id.btnDecrease);
        btnIncrease = (Button) findViewById(R.id.btnIncrease);
        btnTambah = (Button) findViewById(R.id.btnTambah);

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAmount--;
                if (mAmount >= 1) {
                    tvAmount.setText(Integer.toString(mAmount));
                    btnIncrease.setEnabled(true);
                } else {
                    mAmount = 1;
                    tvAmount.setText(Integer.toString(mAmount));
                    btnDecrease.setEnabled(false);
                }
            }
        });

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAmount++;
                if (mAmount <= Integer.parseInt(stock)) {
                    tvAmount.setText(Integer.toString(mAmount));
                    btnDecrease.setEnabled(true);
                } else {
                    mAmount = Integer.parseInt(stock);
                    tvAmount.setText(Integer.toString(mAmount));
                    btnIncrease.setEnabled(false);
                }
                tvAmount.setText(Integer.toString(mAmount));
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = tvAmount.getText().toString();
                storeCartRequest();
            }
        });

        loading();
        itemDetailRequest();

//        bnvMain = findViewById(R.id.bnvMain);

    }

    private void itemDetailRequest() {
        mApiService.itemDetailRequest("Bearer " + token, item_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            try {
                                ArrayList<HashMap<String, String>> listRating = new ArrayList<>();
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                if (jsonResult.has(ConfigApi.JSON_USER_SELLER)) {
                                    JSONArray jsonData = jsonResult.getJSONArray(ConfigApi.JSON_USER_SELLER);
                                    JSONObject jData = jsonData.getJSONObject(0);

                                    item_id = jData.getString(ConfigApi.TAG_ITEM_ID);
                                    seller_id = jData.getString(ConfigApi.TAG_SELLER_ID);

                                    String gambarUrl = ConfigApi.URL_DATA_FILE + item_id;
                                    Glide.with(mContext).load(gambarUrl).into(ivItem);

                                    stock = jData.getString(ConfigApi.TAG_STOCK);

                                    tvName.setText(jData.getString(ConfigApi.TAG_NAME));
                                    tvStock.setText(stock);
                                    tvDescription.setText(jData.getString(ConfigApi.TAG_DESCRIPTION));
                                    tvSellingPrice.setText(jData.getString(ConfigApi.TAG_SELLING_PRICE));

                                    JSONArray jsonRating = jsonResult.getJSONArray(ConfigApi.JSON_RATING);
                                    for (int j = 0; j < jsonRating.length(); j++) {
                                        JSONObject jRating = jsonRating.getJSONObject(j);
                                        String rating = jRating.getString(ConfigApi.TAG_RATING);
                                        String review = jRating.getString(ConfigApi.TAG_REVIEW);
                                        String time = jRating.getString(ConfigApi.TAG_TIME);

                                        HashMap<String, String> ratingStr = new HashMap<>();
                                        ratingStr.put(ConfigApi.TAG_RATING, rating);
                                        ratingStr.put(ConfigApi.TAG_REVIEW, review);
                                        ratingStr.put(ConfigApi.TAG_TIME, time);
                                        listRating.add(ratingStr);
                                    }

                                } else if (jsonResult.has(ConfigApi.JSON_ERROR)) {
                                    Toast.makeText(mContext, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                }

                                ListAdapter adapter = new SimpleAdapter(
                                        mContext, listRating, R.layout.list_rating,
                                        new String[]{ConfigApi.TAG_RATING, ConfigApi.TAG_REVIEW, ConfigApi.TAG_TIME},
                                        new int[]{R.id.tvRating, R.id.tvReview, R.id.tvTime});
                                lvRating.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, getString(R.string.badConnection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeCartRequest() {
        mApiService.storeCartRequest("Bearer " + token, seller_id, item_id, amount)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                JSONObject jsonSuccess = jsonResult.getJSONObject(ConfigApi.JSON_STATUS);
                                if (!jsonSuccess.isNull(ConfigApi.TAG_SUCCESS)) {
                                    String status = (String) jsonSuccess.get(ConfigApi.TAG_SUCCESS);
                                    Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
                                } else {
                                    loading();
                                    itemDetailRequest();
                                    String status = (String) jsonSuccess.get(ConfigApi.TAG_ERROR);
                                    Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mContext, getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(mContext, getString(R.string.badConnection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loading() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

}
