package com.example.keeper.fragments;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.keeper.BillItem;
import com.example.keeper.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ChartFragment extends Fragment {
    public static ChartFragment newInstance(Bundle dataBundle, int type) {
        ChartFragment fragment = new ChartFragment();
        dataBundle.putInt("type", type);
        fragment.setArguments(dataBundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        List<BillItem> list = getArguments().getParcelableArrayList("billList");
        int type = getArguments().getInt("type");

        Map<String, Float> categoriesCount = new HashMap<>();
        for (BillItem billItem : list) {
            if (billItem.getType() == type) {
                String category = billItem.getCategory();
                categoriesCount.put(category, categoriesCount.getOrDefault(category, 0f) + Math.abs(billItem.getPrice()));
            }
        }
        Set<Map.Entry<String, Float>> countEntry = categoriesCount.entrySet();
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : countEntry) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(entries, null);
        List<Integer> colors = new ArrayList<>();
        TypedArray colorResource = getResources().obtainTypedArray(R.array.colors);
        for (int i = 0; i < colorResource.length(); i++) {
            colors.add(colorResource.getColor(i, 0));
        }
        colorResource.recycle();
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12.f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.8f);
        dataSet.setValueLinePart2Length(0.3f);
        dataSet.setValueLineColor(Color.BLACK);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> "¥" + value);


        PieData pieData = new PieData(dataSet);

        PieChart chart = view.findViewById(R.id.pie_chart);
        chart.setData(pieData);
        chart.setDrawEntryLabels(true);
        chart.getDescription().setEnabled(false);
        chart.setHoleRadius(30f);
        chart.setTransparentCircleRadius(40f);

        chart.setExtraOffsets(20, 20, 20, 20);
        chart.animateY(500, Easing.EaseInOutCubic);

        chart.setEntryLabelColor(Color.BLACK);
        chart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        chart.invalidate();
        return view;
    }
}
