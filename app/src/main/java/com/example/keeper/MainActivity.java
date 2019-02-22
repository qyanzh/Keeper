package com.example.keeper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import com.example.keeper.HomeFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.litepal.LitePal;

import java.util.function.IntBinaryOperator;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigation;
    private Fragment homeFragment, statusFragment,currentFragment;
    static Group emptyListImage;



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
            welcome.setCategory("欢迎!");
            welcome.setRemark("点击加号创建一条新记录");
            welcome.save();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("isFirstOpen", false);
            editor.commit();
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigation = findViewById(R.id.nav_view);
        mNavigation.setCheckedItem(R.id.nav_menu_home);
        mNavigation.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        if (homeFragment == null) homeFragment = new HomeFragment();
        if (statusFragment == null) statusFragment = new StatusFragment();
        getSupportFragmentManager().beginTransaction()
            .add(R.id.home_fragment_container, homeFragment,"homefragment")
            .add(R.id.home_fragment_container, statusFragment,"statusfragment")
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
            case R.id.query:
                //TODO: select * from databases.
                break;
        }
        return true;
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
                toolbar.setTitle("记账");
                break;
            case R.id.nav_menu_today:
                showFragment(statusFragment);
                toolbar.setTitle("今天");
                break;
            case R.id.nav_menu_monthly:
                showFragment(statusFragment);
                toolbar.setTitle("本月");
                break;
            case R.id.nav_menu_yearly:
                showFragment(statusFragment);
                toolbar.setTitle("年度");
                break;
        }
        mDrawerLayout.closeDrawers();
        return true;
    }
}