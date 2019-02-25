package com.example.keeper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.keeper.R;

import java.util.ArrayList;
import java.util.List;

public class DateQueryFragment extends QueryFragment {
    Spinner spinnerYear;
    Spinner spinnerMonth;
    Spinner spinnerDay;
    List<Spinner> spinnerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        spinnerYear = view.findViewById(R.id.spinner_year);
        spinnerMonth = view.findViewById(R.id.spinner_month);
        spinnerDay = view.findViewById(R.id.spinner_day);
        int length = queryArguments.length;
        if (length > 0) {
            spinnerYear.setVisibility(View.VISIBLE);
            spinnerList.add(spinnerYear);
        }
        if (length > 1) {
            spinnerMonth.setVisibility(View.VISIBLE);
            spinnerList.add(spinnerMonth);
        }
        if (length > 2) {
            spinnerDay.setVisibility(View.VISIBLE);
            spinnerList.add(spinnerDay);
        }
        /////
        return view;
    }

}
