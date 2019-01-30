package com.example.keeper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    View view;
    RecyclerView billRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        billRecyclerView = view.findViewById(R.id.bill_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        billRecyclerView.setLayoutManager(linearLayoutManager);

        BillAdapter adapter = new BillAdapter(getBillList());
        billRecyclerView.setAdapter(adapter);
        billRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                BottomNavigationView nav = getActivity().findViewById(R.id.navigation);
                FloatingActionButton fab = getActivity().findViewById(R.id.fab);
                if(dy>0) {
                    nav.setVisibility(View.INVISIBLE);
                    fab.hide();
                } else {
                    fab.show();
                    nav.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    public List<Bill> getBillList(){
        List<Bill> billList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        for(int i=0;i<20;i++) {
            if(i%3==0) {
                billList.add(new Bill(Bill.INCOME, (float) (5.232+i),"学习",year,month,day,hour,minute));
            }else {
                billList.add(new Bill(Bill.PAYOUT, (float) (19.00+i),"娱乐",year,month,day,hour,minute));
            }
        }
        return billList;
    }
}
