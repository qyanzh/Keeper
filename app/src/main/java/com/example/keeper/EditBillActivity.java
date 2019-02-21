package com.example.keeper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EditBillActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText inputMoneyAmount;
    RadioButton radioButtonPayout;
    RadioButton radioButtonIncome;
    EditText inputRemarks;
    Spinner spinnerCategory;
    List<String> categories;
    ArrayAdapter<String> spinnerAdapter;
    static Button buttonChooseDate;
    static Button buttonChooseTime;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    static Bill bill;
    String action;
    int prePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bill);
        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if (action.equals("add")) {
            bill = new Bill();
        } else if (action.equals("edit")) {
            long id = intent.getLongExtra("id", -1);
            prePosition = intent.getIntExtra("prePosition", -1);
            bill = (Bill) LitePal.find(Bill.class, id).clone();
        }
        initToolBar();
        initAmountEditor();
        initRadioButtons();
        initRemarksEditor();
        initSpinner();
        initTimeButton();
    }

    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar_edit);
        if (action.equals("edit")) {
            toolbar.setTitle("编辑");
        } else {
            toolbar.setTitle("新建");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        getMenuInflater().inflate(R.menu.edit_bill_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(action.equals("add")) {
            confirmDialog("放弃本次操作?", "back");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add_done:
                saveBill();
                break;
            case R.id.delete:
                confirmDialog("确认删除?", "delete");
                break;
        }
        return true;
    }

    public void confirmDialog(String content, String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(content)
                .setPositiveButton("确认", (dialog, id) -> {
                    if (action.equals("delete")) {
                        deleteBill();
                    } else if (action.equals("back")) {
                        super.onBackPressed();
                    }
                })
                .setNegativeButton("取消", (dialog, id) -> {
                });
        builder.create().show();
    }

    public void saveBill() {
        if (!inputMoneyAmount.getText().toString().equals("")) {
            float price = Float.parseFloat(inputMoneyAmount.getText().toString());
            if (bill.isPAYOUT()) price = -price;
            bill.setPrice(price);
        }
        String remark = inputRemarks.getText().toString();
        bill.setRemark(remark);
        bill.save();

        Intent intent = new Intent();
        intent.putExtra("id", bill.getId());
        if (action.equals("add")) {
            intent.putExtra("action", "add");
        } else {
            intent.putExtra("action", "edit");
            intent.putExtra("prePosition", prePosition);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    public void deleteBill() {
        LitePal.delete(Bill.class, bill.getId());

        Intent intent = new Intent();
        intent.putExtra("action", "delete");
        intent.putExtra("prePosition", prePosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initAmountEditor() {
        inputMoneyAmount = findViewById(R.id.editText_amount);
        inputMoneyAmount.setFocusable(true);
        inputMoneyAmount.requestFocus();
        inputMoneyAmount.setText("" + (bill.getPrice() == 0 ? "" : Math.abs(bill.getPrice())));
        if(action.equals("add")) {
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

    public void initRadioButtons() {
        radioButtonIncome = findViewById(R.id.radioButton_income);
        radioButtonPayout = findViewById(R.id.radioButton_payout);
        radioButtonIncome.setOnClickListener(v -> {
            radioButtonPayout.setChecked(false);
            bill.setType(Bill.INCOME);
            bill.setCategory("转账");
            refreshSpinner();
        });
        radioButtonPayout.setOnClickListener(v -> {
            radioButtonIncome.setChecked(false);
            bill.setType(Bill.PAYOUT);
            bill.setCategory("消费");
            refreshSpinner();
        });
        if (bill.isINCOME()) {
            radioButtonIncome.setChecked(true);
        } else {
            radioButtonPayout.setChecked(true);
        }
    }

    private void initRemarksEditor() {
        inputRemarks = findViewById(R.id.editText_remarks);
        inputRemarks.setText(bill.getRemark());
    }

    public void initSpinner() {
        spinnerCategory = findViewById(R.id.spinner_category);
        if (bill.isPAYOUT()) {
            categories = new ArrayList<>(Arrays.asList(Bill.payoutCategory));
        } else {
            categories = new ArrayList<>(Arrays.asList(Bill.incomeCategory));
        }
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bill.setCategory(categories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(EditBillActivity.this, "onNothingSelected", Toast.LENGTH_SHORT).show();
            }
        });
        for (int i = 0; i < spinnerAdapter.getCount(); ++i) {
            if (spinnerAdapter.getItem(i).equals(bill.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    public void refreshSpinner() {
        if (bill.isPAYOUT()) {
            categories = new ArrayList<>(Arrays.asList(Bill.payoutCategory));
        } else {
            categories = new ArrayList<>(Arrays.asList(Bill.incomeCategory));
        }
        spinnerAdapter.clear();
        spinnerAdapter.addAll(categories);
        spinnerAdapter.notifyDataSetChanged();
        spinnerCategory.setSelection(0);
    }

    private void initTimeButton() {
        buttonChooseDate = findViewById(R.id.editButton_date);
        buttonChooseTime = findViewById(R.id.editButton_time);
        buttonChooseDate.setOnClickListener(this::showDatePickerDialog);
        buttonChooseTime.setOnClickListener(this::showTimePickerDialog);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(bill.getTimeMills());
        buttonChooseDate.setText(dateFormat.format(c.getTime()));
        buttonChooseTime.setText(timeFormat.format(c.getTime()));
    }


    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    true);
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(bill.getTimeMills());
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            c.set(year, month, day, hourOfDay, minute);
            bill.setTime(c.getTimeInMillis());
            buttonChooseTime.setText(timeFormat.format(c.getTime()));
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, bill.getHour(), bill.getMinute());
            bill.setTime(c.getTimeInMillis());
            buttonChooseDate.setText(dateFormat.format(c.getTime()));
        }

    }

}