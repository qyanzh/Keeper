package com.example.keeper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class StatusFragment extends Fragment {
    public static final String TAG = "statusfragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        Button hey = view.findViewById(R.id.hey);
        hey.setOnClickListener(v->{
            Snackbar.make(view,"hey!",Snackbar.LENGTH_SHORT).show();
        });
        return view;
    }
}
