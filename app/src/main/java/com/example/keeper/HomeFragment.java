package com.example.keeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.keeper.MainActivity.TAG;

public class HomeFragment extends Fragment {

    public static final int REQUEST_ADD_BILL = 0;
    public static final int REQUEST_EDIT_BILL = 1;
    View view;
    List<Bill> billList;
    BillAdapter adapter;
    RecyclerView billRecyclerView;
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_home, container, false);
        billList = getBillListFromDatabase();
        initView();
        return view;
    }

    public void initView() {
        billRecyclerView = view.findViewById(R.id.include);
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

        billRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    public List<Bill> getBillListFromDatabase() {
        List<Bill> billList = LitePal
                .where("")
                .order("timeMills desc")
                .find(Bill.class);
        return billList;
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
                Toast.makeText(getContext(), "已保存", Toast.LENGTH_SHORT).show();
                break;
            case "delete":
                onBillDeleted(prePosition);
                Toast.makeText(getContext(),"已删除",Toast.LENGTH_SHORT).show();
                break;
            case "edit":
                Toast.makeText(getContext(), "已保存", Toast.LENGTH_SHORT).show();
                onBillDeleted(prePosition);
                onBillAdded(id);
                break;
        }
        fab.show();
    }

    private void onBillAdded(long id) {
        Bill bill = LitePal.find(Bill.class, id);
        int index = Collections.binarySearch(billList, bill, new Bill.CompareBillByTime());
        if (index < 0) index = -index - 1;
        billList.add(index, bill);
        adapter.notifyItemInserted(index);
        billRecyclerView.scrollToPosition(index);
    }

    private void onBillDeleted(int prePosition) {
        billList.remove(prePosition);
        adapter.notifyItemRemoved(prePosition);
    }
}
