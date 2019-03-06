package com.example.keeper.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.keeper.BillItem
import com.example.keeper.R
import com.example.keeper.mytools.MyDateFormat
import com.example.keeper.pickers.DatePickerFragment
import com.example.keeper.pickers.TimePickerFragment
import org.litepal.LitePal
import java.util.*

class EditActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var billItem: BillItem
    private lateinit var toolbar: Toolbar
    private lateinit var inputMoneyAmount: EditText
    private lateinit var radioGroupType: RadioGroup
    private lateinit var inputRemarks: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var categories: List<String>
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var editButtonDate: Button
    private lateinit var editButtonTime: Button
    private lateinit var action: String
    private var prePosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        intent.let {
            action = it.getStringExtra("action")
            if (action == "add") {
                billItem = BillItem()
            } else if (action == "edit") {
                val id = it.getLongExtra("id", -1)
                prePosition = it.getIntExtra("prePosition", -1)
                billItem = LitePal.find(BillItem::class.java, id)
            }
        }
        initToolBar()
        initAmountEditor()
        initSpinner()
        initRadioButtons()
        initRemarksEditor()
        initTimeBox()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (action == "add") {
            menu.getItem(0).isVisible = false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.add_done -> saveBillItemAndFinish()
            R.id.delete -> showItemSelectedConfirmDialog(
                getString(R.string.confirmDeleteQM),
                "delete"
            )
        }
        return true
    }

    private fun showItemSelectedConfirmDialog(content: String, action: String) {
        AlertDialog.Builder(this).setMessage(content)
            .setPositiveButton(R.string.confirm) { _, _ ->
                if (action == "delete") {
                    deleteBillItemAndFinish()
                } else if (action == "back") {
                    super.onBackPressed()
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create().show()
    }

    override fun onBackPressed() {
        if (action == "add") {
            showItemSelectedConfirmDialog(getString(R.string.giveUpOperationQM), "back")
        } else {
            super.onBackPressed()
        }
    }

    private fun initToolBar() {
        toolbar = findViewById<Toolbar>(R.id.toolbar_edit).apply {
            if (action == "edit") {
                setTitle(R.string.edit)
            } else {
                setTitle(R.string.add)
            }
            setSupportActionBar(this)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAmountEditor() {
        val price = billItem.price
        inputMoneyAmount = findViewById(R.id.editText_amount)
        if (price != 0f) inputMoneyAmount.setText(Math.abs(price).toString())
        if (action == "add") {
            inputMoneyAmount.requestFocus()
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    val manager =
                        inputMoneyAmount.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.showSoftInput(inputMoneyAmount, 0)
                }
            }, 500)
        }
    }

    private fun initSpinner() {
        getCategoriesOfType()
        spinnerAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, categories).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        spinnerCategory = findViewById<Spinner>(R.id.spinner_category).apply {
            adapter = spinnerAdapter
        }
        autoSelectCategory()
    }

    private fun refreshSpinner() {
        getCategoriesOfType()
        spinnerAdapter.run {
            clear()
            addAll(categories)
            notifyDataSetChanged()
        }
        autoSelectCategory()
    }

    private fun getCategoriesOfType() {
        if (billItem.isPayout) {
            categories = ArrayList(Arrays.asList(*resources.getStringArray(R.array.payoutCategory)))
        } else {
            categories = ArrayList(Arrays.asList(*resources.getStringArray(R.array.incomeCategory)))
        }
    }

    private fun autoSelectCategory() {
        var i = 0
        val count = spinnerAdapter.count
        while (i < count) {
            if (billItem.category == spinnerAdapter.getItem(i)) {
                spinnerCategory.setSelection(i)
                break
            }
            ++i
        }
        if (i == count) spinnerCategory.setSelection(0)
    }

    private fun initRadioButtons() {
        radioGroupType = findViewById<RadioGroup>(R.id.radio_group_type).apply {
            if (billItem.isIncome) {
                check(R.id.radioButton_income)
            } else {
                check(R.id.radioButton_payout)
            }
            setOnCheckedChangeListener { _, checkedId ->
                Log.d("test", "checked")
                when (checkedId) {
                    R.id.radioButton_income -> billItem.type = BillItem.INCOME
                    R.id.radioButton_payout -> billItem.type = BillItem.PAYOUT
                }
                refreshSpinner()
            }
        }
    }

    private fun initRemarksEditor() {
        inputRemarks = findViewById<EditText>(R.id.editText_remarks).apply {
            setText(billItem.remark)
        }
    }

    private fun initTimeBox() {
        editButtonDate = findViewById<Button>(R.id.editButton_date).apply {
            text = MyDateFormat.format(billItem.timeMills, false)
            setOnClickListener {
                DatePickerFragment.getInstance(billItem)
                    .show(supportFragmentManager, "datePicker")
            }
        }
        editButtonTime = findViewById<Button>(R.id.editButton_time).apply {
            text = MyDateFormat.timeFormatter.format(billItem.timeMills)
            setOnClickListener {
                TimePickerFragment.getInstance(billItem)
                    .show(supportFragmentManager, "timePicker")
            }
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        Calendar.getInstance().let {
            it.set(year, month, day, billItem.hour, billItem.minute)
            billItem.setTime(it.timeInMillis)
        }
        editButtonDate.text = MyDateFormat.format(billItem.timeMills, false)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        Calendar.getInstance().let {
            it.set(billItem.year, billItem.month - 1, billItem.day, hourOfDay, minute)
            billItem.setTime(it.timeInMillis)
        }
        editButtonTime.text = MyDateFormat.timeFormatter.format(billItem.timeMills)
    }

    private fun saveData() {
        if (inputMoneyAmount.text.toString() != "") {
            var price = java.lang.Float.parseFloat(inputMoneyAmount.text.toString())
            if (billItem.isPayout) price = -price
            billItem.price = price
        }
        val remark = inputRemarks.text.toString()
        billItem.remark = remark
        val category = spinnerCategory.selectedItem.toString()
        billItem.category = category
    }

    private fun saveBillItemAndFinish() {
        saveData()
        billItem.save()
        Intent().let {
            it.putExtra("id", billItem.id)
            if (action == "add") {
                it.putExtra("action", "add")
            } else if (action == "edit") {
                it.putExtra("action", "edit")
                it.putExtra("prePosition", prePosition)
            }
            setResult(Activity.RESULT_OK, it)
        }
        finish()
    }

    private fun deleteBillItemAndFinish() {
        LitePal.delete(BillItem::class.java, billItem.id)
        Intent().apply {
            putExtra("action", "delete")
            putExtra("prePosition", prePosition)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

}