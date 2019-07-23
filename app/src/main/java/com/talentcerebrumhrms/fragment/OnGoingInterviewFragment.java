package com.talentcerebrumhrms.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.talentcerebrumhrms.adapter.OnGoingInterviewAdapter;
import com.talentcerebrumhrms.R;

import static com.talentcerebrumhrms.utils.AppController.timesheet_approved_array;

/**
 * Created by Harshit on 06-Jul-17.
 */

public class OnGoingInterviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootview;
    TextView emptyView;
    ListView on_going_interview_view;
    SwipeRefreshLayout on_going_interview_swipe;
    private SwipeRefreshLayout mEmptyViewContainer;
    private OnGoingInterviewAdapter onGoingInterviewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_on_going_interview, null);

        emptyView = (TextView) rootview.findViewById(R.id.emptyView);
        emptyView.setText(getResources().getString(R.string.no_on_going_interview));
        onGoingInterviewAdapter = new OnGoingInterviewAdapter(getActivity(), timesheet_approved_array, getResources().getString(R.string.approved));
        on_going_interview_swipe = (SwipeRefreshLayout) rootview.findViewById(R.id.on_going_interview_swipe);
        mEmptyViewContainer = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout_emptyView);
        on_going_interview_view = (ListView) rootview.findViewById(R.id.on_going_interview_view);

        on_going_interview_view.setEmptyView(mEmptyViewContainer);
        on_going_interview_view.setAdapter(onGoingInterviewAdapter);
        getActivity().invalidateOptionsMenu();
        on_going_interview_swipe.setOnRefreshListener(this);
        on_going_interview_swipe.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        mEmptyViewContainer.setOnRefreshListener(this);
        mEmptyViewContainer.setColorSchemeColors(

                ContextCompat.getColor(getActivity(), R.color.colorAccent)
        );

        on_going_interview_swipe.post(new Runnable() {
                                          @Override
                                          public void run() {
                                              on_going_interview_swipe.setRefreshing(true);
                                              onGoingInterviewApiCall();
                                          }
                                      }
        );

        return rootview;
    }

    @Override
    public void onRefresh() {
        Log.e("on refresh", "ApprovedTimesheetFragment");
        on_going_interview_swipe.setRefreshing(true);
        mEmptyViewContainer.setRefreshing(false);
        onGoingInterviewApiCall();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("on resume", "ApprovedTimesheetFragment");
        getActivity().invalidateOptionsMenu();
    }

    private void onGoingInterviewApiCall() {


    }
}
