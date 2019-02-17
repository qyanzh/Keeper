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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    public static final int ADD_BILL = 0;
    public static final int REQUEST_ADD_BILL = 0;
    public static final int REQUEST_EDIT_BILL = 1;
    View view;
    RecyclerView billRecyclerView;
    FloatingActionButton fab;
    BottomNavigationView nav;
    List<Bill> billList;
    BillAdapter adapter;
    SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LitePal.initialize(getContext());
        db = Connector.getDatabase();

        view = inflater.inflate(R.layout.home_fragment, container, false);
        billRecyclerView = view.findViewById(R.id.bill_recyclerview);
        fab = getActivity().findViewById(R.id.fab);
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
        adapter = new BillAdapter(this,billList);
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
        Intent intent = new Intent(getContext(), EditBillActivity.class);
        intent.putExtra("action","add");
        startActivityForResult(intent, REQUEST_ADD_BILL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_ADD_BILL:
                if(resultCode == RESULT_OK) {
                    refreshRecyclerView(data.getLongExtra("id",-1),-1);
                }
                break;
            case REQUEST_EDIT_BILL:
                if (resultCode == RESULT_OK) {
                    refreshRecyclerView(data.getLongExtra("id",-1),data.getIntExtra("position",-1));
                }
                break;
            default:
                break;
        }
    }

    private void refreshRecyclerView(long id,int position) {
        if(position>=0) {
            billList.remove(position);
            adapter.notifyItemRemoved(position);
        }
        if(id>0) {
            Bill bill = LitePal.find(Bill.class,id);
            int index =Collections.binarySearch(billList,bill,new Bill.CompareBillInTime());
            if(index < 0) index = -index-1;
            billList.add(index, bill);
            adapter.notifyItemInserted(index);
            billRecyclerView.scrollToPosition(index);
        }
        fab.show();
    }

    public List<Bill> getBillListFromDatabase(){
        List<Bill> billList = LitePal.order("timeMills desc").find(Bill.class);
        return billList;
    }
}
