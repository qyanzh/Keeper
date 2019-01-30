package com.example.keeper;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.litepal.tablemanager.Connector;

public class MainActivity extends AppCompatActivity {


    private Fragment homeFragment,statusFragment;
    FloatingActionButton fab;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(homeFragment==null) {
                        homeFragment = new HomeFragment();
                    }
                    getSupportActionBar().setTitle("Keeper");
                    fab.show();
                    getFragmentManager()
                            .beginTransaction().hide(statusFragment).show(homeFragment).commit();
                    return true;
                case R.id.navigation_status:
                    if(statusFragment==null) {
                        statusFragment = new StatusFragment();
                    }
                    getSupportActionBar().setTitle("Status");
                    fab.hide();
                    getFragmentManager()
                            .beginTransaction().hide(homeFragment).show(statusFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Connector.getDatabase();
        homeFragment = new HomeFragment();
        statusFragment = new StatusFragment();

        getFragmentManager()
                .beginTransaction().add(R.id.home_fragment_holder, homeFragment).add(R.id.home_fragment_holder,statusFragment).hide(statusFragment).commit();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v->{
            Intent intent = new Intent(this, AddBill.class);
            startActivity(intent);
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


}
