package com.example.keeper.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.keeper.Bill;

import org.jetbrains.annotations.NotNull;

public class TimePickerFragment extends DialogFragment {
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Bill bill;
        if(bundle!=null) {
            bill = (Bill)bundle.getSerializable("bill");
        } else {
            bill = new Bill();
        }
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(), bill.getHour(), bill.getMinute(),
                true);
    }
}
