package com.example.keeper.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.keeper.BillItem;

import org.jetbrains.annotations.NotNull;

public class DatePickerFragment extends DialogFragment {
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        BillItem billItem;
        if(bundle!=null) {
            billItem = (BillItem) bundle.getSerializable("billItem");
        } else {
            billItem = new BillItem();
        }
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), billItem.getYear(), billItem.getMonth() - 1, billItem.getDay());
    }
}
