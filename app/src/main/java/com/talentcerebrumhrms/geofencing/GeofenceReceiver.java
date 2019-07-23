package com.talentcerebrumhrms.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.talentcerebrumhrms.GeofenceTransitionService;

import java.util.Calendar;
import java.util.Date;

public class GeofenceReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";

    /**
     * Receives incoming intents.
     *
     * @param context the application context.
     * @param intent  sent by Location Services. This Intent is provided to Location
     *                Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        GeofenceTransitionService.enqueueWork(context, intent);
        Log.d(TAG, "GeofenceReceiver : onReceive");
        SharedPreferences sp = context.getSharedPreferences("LastShutDown", context.MODE_PRIVATE);
        String lastShutDownTime=sp.getString("LastShutDownTime",null);
        Log.d(TAG, "GeofenceReceiver : lastShutDownTime :: " +lastShutDownTime);
        Date currentTimeRestart = Calendar.getInstance().getTime();
        Log.d(TAG, "GeofenceReceiver : currentTimeRestart :: " +currentTimeRestart);
    }
}
//    /**
//     * Receives incoming intents.
//     *
//     * @param context the application context.
//     * @param intent  sent by Location Services. This Intent is provided to Location
//     *                Services (inside a PendingIntent) when addGeofences() is called.
//     */
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // Enqueues a JobIntentService passing the context and intent as parameters
//        Log.i("Broadcast", "onReceive: ");
//        Intent serviceIntent  = new Intent(context , GeofenceTransitionService.class);
//        context.startService(serviceIntent);
//        GeofenceTransitionService.enqueueWork(context, intent);
//    }
//}
