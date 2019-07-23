package com.talentcerebrumhrms.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.talentcerebrumhrms.activity.HiringStatusActivity;
import com.talentcerebrumhrms.activity.VacancyActivity;
import com.talentcerebrumhrms.R;

/**
 * Created by Harshit on 06-Jul-17.
 */

public class RecruitmentFragment extends Fragment implements View.OnClickListener {
    View rootview;
    Button new_vacancies, hiring_status, schedule, assessment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_recruitment, null);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0.0f);
        }

        getActivity().invalidateOptionsMenu();
        new_vacancies = (Button) rootview.findViewById(R.id.new_vacancies);
        hiring_status = (Button) rootview.findViewById(R.id.hiring_status);
        schedule = (Button) rootview.findViewById(R.id.schedule);
        assessment = (Button) rootview.findViewById(R.id.assessment);
        new_vacancies.setOnClickListener(this);
        hiring_status.setOnClickListener(this);
        schedule.setOnClickListener(this);
        assessment.setOnClickListener(this);

        return rootview;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.new_vacancies:
                intent = new Intent(getActivity(), VacancyActivity.class);
                startActivity(intent);
                break;
            case R.id.hiring_status:
                intent = new Intent(getActivity(), HiringStatusActivity.class);
                startActivity(intent);
                break;
            case R.id.schedule:

                break;
            case R.id.assessment:

                break;
        }
    }
}
