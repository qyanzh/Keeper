package com.example.keeper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.keeper.R;

import java.util.ArrayList;
import java.util.List;

public class DateQueryFragment extends QueryFragment {
    Spinner spinnerYear;
    Spinner spinnerMonth;
    Spinner spinnerDay;
    static final String[] years = new String[]{"2019", "2018", "2017"};
    static final String[] months = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    static final String[] days = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    ArrayAdapter<String> yearAdapter, monthAdapter, dayAdapter;
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
            yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                    years);
            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerYear.setAdapter(yearAdapter);
            spinnerList.add(spinnerYear);
        }
        if (length > 1) {
            spinnerMonth.setVisibility(View.VISIBLE);
            monthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                    months);
            monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMonth.setAdapter(monthAdapter);
            spinnerList.add(spinnerMonth);
        }
        if (length > 2) {
            spinnerDay.setVisibility(View.VISIBLE);
            dayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                    days);
            dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDay.setAdapter(dayAdapter);
            spinnerList.add(spinnerDay);
        }

        return view;
    }

}
