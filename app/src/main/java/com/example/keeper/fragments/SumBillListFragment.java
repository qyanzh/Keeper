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
        view = inflater.inflate(R.layout.fragment_billlist, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        spinnerYear = view.findViewById(R.id.spinner_year);
        spinnerMonth = view.findViewById(R.id.spinner_month);
        spinnerDay = view.findViewById(R.id.spinner_day);

        switch (queryArguments.length) {
            case 3:
                setSpinner(spinnerDay, days, 2);
            case 2:
                setSpinner(spinnerMonth, months, 1);
            case 1:
                setSpinner(spinnerYear, years, 0);
            default:
                reloadData();
                Log.d(TAG, "setSpinner " + queryArguments.length);
        }
        return view;
    }

    public void setSpinner(Spinner spinner, String[] strings, int pos) {
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> arrayAdapter = null;
        if (getContext() != null) {
            arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                    strings);
        }
        if (arrayAdapter != null) {
            arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        }
        spinner.setAdapter(arrayAdapter);
        Calendar c = Calendar.getInstance();
        switch (pos) {
            case 0:
                int year = c.get(YEAR);
                spinnerYear.setSelection(year - 2018);
                break;
            case 1:
                int month = c.get(MONTH) + 1;
                spinnerMonth.setSelection(month - 1);
                break;
            case 2:
                int day = c.get(DAY_OF_MONTH);
                spinnerDay.setSelection(day - 1);
        }
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
