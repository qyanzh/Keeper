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

import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.*;


public class RecentFragment extends BillListFragment {

    public static RecentFragment newInstance(String TAG) {
        RecentFragment fragment = new RecentFragment();
        fragment.TAG = TAG;
        return fragment;
    }

    @Override
    List<BillItem> getList() {
        return LitePal.order("timeMills desc").where(getMergedWhereString()).find(BillItem.class);
    }

    @Override
    String getMergedWhereString() {
        String limit = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_recent_amount","7");
        int offset = Integer.parseInt(limit);
        Calendar c = Calendar.getInstance();
        String now = String.valueOf(c.getTimeInMillis());
        c.set(c.get(YEAR),c.get(MONTH),c.get(DAY_OF_MONTH)-offset,0,0);
        String before = String.valueOf(c.getTimeInMillis());
        return "timeMills >= " + before + " and timeMills <= " + now;
    }

    @Override
    boolean isMatchCondition(long id) {
        String limit = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_recent_amount","7");
        int offset = Integer.parseInt(limit);
        Calendar c = Calendar.getInstance();
        c.set(c.get(YEAR),c.get(MONTH),c.get(DAY_OF_MONTH)-offset,0,0);
        return LitePal.find(BillItem.class,id).getTimeMills() >= c.getTimeInMillis();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }


}
