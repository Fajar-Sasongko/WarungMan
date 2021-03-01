package com.punyakita.portalevent.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.punyakita.portalevent.Service.BaseApiService;
import com.punyakita.portalevent.Activity.CartListActivity;
import com.punyakita.portalevent.Service.ConfigApi;
import com.punyakita.portalevent.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TransactionFragment extends Fragment {

    ProgressDialog progressDialog;
    String token;
    View view;
    Context mContext;
    BaseApiService mApiService;
    ListView lvTransaction;

    public TransactionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transaction, container, false);

        token = getArguments().getString(ConfigApi.TAG_TOKEN);

        mContext = getActivity();
        mApiService = ConfigApi.getAPIService();

        lvTransaction = (ListView) view.findViewById(R.id.lvTransaction);
        lvTransaction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(mContext, CartListActivity.class);
                HashMap<String, String> map = (HashMap) adapterView.getItemAtPosition(position);
                i.putExtra(ConfigApi.TAG_TOKEN, token);
                i.putExtra(ConfigApi.TAG_PURCHASE_ID, map.get(ConfigApi.TAG_PURCHASE_ID).toString());
                startActivity(i);
            }
        });


        final SwipeRefreshLayout sdRefresh = (SwipeRefreshLayout) view.findViewById(R.id.sdRefresh);
        sdRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                transactionListRequest();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sdRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        loading();
        transactionListRequest();

        return view;
    }

//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        //        final LayoutInflater layoutInflater = getLayoutInflater();
////        final View v = layoutInflater.inflate(R.layout.list_transaction, null);
////        lvItem = (ListView) v.findViewById(R.id.lvItem);
////        lvItem = (ListView) view.findViewById(R.id.lvItem);
//
//        LayoutInflater inflater = getLayoutInflater();
//        View row = inflater.inflate(R.layout.list_transaction, parent, false);
//        Button deleteImageView = row.findViewById(R.id.lvItem);
//        deleteImageView.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(mContext, "ASU", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return convertView;
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void transactionListRequest() {
        mApiService.transactionListRequest("Bearer " + token)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            try {
                                ArrayList<HashMap<String, String>> listTransaction = new ArrayList<>();
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                if (jsonResult.has(ConfigApi.JSON_CART_SELLER)) {
                                    JSONArray jsonData = jsonResult.getJSONArray(ConfigApi.JSON_CART_SELLER);
                                    for (int i = 0; i < jsonData.length(); i++) {
                                        JSONObject jData = jsonData.getJSONObject(i);
                                        String purchase_id = jData.getString(ConfigApi.TAG_PURCHASE_ID);
                                        String seller = jData.getString(ConfigApi.TAG_SELLER);

                                        HashMap<String, String> transaction = new HashMap<>();
                                        transaction.put(ConfigApi.TAG_PURCHASE_ID, purchase_id);
                                        transaction.put(ConfigApi.TAG_SELLER, seller);
                                        listTransaction.add(transaction);
                                    }
                                } else if (jsonResult.has(ConfigApi.JSON_ERROR)) {
                                    Toast.makeText(mContext, getString(R.string.error), Toast.LENGTH_SHORT).show();
                                }
                                ListAdapter adapter = new SimpleAdapter(
                                        mContext, listTransaction, R.layout.list_transaction,
                                        new String[]{ConfigApi.TAG_PURCHASE_ID, ConfigApi.TAG_SELLER},
                                        new int[]{R.id.tvPurchaseId, R.id.tvSeller});
                                lvTransaction.setAdapter(adapter);
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

    private void loading() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }

}
