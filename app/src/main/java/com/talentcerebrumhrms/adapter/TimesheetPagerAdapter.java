package com.talentcerebrumhrms.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.talentcerebrumhrms.fragment.ApprovedTimesheetFragment;
import com.talentcerebrumhrms.fragment.RejectedTimesheetFragment;

/**
 * Created by Harshit on 15-Jun-17.
 */

public class TimesheetPagerAdapter extends FragmentPagerAdapter {
    public TimesheetPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ApprovedTimesheetFragment();
            case 1:
                return new RejectedTimesheetFragment();
            default:
                return new ApprovedTimesheetFragment();
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
                return "APPROVED";
            case 1:
                return "REJECTED";
        }
        return null;
    }
}
