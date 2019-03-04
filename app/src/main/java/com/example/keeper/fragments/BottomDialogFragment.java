package com.example.keeper.fragments;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.keeper.BillItem;
import com.example.keeper.R;

import java.util.ArrayList;
import java.util.List;

public class BottomDialogFragment extends BottomSheetDialogFragment {
    public static BottomDialogFragment newInstance(List<BillItem> billItemList) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("billList", (ArrayList<? extends Parcelable>) billItemList);
        BottomDialogFragment fragment = new BottomDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_bottom_sheet, container);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        TabLayout tabLayout = view.findViewById(R.id.tab);
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        fragments.add(ChartFragment.newInstance((Bundle) getArguments().clone(), BillItem.PAYOUT));
        titles.add(getString(R.string.payout));
        fragments.add(ChartFragment.newInstance((Bundle) getArguments().clone(), BillItem.INCOME));
        titles.add(getString(R.string.income));
        BottomFragmentAdapter adapter = new BottomFragmentAdapter(getChildFragmentManager(),
                fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int height = (point.y) / 2;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                height + 70);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        View view = dialog.getWindow().findViewById(android.support.design.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(view).setPeekHeight(height + 70);
    }
}
