package com.example.keeper.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.keeper.Bill;

import org.jetbrains.annotations.NotNull;

public class DatePickerFragment extends DialogFragment {
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Bill bill;
        if(bundle!=null) {
            bill = (Bill) bundle.getSerializable("bill");
        } else {
            bill = new Bill();
        }
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), bill.getYear(), bill.getMonth() - 1, bill.getDay());
    }
}
