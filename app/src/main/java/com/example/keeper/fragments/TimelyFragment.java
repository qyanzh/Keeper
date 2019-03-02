package com.example.keeper.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.keeper.BillItem;
import com.example.keeper.R;
import com.example.keeper.mytools.MyDateFormat;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class TimelyFragment extends BillListFragment {
    public static final String YEARLY_FRAGMENT_TAG = "YearlyFragment";
    public static final String MONTHLY_FRAGMENT_TAG = "MonthlyFragment";
    public static final String DAILY_FRAGMENT_TAG = "DailyFragment";

    public static TimelyFragment newInstance(String TAG, Bundle bundle) {
        TimelyFragment fragment = new TimelyFragment();
        bundle.putString("TAG", TAG);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    List<BillItem> getList() {
        return LitePal.where(getMergedWhereString()).order("timeMills desc").find(BillItem.class);
    }

    String getMergedWhereString() {
        StringBuilder sb = new StringBuilder();
        if (queryArguments != null) {
            int length = queryArguments.length;
            for (int i = 0; i < length; ++i) {
                sb.append(queryConditions[i]).append("=").append(queryArguments[i]);
                if (i != length - 1) {
                    sb.append(" and ");
                } else {
                    sb.append(".");
                }
            }
        }
        Log.d(TAG, "getMergedWhereString : " + sb.toString());
        return sb.toString();
    }

    @Override
    boolean isMatchCondition(long id) {
        if (queryArguments == null) {
            return true;
        }
        int i = 0, length = queryArguments.length;
        BillItem billItem = LitePal.find(BillItem.class, id);
        outer:
        for (; i < length; ++i) {
            switch (queryConditions[i]) {
                case "year":
                    if (!String.valueOf(billItem.getYear()).equals(queryArguments[i])) {
                        break outer;
                    }
                    break;
                case "month":
                    if (!String.valueOf(billItem.getMonth()).equals(queryArguments[i])) {
                        break outer;
                    }
                    break;
                case "day":
                    if (!String.valueOf(billItem.getDay()).equals(queryArguments[i])) {
                        break outer;
                    }
                    break;
                default:
            }
        }
        return i == length;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        LinearLayout barDate = view.findViewById(R.id.bar_date);
        TextView barTextTime = view.findViewById(R.id.bar_text_date);
        boolean[] mode = new boolean[6];
        int length = queryArguments.length;
        for (int i = 0; i < 3; i++) {
            if (length > i) {
                mode[i] =true;
            }
        }
        Calendar selectedTime = getSelectedCalendar();
        barTextTime.setText(MyDateFormat.formatForTimely(selectedTime.getTime(), mode));
        barDate.setOnClickListener((View view) -> {
            TimePickerView pvTime = new TimePickerBuilder(getContext(), (Date date, View v) -> {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                if (mode[0]) queryArguments[0] = String.valueOf(c.get(YEAR));
                if (mode[1]) queryArguments[1] = String.valueOf(c.get(MONTH) + 1);
                if (mode[2]) queryArguments[2] = String.valueOf(c.get(DAY_OF_MONTH));
                barTextTime.setText(MyDateFormat.formatForTimely(date, mode));
                reloadData();
            }).setType(mode)
                    .setSubmitColor(Color.parseColor("#4050B5"))
                    .setCancelColor(Color.parseColor("#4050B5"))
                    .setDate(getSelectedCalendar()).build();
            pvTime.show();
        });
        return view;
    }

    private Calendar getSelectedCalendar() {
        int length = queryArguments.length;
        Calendar c = Calendar.getInstance();
        if (length > 0) c.set(YEAR, Integer.parseInt(queryArguments[0]));
        if (length > 1) c.set(MONTH, Integer.parseInt(queryArguments[1]) - 1);
        if (length > 2) c.set(DAY_OF_MONTH, Integer.parseInt(queryArguments[2]));
        return c;
    }

}
