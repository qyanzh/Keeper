package com.example.keeper.mytools;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyRecyclerView extends RecyclerView {
    @Nullable View emptyView;
    public MyRecyclerView(Context context) { super(context); }

    public MyRecyclerView(Context context, AttributeSet attrs) { super(context, attrs); }

    public MyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (emptyView != null) {
            if(getAdapter()!=null) {
                emptyView.setVisibility(getAdapter().getItemCount() > 0 ? GONE : VISIBLE);
            }
        }
    }

    OnItemChangedObserver o;

    public interface OnItemChangedObserver{
        default void onItemChanged(){}
    }

    public void setOnItemChangedObserver(OnItemChangedObserver o){
        this.o = o;
        o.onItemChanged();
    }

    final @NotNull AdapterDataObserver observer = new AdapterDataObserver() {
        @Override public void onChanged() {
            super.onChanged();
            o.onItemChanged();
            checkIfEmpty();
            invalidateItemDecorations();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            o.onItemChanged();
            checkIfEmpty();
            invalidateItemDecorations();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            o.onItemChanged();
            checkIfEmpty();
            invalidateItemDecorations();
        }

    };

    @Override public void setAdapter(@Nullable Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(@Nullable View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }
}