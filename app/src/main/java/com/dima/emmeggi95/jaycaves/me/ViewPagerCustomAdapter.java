package com.dima.emmeggi95.jaycaves.me;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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
