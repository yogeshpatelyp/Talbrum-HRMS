package com.talentcerebrumhrms.geofencing;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import androidx.core.app.JobIntentService;
import android.util.Log;

import com.talentcerebrumhrms.models.JSONResponse;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GPSService
        extends
        JobIntentService {

    private static final int JOB_ID = 575;

    private static final String TAG = "GPSIS";

    private static final String CHANNEL_ID = "channel_01";


    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GPSService.class, JOB_ID, intent);
        Log.d(TAG, "GPSService : enqueueWork");
    }

    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleWork(Intent intent) {
        Log.d(TAG, "GPSService : onHandleWork");
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        System.out.println("GPSCHECK 2");

        if (locationManager.isProviderEnabled(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            // Toast.makeText(context, "GPS ON", Toast.LENGTH_LONG).show();
            Log.e("GPSCHECK ", "GPS ON");
            Date currentTime = Calendar.getInstance().getTime();
            System.out.println("GPS ON TIME :: currentTime> " + currentTime);
           // sendNotification("GPS ON");
        } else {
            Date currentTime = Calendar.getInstance().getTime();
            System.out.println("GPS OFF TIME :: currentTime> " + currentTime);
           // sendNotification("GPS OFF");
            // Toast.makeText(context, "Please switch on the GPS", Toast.LENGTH_LONG).show();
            Log.e("GPSCHECK ", "PLEASE GPS ON");
        }

       /* if (intent.getAction().matches("android.location.GPS_ENABLED_CHANGE"))
        {
            boolean enabled = intent.getBooleanExtra("enabled",false);
            Log.e("GPSCHECK ", "PLEASE GPS ON "+enabled);
            Toast.makeText(this, "GPS : " + enabled,
                    Toast.LENGTH_SHORT).show();
        }*/
       /* LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //Location Listener is an interface. It will be called every time when the location manager reacted.
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // This method is called when a new location is found by the network location provider or Gps provider.
                //do you logics here.

                System.out.println("GPS LOCATION CHANGED");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                System.out.println("GPS STATUS CHANGED");
            }

            public void onProviderEnabled(String provider) {
                System.out.println("GPS PROVIDER ENABLE");
            }

            public void onProviderDisabled(String provider) {
                System.out.println("GPS PROVIDER DISABLE");
            }
        };

        // Register the listener with Location Manager's network provider
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        //Or  Register the listener with Location Manager's gps provider
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


    }*/

    }
    private void loadJSON(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call = request.fetchlatlon("1","1.3","jayashree.ghosh@village.net.in","12345678");
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {

                // Jobs jsonResponse = response.body();
                // data = new ArrayList<>(Arrays.asList(jsonResponse.getData()));
                System.out.println("response.body() " + response.body());
                JSONResponse jsonResponse = response.body();

            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
                // ProgressDialogCustom.hideProgressDialog(activity);

            }
        });
    }


}

