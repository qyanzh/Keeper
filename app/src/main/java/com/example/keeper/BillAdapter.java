package com.example.keeper;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.DecimalFormat;
import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter {
    public static final DecimalFormat df = new DecimalFormat("+0.00;-0.00");
    static int orange = Color.parseColor("#E8541E");
    private List<Bill> mBillList;
    Fragment mFragment;

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
        mFragment = fragment;
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
            editBill(bill.getId(),position);
            //Snackbar.make(v,""+position,Snackbar.LENGTH_SHORT).show();
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BillAdapter.ViewHolder viewHolder, int i) {
        Bill bill = mBillList.get(i);
        viewHolder.billCategory.setText(bill.category);
        float price = bill.getPrice();
        if(price == 0) {
            viewHolder.billPrice.setText("0.00");
        } else {
            viewHolder.billPrice.setText(df.format(price));
        }
        if (bill.isINCOME()) {
            viewHolder.billPrice.setTextColor(orange);
        } else {
            viewHolder.billPrice.setTextColor(Color.BLACK);
        }
        viewHolder.billRemark.setText(bill.remark.equals("") ? (bill.isINCOME() ? "收入" : "支出"):bill.remark );
        viewHolder.billTime.setText(MyDateFormat.timeFormatter.format(bill.getTimeMills()));
    }

    @Override
    public long getHeaderId(int position) {
        Bill bill = mBillList.get(position);
        return bill.getYear()*10000+bill.getMonth()*100+bill.getDay();
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_time,parent,false)) {};
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.header_title);
        Bill bill = mBillList.get(position);
        String time = MyDateFormat.format(bill.getTimeMills(), MyDateFormat.FORMATTYPE.DAY);
        textView.setText(time);
    }

    @Override
    public int getItemCount() {
        return mBillList.size();
    }

    private void editBill(long id,int position) {
        Intent intent = new Intent(mFragment.getContext(), EditBillActivity.class);
        intent.putExtra("action","edit");
        intent.putExtra("id",id);
        intent.putExtra("prePosition",position);
        mFragment.startActivityForResult(intent,HomeFragment.REQUEST_EDIT_BILL);
    }
}
