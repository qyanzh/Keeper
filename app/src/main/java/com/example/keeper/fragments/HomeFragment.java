package com.example.keeper.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.keeper.Bill;
import com.example.keeper.BillAdapter;
import com.example.keeper.activities.EditBillActivity;
import com.example.keeper.R;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jetbrains.annotations.TestOnly;
import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    public static final String TAG = "homefragment";
    public static final int REQUEST_ADD_BILL = 0;
    public static final int REQUEST_EDIT_BILL = 1;
    View view;
    public List<Bill> billList;
    public BillAdapter adapter;
    public RecyclerView billRecyclerView;
    public FloatingActionButton fab;
    Group emptyListImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_home, container, false);
        emptyListImage = container.findViewById(R.id.empty_list_image);
        billList = getBillListFromDatabase();
        initView();
        return view;
    }

    public void initView() {
        billRecyclerView = view.findViewById(R.id.home_recyclerview);
        billRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
        adapter = new BillAdapter(this, billList);
        billRecyclerView.setAdapter(adapter);
        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(adapter);
        billRecyclerView.addItemDecoration(headersDecoration);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        });

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> addNewBill());
        fab.setOnLongClickListener((View v) -> addBillListRandomly());

        billRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 ) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
        checkListEmpty();
    }

    public List<Bill> getBillListFromDatabase() {
        List<Bill> billList = LitePal
                .where("")
                .order("timeMills desc")
                .find(Bill.class);
        return billList;
    }

    @TestOnly
    public boolean addBillListRandomly() {
        emptyListImage.setVisibility(View.INVISIBLE);
        for(int i=0;i<10;++i) {
            Bill bill = new Bill();
            bill.setPrice(new Random().nextInt()%200);
            if(bill.getPrice()<0) bill.setType(Bill.PAYOUT);
            else bill.setType(Bill.INCOME);
            if(bill.isIncome()) {
                bill.setCategory(Bill.incomeCategory[Math.abs(new Random().nextInt()%Bill.incomeCategory.length)]);
            } else {
                bill.setCategory(Bill.payoutCategory[Math.abs(new Random().nextInt()%Bill.payoutCategory.length)]);
            }
            Calendar c = Calendar.getInstance();
            c.set(2018+i%2,new Random().nextInt()%12,new Random().nextInt()%27+1,new Random().nextInt()%24+1,new Random().nextInt()%60);
            bill.setTime(c.getTimeInMillis());
            bill.save();
            onBillAdded(bill.getId());
        }
        billRecyclerView.scrollToPosition(0);
        return true;
    }

    private void addNewBill() {
        Intent intent = new Intent(getContext(), EditBillActivity.class);
        intent.putExtra("action", "add");
        startActivityForResult(intent, REQUEST_ADD_BILL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_BILL:
                if (resultCode == RESULT_OK) {
                    refreshRecyclerView(data);
                }
                break;
            case REQUEST_EDIT_BILL:
                if (resultCode == RESULT_OK) {
                    refreshRecyclerView(data);
                }
                break;
            default:
                break;
        }
    }

    private void refreshRecyclerView(Intent intent) {
        int prePosition = intent.getIntExtra("prePosition", -1);
        long id = intent.getLongExtra("id", -1);
        switch (intent.getStringExtra("action")) {
            case "add":
                onBillAdded(id);
                emptyListImage.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "已保存", Toast.LENGTH_SHORT).show();
                break;
            case "delete":
                onBillDeleted(prePosition);
                checkListEmpty();
                Toast.makeText(getContext(),"已删除",Toast.LENGTH_SHORT).show();
                break;
            case "edit":
                onBillEdited(prePosition,id);
                Toast.makeText(getContext(), "已保存", Toast.LENGTH_SHORT).show();
                break;
        }
        fab.show();
    }

    private void onBillAdded(long id) {
        Bill bill = LitePal.find(Bill.class, id);
        int newPosition = Collections.binarySearch(billList, bill, new Bill.CompareBillByTime());
        if (newPosition < 0) newPosition = -newPosition - 1;
        billList.add(newPosition, bill);
        adapter.notifyItemInserted(newPosition);
        billRecyclerView.scrollToPosition(newPosition);
    }

    private void onBillDeleted(int prePosition) {
        billList.remove(prePosition);
        adapter.notifyItemRemoved(prePosition);
    }

    private void onBillEdited(int prePosition,long id) {
        billList.remove(prePosition);
        Bill bill = LitePal.find(Bill.class, id);
        int newPosition = Collections.binarySearch(billList, bill, new Bill.CompareBillByTime());
        if (newPosition < 0) newPosition = -newPosition - 1;
        billList.add(newPosition, bill);
        if(newPosition == prePosition) {
            adapter.notifyItemChanged(prePosition);
        } else {
            adapter.notifyItemRemoved(prePosition);
            adapter.notifyItemInserted(newPosition);
            billRecyclerView.scrollToPosition(newPosition);
        }
    }

    public void checkListEmpty() {
        if(billList.isEmpty()) {
            emptyListImage.setVisibility(View.VISIBLE);
            emptyListImage.requestLayout();
            fab.show();
        } else {
            emptyListImage.setVisibility(View.INVISIBLE);
            emptyListImage.requestLayout();
        }

    }
}
