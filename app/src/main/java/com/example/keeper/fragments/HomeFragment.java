package com.example.keeper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.keeper.R;


public class HomeFragment extends BillListFragment {

    public static HomeFragment newInstance(String TAG) {
        HomeFragment fragment = new HomeFragment();
        fragment.TAG = TAG;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayoutCompat barDateSpinner = view.findViewById(R.id.bar_date_spinner);
        barDateSpinner.setVisibility(View.GONE);
        return view;
    }








}
