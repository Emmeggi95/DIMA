package com.dima.emmeggi95.jaycaves.me.activities;

import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.ViewPagerAdapter;
import com.dima.emmeggi95.jaycaves.me.fragments.NewAlbumFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.NewArtistFragment;

/**
 * New content is inserted in the system through this activity. There are two main fragments: @NewAlbumFragment, where user can insert new albums, and @NewArtistFragment, where user can insert new artists
 * @see NewAlbumFragment
 * @see NewArtistFragment
 */
public class AddContentActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private BottomNavigationView navigationView;
    MenuItem prevMenuItem;
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);

        // ViewPager
        viewPager = findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewAlbumFragment());
        adapter.addFragment(new NewArtistFragment());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    navigationView.getMenu().getItem(0).setChecked(false);

                navigationView.getMenu().getItem(i).setChecked(true);
                prevMenuItem = navigationView.getMenu().getItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // Navigation view
        navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_add_album:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_add_artist:
                        viewPager.setCurrentItem(1);
                        return true;
                }
                return false;
            }
        });

        // Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle(R.string.add_content_title);


    }

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

    public ViewPager getViewPager() {
        return viewPager;
    }
}
