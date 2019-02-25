package com.example.keeper.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.keeper.Bill;
import com.example.keeper.R;
import com.example.keeper.fragments.DateQueryFragment;
import com.example.keeper.fragments.HomeFragment;
import com.example.keeper.fragments.QueryFragment;
import com.example.keeper.mytools.MyDoubleClickListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String HOME_FRAGMENT_TAG = "HomeFragment";
    public static final String YEARLY_FRAGMENT_TAG = "YearlyFragment";
    public static final String MONTHLY_FRAGMENT_TAG = "MonthlyFragment";
    public static final String DAILY_FRAGMENT_TAG = "DailyFragment";

    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private Fragment currentFragment;
    private HomeFragment homeFragment;
    DateQueryFragment yearlyFragment;
    DateQueryFragment monthlyFragment;
    DateQueryFragment dailyFragment;
    List<QueryFragment> queryFragments = new ArrayList<>();
    Group emptyListImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.initialize(this);
        welcome();
        if (savedInstanceState != null) {
            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(HOME_FRAGMENT_TAG);
            yearlyFragment = (DateQueryFragment) getSupportFragmentManager().findFragmentByTag(YEARLY_FRAGMENT_TAG);
            monthlyFragment = (DateQueryFragment) getSupportFragmentManager().findFragmentByTag(MONTHLY_FRAGMENT_TAG);
            dailyFragment = (DateQueryFragment) getSupportFragmentManager().findFragmentByTag(DAILY_FRAGMENT_TAG);
        }
        initView();
    }

    public void welcome() {
        SharedPreferences sp = getSharedPreferences("isFirstOpen", MODE_PRIVATE);
        boolean isFirstOpen = sp.getBoolean("isFirstOpen", true);
        if (isFirstOpen) {
            Bill welcome = new Bill();
            welcome.setPrice(0);
            welcome.setType(Bill.INCOME);
            welcome.setCategory(getString(R.string.welcome));
            welcome.setRemark(getString(R.string.clickFabToAdd));
            welcome.save();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstOpen", false);
            editor.apply();
        }
    }

    private void initView() {
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
                if (currentFragment == homeFragment) {
                    homeFragment.billRecyclerView.scrollToPosition(0);
                }
            }
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);
        emptyListImage = findViewById(R.id.empty_list_image);
        NavigationView mNavigation = findViewById(R.id.nav_view);
        mNavigation.setCheckedItem(R.id.nav_menu_home);
        mNavigation.setNavigationItemSelectedListener(this::onNavigationItemSelected);


        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }

        if (yearlyFragment == null) {
            yearlyFragment = new DateQueryFragment();
            Calendar c = Calendar.getInstance();
            Bundle bundle = getDateQueryBundle(new String[]{"year"}, c.get(Calendar.YEAR));
            bundle.putString("TAG", YEARLY_FRAGMENT_TAG);
            yearlyFragment.setArguments(bundle);
            queryFragments.add(yearlyFragment);
        }

        if (monthlyFragment == null) {
            monthlyFragment = new DateQueryFragment();
            Calendar c = Calendar.getInstance();
            Bundle bundle = getDateQueryBundle(new String[]{"year", "month"}, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
            bundle.putString("TAG", MONTHLY_FRAGMENT_TAG);
            monthlyFragment.setArguments(bundle);
            queryFragments.add(monthlyFragment);

        }
        if (dailyFragment == null) {
            dailyFragment = new DateQueryFragment();
            Calendar c = Calendar.getInstance();
            Bundle bundle = getDateQueryBundle(new String[]{"year", "month", "day"}, c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
            bundle.putString("TAG", DAILY_FRAGMENT_TAG);
            dailyFragment.setArguments(bundle);
            queryFragments.add(dailyFragment);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!homeFragment.isAdded()) {
            ft.add(R.id.home_fragment_container, homeFragment, HomeFragment.TAG);
        }
        queryFragments.forEach(queryFragment -> {
            if (!queryFragment.isAdded()) {
                ft.add(R.id.home_fragment_container, queryFragment, queryFragment.TAG);
            }
        });
        queryFragments.forEach(ft::hide);
        ft.show(homeFragment).commitNow();
        currentFragment = homeFragment;
    }

    @NotNull
    private Bundle getDateQueryBundle(String[] queryConditions, int... dateArguments) {
        int length = dateArguments.length;
        String[] queryArguments = new String[length];
        for (int i = 0; i < length; ++i) {
            queryArguments[i] = String.valueOf(dateArguments[i]);
        }
        return getDateQueryBundle(queryConditions, queryArguments);
    }

    @NotNull
    private Bundle getDateQueryBundle(String[] queryConditions, String[] queryArguments) {
        Bundle bundle = new Bundle();
        bundle.putStringArray("queryConditions", queryConditions);
        bundle.putStringArray("queryArguments", queryArguments);
        return bundle;
    }

    private void showFragment(Fragment selectedFragment) {
        if (currentFragment != selectedFragment) {
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .show(selectedFragment)
                    .commit();
        }
        currentFragment = selectedFragment;
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
                //TODO: select * from databases.
                deleteDatabase();
                break;
        }
        return true;
    }

    @TestOnly
    public void deleteDatabase() {
        LitePal.deleteAll(Bill.class);
        homeFragment.billList.clear();
        homeFragment.adapter.notifyDataSetChanged();
        homeFragment.checkListEmpty();
        homeFragment.fab.show();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem i) {
        emptyListImage.requestLayout();
        i.setChecked(true);
        switch (i.getItemId()) {
            case R.id.nav_menu_home:
                showFragment(homeFragment);
                toolbar.setTitle(R.string.app_name);
                break;
            case R.id.nav_menu_today:
                dailyFragment.reloadData();
                showFragment(dailyFragment);
                toolbar.setTitle(R.string.daily);
                break;
            case R.id.nav_menu_monthly:
                monthlyFragment.reloadData();
                monthlyFragment.adapter.notifyDataSetChanged();
                showFragment(monthlyFragment);
                toolbar.setTitle(R.string.monthly);
                break;
            case R.id.nav_menu_yearly:
                yearlyFragment.reloadData();
                showFragment(yearlyFragment);
                toolbar.setTitle(R.string.yearly);
                break;
        }
        mDrawerLayout.closeDrawers();
        return true;
    }
}