package com.example.keeper.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.keeper.BillItem;
import com.example.keeper.R;
import com.example.keeper.mytools.MyDateFormat;
import com.example.keeper.pickers.DatePickerFragment;
import com.example.keeper.pickers.TimePickerFragment;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EditActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Toolbar toolbar;
    EditText inputMoneyAmount;
    RadioGroup radioGroupType;
    EditText inputRemarks;
    Spinner spinnerCategory;
    List<String> categories;
    ArrayAdapter<String> spinnerAdapter;
    Button editButtonDate;
    Button editButtonTime;
    static BillItem billItem;
    String action;
    int prePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if (action.equals("add")) {
            billItem = new BillItem();
        } else if (action.equals("edit")) {
            long id = intent.getLongExtra("id", -1);
            prePosition = intent.getIntExtra("prePosition", -1);
            billItem = (BillItem) (LitePal.find(BillItem.class, id)).clone();
        }
        initToolBar();
        initAmountEditor();
        initSpinner();
        initRadioButtons();
        initRemarksEditor();
        initTimeBox();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (action.equals("add")) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add_done:
                saveBillItemAndFinish();
                break;
            case R.id.delete:
                showItemSelectedConfirmDialog(getString(R.string.confirmDeleteQM), "delete");
                break;
        }
        return true;
    }

    public void showItemSelectedConfirmDialog(String content, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(content)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    if (action.equals("delete")) {
                        deleteBillItemAndFinish();
                    } else if (action.equals("back")) {
                        super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        if (action.equals("add")) {
            showItemSelectedConfirmDialog(getString(R.string.giveUpOperationQM), "back");
        } else {
            super.onBackPressed();
        }
    }

    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar_edit);
        if (action.equals("edit")) {
            toolbar.setTitle(R.string.edit);
        } else {
            toolbar.setTitle(R.string.add);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initAmountEditor() {
        float price = billItem.getPrice();
        inputMoneyAmount = findViewById(R.id.editText_amount);
        if (price != 0) inputMoneyAmount.setText(String.valueOf(Math.abs(price)));
        if (action.equals("add")) {
            inputMoneyAmount.requestFocus();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager manager = (InputMethodManager) inputMoneyAmount.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.showSoftInput(inputMoneyAmount, 0);
                }
            }, 500);
        }
    }

    public void initSpinner() {
        spinnerCategory = findViewById(R.id.spinner_category);
        getCategoriesOfType();
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);
        autoSelectCategory();
    }

    public void refreshSpinner() {
        getCategoriesOfType();
        spinnerAdapter.clear();
        spinnerAdapter.addAll(categories);
        spinnerAdapter.notifyDataSetChanged();
        autoSelectCategory();
    }

    public void getCategoriesOfType() {
        if (billItem.isPayout()) {
            categories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.payoutCategory)));
        } else {
            categories = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.incomeCategory)));
        }
    }

    public void autoSelectCategory() {
        int i = 0, count = spinnerAdapter.getCount();
        for (; i < count; ++i) {
            if (billItem.getCategory().equals(spinnerAdapter.getItem(i))) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
        if (i == count) spinnerCategory.setSelection(0);
    }

    public void initRadioButtons() {
        radioGroupType = findViewById(R.id.radio_group_type);
        if (billItem.isIncome()) {
            radioGroupType.check(R.id.radioButton_income);
        } else {
            radioGroupType.check(R.id.radioButton_payout);
        }
        refreshSpinner();
        radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButton_income:
                    billItem.setType(BillItem.INCOME);
                    break;
                case R.id.radioButton_payout:
                    billItem.setType(BillItem.PAYOUT);
                    break;
            }
            refreshSpinner();
        });
    }

    private void initRemarksEditor() {
        inputRemarks = findViewById(R.id.editText_remarks);
        inputRemarks.setText(billItem.getRemark());
    }

    private void initTimeBox() {
        editButtonDate = findViewById(R.id.editButton_date);
        editButtonTime = findViewById(R.id.editButton_time);
        editButtonDate.setText(MyDateFormat.format(billItem.getTimeMills(), false));
        editButtonTime.setText(MyDateFormat.timeFormatter.format(billItem.getTimeMills()));
        editButtonDate.setOnClickListener(v -> {
            DatePickerFragment datePickerFragment = DatePickerFragment.getInstance(billItem);
            datePickerFragment.show(getSupportFragmentManager(), "datePicker");
        });
        editButtonTime.setOnClickListener(v -> {
            TimePickerFragment timePickerFragment = TimePickerFragment.getInstance(billItem);
            timePickerFragment.show(getSupportFragmentManager(), "timePicker");
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, billItem.getHour(), billItem.getMinute());
        billItem.setTime(c.getTimeInMillis());
        editButtonDate.setText(MyDateFormat.format(billItem.getTimeMills(), false));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(billItem.getYear(), billItem.getMonth() - 1, billItem.getDay(), hourOfDay, minute);
        billItem.setTime(c.getTimeInMillis());
        editButtonTime.setText(MyDateFormat.timeFormatter.format(billItem.getTimeMills()));
    }

    public void saveData() {
        if (!inputMoneyAmount.getText().toString().equals("")) {
            float price = Float.parseFloat(inputMoneyAmount.getText().toString());
            if (billItem.isPayout()) price = -price;
            billItem.setPrice(price);
        }
        String remark = inputRemarks.getText().toString();
        billItem.setRemark(remark);
        String category = spinnerCategory.getSelectedItem().toString();
        billItem.setCategory(category);
    }

    public void saveBillItemAndFinish() {
        saveData();
        billItem.save();
        Intent intent = new Intent();
        intent.putExtra("id", billItem.getId());
        if (action.equals("add")) {
            intent.putExtra("action", "add");
        } else if (action.equals("edit")) {
            intent.putExtra("action", "edit");
            intent.putExtra("prePosition", prePosition);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    public void deleteBillItemAndFinish() {
        LitePal.delete(BillItem.class, billItem.getId());
        Intent intent = new Intent();
        intent.putExtra("action", "delete");
        intent.putExtra("prePosition", prePosition);
        setResult(RESULT_OK, intent);
        finish();
    }

}