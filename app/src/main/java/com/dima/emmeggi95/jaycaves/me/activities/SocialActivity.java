package com.dima.emmeggi95.jaycaves.me.activities;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.ViewPagerAdapter;
import com.dima.emmeggi95.jaycaves.me.fragments.ChatFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.NotificationsFragment;

public class SocialActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView navView;
    ViewPager viewPager;
    MenuItem prevMenuItem;

    private final String SOCIAL_FRAGMENT_SELECTED = "social_fragment_selected";

    // Networking elements
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Set toolbar
        toolbar = findViewById(R.id.toolbar_social);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle("");

        // View Pager
        viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new NotificationsFragment());
        viewPagerAdapter.addFragment(new ChatFragment());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    navView.getMenu().getItem(0).setChecked(false);

                navView.getMenu().getItem(i).setChecked(true);
                prevMenuItem = navView.getMenu().getItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // Init fragments
        viewPager.setCurrentItem(0);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_chat:
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };

    // OVERRIDE ON ACTIVITY METHODS

    @Override
    protected void onPause() {
        super.onPause();
        if(this.networkChangeReceiver!=null) {
            unregisterReceiver(this.networkChangeReceiver);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Create an IntentFilter instance.
        intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        // Set broadcast receiver priority.
        intentFilter.setPriority(100);

        // Create a network change broadcast receiver.
        networkChangeReceiver = new NetworkChangeReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(networkChangeReceiver, intentFilter);

    }
}
