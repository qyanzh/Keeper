package com.example.keeper.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.keeper.BillAdapter;
import com.example.keeper.BillItem;
import com.example.keeper.R;
import com.example.keeper.activities.EditActivity;
import com.example.keeper.activities.MainActivity;
import com.example.keeper.mytools.MyBillTools;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jetbrains.annotations.TestOnly;
import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class BillListFragment extends Fragment {

    public View view;
    public String TAG;
    public List<BillItem> billItemList;
    public BillAdapter adapter;
    public RecyclerView billRecyclerView;
    public MainActivity mActivity;
    Group emptyListImage;

    public static final int REQUEST_ADD_BILL = 0;
    public static final int REQUEST_EDIT_BILL = 1;
    String[] queryConditions;
    String[] queryArguments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        Bundle bundle = getArguments();
        if (bundle != null) {
            queryConditions = bundle.getStringArray("queryConditions");
            queryArguments = bundle.getStringArray("queryArguments");
            TAG = bundle.getString("TAG");
        }
        billItemList = LitePal.where(getMergedQueryString()).order("timeMills desc").find(BillItem.class);
        adapter = new BillAdapter(this, billItemList);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_query, container, false);
        }
        emptyListImage = view.findViewById(R.id.empty_list_image);
        billRecyclerView = view.findViewById(R.id.bill_recyclerview);
        billRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        billRecyclerView.setAdapter(adapter);
        billRecyclerView.setHasFixedSize(true);
        final StickyRecyclerHeadersDecoration headersDecoration = new StickyRecyclerHeadersDecoration(adapter);
        billRecyclerView.addItemDecoration(headersDecoration);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecoration.invalidateHeaders();
            }
        });
        return view;
    }

    String getMergedQueryString() {
        StringBuilder sb = new StringBuilder();
        if (queryArguments != null) {
            int length = queryArguments.length;
            for (int i = 0; i < length; ++i) {
                sb.append(queryConditions[i]).append("=").append(queryArguments[i]);
                if (i != length - 1) {
                    sb.append(" and ");
                } else {
                    sb.append(".");
                }
            }
        }
        Log.d(TAG, "getMergedQueryString : " + sb.toString());
        return sb.toString();
    }


    private boolean isMatchCondition(long id) {
        if (queryArguments == null) {
            return true;
        }
        int i = 0, length = queryArguments.length;
        BillItem billItem = LitePal.find(BillItem.class, id);
        outer:
        for (; i < length; ++i) {
            switch (queryConditions[i]) {
                case "year":
                    if (!String.valueOf(billItem.getYear()).equals(queryArguments[i])) {
                        break outer;
                    }
                    break;
                case "month":
                    if (!String.valueOf(billItem.getMonth()).equals(queryArguments[i])) {
                        break outer;
                    }
                    break;
                case "day":
                    if (!String.valueOf(billItem.getDay()).equals(queryArguments[i])) {
                        break outer;
                    }
                    break;
                default:
            }
        }
        return i == length;
    }

    void onBillItemAdded(long id) {
        Log.d(TAG, "onBillItemAdded: ");
        BillItem billItem = LitePal.find(BillItem.class, id);
        if (isMatchCondition(id)) {
            int newPosition = Collections.binarySearch(billItemList, billItem, new MyBillTools.CompareBillByTime());
            if (newPosition < 0) newPosition = -newPosition - 1;
            billItemList.add(newPosition, billItem);
            adapter.notifyItemInserted(newPosition);
            billRecyclerView.scrollToPosition(newPosition);
        }
    }

    void onBillDeleted(int prePosition) {
        billItemList.remove(prePosition);
        adapter.notifyItemRemoved(prePosition);
    }

    void onBillEdited(int prePosition, long id) {
        BillItem billItem = LitePal.find(BillItem.class, id);
        if (isMatchCondition(id)) {
            int newPosition = Collections.binarySearch(billItemList, billItem, new MyBillTools.CompareBillByTime());
            if (newPosition < 0) newPosition = -newPosition - 1;
            if (newPosition == prePosition) {
                billItemList.set(prePosition, billItem);
                adapter.notifyItemChanged(prePosition);
            } else {
                adapter.notifyItemRemoved(prePosition);
                adapter.notifyItemInserted(newPosition);
                billRecyclerView.scrollToPosition(newPosition);
            }
        } else {
            adapter.notifyItemRemoved(prePosition);
        }
    }

    public void startEditActivityForAdd() {
        Intent intent = new Intent(getContext(), EditActivity.class);
        intent.putExtra("action", "add");
        startActivityForResult(intent, REQUEST_ADD_BILL);
    }

    @TestOnly
    public boolean addBillListRandomly() {
        MyBillTools.getBillListRandomly(10, getResources()).forEach((bill -> {
            bill.save();
            onBillItemAdded(bill.getId());
        }));
        billRecyclerView.scrollToPosition(0);
        checkListEmpty();
        return true;
    }


    public void reloadData() {
        billItemList.clear();
        Log.d(TAG, " reloadData");
        billItemList.addAll(LitePal.where(getMergedQueryString()).order("timeMills desc").find(BillItem.class));
        adapter.notifyDataSetChanged();
        billRecyclerView.invalidateItemDecorations();
        checkListEmpty();
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
                onBillItemAdded(id);
                Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                break;
            case "delete":
                onBillDeleted(prePosition);
                Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                break;
            case "edit":
                onBillEdited(prePosition, id);
                Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                break;
        }
        checkListEmpty();
        mActivity.fab.show();
    }

    public void checkListEmpty() {
        if (billItemList.isEmpty()) {
            emptyListImage.setVisibility(View.VISIBLE);
            mActivity.fab.show();
        } else {
            emptyListImage.setVisibility(View.GONE);
        }
        Log.d(TAG, "checkListEmpty: " + (emptyListImage.getVisibility() == View.VISIBLE ? "Visible" : "invisible"));
    }

    @Override
    public void onResume() {
        super.onResume();
        emptyListImage.requestLayout();
    }
}