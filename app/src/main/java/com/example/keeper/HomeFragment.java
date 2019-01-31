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
import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {

    View view;
    RecyclerView billRecyclerView;
    FloatingActionButton fab;
    BottomNavigationView nav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        billRecyclerView = view.findViewById(R.id.bill_recyclerview);
        fab = view.findViewById(R.id.fab);
        nav = getActivity().findViewById(R.id.navigation);
        nav.setOnNavigationItemReselectedListener(i->{
            if(i.getItemId() == R.id.navigation_home) {
                billRecyclerView.scrollToPosition(0);
                fab.show();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        billRecyclerView.setLayoutManager(linearLayoutManager);

        List<Bill> billList = getBillList();
        BillAdapter adapter = new BillAdapter(billList);
        billRecyclerView.setAdapter(adapter);
        billRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
        fab.setOnClickListener(v->{
            billList.add(0,new Bill(Bill.PAYOUT,Calendar.getInstance().get(Calendar.SECOND),"哈哈"));
            adapter.notifyItemInserted(0);
            billRecyclerView.scrollToPosition(0);
        });


        return view;
    }

    public List<Bill> getBillList(){
        List<Bill> billList = new LinkedList<>();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        for(int i=0;i<300;i++) {
            if(i%3==0) {
                billList.add(new Bill(Bill.INCOME, (float) (5.232+i),"学习",year,month,day,hour,minute));
            }else {
                billList.add(new Bill(Bill.PAYOUT, (float) (19.00+i),"娱乐",year,month,day,hour,minute));
            }
        }
        return billList;
    }
}
