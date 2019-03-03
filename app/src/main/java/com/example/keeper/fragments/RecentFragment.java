package com.example.keeper.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.keeper.BillItem;
import com.example.keeper.R;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class RecentFragment extends BillListFragment {

    public static final String RECENT_FRAGMENT_TAG = "RecentFragment";

    public static RecentFragment newInstance(String TAG) {
        RecentFragment fragment = new RecentFragment();
        fragment.TAG = TAG;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        TextView dummy = view.findViewById(R.id.dummy);
        dummy.setVisibility(View.GONE);
        TextView barTextDate = view.findViewById(R.id.bar_text_date);
        barTextDate.setText("最近"+ PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_recent_amount","7")+"天");
        return view;
    }

    @Override
    List<BillItem> getList() {
        return LitePal.order("timeMills desc").where(getMergedWhereString()).find(BillItem.class);
    }

    String getMergedWhereString() {
        String limit = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_recent_amount", "7");
        int offset = Integer.parseInt(limit);
        Calendar c = Calendar.getInstance();
        c.set(HOUR,0);
        c.set(MINUTE,0);
        c.set(DAY_OF_MONTH,c.get(DAY_OF_MONTH)+1);
        String now = String.valueOf(c.getTimeInMillis());
        c.set(c.get(YEAR), c.get(MONTH), c.get(DAY_OF_MONTH) - offset, 0, 0);
        String before = String.valueOf(c.getTimeInMillis());
        return "timeMills >= " + before + " and timeMills < " + now;
    }

    @Override
    boolean isMatchCondition(long id) {
        String limit = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("pref_recent_amount", "7");
        int offset = Integer.parseInt(limit);
        Calendar c = Calendar.getInstance();
        c.set(HOUR,0);
        c.set(MINUTE,0);
        c.set(DAY_OF_MONTH,c.get(DAY_OF_MONTH)+1);
        long now = c.getTimeInMillis();
        c.set(c.get(YEAR), c.get(MONTH), c.get(DAY_OF_MONTH) - offset, 0, 0);
        long before = c.getTimeInMillis();
        BillItem billItem = LitePal.find(BillItem.class, id);
        return billItem.getTimeMills() >= before && billItem.getTimeMills() < now;
    }

}
