package com.dima.emmeggi95.jaycaves.me;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toolbar;



public class AddContentActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_content);
        viewPager= findViewById(R.id.pager);
        adapter= new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        setTitle(R.string.add_content_title);

        // setSupportActionBar(toolbar);
        TabLayout tabLayout= findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.album));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.artist));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





    }
}
