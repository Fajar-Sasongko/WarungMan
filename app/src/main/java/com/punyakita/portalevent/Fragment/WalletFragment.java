package com.punyakita.portalevent.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.punyakita.portalevent.Service.ConfigApi;
import com.punyakita.portalevent.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class WalletFragment extends Fragment {

    TextView tvBalance;

    View view;

    public WalletFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wallet, container, false);

        tvBalance = (TextView) view.findViewById(R.id.tvBalance);
        tvBalance.setText("Rp" + getArguments().getString(ConfigApi.TAG_BALANCE));

        final SwipeRefreshLayout sdRefresh = (SwipeRefreshLayout) view.findViewById(R.id.sdRefresh);
        sdRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                function();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sdRefresh.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
