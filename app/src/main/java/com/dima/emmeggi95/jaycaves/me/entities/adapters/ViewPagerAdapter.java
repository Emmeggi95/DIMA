package com.dima.emmeggi95.jaycaves.me.entities.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dima.emmeggi95.jaycaves.me.fragments.ChatFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.LoadingFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.NotificationsFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        if(fragments.size()>i){
            return fragments.get(i);
        } else {
            return new LoadingFragment();
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment){
        fragments.add(fragment);
    }
}
