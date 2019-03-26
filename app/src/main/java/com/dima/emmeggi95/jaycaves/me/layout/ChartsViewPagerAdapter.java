package com.dima.emmeggi95.jaycaves.me.layout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dima.emmeggi95.jaycaves.me.ChartAllTimeFragment;
import com.dima.emmeggi95.jaycaves.me.ChartGenreFragment;
import com.dima.emmeggi95.jaycaves.me.ChartYearFragment;
import com.dima.emmeggi95.jaycaves.me.LoadingFragment;

public class ChartsViewPagerAdapter extends FragmentStatePagerAdapter {

    public ChartsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0:
                return new ChartAllTimeFragment();
            case 1:
                return new ChartGenreFragment();
            case 2:
                return new ChartYearFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
