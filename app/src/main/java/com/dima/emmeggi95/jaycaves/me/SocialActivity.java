package com.dima.emmeggi95.jaycaves.me;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class SocialActivity extends AppCompatActivity {

    Toolbar toolbar;
    Fragment notificationsFragment;
    Fragment chatFragment;
    BottomNavigationView navView;

    private int fragmentSelected;
    private final String SOCIAL_FRAGMENT_SELECTED = "social_fragment_selected";

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

        // Init fragments
        notificationsFragment = new NotificationsFragment();
        chatFragment = new ChatFragment();
        if(savedInstanceState != null){
            navView.setSelectedItemId(fragmentSelected);
        } else {
            setFragment(notificationsFragment);
            fragmentSelected = navView.getSelectedItemId();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_notifications:
                    setFragment(notificationsFragment);
                    fragmentSelected = navView.getSelectedItemId();
                    return true;
                case R.id.navigation_chat:
                    setFragment(chatFragment);
                    fragmentSelected = navView.getSelectedItemId();
                    return true;
            }
            return false;
        }
    };

    private void setFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_social, fragment);
        ft.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SOCIAL_FRAGMENT_SELECTED, fragmentSelected);
        super.onSaveInstanceState(outState);
    }
}
