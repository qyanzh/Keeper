package com.example.keeper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddBillActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

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
            //  Log.d(TAG, "onTimeSet: "+bill.year+bill.month+bill.day);
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
            // Log.d(TAG, "onDateSet: "+bill.year+bill.month+bill.day);
        }
    }

    Toolbar toolbar;
    EditText inputMoneyAmount;
    RadioButton radioButtonPayout;
    RadioButton radioButtonIncome;
    EditText inputRemarks;
    Spinner spinnerCategory;
    static Button buttonChooseDate;
    static Button buttonChooseTime;
    static Bill bill;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    ;
    static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
    static String TAG = "时间测试";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bill);
        initToolBar();
        bill = new Bill();
        inputMoneyAmount = findViewById(R.id.editText_amount);
        inputRemarks = findViewById(R.id.editText_remarks);
        inputRemarks.setText("");
        initRadioButtons();
        initSpinner();
        initTimeButton();
    }

    static String getTimeString(Date date) {
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }


    private void initTimeButton() {
        buttonChooseDate = findViewById(R.id.editButton_date);
        buttonChooseTime = findViewById(R.id.editButton_time);
        buttonChooseDate.setOnClickListener(this);
        buttonChooseTime.setOnClickListener(this);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(bill.getTimeMills());
        buttonChooseDate.setText(dateFormat.format(c.getTime()));
        buttonChooseTime.setText(timeFormat.format(c.getTime()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        Toast.makeText(this, "取消新建", Toast.LENGTH_SHORT).show();
    }

    public void initRadioButtons() {
        radioButtonIncome = findViewById(R.id.radioButton_income);
        radioButtonPayout = findViewById(R.id.radioButton_payout);
        radioButtonPayout.setChecked(true);
    }

    public void initSpinner() {
        spinnerCategory = findViewById(R.id.spinner_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Bill.payoutCategory);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(this);
    }

    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar_edit);
        toolbar.setTitle("Add a new bill");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioButton_income:
                if (checked)
                    radioButtonPayout.setChecked(false);
                bill.setType(Bill.INCOME);
                break;
            case R.id.radioButton_payout:
                if (checked)
                    radioButtonIncome.setChecked(false);
                bill.setType(Bill.PAYOUT);
                // Ninjas rule
                break;
        }
        Toast.makeText(this, "" + ((RadioButton) view).getText() + "is checked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add_done:
                if(!inputMoneyAmount.getText().equals("")) {
                    bill.setPrice(Float.parseFloat(inputMoneyAmount.getText().toString()));
                }
                String remark = inputRemarks.getText().toString();
                bill.setRemark(!remark.equals("")?remark:(bill.isINCOME()?"收入":"支出"));
                bill.save();
                setResult(RESULT_OK);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_bill_done, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCategory = Bill.payoutCategory[position];
        bill.setCategory(selectedCategory);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "canceld", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editButton_date:
                showDatePickerDialog(v);
                break;
            case R.id.editButton_time:
                showTimePickerDialog(v);
                break;
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}