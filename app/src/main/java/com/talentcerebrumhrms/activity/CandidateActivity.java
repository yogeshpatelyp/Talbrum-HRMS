package com.talentcerebrumhrms.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.talentcerebrumhrms.adapter.CandidateAdapter;
import com.talentcerebrumhrms.R;

import static com.talentcerebrumhrms.utils.AppController.candidate_array;

/**
 * Created by Harshit on 10-Jul-17.
 */

public class CandidateActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ListView candidate_listview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout mEmptyViewContainer;
    CandidateAdapter candidateAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);
        Log.e("CandidateActivity", "onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.candidates));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        candidate_listview = (ListView) findViewById(R.id.candidate_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.candidate_swipe_refresh_layout);
        mEmptyViewContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_emptyView);
        candidateAdapter = new CandidateAdapter(this, candidate_array);
        candidate_listview.setEmptyView(mEmptyViewContainer);
        candidate_listview.setAdapter(candidateAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(

                ContextCompat.getColor(this, R.color.colorAccent)
        );

        mEmptyViewContainer.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(this, R.color.colorAccent)
        );
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        //    mEmptyViewContainer.setRefreshing(false);
                                        //getCandidateListApiCall();
                                        //fetch data
                                    }
                                }
        );
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

    @Override
    public void onRefresh() {
        Log.e("swiping", "");
        swipeRefreshLayout.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        refreshContent();
    }

    private void refreshContent() {
        //   new Handler().postDelayed(new Runnable() {
        //     @Override
        //   public void run() {
        swipeRefreshLayout.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        //getCandidateListApiCall();
        //    }
        //   }, 3000);
    }
}
