package com.example.keeper;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class HomeFragment extends Fragment {

    public static final int ADD_BILL = 0;
    View view;
    RecyclerView billRecyclerView;
    FloatingActionButton fab;
    BottomNavigationView nav;
    List<Bill> billList;
    SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LitePal.initialize(getContext());
        db = Connector.getDatabase();

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

        billList = getBillListFromDatabase();
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
            addNewBill();
        });


        return view;
    }

    private void addNewBill() {
        Intent intent = new Intent(getContext(), AddBillActivity.class);
        startActivity(intent);
    }

    public List<Bill> getBillListFromDatabase(){
        List<Bill> billList = LitePal.order("time desc").find(Bill.class);
        return billList;
    }
}
