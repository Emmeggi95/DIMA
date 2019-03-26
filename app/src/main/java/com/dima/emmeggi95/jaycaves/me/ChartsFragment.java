package com.dima.emmeggi95.jaycaves.me;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dima.emmeggi95.jaycaves.me.layout.ChartsViewPagerAdapter;

public class ChartsFragment extends Fragment {

    ViewPager viewPager;
    ChartsViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.charts_view_pager);
        adapter = new ChartsViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.charts_tab_layout);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                System.out.println("Tab premuto");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}
