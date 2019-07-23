package com.talentcerebrumhrms.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

public class ShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Insert code here
        Date currentTime = Calendar.getInstance().getTime();
        System.out.println("ShutdownReceiver :: currentTime> "+currentTime);

        SharedPreferences sp = context.getSharedPreferences("LastShutDown", context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString("LastShutDownTime", currentTime.toString());
        et.commit();
    }

}