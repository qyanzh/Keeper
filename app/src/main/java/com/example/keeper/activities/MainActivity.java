package com.example.keeper.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.keeper.Bill;
import com.example.keeper.R;
import com.example.keeper.fragments.HomeFragment;
import com.example.keeper.fragments.StatusFragment;
import com.example.keeper.mytools.MyDoubleClickListener;

import org.jetbrains.annotations.TestOnly;
import org.litepal.LitePal;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private Fragment currentFragment;
    private HomeFragment homeFragment;
    private StatusFragment statusFragment;
    Group emptyListImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.initialize(this);
        welcome();
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
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        toolbar.setOnClickListener(new MyDoubleClickListener(300) {
            @Override
            public void onDoubleClick() {
                if(currentFragment == homeFragment) {
                    homeFragment.billRecyclerView.scrollToPosition(0);
                }
            }
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView mNavigation = findViewById(R.id.nav_view);
        mNavigation.setCheckedItem(R.id.nav_menu_home);
        mNavigation.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        if (homeFragment == null) homeFragment = new HomeFragment();
        if (statusFragment == null) statusFragment = new StatusFragment();
        getSupportFragmentManager().beginTransaction()
            .add(R.id.home_fragment_container, homeFragment,HomeFragment.TAG)
            .add(R.id.home_fragment_container, statusFragment,StatusFragment.TAG)
            .hide(statusFragment)
            .commit();
        currentFragment = homeFragment;
        emptyListImage = findViewById(R.id.empty_list_image);
        Log.d(TAG, "listImage Main "+emptyListImage.toString());
    }

    private void showFragment(Fragment selectedFragment) {
         if(currentFragment != selectedFragment) {
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .show(selectedFragment)
                    .commit();
        }
        currentFragment=selectedFragment;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
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
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem i) {
        if(emptyListImage.getVisibility()==View.VISIBLE) {
            showFragment(homeFragment);
            mDrawerLayout.closeDrawers();
           return true;
        }
        emptyListImage.requestLayout();
        i.setChecked(true);
        switch (i.getItemId()) {
            case R.id.nav_menu_home:
                showFragment(homeFragment);
                toolbar.setTitle(getString(R.string.app_name));
                break;
            case R.id.nav_menu_today:
                showFragment(statusFragment);
                toolbar.setTitle(getString(R.string.daily));
                break;
            case R.id.nav_menu_monthly:
                showFragment(statusFragment);
                toolbar.setTitle(getString(R.string.monthly));
                break;
            case R.id.nav_menu_yearly:
                showFragment(statusFragment);
                toolbar.setTitle(getString(R.string.yearly));
                break;
        }
        mDrawerLayout.closeDrawers();
        return true;
    }
}