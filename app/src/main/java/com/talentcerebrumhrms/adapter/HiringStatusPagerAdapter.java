package com.talentcerebrumhrms.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.talentcerebrumhrms.fragment.ClosedInterviewFragment;
import com.talentcerebrumhrms.fragment.OnGoingInterviewFragment;

/**
 * Created by Harshit on 06-Jul-17.
 */

public class HiringStatusPagerAdapter extends FragmentPagerAdapter {
    public HiringStatusPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnGoingInterviewFragment();
            case 1:
                return new ClosedInterviewFragment();
            default:
                return new OnGoingInterviewFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ON GOING INTERVIEWS";
            case 1:
                return "CLOSED INTERVIEWS";
        }
        return null;
    }
}
