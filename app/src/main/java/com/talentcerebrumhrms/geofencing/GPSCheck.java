package com.talentcerebrumhrms.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSCheck extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

       GPSService.enqueueWork(context, intent);


System.out.println("GPSCHECK 1");

    }


}