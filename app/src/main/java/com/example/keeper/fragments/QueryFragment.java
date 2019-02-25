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

import org.litepal.LitePal;

import java.util.List;

public class QueryFragment extends Fragment {
    public String TAG;
    public List<Bill> billList;
    public BillAdapter adapter;
    public RecyclerView billRecyclerView;
    String[] queryConditions;
    String[] queryArguments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_query, container, false);
        Bundle bundle = getArguments();
        queryConditions = bundle.getStringArray("queryConditions");
        queryArguments = bundle.getStringArray("queryArguments");
        billRecyclerView = view.findViewById(R.id.query_recyclerview);
        billRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        billList = LitePal.where(getMergedQueryString()).order("timeMills desc").find(Bill.class);
        adapter = new BillAdapter(this, billList);
        billRecyclerView.setAdapter(adapter);
//        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(adapter);
//        billRecyclerView.addItemDecoration(headersDecoration);
//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                headersDecoration.invalidateHeaders();
//            }
//        });
        return view;
    }

    String getMergedQueryString() {
        StringBuilder sb = new StringBuilder();
        int length = queryArguments.length;
        for (int i = 0; i < length; ++i) {
            sb.append(queryConditions[i]).append("=").append(queryArguments[i]);
            if (i != length - 1) {
                sb.append(" and ");
            } else {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public void reloadData() {
        billList.clear();
        billList.addAll(LitePal.where(getMergedQueryString()).order("timeMills desc").find(Bill.class));
        adapter.notifyDataSetChanged();
    }
}