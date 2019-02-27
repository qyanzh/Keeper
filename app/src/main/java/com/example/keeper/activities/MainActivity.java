package com.example.keeper.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.keeper.BillItem;
import com.example.keeper.R;
import com.example.keeper.fragments.BillListFragment;
import com.example.keeper.fragments.HomeFragment;
import com.example.keeper.fragments.SumBillListFragment;
import com.example.keeper.mytools.MyBundleHelper;
import com.example.keeper.mytools.MyDoubleClickListener;

import org.jetbrains.annotations.TestOnly;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.keeper.fragments.SumBillListFragment.DAILY_FRAGMENT_TAG;
import static com.example.keeper.fragments.SumBillListFragment.HOME_FRAGMENT_TAG;
import static com.example.keeper.fragments.SumBillListFragment.MONTHLY_FRAGMENT_TAG;
import static com.example.keeper.fragments.SumBillListFragment.YEARLY_FRAGMENT_TAG;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";


    Toolbar toolbar;
    NavigationView mNavigation;
    private DrawerLayout mDrawerLayout;
    private BillListFragment currentFragment;
    private HomeFragment homeFragment;
    SumBillListFragment yearlyFragment;
    SumBillListFragment monthlyFragment;
    SumBillListFragment dailyFragment;
    List<BillListFragment> mFragments = new ArrayList<>();
    public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.initialize(this);
        welcome();
        if (savedInstanceState != null) {
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG);
            yearlyFragment = (SumBillListFragment) getSupportFragmentManager().findFragmentByTag(YEARLY_FRAGMENT_TAG);
            monthlyFragment = (SumBillListFragment) getSupportFragmentManager().findFragmentByTag(MONTHLY_FRAGMENT_TAG);
            dailyFragment = (SumBillListFragment) getSupportFragmentManager().findFragmentByTag(DAILY_FRAGMENT_TAG);
        }
        initView();
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
        initFragment();
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
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
                showFragment(homeFragment);
                toolbar.setTitle(R.string.app_name);
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
        }
        setFabClick();
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void initFragment() {
        if (yearlyFragment == null) {
            yearlyFragment = new SumBillListFragment();
            Calendar c = Calendar.getInstance();
            Bundle bundle = MyBundleHelper.getDateQueryBundle(MyBundleHelper.YEAR_MODE, c.get(YEAR));
            bundle.putString("TAG", YEARLY_FRAGMENT_TAG);
            yearlyFragment.setArguments(bundle);
            mFragments.add(yearlyFragment);
        }

        if (monthlyFragment == null) {
            monthlyFragment = new SumBillListFragment();
            Calendar c = Calendar.getInstance();
            Bundle bundle = MyBundleHelper.getDateQueryBundle(MyBundleHelper.MONTH_MODE, c.get(YEAR), c.get(MONTH) + 1);
            bundle.putString("TAG", MONTHLY_FRAGMENT_TAG);
            monthlyFragment.setArguments(bundle);
            mFragments.add(monthlyFragment);
        }
        if (dailyFragment == null) {
            dailyFragment = new SumBillListFragment();
            Calendar c = Calendar.getInstance();
            Bundle bundle = MyBundleHelper.getDateQueryBundle(MyBundleHelper.DAY_MODE, c.get(YEAR), c.get(MONTH) + 1, c.get(DAY_OF_MONTH));
            bundle.putString("TAG", DAILY_FRAGMENT_TAG);
            dailyFragment.setArguments(bundle);
            mFragments.add(dailyFragment);
        }

        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("TAG", "HomeFragment");
            homeFragment.setArguments(bundle);
            mFragments.add(homeFragment);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFragments.forEach(mFragment -> {
            if (!mFragment.isAdded()) {
                ft.add(R.id.home_fragment_container, mFragment, mFragment.TAG);
            }
        });
        mFragments.forEach(ft::hide);
        ft.show(homeFragment).commit();
        currentFragment = homeFragment;
    }


    private void showFragment(BillListFragment selectedFragment) {
        if (currentFragment != selectedFragment) {
            selectedFragment.reloadData();
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .show(selectedFragment)
                    .commit();
        }
        currentFragment = selectedFragment;
    }

    public void setFabClick() {
        fab.setOnClickListener(v -> currentFragment.startEditActivityForAdd());
        fab.setOnLongClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.randomlyAddForTest))
                    .setPositiveButton(R.string.confirm, (dialog, id) -> currentFragment.addBillListRandomly())
                    .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    });
            builder.create().show();
            return true;
        });
        currentFragment.billRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @TestOnly
    public void deleteDatabase() {
        android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmDeleteAll))
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    LitePal.deleteAll(BillItem.class);
                    mFragments.forEach(mFragment -> {
                        mFragment.billItemList.clear();
                        mFragment.adapter.notifyDataSetChanged();
                        mFragment.refreshAmountOfMoney();
                        mFragment.checkListEmpty();
                    });
                    fab.show();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                });
        builder.create().show();
    }

}