package com.example.keeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    public static final int EDIT_BILL = 1;

    private List<Bill> mBillList;
    Fragment homeFragment;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View billView;
        TextView billCategory;
        TextView billPrice;
        TextView billRemark;
        TextView billTime;

        public ViewHolder(View view) {
            super(view);
            billView = view;
            billCategory = view.findViewById(R.id.bill_category);
            billPrice = view.findViewById(R.id.bill_price);
            billRemark = view.findViewById(R.id.item_remark);
            billTime = view.findViewById(R.id.item_time);
        }
    }

    public BillAdapter(Fragment fragment, List<Bill> billList) {
        homeFragment = fragment;
        mBillList = billList;
    }

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bill_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.billView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Bill bill = mBillList.get(position);
            editBill(bill.getId());
            Snackbar.make(holder.billView, ""+bill.getId(), Snackbar.LENGTH_SHORT).show();
        });
        return holder;
    }

    private void editBill(long id) {
        Intent intent = new Intent(homeFragment.getContext(), EditBillActivity.class);
        intent.putExtra("action","edit");
        intent.putExtra("id",id);
        homeFragment.startActivityForResult(intent,EDIT_BILL);
    }

    @Override
    public void onBindViewHolder(@NonNull BillAdapter.ViewHolder viewHolder, int i) {
        Bill bill = mBillList.get(i);
        viewHolder.billCategory.setText(bill.category);
        viewHolder.billPrice.setText(String.valueOf(String.format((bill.isINCOME() ? "" : "-") + "¥%.1f", bill.price)));
        if (bill.isINCOME()) {
            viewHolder.billPrice.setTextColor(ContextCompat.getColor(viewHolder.billView.getContext(), R.color.green));
        } else {
            viewHolder.billPrice.setTextColor(ContextCompat.getColor(viewHolder.billView.getContext(), R.color.black));
        }
        viewHolder.billRemark.setText(bill.remark.equals("") ? (bill.isINCOME() ? "收入" : "支出"):bill.remark );
        viewHolder.billTime.setText(String.format("%02d", bill.hour) + ":" + String.format("%02d", bill.minute));
    }

    @Override
    public int getItemCount() {
        return mBillList.size();
    }

}
