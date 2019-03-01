package com.example.keeper.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.keeper.BillItem;

import org.litepal.LitePal;

import java.util.List;


public class RecentFragment extends BillListFragment {

    public static RecentFragment newInstance(String TAG) {
        RecentFragment fragment = new RecentFragment();
        fragment.TAG = TAG;
        return fragment;
    }

    @Override
    List<BillItem> getInitList() {
        String limit = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_recent_amount","20");
        return LitePal.order("timeMills desc").limit(Integer.valueOf(limit)).find(BillItem.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }


}
