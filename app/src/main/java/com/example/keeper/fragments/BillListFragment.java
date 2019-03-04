package com.example.keeper.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keeper.BillAdapter;
import com.example.keeper.BillItem;
import com.example.keeper.R;
import com.example.keeper.activities.EditActivity;
import com.example.keeper.mytools.MyRecyclerView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jetbrains.annotations.TestOnly;
import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public abstract class BillListFragment extends Fragment implements MyRecyclerView.OnItemChangedObserver {

    private static final DecimalFormat df = new DecimalFormat("¥###,###,##0.00");
    public View view;
    public String TAG;
    public List<BillItem> billItemList;
    public BillAdapter billAdapter;
    public MyRecyclerView billRecyclerView;
    StickyRecyclerHeadersDecoration headersDecoration;
    TextView textTotalIncome;
    TextView textTotalPayout;
    ConstraintLayout barAmountDisplay;
    public boolean isFirstShow = true;
    public static final int REQUEST_ADD_BILL = 0;
    public static final int REQUEST_EDIT_BILL = 1;
    String[] queryConditions;
    String[] queryArguments;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            queryConditions = bundle.getStringArray("queryConditions");
            queryArguments = bundle.getStringArray("queryArguments");
            TAG = bundle.getString("TAG");
        }
        billItemList = getList();
        billAdapter = new BillAdapter(this, billItemList);
        setRetainInstance(true);
        Log.d(TAG, "onCreate: ");
    }

    abstract List<BillItem> getList();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_billlist, container, false);
        barAmountDisplay = view.findViewById(R.id.bar_total_amount);
        barAmountDisplay.setVisibility(View.VISIBLE);
        barAmountDisplay.setOnClickListener(v -> {
            if (!billItemList.isEmpty()) {
                BottomDialogFragment.newInstance(billItemList).show(getFragmentManager(), "tag");
            }
        });
        textTotalIncome = view.findViewById(R.id.bar_text_income);
        textTotalPayout = view.findViewById(R.id.bar_text_payout);
        billRecyclerView = view.findViewById(R.id.bill_recyclerView);
        billRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        billRecyclerView.setAdapter(billAdapter);
        billRecyclerView.setEmptyView(view.findViewById(R.id.empty_view));
        headersDecoration = new StickyRecyclerHeadersDecoration(billAdapter);
        billRecyclerView.addItemDecoration(headersDecoration);
        billRecyclerView.setHasFixedSize(true);
        billRecyclerView.setOnItemChangedObserver(this);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.rec_divider_line, null));
        billRecyclerView.addItemDecoration(divider);
        return view;
    }


    abstract boolean isMatchCondition(long id);

    public void addBill() {
        Intent intent = new Intent(getContext(), EditActivity.class);
        intent.putExtra("action", "add");
        startActivityForResult(intent, REQUEST_ADD_BILL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ADD_BILL:
                if (resultCode == RESULT_OK) {
                    refreshRecyclerViewAfterEdit(data);
                }
                break;
            case REQUEST_EDIT_BILL:
                if (resultCode == RESULT_OK) {
                    refreshRecyclerViewAfterEdit(data);
                }
                break;
            default:
                break;
        }
    }

    void onBillItemAdded(long id) {
        Log.d(TAG, "onBillItemAdded: ");
        BillItem billItem = LitePal.find(BillItem.class, id);
        if (isMatchCondition(id)) {
            int newPosition = Collections.binarySearch(billItemList, billItem);
            if (newPosition < 0) newPosition = -newPosition - 1;
            billItemList.add(newPosition, billItem);
            billAdapter.notifyItemInserted(newPosition);
            billRecyclerView.scrollToPosition(newPosition);
        }
    }

    void onBillDeleted(int prePosition) {
        billItemList.remove(prePosition);
        billAdapter.notifyItemRemoved(prePosition);
    }

    void onBillEdited(int prePosition, long id) {
        onBillDeleted(prePosition);
        if (isMatchCondition(id)) onBillItemAdded(id);

    }

    public void reloadData() {
        if (!isFirstShow) {
            billItemList.clear();
            Log.d(TAG, " reloadData");
            billItemList.addAll(getList());
            billAdapter.notifyDataSetChanged();
        }
        isFirstShow = false;
    }

    private void refreshRecyclerViewAfterEdit(Intent intent) {
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
    }

    public void refreshAmountOfMoney() {
        float payout = 0;
        float income = 0;
        for (BillItem billItem : billItemList) {
            float price = billItem.getPrice();
            if (billItem.isPayout()) {
                payout += price;
            } else {
                income += price;
            }
        }

        textTotalIncome.setText(df.format(income));
        textTotalPayout.setText(df.format(payout));
    }


    @TestOnly
    public void addBillListRandomly() {
        getBillListRandomly(10).forEach((bill -> {
            bill.save();
            onBillItemAdded(bill.getId());
        }));
        billRecyclerView.scrollToPosition(0);
    }

    @TestOnly
    public List<BillItem> getBillListRandomly(int amounts) {
        String[] incomeCategory = getResources().getStringArray(R.array.incomeCategory);
        String[] payoutCategory = getResources().getStringArray(R.array.payoutCategory);
        List<BillItem> billItemList = new ArrayList<>();
        for (int i = 0; i < amounts; ++i) {
            Calendar c = Calendar.getInstance();
            Random random = new Random();
            int year = c.get(YEAR) - Math.abs(random.nextInt(2));
            int month = Math.abs(random.nextInt((year == c.get(YEAR) ? c.get(MONTH) + 1 : 12)));
            int day = Math.abs(random.nextInt(28));
            for (int j = 0; j < 4; ++j) {
                int hour = Math.abs(random.nextInt(24));
                int minute = Math.abs(random.nextInt(60));
                BillItem billItem = new BillItem();
                billItem.setPrice(new Random().nextInt() % 200);
                if (billItem.getPrice() < 0) billItem.setType(BillItem.PAYOUT);
                else billItem.setType(BillItem.INCOME);
                if (billItem.isIncome()) {
                    billItem.setCategory(incomeCategory[Math.abs(new Random().nextInt() % incomeCategory.length)]);
                } else {
                    billItem.setCategory(payoutCategory[Math.abs(new Random().nextInt() % payoutCategory.length)]);
                }

                c.set(year, month, day, hour, minute);
                billItem.setTime(c.getTimeInMillis());
                billItemList.add(billItem);
            }
        }
        return billItemList;
    }

    @Override
    public void onItemChanged() {
        headersDecoration.invalidateHeaders();
        refreshAmountOfMoney();

    }
}