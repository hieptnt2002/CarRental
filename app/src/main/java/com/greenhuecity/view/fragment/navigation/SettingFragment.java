package com.greenhuecity.view.fragment.navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.greenhuecity.R;
import com.greenhuecity.data.contract.SettingContract;
import com.greenhuecity.data.model.Distributors;
import com.greenhuecity.data.presenter.SettingPresenter;
import com.greenhuecity.view.activity.LeaseActivity;
import com.greenhuecity.view.activity.LoginActivity;
import com.greenhuecity.view.activity.ManagerActivity;
import com.greenhuecity.view.activity.OrderManagementActivity;
import com.greenhuecity.view.activity.UploadCarsActivity;
import com.greenhuecity.view.activity.UserOrderActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends Fragment implements SettingContract.IView {
    CircleImageView civProfile;
    TextView tvNameProfile, tvManager, tvCarRental, tvOrder, tvLogout;
    View view;
    SettingPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        initGUI();
        mPresenter = new SettingPresenter(this, getActivity());
        mPresenter.getDataShared();
        mPresenter.getDistributors();
        if (mPresenter.isLogged()) {
            tvManager.setOnClickListener(view -> startActivity(new Intent(getActivity(), ManagerActivity.class)));
            tvOrder.setOnClickListener(view -> startActivity(new Intent(getActivity(), UserOrderActivity.class)));
            tvCarRental.setOnClickListener(view -> startActivity(new Intent(getActivity(), LeaseActivity.class)));
        } else
            tvNameProfile.setOnClickListener(view -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        eventLogout();
        return view;
    }


    private void initGUI() {
        civProfile = view.findViewById(R.id.profileCircleImageView);
        tvNameProfile = view.findViewById(R.id.usernameTextView);
        tvManager = view.findViewById(R.id.textView_manager);
        tvCarRental = view.findViewById(R.id.textView_motorbike_rental);
        tvOrder = view.findViewById(R.id.textView_order);
        tvLogout = view.findViewById(R.id.textView_logout);
    }

    @Override
    public void setDataSetting(String name, String url) {
        if (url != null) Glide.with(getActivity()).load(url).into(civProfile);
        if (name != null) tvNameProfile.setText(name);

    }

    @Override
    public void checkDistributors(List<Distributors> list) {
        try {
            for (Distributors distributors : list) {
                if (distributors.getUser_id() == mPresenter.getUsersId()) {
                    tvManager.setVisibility(View.VISIBLE);
                    return;
                } else tvManager.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {

        }

    }


    public void eventLogout() {
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SharedPreferences.Editor preferences = getActivity().getSharedPreferences("Success", Context.MODE_PRIVATE).edit();
                    preferences.remove("users");
                    startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }
}
