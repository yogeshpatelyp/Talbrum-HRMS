package com.talentcerebrumhrms.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.talentcerebrumhrms.fragment.LeaveBalanceFragment;
import com.talentcerebrumhrms.fragment.List_Holidays_Fragment;

/**
 * Created by saransh on 15-10-2016.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LeaveBalanceFragment();
            case 1:
                return new List_Holidays_Fragment();
            default:
                return new LeaveBalanceFragment();
        }
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
                return "LEAVE BALANCE";
            case 1:
                return "LIST OF HOLIDAYS";

        }
        return null;
    }
}