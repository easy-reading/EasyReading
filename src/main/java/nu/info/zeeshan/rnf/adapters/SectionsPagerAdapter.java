package nu.info.zeeshan.rnf.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import nu.info.zeeshan.rnf.FragmentFacebook;
import nu.info.zeeshan.rnf.FragmentNews;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        if(position== 0)
            return new FragmentNews();
        else
            return new FragmentFacebook();
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "NEWS";
            case 1:
                return "FACEBOOK";
        }
        return null;
    }
}
