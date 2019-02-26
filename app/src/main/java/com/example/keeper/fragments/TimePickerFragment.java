package com.example.keeper.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.keeper.BillItem;

import org.jetbrains.annotations.NotNull;

public class TimePickerFragment extends DialogFragment {
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
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), billItem.getHour(), billItem.getMinute(),
                true);
    }
}
