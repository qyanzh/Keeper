package com.example.keeper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.keeper.Bill;
import com.example.keeper.BillAdapter;
import com.example.keeper.R;

import java.util.List;

public class StatusFragment extends Fragment {
    public static final String TAG = "statusfragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.status_recyclerview);
        List<Bill> billList = HomeFragment.getBillListFromDatabase();
        BillAdapter adapter = new BillAdapter(this,billList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        return view;
    }
}
