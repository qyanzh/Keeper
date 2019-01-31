package com.example.keeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AddBillActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Toolbar toolbar;
    EditText inputMoneyAmount;
    RadioButton radioButtonPayout;
    RadioButton radioButtonIncome;
    EditText inputRemarks;
    Spinner spinnerCategory;
    Button buttonChooseDate;
    Button buttonChooseTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bill);

        initToolBar();

        initRadioButtons();


        initSpinner();

    }

    private void initRadioButtons() {
        radioButtonIncome = findViewById(R.id.radioButton_income);
        radioButtonPayout = findViewById(R.id.radioButton_payout);
        radioButtonPayout.setChecked(true);
    }

    public void initSpinner() {
        spinnerCategory = findViewById(R.id.spinner_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, Bill.payoutCategory);
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

        switch(view.getId()) {
            case R.id.radioButton_income:
                if (checked)
                    radioButtonPayout.setChecked(false);
                    // Pirates are the best
                    break;
            case R.id.radioButton_payout:
                if (checked)
                    radioButtonIncome.setChecked(false);
                    // Ninjas rule
                    break;
        }
        Toast.makeText(this, ""+((RadioButton) view).getText()+"is checked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            case R.id.add_done:
                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
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
        String s = Bill.payoutCategory[position];
        Toast.makeText(this, Bill.payoutCategory[position], Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "canceld", Toast.LENGTH_SHORT).show();
    }


}
