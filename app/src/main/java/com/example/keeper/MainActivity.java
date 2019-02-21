package com.example.keeper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.litepal.LitePal;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TEST";
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigation;
    private Fragment homeFragment, statusFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LitePal.initialize(this);
        welcome();
        initView(savedInstanceState);
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

    private void initView(Bundle savedInstanceState) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigation = findViewById(R.id.nav_view);
        mNavigation.setCheckedItem(R.id.nav_menu_home);
        mNavigation.setNavigationItemSelectedListener(i->{
            switch (i.getItemId()) {
                case R.id.nav_menu_home:
                    i.setChecked(true);
                    Toast.makeText(this, "haha", Toast.LENGTH_SHORT).show();
            }
            mDrawerLayout.closeDrawers();
            return true;
        });
        if(savedInstanceState == null) {
            if (homeFragment == null) homeFragment = new HomeFragment();
            if (statusFragment == null) statusFragment = new StatusFragment();
            //     getSupportFragmentManager().beginTransaction().

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.home_fragment_container, homeFragment)
                    .add(R.id.home_fragment_container, statusFragment)
                    .hide(statusFragment)
                    .commit();
            mDrawerLayout = findViewById(R.id.drawer_layout);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
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

//    public boolean onNavigationItemSelected(MenuItem item) {
//
//
//        switch (item.getItemId()) {
//            case R.id.home:
//                if (homeFragment == null) {
//                    homeFragment = new HomeFragment();
//                }
//                getSupportActionBar().setTitle("Keeper");
//                getFragmentManager()
//                        .beginTransaction().hide(statusFragment).show(homeFragment).commit();
//                return true;
//            case R.id.showTitle:
//                if (statusFragment == null) {
//                    statusFragment = new StatusFragment();
//                }
//                getSupportActionBar().setTitle("Status");
//                getFragmentManager()
//                        .beginTransaction().hide(homeFragment).show(statusFragment).commit();
//                return true;
//        }
//        return false;
//    }
}
