package com.dima.emmeggi95.jaycaves.me.activities;

import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.dima.emmeggi95.jaycaves.me.R;
import com.dima.emmeggi95.jaycaves.me.UploadFragment;
import com.dima.emmeggi95.jaycaves.me.entities.NetworkChangeReceiver;
import com.dima.emmeggi95.jaycaves.me.entities.adapters.ViewPagerAdapter;
import com.dima.emmeggi95.jaycaves.me.fragments.NewAlbumFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.NewArtistFragment;

import static android.view.View.GONE;

/**
 * New content is inserted in the system through this activity. There are two main fragments: @NewAlbumFragment, where user can insert new albums, and @NewArtistFragment, where user can insert new artists
 * @see NewAlbumFragment
 * @see NewArtistFragment
 */
public class AddContentActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private BottomNavigationView navigationView;
    private View container;
    private Toolbar toolbar;
    MenuItem prevMenuItem;
    private NetworkChangeReceiver networkChangeReceiver = null;
    private IntentFilter intentFilter;

    private boolean backEnabled = true;
    private UploadFragment uploadFragment;

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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setTitle(R.string.add_content_title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        container = findViewById(R.id.container);

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

    @Override
    public void onBackPressed() {
        if(backEnabled){
            finish();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void showUploadFragment(){
        uploadFragment = new UploadFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, uploadFragment);
        ft.commit();
        backEnabled = false;
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        container.setAlpha(0f);
        container.setVisibility(View.VISIBLE);
        container.animate().alpha(1f).setDuration(250);
        viewPager.setVisibility(GONE);
        navigationView.setVisibility(GONE);
    }

    public void uploadSuccess(String message){
        uploadFragment.success(message);
    }

    public void uploadFailure(String message){
        uploadFragment.failure(message);
    }

    public void goBackToFragment(){
        container.setVisibility(GONE);
        viewPager.setVisibility(View.VISIBLE);
        navigationView.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.add_content_title);
        backEnabled = true;
    }
}
