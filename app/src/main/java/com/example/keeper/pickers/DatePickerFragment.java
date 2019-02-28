package com.example.keeper.pickers;

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
        BillItem billItem = new BillItem();
        if (bundle != null) {
            BillItem nullable = (BillItem) bundle.getSerializable("billItem");
            if (nullable != null) {
                billItem = nullable;
            }
        }
        if (getActivity() != null) {
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), billItem.getYear(), billItem.getMonth() - 1, billItem.getDay());
        } else {
            throw new RuntimeException("Activity is null");
        }
    }
}
