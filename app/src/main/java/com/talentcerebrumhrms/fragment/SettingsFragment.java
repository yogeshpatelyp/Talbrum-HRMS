package com.talentcerebrumhrms.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.NotificationReceiver;

import java.util.Calendar;

import static com.talentcerebrumhrms.utils.AppController.holidays;
import static com.talentcerebrumhrms.utils.Utility.dateToMilisec2;

/**
 * Created by Harshit on 07-Jun-17.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    CheckBoxPreference enable_notifications;
    ListPreference notification_days;
    Preference privacy_policy, terms_conditions;

    AlarmManager alarmMgr;
    PendingIntent alarmIntent;

    int daysValue;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.settings);

        alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        enable_notifications = (CheckBoxPreference) getPreferenceManager().findPreference("enable_notifications");
        notification_days = (ListPreference) getPreferenceManager().findPreference("notification_days");
        privacy_policy = getPreferenceManager().findPreference("privacy_policy");
        terms_conditions = getPreferenceManager().findPreference("terms_conditions");

        if (!enable_notifications.isChecked())
            notification_days.setEnabled(false);

        enable_notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Answers.getInstance().logCustom(new CustomEvent("Holiday Notifications Clicked"));
                if (newValue.toString().equalsIgnoreCase("true")) {

                    notification_days.setEnabled(true);
                    daysValue = Integer.parseInt(notification_days.getValue());
                    setNoifications();

                } else {
                    if (alarmIntent != null)
                        alarmMgr.cancel(alarmIntent);
                    notification_days.setEnabled(false);
                }
                return true;
            }
        });

        notification_days.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                Answers.getInstance().logCustom(new CustomEvent("Holiday Notification Days Set"));
                if (alarmIntent != null)
                    alarmMgr.cancel(alarmIntent);
                String val = String.valueOf(newValue);
                daysValue = Integer.parseInt(val);
                setNoifications();
                return true;
            }
        });


        privacy_policy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Answers.getInstance().logCustom(new CustomEvent("Privacy Policy Clicked"));
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new PrivacyPolicyFragment(), "settings").addToBackStack("settings").commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.privacy_policy);
                return true;
            }
        });

        terms_conditions.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Answers.getInstance().logCustom(new CustomEvent("Terms and Conditions Clicked"));
                getFragmentManager().beginTransaction().replace(R.id.content_frame, new TermsOfUseFragment(), "settings").addToBackStack("settings").commit();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.terms_conditions);
                return true;
            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setPadding(0, 32, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings);
    }

    private void setNoifications() {

        if (holidays.size() != 0) {
            int position = -1;
            for (int i = 0; i < holidays.size(); i++) {
                if (System.currentTimeMillis() < dateToMilisec2(holidays.get(i).getDate())) {
                    position = i;
                    break;
                }
            }

            if (position != -1) {

                long t = getTime(position);

                Intent intent = new Intent(getActivity(), NotificationReceiver.class);
                intent.putExtra("Holiday_name", holidays.get(position).getName());
                intent.putExtra("Holiday_date", holidays.get(position).getDate());
                intent.putExtra("Holiday_day", holidays.get(position).getDay());

                alarmIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + t, alarmIntent);
            } else
                Toast.makeText(getActivity(), "No more Holidays found !!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity(), "Loading Holidays !!!\nPlease Wait...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("handler preference", String.valueOf(enable_notifications.isChecked()));
                    if (enable_notifications.isChecked()) {
                        enable_notifications.setChecked(false);
                        notification_days.setEnabled(false);
                    }
                }
            }, 10);
        }
    }

    private long getTime(int position) {

        Log.e("holiday_time_in_millis", "" + dateToMilisec2(holidays.get(position).getDate()));
        Log.e("days", "" + daysValue);
        Log.e("days_time_in_millis", "" + daysValue * 86400000);
        Log.e("current_time_in_millis", "" + System.currentTimeMillis());
        long time = dateToMilisec2(holidays.get(position).getDate()) - daysValue * 86400000 - System.currentTimeMillis();
        if (time < 0)
            time = 0;
        Log.e("time_in_millis", "" + time);
        return time;
    }
}
