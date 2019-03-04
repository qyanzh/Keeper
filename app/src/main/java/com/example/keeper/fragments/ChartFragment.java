package com.example.keeper.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.keeper.BillItem;
import com.example.keeper.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

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

    PieData pieData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<BillItem> list = getArguments().getParcelableArrayList("billList");
        int type = getArguments().getInt("type");
        Map<String, Float> categoriesCount = new HashMap<>();
        for (BillItem billItem : list) {
            if (billItem.getType() == type) {
                String category = billItem.getCategory();
                categoriesCount.put(category, categoriesCount.getOrDefault(category, 0f) + billItem.getPrice());
            }
        }
        Set<Map.Entry<String, Float>> countEntry = categoriesCount.entrySet();
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : countEntry) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.category));
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieData = new PieData(dataSet);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        PieChart chart = view.findViewById(R.id.pie_chart);
        chart.setData(pieData);
        chart.setRotationEnabled(false);
        chart.setDrawEntryLabels(true);
        chart.setDescription(null);
        chart.invalidate();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
