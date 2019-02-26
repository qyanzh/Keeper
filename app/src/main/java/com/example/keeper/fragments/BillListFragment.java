package com.example.keeper.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        }
        billItemList = LitePal.where(getMergedQueryString()).order("timeMills desc").find(BillItem.class);
        adapter = new BillAdapter(this, billItemList);
        checkListEmpty();
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_query, container, false);
        }
        checkListEmpty();
        billRecyclerView = view.findViewById(R.id.bill_recyclerview);
        billRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        billRecyclerView.setAdapter(adapter);
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
        return sb.toString();
    }


    private boolean isMatchCondition(long id) {
        if (queryArguments == null) {
            Log.d(TAG, "isMatchCondition: hahahahah");
            return true;
        }
        int i = 0, length = queryArguments.length;
        BillItem billItem = LitePal.find(BillItem.class, id);
        for (; i < length; i++) {
            switch (queryConditions[i]) {
                case "year":
                    if (String.valueOf(billItem.getYear()).equals(queryArguments[i])) {
                        break;
                    }
                    break;
                case "month":
                    if (String.valueOf(billItem.getMonth()).equals(queryArguments[i])) {
                        break;
                    }
                    break;
                case "day":
                    if (String.valueOf(billItem.getMonth()).equals(queryArguments[i])) {
                        break;
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
        MyBillTools.getBillListRandomly(10).forEach((bill -> {
            bill.save();
            onBillItemAdded(bill.getId());
        }));
        billRecyclerView.scrollToPosition(0);
        checkListEmpty();
        return true;
    }


    public void reloadData() {
        billItemList.clear();
        Log.d(TAG, "reloadData: BillListFragment" + this);
        billItemList.addAll(LitePal.where(getMergedQueryString()).order("timeMills desc").find(BillItem.class));
        adapter.notifyDataSetChanged();
        billRecyclerView.invalidateItemDecorations();
    }

    public void checkListEmpty() {
        if (billItemList.isEmpty()) {
            mActivity.emptyListImage.setVisibility(View.VISIBLE);
            mActivity.emptyListImage.requestLayout();
            mActivity.fab.show();
        } else {
            mActivity.emptyListImage.setVisibility(View.GONE);
            mActivity.emptyListImage.requestLayout();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
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
                mActivity.emptyListImage.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                break;
            case "delete":
                onBillDeleted(prePosition);
                checkListEmpty();
                Toast.makeText(getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
                break;
            case "edit":
                onBillEdited(prePosition, id);
                Toast.makeText(getContext(), R.string.saved, Toast.LENGTH_SHORT).show();
                break;
        }
        mActivity.fab.show();
    }

}