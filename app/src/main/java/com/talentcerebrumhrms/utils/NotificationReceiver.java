package com.talentcerebrumhrms.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.activity.SettingsActivity;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String holiday_name = intent.getStringExtra("Holiday_name");
        String holiday_date = intent.getStringExtra("Holiday_date");
        String holiday_day = intent.getStringExtra("Holiday_day");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context, context.getString(R.string.notification_channel))
                .setContentTitle(holiday_name)
                .setContentText(context.getString(R.string.upcoming_holiday) + holiday_date + " (" + holiday_day + ")")
                .setSmallIcon(R.mipmap.app_logo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.upcoming_holiday) + holiday_date + " (" + holiday_day + ")"))
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, SettingsActivity.class), 0))
                .build();

        if (notificationManager != null) {
            notificationManager.notify(0, notification);
        }

    }
}
