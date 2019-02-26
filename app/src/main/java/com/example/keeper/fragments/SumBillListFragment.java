package com.example.keeper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.keeper.R;
import com.example.keeper.mytools.MyBillTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class SumBillListFragment extends BillListFragment {
    Spinner spinnerYear;
    Spinner spinnerMonth;
    Spinner spinnerDay;
    public static final String HOME_FRAGMENT_TAG = "HomeFragment";
    public static final String YEARLY_FRAGMENT_TAG = "YearlyFragment";
    public static final String MONTHLY_FRAGMENT_TAG = "MonthlyFragment";
    public static final String DAILY_FRAGMENT_TAG = "DailyFragment";
    static final String[] years = MyBillTools.getYearStrings();
    static final String[] months = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    static final String[] days = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    ArrayAdapter<String> yearAdapter, monthAdapter, dayAdapter;
    List<Spinner> spinnerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_datequery, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        spinnerYear = view.findViewById(R.id.spinner_year);
        spinnerMonth = view.findViewById(R.id.spinner_month);
        spinnerDay = view.findViewById(R.id.spinner_day);
        Calendar c = Calendar.getInstance();
        int year = c.get(YEAR);
        int month = c.get(MONTH);
        int day = c.get(DAY_OF_MONTH);
        switch (queryArguments.length) {
            case 3:
                setSpinner(spinnerDay, dayAdapter, days, 2);
                spinnerDay.setSelection(day - 1);
            case 2:
                setSpinner(spinnerMonth, monthAdapter, months, 1);
                spinnerMonth.setSelection(month);
            case 1:
                setSpinner(spinnerYear, yearAdapter, years, 0);
                spinnerYear.setSelection(year - 2018);
            default:
                break;
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
                for (String s : queryArguments) {
                    Log.d(TAG, "onItemSelected: " + s);
                }
                reloadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerList.add(spinner);
    }

}
