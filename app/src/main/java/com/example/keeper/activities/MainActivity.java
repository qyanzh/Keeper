package com.example.keeper.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.keeper.BillItem;
import com.example.keeper.R;
import com.example.keeper.fragments.BillListFragment;
import com.example.keeper.fragments.RecentFragment;
import com.example.keeper.fragments.TimelyFragment;
import com.example.keeper.mytools.MyBundleHelper;
import com.example.keeper.mytools.MyDoubleClickListener;

import org.jetbrains.annotations.TestOnly;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.keeper.fragments.RecentFragment.RECENT_FRAGMENT_TAG;
import static com.example.keeper.fragments.TimelyFragment.DAILY_FRAGMENT_TAG;
import static com.example.keeper.fragments.TimelyFragment.MONTHLY_FRAGMENT_TAG;
import static com.example.keeper.fragments.TimelyFragment.YEARLY_FRAGMENT_TAG;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final String PREF_DEV_MODE = "pref_dev_mode";


    Toolbar toolbar;
    NavigationView mNavigation;
    private DrawerLayout mDrawerLayout;
    private BillListFragment currentFragment;
    private RecentFragment recentFragment;
    TimelyFragment yearlyFragment;
    TimelyFragment monthlyFragment;
    TimelyFragment dailyFragment;
    List<BillListFragment> mFragments = new ArrayList<>();
    public FloatingActionButton fab;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        setTheme(R.style.TransparentStatusBar);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.initialize(this);
        welcome();
        if (savedInstanceState != null) {
            recentFragment = (RecentFragment) getSupportFragmentManager().findFragmentByTag(RECENT_FRAGMENT_TAG);
            yearlyFragment = (TimelyFragment) getSupportFragmentManager().findFragmentByTag(YEARLY_FRAGMENT_TAG);
            monthlyFragment = (TimelyFragment) getSupportFragmentManager().findFragmentByTag(MONTHLY_FRAGMENT_TAG);
            dailyFragment = (TimelyFragment) getSupportFragmentManager().findFragmentByTag(DAILY_FRAGMENT_TAG);
        }
        initView();
        initFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setFabClick();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.delete_all:
                deleteDatabase();
                break;
        }
        return true;
    }


    public void welcome() {
        SharedPreferences sp = getSharedPreferences("isFirstOpen", MODE_PRIVATE);
        boolean isFirstOpen = sp.getBoolean("isFirstOpen", true);
        if (isFirstOpen) {
            BillItem welcome = new BillItem();
            welcome.setPrice(0);
            welcome.setType(BillItem.INCOME);
            welcome.setCategory(getString(R.string.welcome));
            welcome.setRemark(getString(R.string.clickFabToAdd));
            welcome.save();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstOpen", false);
            editor.apply();
        }
    }

    private void initView() {
        initToolBar();
        fab = findViewById(R.id.fab);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        initNavigation();

    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.tb_ic_menu_white_24dp);
        }
        toolbar.setOnClickListener(new MyDoubleClickListener(300) {
            @Override
            public void onDoubleClick() {
                currentFragment.billRecyclerView.scrollToPosition(0);
                fab.show();
            }
        });
    }

    private void initNavigation() {
        mNavigation = findViewById(R.id.nav_view);
        mNavigation.setCheckedItem(R.id.nav_menu_home);
        mNavigation.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    public boolean onNavigationItemSelected(MenuItem i) {
        switch (i.getItemId()) {
            case R.id.nav_menu_home:
                showFragment(recentFragment);
                toolbar.setTitle(R.string.home);
                break;
            case R.id.nav_menu_today:
                showFragment(dailyFragment);
                toolbar.setTitle(R.string.daily);
                break;
            case R.id.nav_menu_monthly:
                showFragment(monthlyFragment);
                toolbar.setTitle(R.string.monthly);
                break;
            case R.id.nav_menu_yearly:
                showFragment(yearlyFragment);
                toolbar.setTitle(R.string.yearly);
                break;
            case R.id.nav_menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        setFabClick();
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void initFragment() {
        Calendar c = Calendar.getInstance();
        if (yearlyFragment == null) {
            Bundle bundle = MyBundleHelper.getDateQueryBundle(c.get(YEAR));
            yearlyFragment = TimelyFragment.newInstance(YEARLY_FRAGMENT_TAG, bundle);
        }

        if (monthlyFragment == null) {
            Bundle bundle = MyBundleHelper.getDateQueryBundle(c.get(YEAR), c.get(MONTH) + 1);
            monthlyFragment = TimelyFragment.newInstance(MONTHLY_FRAGMENT_TAG, bundle);
        }
        if (dailyFragment == null) {
            Bundle bundle = MyBundleHelper.getDateQueryBundle(c.get(YEAR), c.get(MONTH) + 1, c.get(DAY_OF_MONTH));
            dailyFragment = TimelyFragment.newInstance(DAILY_FRAGMENT_TAG, bundle);
        }

        if (recentFragment == null) {
            recentFragment = RecentFragment.newInstance(RECENT_FRAGMENT_TAG);
        }
        showFragment(recentFragment);
        getSupportActionBar().setTitle(R.string.home);
    }


    private void showFragment(BillListFragment selectedFragment) {
        if (currentFragment != selectedFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (currentFragment != null) {
                ft.hide(currentFragment);
            }
            if (!selectedFragment.isAdded()) {
                ft.add(R.id.home_fragment_container, selectedFragment, selectedFragment.TAG);
                mFragments.add(selectedFragment);
            }
            ft.show(selectedFragment);
            ft.commitNow();
            Log.d(TAG, "showFragment: selected");
            selectedFragment.reloadData();
            currentFragment = selectedFragment;
        }
    }

    public void setFabClick() {
        if (currentFragment != null) {
            fab.setOnClickListener(v -> currentFragment.addBill());
            if (sharedPreferences.getBoolean(PREF_DEV_MODE, false)) {
                fab.setOnLongClickListener(v -> {
                    AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.randomlyAddForTest))
                            .setPositiveButton(R.string.confirm, (dialog, id) -> currentFragment.addBillListRandomly())
                            .setNegativeButton(R.string.cancel, (dialog, id) -> {
                            });
                    builder.create().show();
                    return true;
                });
            } else {
                fab.setOnLongClickListener(null);
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (sharedPreferences.getBoolean(PREF_DEV_MODE, false)) {
            menu.findItem(R.id.delete_all).setVisible(true);
        } else {
            menu.findItem(R.id.delete_all).setVisible(false);
        }
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @TestOnly
    public void deleteDatabase() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmDeleteAll))
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    LitePal.deleteAll(BillItem.class);
                    mFragments.forEach(mFragment -> {
                        mFragment.billItemList.clear();
                        mFragment.billAdapter.notifyDataSetChanged();
                    });
                    fab.show();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                });
        builder.create().show();
    }

}