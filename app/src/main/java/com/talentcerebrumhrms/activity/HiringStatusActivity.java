package com.talentcerebrumhrms.activity;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.talentcerebrumhrms.adapter.HiringStatusPagerAdapter;
import com.talentcerebrumhrms.R;

/**
 * Created by Harshit on 06-Jul-17.
 */

public class HiringStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiring_status);
        Log.e("HiringStatusActivity", "onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0.0f);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.hiring_status));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HiringStatusPagerAdapter mHiringStatusPagerAdapter = new HiringStatusPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mHiringStatusPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
