package com.talentcerebrumhrms.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.talentcerebrumhrms.datatype.PeopleDataType;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by saransh on 01-11-2016.
 */

public class Utility {

    //calculating DayTypeId for leaves
    public String day_type(String value) {

        Log.e("value utility", value);
        if (value.equalsIgnoreCase("full day")) {
            Log.e("utility", "0");
            return "0";
        } else if (value.equalsIgnoreCase("before noon")) {
            Log.e("utility", "1");
            return "1";
        } else if (value.equalsIgnoreCase("after noon")) {
            Log.e("utility", "2");
            return "2";
        } else {
            return "301";
        }
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

    public static ArrayList<PeopleDataType> getSortedPeople() {
        Collections.sort(AppController.people_data_array, PeopleDataType.listComparator);
        Log.e("sorting list", "sort");
        return AppController.people_data_array;
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static int pxToDp(Context context, int pixel) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((pixel * scale) + 0.5f);
    }

    public static long dateToMilisec(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        try {
            date1 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("Utility datetomili", String.valueOf(date1.getTime()));
        return date1.getTime();
    }

    public static String milisecToDate(long milisec) {
        Date date = new Date(milisec);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public static long dateToMilisec2(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Date date1 = null;
        try {
            date1 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.i("Utility datetomili", String.valueOf(date1.getTime()));
        return date1.getTime();
    }

    public static int milisecToDays(long milisec) {
        return (int) (milisec / (1000 * 60 * 60 * 24));
    }

    public static double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // output distance, in MILES
    }

    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Log.e("isavailable", String.valueOf(list.size()));
        return list.size() > 0;
    }

    public static Bitmap resizeBitmap(Bitmap bitmapImage, float viewHeight) {
        //maintains aspect ratio while resizing it
        int nh = (int) (bitmapImage.getHeight() * (viewHeight / bitmapImage.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, (int) viewHeight, nh, true);
        return scaled;
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ", e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }
}
