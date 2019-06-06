package com.dima.emmeggi95.jaycaves.me.entities.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dima.emmeggi95.jaycaves.me.activities.AddContentActivity;
import com.dima.emmeggi95.jaycaves.me.fragments.NewAlbumFragment;
import com.dima.emmeggi95.jaycaves.me.fragments.NewArtistFragment;

/**
 * Custom Adapter to allow swiping of tabs in the @AddContentActivity
 * @see AddContentActivity
 */
public class ViewPagerCustomAdapter extends FragmentStatePagerAdapter {

    public ViewPagerCustomAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new NewAlbumFragment();
        } else {
            return new NewArtistFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
