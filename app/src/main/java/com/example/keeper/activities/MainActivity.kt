package com.example.keeper.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.keeper.BillItem
import com.example.keeper.R
import com.example.keeper.fragments.BillListFragment
import com.example.keeper.fragments.RecentFragment
import com.example.keeper.fragments.RecentFragment.RECENT_FRAGMENT_TAG
import com.example.keeper.fragments.TimelyFragment
import com.example.keeper.fragments.TimelyFragment.*
import com.example.keeper.mytools.MyBundleHelper
import com.example.keeper.mytools.MyDoubleClickListener
import org.jetbrains.annotations.TestOnly
import org.litepal.LitePal
import java.util.*
import java.util.Calendar.*
import kotlin.collections.HashSet


class MainActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout
    private lateinit var sharedPref: SharedPreferences
    private var mFragments: MutableSet<BillListFragment> = HashSet()
    private var curFragment: BillListFragment? = null
    private var recentFragment: RecentFragment? = null
    private var yearlyFragment: TimelyFragment? = null
    private var monthlyFragment: TimelyFragment? = null
    private var dailyFragment: TimelyFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.TransparentStatusBar)
        setContentView(R.layout.activity_main)
        LitePal.initialize(this)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        if (savedInstanceState != null) {
            supportFragmentManager.run {
                recentFragment = findFragmentByTag(RECENT_FRAGMENT_TAG) as RecentFragment?
                yearlyFragment = findFragmentByTag(YEARLY_FRAGMENT_TAG) as TimelyFragment?
                monthlyFragment = findFragmentByTag(MONTHLY_FRAGMENT_TAG) as TimelyFragment?
                dailyFragment = findFragmentByTag(DAILY_FRAGMENT_TAG) as TimelyFragment?
            }
        }
        welcome()
        initFragment()
        initView()
        supportActionBar?.setTitle(R.string.home)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> deleteDatabase()
        }
        return true
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.delete_all).isVisible =
            sharedPref.getBoolean(PREF_DEV_MODE, false)
        invalidateOptionsMenu()
        return super.onPrepareOptionsMenu(menu)
    }

    private fun welcome() {
        if (sharedPref.getBoolean("isFirstOpen", true)) {
            BillItem().apply {
                price = 0F
                type = BillItem.INCOME
                category = getString(R.string.welcome)
                remark = getString(R.string.clickFabToAdd)
                save()
            }
            sharedPref.edit().putBoolean("isFirstOpen", false).apply()
        }
    }

    private fun initView() {

        val toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            setSupportActionBar(this)
            setOnClickListener(object : MyDoubleClickListener(300) {
                override fun onDoubleClick() {
                    curFragment?.billRecyclerView?.scrollToPosition(0)
                }
            })
        }
        supportActionBar?.run {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        drawer = findViewById(R.id.drawer_layout)

        ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.toggle_open, R.string.toggle_close
        ).run {
            drawer.addDrawerListener(this)
            syncState()
        }

        findViewById<NavigationView>(R.id.nav_view).run {
            setCheckedItem(R.id.nav_menu_home)
            setNavigationItemSelectedListener { i -> onNavigationItemSelected(i) }
        }

        findViewById<FloatingActionButton>(R.id.fab).let {
            if (curFragment != null) {
                it.setOnClickListener { curFragment?.addBill() }
            }
            if (sharedPref.getBoolean(PREF_DEV_MODE, false)) {
                it.setOnLongClickListener {
                    AlertDialog.Builder(this)
                        .setMessage(getString(R.string.randomlyAddForTest))
                        .setPositiveButton(R.string.confirm) { _, _ -> curFragment?.addBillListRandomly() }
                        .setNegativeButton(R.string.cancel) { _, _ -> }
                        .create().show()
                    true
                }
            }
        }
    }


    private fun onNavigationItemSelected(i: MenuItem): Boolean {
        when (i.itemId) {
            R.id.nav_menu_home -> {
                recentFragment?.show()
                supportActionBar?.setTitle(R.string.recent)
            }
            R.id.nav_menu_today -> {
                dailyFragment?.show()
                supportActionBar?.setTitle(R.string.daily)
            }
            R.id.nav_menu_monthly -> {
                monthlyFragment?.show()
                supportActionBar?.setTitle(R.string.monthly)
            }
            R.id.nav_menu_yearly -> {
                yearlyFragment?.show()
                supportActionBar?.setTitle(R.string.yearly)
            }
            R.id.nav_menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        drawer.closeDrawers()
        return true
    }

    private fun initFragment() {
        val c = Calendar.getInstance()
        if (yearlyFragment == null) {
            MyBundleHelper.getDateQueryBundle(c.get(YEAR)).run {
                yearlyFragment = TimelyFragment.newInstance(YEARLY_FRAGMENT_TAG, this)
            }
        }

        if (monthlyFragment == null) {
            MyBundleHelper.getDateQueryBundle(c.get(YEAR), c.get(MONTH) + 1).run {
                monthlyFragment = TimelyFragment.newInstance(MONTHLY_FRAGMENT_TAG, this)
            }
        }

        if (dailyFragment == null) {
            MyBundleHelper.getDateQueryBundle(c.get(YEAR), c.get(MONTH) + 1, c.get(DAY_OF_MONTH))
                .run {
                    dailyFragment = TimelyFragment.newInstance(DAILY_FRAGMENT_TAG, this)
                }
        }

        if (recentFragment == null) {
            recentFragment = RecentFragment.newInstance(RECENT_FRAGMENT_TAG)
        }
        recentFragment?.show()
    }

    private fun BillListFragment.show() {
        if (curFragment != this) {
            supportFragmentManager.beginTransaction().also {
                curFragment?.run { it.hide(this) }
                if (isAdded) {
                    it.show(this)
                } else {
                    it.add(R.id.home_fragment_container, this, TAG)
                    mFragments.add(this)
                }
                it.commitNow()
            }
            curFragment = this
            reloadData()
        }
    }

    @TestOnly
    fun deleteDatabase() {
        AlertDialog.Builder(this).apply {
            setMessage(getString(R.string.confirmDeleteAll))
            setPositiveButton(R.string.confirm) { _, _ ->
                LitePal.deleteAll(BillItem::class.java)
                mFragments.forEach { mFragment ->
                    mFragment.billItemList.clear()
                    mFragment.billAdapter.notifyDataSetChanged()
                }
            }.setNegativeButton(R.string.cancel) { _, _ -> }
            create().show()
        }
    }

    companion object {
        private const val PREF_DEV_MODE = "pref_dev_mode"
    }

}