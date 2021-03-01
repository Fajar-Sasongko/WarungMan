package com.punyakita.portalevent.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.punyakita.portalevent.Service.BaseApiService;
import com.punyakita.portalevent.Service.ConfigApi;
import com.punyakita.portalevent.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartListActivity extends AppCompatActivity {

    String token, confirm_id, purchase_id, note, total_price;
    Context mContext;
    BaseApiService mApiService;
    Button btnOrder, btnDelete;
    TextView tvTotalPrice;
    EditText etNote;
    ListView lvCartItem;

//    BottomNavigationView bnvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        token = getIntent().getStringExtra(ConfigApi.TAG_TOKEN);
        purchase_id = getIntent().getStringExtra(ConfigApi.TAG_PURCHASE_ID);

        mContext = CartListActivity.this;
        mApiService = ConfigApi.getAPIService();

        lvCartItem = (ListView) findViewById(R.id.lvCartItem);
//        btnDelete = (Button) findViewById(R.id.btnDelete);
        etNote = (EditText)findViewById(R.id.etNote);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);


        final LayoutInflater layoutInflater = getLayoutInflater();
        final View v = layoutInflater.inflate(R.layout.list_cart_item, null);
        btnDelete = v.findViewById(R.id.btnDelete);
        btnOrder = (Button) findViewById(R.id.btnOrder);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                deleteCartRequest();
            }
        });

        btnOrder.setEnabled(false);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(confirm_id) == 1) {
                    orderNow();
                } else if (Integer.parseInt(confirm_id) == 2) {
                    cancelOrderAlertDialog();
                } else if (Integer.parseInt(confirm_id) == 3) {

                } else if (Integer.parseInt(confirm_id) == 4) {

                } else if (Integer.parseInt(confirm_id) == 5) {

                }
            }
        });

        cartListRequest();

//        bnvMain = findViewById(R.id.bnvMain);

    }

    private void cartListRequest() {
        mApiService.cartListRequest("Bearer " + token, purchase_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
//                            pdLoading.dismiss();
                            try {
                                ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
                                ArrayList<HashMap<String, String>> listRating = new ArrayList<>();
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                if (jsonResult.has(ConfigApi.JSON_CONFIRM)) {
                                    JSONArray jsonConfirm = jsonResult.getJSONArray(ConfigApi.JSON_CONFIRM);
                                    JSONObject jConfirm = jsonConfirm.getJSONObject(0);
                                    confirm_id = jConfirm.getString(ConfigApi.TAG_CONFIRM_ID);

                                    if (Integer.parseInt(confirm_id) == 1) {
                                        btnOrder.setEnabled(true);
                                        btnOrder.setText("ORDER SEKARANG");
                                        Toast.makeText(mContext, "BELUM BAYAR", Toast.LENGTH_SHORT).show();
                                    } else if (Integer.parseInt(confirm_id) == 2) {
                                        btnOrder.setEnabled(true);
                                        btnOrder.setText("BATALKAN PESANAN");
                                        Toast.makeText(mContext, "PROSES", Toast.LENGTH_SHORT).show();
                                    } else if (Integer.parseInt(confirm_id) == 3) {
                                        btnOrder.setEnabled(true);
                                        btnOrder.setText("ORDER LAGI");
                                        Toast.makeText(mContext, "SELESAI", Toast.LENGTH_SHORT).show();
                                    } else if (Integer.parseInt(confirm_id) == 4) {
                                        btnOrder.setEnabled(true);
                                        Toast.makeText(mContext, "DIBATALKAN OLEH USER", Toast.LENGTH_SHORT).show();
                                    } else if (Integer.parseInt(confirm_id) == 5) {
                                        btnOrder.setEnabled(true);
                                        Toast.makeText(mContext, "DIBATALKAN OLEH RESTO", Toast.LENGTH_SHORT).show();
                                    }

                                    JSONObject jsonTotalPrice = jsonResult.getJSONObject(ConfigApi.JSON_TOTAL_PRICE);
                                    total_price = String.valueOf(jsonTotalPrice.get(ConfigApi.TAG_TOTAL_PRICE));
                                    tvTotalPrice.setText(total_price);

                                    JSONArray jsonItem = jsonResult.getJSONArray(ConfigApi.JSON_CART_LIST);
                                    for (int j = 0; j < jsonItem.length(); j++) {
                                        JSONObject jItem = jsonItem.getJSONObject(j);
                                        String name = jItem.getString(ConfigApi.TAG_NAME);
                                        String amount = jItem.getString(ConfigApi.TAG_AMOUNT);
                                        String selling_price = jItem.getString(ConfigApi.TAG_SELLING_PRICE);

                                        HashMap<String, String> item = new HashMap<>();
                                        item.put(ConfigApi.TAG_NAME, name);
                                        item.put(ConfigApi.TAG_AMOUNT, amount);
                                        item.put(ConfigApi.TAG_SELLING_PRICE, selling_price);
                                        listItem.add(item);
                                    }
                                } else if (jsonResult.has(ConfigApi.JSON_ERROR)) {
                                    Toast.makeText(mContext, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                }
                                ListAdapter adapter = new SimpleAdapter(
                                        mContext, listItem, R.layout.list_cart_item,
                                        new String[]{ConfigApi.TAG_NAME, ConfigApi.TAG_AMOUNT, ConfigApi.TAG_SELLING_PRICE},
                                        new int[]{R.id.tvName, R.id.tvAmount, R.id.tvSellingPrice});
                                lvCartItem.setAdapter(adapter);
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
                        Toast.makeText(mContext, getString(R.string.badConnection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void orderNow() {
        note = etNote.getText().toString();

        Intent i = new Intent(mContext, QrScanActivity.class);
        i.putExtra(ConfigApi.TAG_TOKEN, token);
        i.putExtra(ConfigApi.TAG_PURCHASE_ID, purchase_id);
        i.putExtra(ConfigApi.TAG_NOTE, note);
        i.putExtra(ConfigApi.TAG_TOTAL_PRICE, total_price);
        startActivity(i);
    }

    private void cancelOrderAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ANDA AKAN MEMBATALKAN ORDER?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "YA, BATALKAN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cancelOrder();
                    }
                });

        builder.setNegativeButton(
                "TETAP ORDER",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void cancelOrder() {
        mApiService.cancelOrderRequest("Bearer " + token, purchase_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
//                            pdLoading.dismiss();
                            try {
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                JSONObject jsonSuccess = jsonResult.getJSONObject(ConfigApi.JSON_STATUS);
                                if (!jsonSuccess.isNull(ConfigApi.TAG_SUCCESS)) {
                                    String status = (String) jsonSuccess.get(ConfigApi.TAG_SUCCESS);
                                    Toast.makeText(mContext, status, Toast.LENGTH_LONG).show();

                                    cartListRequest();
                                } else {
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
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(mContext, getString(R.string.badConnection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteCartRequest() {
        mApiService.deleteCartRequest("Bearer " + token, purchase_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
//                            pdLoading.dismiss();
                            try {
                                ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                if (jsonResult.has(ConfigApi.JSON_CART_LIST)) {
                                    JSONArray jsonItem = jsonResult.getJSONArray(ConfigApi.JSON_CART_LIST);

                                    Toast.makeText(mContext, "OK", Toast.LENGTH_SHORT).show();
                                } else if (jsonResult.has(ConfigApi.JSON_ERROR)) {
                                    Toast.makeText(mContext, "ERROR", Toast.LENGTH_SHORT).show();
                                }
                                ListAdapter adapter = new SimpleAdapter(
                                        mContext, listItem, R.layout.list_cart_item,
                                        new String[]{ConfigApi.TAG_NAME, ConfigApi.TAG_AMOUNT, ConfigApi.TAG_SELLING_PRICE},
                                        new int[]{R.id.tvName, R.id.tvAmount, R.id.tvSellingPrice});
                                lvCartItem.setAdapter(adapter);
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
                        Toast.makeText(mContext, getString(R.string.badConnection), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
