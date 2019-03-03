package com.example.keeper.pickers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.keeper.BillItem;

import org.jetbrains.annotations.NotNull;

public class TimePickerFragment extends DialogFragment {

    public static TimePickerFragment getInstance(BillItem billItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("billItem",billItem);
        TimePickerFragment tp = new TimePickerFragment();
        tp.setArguments(bundle);
        return tp;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        BillItem billItem = new BillItem();
        if (bundle != null) {
            BillItem nullable = (BillItem) bundle.getParcelable("billItem");
            if (nullable != null) {
                billItem = nullable;
            }
        }
        if (getActivity() != null) {
            return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), billItem.getHour(), billItem.getMinute(),
                    true);
        } else {
            throw new RuntimeException("Activity is null");
        }
    }
}
