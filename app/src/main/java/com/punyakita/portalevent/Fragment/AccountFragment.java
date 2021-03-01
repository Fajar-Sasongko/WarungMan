package com.punyakita.portalevent.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.punyakita.portalevent.Service.ConfigApi;
import com.punyakita.portalevent.LoginActivity;
import com.punyakita.portalevent.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class AccountFragment extends Fragment {

    TextView tvName, tvPhoneNumber;
    LinearLayout llLogout;
    View view;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        llLogout = (LinearLayout) view.findViewById(R.id.llLogout);
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLogout();
            }
        });

        tvName = (TextView) view.findViewById(R.id.tvName);
        tvPhoneNumber = (TextView) view.findViewById(R.id.tvPhoneNumber);

        tvName.setText(getArguments().getString(ConfigApi.TAG_NAME));
        tvPhoneNumber.setText(getArguments().getString(ConfigApi.TAG_PHONE_NUMBER));

        final SwipeRefreshLayout sdRefresh = (SwipeRefreshLayout) view.findViewById(R.id.sdRefresh);
        sdRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//               kurang function();
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

    private void setLogout() {
        SharedPreferences sp = getActivity().getSharedPreferences(ConfigApi.KEY_LOGIN_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed=sp.edit();
        ed.putString(ConfigApi.KEY_USERNAME, null);
        ed.putString(ConfigApi.KEY_PASSWORD, null);
        ed.commit();

        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);

        Toast.makeText(getActivity(), getString(R.string.logoutNotification), Toast.LENGTH_SHORT).show();

        getActivity().finish();
    }
}
