package com.example.keeper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EditBillActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

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
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    boolean isEditing;
    int position;
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bill);


        Intent intent = getIntent();
        String action = intent.getStringExtra("action");
        if(action.equals("edit")) {
            long id = intent.getLongExtra("id",-1);
            try {
                bill = (Bill)LitePal.find(Bill.class, id).clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            position=intent.getIntExtra("position",-1);
            isEditing = true;
        } else if(action.equals("add")){
            bill = new Bill();
            isEditing = false;
        }

        initToolBar();
        inputMoneyAmount = findViewById(R.id.editText_amount);
        inputMoneyAmount.setFocusable(true);
        inputMoneyAmount.requestFocus();
        inputMoneyAmount.setText(""+(bill.getPrice()==0?"":Math.abs(bill.getPrice())));
        Timer timer =new Timer();
        timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager manager =(InputMethodManager) inputMoneyAmount.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.showSoftInput(inputMoneyAmount,0);
                }
        },500);
        inputRemarks = findViewById(R.id.editText_remarks);
        inputRemarks.setText(bill.getRemark());
        initRadioButtons();
        initSpinner();
        initTimeButton();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认返回? 将不保存所作更改")
                .setPositiveButton("确认", (dialog, id) -> {
                    super.onBackPressed();
                    setResult(RESULT_CANCELED);
                    Toast.makeText(this, "已取消", Toast.LENGTH_SHORT).show();
                    // FIRE ZE MISSILES!
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        return;
                    }
                });
        builder.create().show();

    }

    public void initRadioButtons() {
        radioButtonIncome = findViewById(R.id.radioButton_income);
        radioButtonPayout = findViewById(R.id.radioButton_payout);
        if(bill.isINCOME()) {
            radioButtonIncome.setChecked(true);
        } else {
            radioButtonPayout.setChecked(true);
        }
    }

    public void initSpinner() {
        spinnerCategory = findViewById(R.id.spinner_category);
        List<String> categories;
        if(bill.isPAYOUT()) {
            categories = new ArrayList<>(Arrays.asList(Bill.payoutCategory));
        } else {
            categories = new ArrayList<String>(Arrays.asList(Bill.incomeCategory));
        }
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);
        spinnerCategory.setOnItemSelectedListener(this);
        for(int i=0;i<spinnerAdapter.getCount();++i) {
            if(spinnerAdapter.getItem(i).equals(bill.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    public void refreshSpinner() {
        List<String> categories;
        if(bill.isPAYOUT()) {
            categories = new ArrayList<>(Arrays.asList(Bill.payoutCategory));
        } else {
            categories = new ArrayList<String>(Arrays.asList(Bill.incomeCategory));
        }
        spinnerAdapter.clear();
        spinnerAdapter.addAll(categories);
        spinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] categories;
        if(bill.isINCOME()) {
            categories = Bill.incomeCategory;
        } else {
            categories = Bill.payoutCategory;
        }
        bill.setCategory(categories[position]);
    }

    public void initToolBar() {
        toolbar = findViewById(R.id.toolbar_edit);
        if(isEditing) {
            toolbar.setTitle("编辑");
        } else {
            toolbar.setTitle("新建");
        }
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
                bill.setCategory("转账");
                refreshSpinner();
                break;
            case R.id.radioButton_payout:
                if (checked)
                    radioButtonIncome.setChecked(false);
                bill.setType(Bill.PAYOUT);
                bill.setCategory("消费");
                refreshSpinner();
                // Ninjas rule
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add_done:
                if(!inputMoneyAmount.getText().toString().equals("")) {
                    float price = Float.parseFloat(inputMoneyAmount.getText().toString());
                    if(bill.isPAYOUT()) price = -price;
                    bill.setPrice(price);
                }
                String remark = inputRemarks.getText().toString();
                bill.setRemark(remark);
                bill.save();
                intent.putExtra("id", bill.getId());
                if(isEditing) intent.putExtra("position", position);
                setResult(RESULT_OK,intent);
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.delete:
                LitePal.delete(Bill.class,bill.getId());
                intent.putExtra("position", position);
                setResult(RESULT_OK,intent);
                Toast.makeText(this,"已删除",Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isEditing==false) {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_bill, menu);
        return true;
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