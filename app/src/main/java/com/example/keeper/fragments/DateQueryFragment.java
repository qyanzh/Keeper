package com.example.keeper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.keeper.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateQueryFragment extends QueryFragment {
    Spinner spinnerYear;
    Spinner spinnerMonth;
    Spinner spinnerDay;
    static final String[] years = new String[]{"2019", "2018", "2017", "2016"};
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
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int length = queryArguments.length;
        if (length > 0) {
            setSpinner(spinnerYear, yearAdapter, years, 0);
            //spinnerYear.setSelection(yearAdapter.getPosition(String.valueOf(year)));
        }
        if (length > 1) {
            setSpinner(spinnerMonth, monthAdapter, months, 1);
            //spinnerMonth.setSelection(monthAdapter.getPosition(String.valueOf(month)));
        }
        if (length > 2) {
            setSpinner(spinnerDay, dayAdapter, days, 2);
            //spinnerDay.setSelection(dayAdapter.getPosition(String.valueOf(day)));
        }

        return view;
    }

    public void setSpinner(Spinner spinner, ArrayAdapter<String> adapter, String[] strings, int pos) {
        spinner.setVisibility(View.VISIBLE);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                strings);
        adapter.setDropDownViewResource(R.layout.spinner_text);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                queryArguments[pos] = (String) parent.getItemAtPosition(position);
                reloadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerList.add(spinner);
    }

}
