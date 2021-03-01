package com.punyakita.portalevent.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.punyakita.portalevent.ItemAdapter;
import com.punyakita.portalevent.ItemModel;
import com.punyakita.portalevent.Service.BaseApiService;
import com.punyakita.portalevent.Service.ConfigApi;
import com.punyakita.portalevent.Activity.ItemDetailActivity;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    ProgressDialog progressDialog;
    String token, getToken;
    View view;
    Context mContext;
    BaseApiService mApiService;

    private RecyclerView rvItem;
    private ItemAdapter adapter;
    private ArrayList<ItemModel> itemArrayList;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        token = getArguments().getString(ConfigApi.TAG_TOKEN);

        mContext = getActivity();
        mApiService = ConfigApi.getAPIService();

        rvItem = (RecyclerView) view.findViewById(R.id.rvItem);
        adapter = new ItemAdapter(itemArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvItem.setLayoutManager(layoutManager);
        rvItem.setAdapter(adapter);

//        rvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Intent i = new Intent(mContext, ItemDetailActivity.class);
//                HashMap<String, String> map = (HashMap) adapterView.getItemAtPosition(position);
//                    i.putExtra(ConfigApi.TAG_TOKEN, token);
//                    i.putExtra(ConfigApi.TAG_ITEM_ID, map.get(ConfigApi.TAG_ITEM_ID).toString());
//                startActivity(i);
//            }
//        });

        final SwipeRefreshLayout sdRefresh = (SwipeRefreshLayout) view.findViewById(R.id.sdRefresh);
        sdRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemListRequest();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sdRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        loading();
        itemListRequest();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void itemListRequest() {
        mApiService.itemListRequest("Bearer " + token)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            progressDialog.dismiss();
                            try {
                                itemArrayList = new ArrayList<>();
                                JSONObject jsonResult = new JSONObject(response.body().string());
                                if (jsonResult.has(ConfigApi.JSON_ITEM)) {
                                    JSONArray jsonData = jsonResult.getJSONArray(ConfigApi.JSON_ITEM);
                                    for (int i = 0; i < jsonData.length(); i++) {
                                        JSONObject jData = jsonData.getJSONObject(i);
                                            String item_id = jData.getString(ConfigApi.TAG_ITEM_ID);
                                            String name = jData.getString(ConfigApi.TAG_NAME);
                                            String selling_price = jData.getString(ConfigApi.TAG_SELLING_PRICE);

                                            itemArrayList.add(new ItemModel(item_id, name, selling_price));
                                    }
                                } else if (jsonResult.has(ConfigApi.JSON_ERROR)) {
                                    Toast.makeText(mContext, getString(R.string.error), Toast.LENGTH_SHORT).show();
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
