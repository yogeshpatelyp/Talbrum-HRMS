package com.talentcerebrumhrms;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.talentcerebrumhrms.geofencing.ApiClient;
import com.talentcerebrumhrms.geofencing.GeofenceReceiver;
import com.talentcerebrumhrms.geofencing.ReminderDetails;
import com.talentcerebrumhrms.geofencing.RequestInterface;
import com.talentcerebrumhrms.geofencing.SessionManagerTwo;
import com.talentcerebrumhrms.models.Data;
import com.talentcerebrumhrms.models.JSONResponse;
import com.talentcerebrumhrms.models.PlaceInfo;
import com.talentcerebrumhrms.models.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityGeoFenceMap extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener
        , GoogleMap.OnMarkerClickListener
        ,OnCompleteListener<Void>
{
    private ArrayList<Data> data;
    private static final String TAG = "ActivityGeoFenceMap";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int NEW_REMINDER_REQUEST_CODE = 330;
    //whole world
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168),new LatLng(71,136));

    //--
    private ImageView mGps;

    private boolean mPermissionsGranted = false;
    private GoogleMap gMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    //private PlaceAutoCompleteAdapter autocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private GeofencingClient mGeofencingClient;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    private PlaceInfo mPlace;
    private Marker marker;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private final String EXTRA_LAT_LNG = "EXTRA_LAT_LNG";
    private final String EXTRA_ZOOM = "EXTRA_ZOOM";


   private ReminderDetails reminder = new ReminderDetails(null, null,  null);

    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    SessionManagerTwo session;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_geofence);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10.0f);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Location");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGps = (ImageView) findViewById(R.id.ic_gps);
         session = new SessionManagerTwo(getApplicationContext());
//loadJSON();
        getLocPermission();

        mGeofencePendingIntent = null;
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mGeofenceList = new ArrayList<>();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geofence, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();
        overridePendingTransition(R.anim.finish_in, R.anim.finish_out);

    }
    /******************************************/
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
                if (response.body() != null) {
                    data = new ArrayList<>(Arrays.asList(jsonResponse.getData()));
                    int countnull = 0, countblank = 0, countdatafound = 0;
                    for (int i = 0; i < 5; i++) {

                        // String latlng = "lat/lng: ("+data.get(i).getLatitude()+","+data.get(i).getLatitude()+")";

                        // System.out.println("latlng:: "+latlng);


                        if (data.get(i).getLatitude() == null && data.get(i).getLongitude() == null) {

                            System.out.println("IF NULL");
    /*LatLng location = new LatLng(22.72393210582694,75.86978361010551);
    reminder.latLng = location;
    reminder.radius=Double.parseDouble("50000");
    reminder.message=data.get(i).getName();*/
                            countnull = countnull + 1;
                            break;
                        } else if (data.get(i).getLatitude().equalsIgnoreCase("") && data.get(i).getLongitude().equalsIgnoreCase("")) {
                            System.out.println("IF BLANK");

    /*LatLng location = new LatLng(22.72393210582694,75.86978361010551);
    reminder.latLng = location;
    reminder.radius=Double.parseDouble("50000");
    reminder.message=data.get(i).getName();*/
                            countblank = countblank + 1;
                            break;
                        } else {
                            String r = "100";
                            System.out.println("IF DATA FOUND " + data.get(i).getLatitude() + " :: " + data.get(i).getLongitude());
                            LatLng location = new LatLng(Double.valueOf("22.7276638"), Double.valueOf("75.8696956"));
                            //LatLng location = new LatLng(Double.valueOf(data.get(i).getLatitude()),Double.valueOf(data.get(i).getLongitude()));
                            reminder.latLng = location;
                            reminder.radius = Double.parseDouble(r);
                            //reminder.message=data.get(i).getName();
                            reminder.message = "";
                            countdatafound = countdatafound + 1;

                        }

                        //  Log.i("NewReminderr", "onClick: "+mNotification.getText().toString());
                        System.out.println("countnull >> " + countnull);
                        System.out.println("countblank >> " + countblank);
                        System.out.println("countdatafound >> " + countdatafound);


                        Log.d(TAG, "adding reminder");
                        System.out.println("REMINDER LAT LON json :: " + reminder.latLng);
                        System.out.println("REMINDER RADIOUS json:: " + reminder.radius);
                        System.out.println("REMINDER ID json:: " + reminder.id);
                        addReminder(reminder);
                        session.createfirsttime("second");
                        showReminderUpdate();

                    }
               /* adapter = new JobAdapter(response.body());
                recyclerView.setAdapter(adapter);
                ProgressDialogCustom.hideProgressDialog(activity);*/

                }
                else {
                    String r = "100";
                   // System.out.println("IF DATA FOUND " + data.get(i).getLatitude() + " :: " + data.get(i).getLongitude());
                    LatLng location = new LatLng(Double.valueOf("22.7276638"), Double.valueOf("75.8696956"));
                    //LatLng location = new LatLng(Double.valueOf(data.get(i).getLatitude()),Double.valueOf(data.get(i).getLongitude()));
                    reminder.latLng = location;
                    reminder.radius = Double.parseDouble(r);
                    //reminder.message=data.get(i).getName();
                    reminder.message = "TESTING ENTER/EXIT";
                    addReminder(reminder);
                    session.createfirsttime("second");
                    showReminderUpdate();
                }
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
               // ProgressDialogCustom.hideProgressDialog(activity);

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        gMap = googleMap;
        gMap.getUiSettings().setMapToolbarEnabled(false);
        //gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EXTRA_LAT_LNG,EXTRA_ZOOM));
        //buttonForGeofence();


        HashMap<String, String> user1 = session.getfirsttime();
        String   strfirsttime = user1.get(SessionManagerTwo.KEY_FIRSTTIME);

        loadJSON();

         if (mPermissionsGranted) {
            getDeviceLocation();
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
            gMap.setMyLocationEnabled(true); //blue dot on map
            gMap.getUiSettings().setMyLocationButtonEnabled(false); //Google's find my location button
            gMap.getUiSettings().setMapToolbarEnabled(false); //Set default directions button false
            gMap.setOnMarkerClickListener(this);

            init();
        }
    }
    private void init(){
        Log.d(TAG, "init: initiliazing");



        mGeoDataClient = Places.getGeoDataClient(this);
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        //autocompleteAdapter = new PlaceAutoCompleteAdapter(this,mGeoDataClient,LAT_LNG_BOUNDS,null);




        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });


        hideSoftKeyboard();
        showReminders();
    }

    private void showReminders(){
        gMap.clear();
        System.out.println("mGeofenceList.size() :: "+mGeofenceList.size());
        for (ReminderDetails reminder : Utils.getAll(this)) {
            Utils.showReminderInMap(this,gMap,reminder);
            System.out.println("show REMINDER LAT LON :: "+reminder.latLng);
            System.out.println("show REMINDER RADIOUS :: "+reminder.radius);
            System.out.println("show REMINDER ID :: "+reminder.id);
        }
    }


    private void showReminderUpdate() {
        Log.d(TAG, "showReminderUpdate: inside");
//        gMap.clear();
       /* System.out.println("REMINDER LAT LON :: "+reminder.latLng);
        System.out.println("REMINDER RADIOUS :: "+reminder.radius);
        System.out.println("REMINDER ID :: "+reminder.id);*/
        Utils.showReminderInMap(this,gMap,reminder);
    }


//    private void setButtonOnClickListener(View view){
//        Intent intent = new Intent(this,ActivityGeoFenceMap.class);
//        intent.putExtra("EXTRA_LAT_LNG",gMap.getCameraPosition().target);
//        intent.putExtra("EXTRA_ZOOM",gMap.getCameraPosition().zoom);
//        startActivityForResult(intent,NEW_REMINDER_REQUEST_CODE);
//
//
//    }

    /*private void geoLocate(){
        hideSoftKeyboard();
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(ActivityGeoFenceMap.this);
        List<Address> list = new ArrayList<>();
        try {

            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.d(TAG, "geoLocate: IOException"+e.getMessage());
        }

        if(list.size()>0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: address"+address.toString());

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }*/

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d(TAG, "onComplete: found location!!");
                            Location currentLoc = (Location) task.getResult();
                            System.out.println("currentLoc.getLatitude() :: "+currentLoc.getLatitude());
                            System.out.println("currentLoc.getLongitude() :: "+currentLoc.getLongitude());
                            moveCamera(new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude()), DEFAULT_ZOOM,"My Location");

                        } else {
                            Log.d(TAG, "onComplete: current location not found");
                            Toast.makeText(ActivityGeoFenceMap.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                            new SweetAlertDialog(ActivityGeoFenceMap.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("GPS Permission")
                                    .setContentText("Please Accept GPS Permission")
                                    .setConfirmText("Ok")
                                    .show();

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "MoveCamera: moving camera to" + latLng);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        //create and drop marker on map
        if(title!="My Location"){
            if(marker != null)
                marker.remove();
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            marker = gMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap() {
        Log.d(TAG, "initMap: inside");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ActivityGeoFenceMap.this);
    }

    private void getLocPermission() {
        Log.d(TAG, "getLocPermission: inside");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mPermissionsGranted = true;
                initMap();

            } else {
                ActivityCompat.requestPermissions(this, permissions, 123);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionsGranted = false;
        Log.d(TAG, "onRequestPermissionsResult: inside");
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPermissionsGranted = false;
                            return;
                        }
                    }
                    mPermissionsGranted = true;
                    //if permissions granted initialize map
                    initMap();

                }
            }
        }
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: "+marker.getTitle());
        ReminderDetails reminder = Utils.get(this,marker.getTag().toString());

        if (reminder != null) {
            showReminderRemoveAlert(reminder);
        }
        return false;
    }

    private void showReminderRemoveAlert(final ReminderDetails reminder) {
        Log.d(TAG, "showReminderRemoveAlert: inside");
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Remove reminder")
                .setMessage("Are you sure you want to delete this reminder?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        //Utils.removeReminder(reminder);
                        removeReminder(reminder);
                        //Snackbar.make(this, R.string.reminder_removed_success, Snackbar.LENGTH_LONG).show()
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    void removeReminder(ReminderDetails reminder){
        Log.d(TAG, "removeReminder: inside");
        ArrayList<String> mGeofenceList = new ArrayList<>();
        mGeofenceList.add(reminder.id);
        List<ReminderDetails> reminders = Utils.getAll(this);
        for(int i = 0; i<reminders.size();i++){
            if(reminders.get(i).id.equals(reminder.id)){
                reminders.remove(i);
                //break;
            }
        }
        Utils.saveAll(getBaseContext(),reminders);
        showReminders();
        mGeofencingClient.removeGeofences(mGeofenceList);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void hideSoftKeyboard(){
        //hide keyboard
        Log.d(TAG, "hideSoftKeyboard: hide keyboard");
        InputMethodManager InputMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      //  InputMM.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: inside");
        if (requestCode == NEW_REMINDER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            showReminders();

            ReminderDetails reminder = Utils.getLast(this);
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(reminder.latLng,DEFAULT_ZOOM));


            View container = findViewById(android.R.id.content);
            if (container != null) {
                Snackbar.make(container, R.string.reminder_added_success, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    //-----------------------------------------------------





    private void addReminder(ReminderDetails reminder){
        Geofence geofence=buildGeofence(reminder);
        mGeofenceList.add(geofence);
        Log.d(TAG, "addReminder: inside");
        if(geofence!=null&& ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mGeofencingClient.addGeofences(buildGeofencingReq(geofence),getGeofencePendingIntent()).addOnCompleteListener(this);
        }
    }

    private GeofencingRequest buildGeofencingReq(Geofence geofence) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        Log.d(TAG, "GeofencingRequest: inside");
        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private Geofence buildGeofence(ReminderDetails reminder) {
        double lat =reminder.latLng.latitude;
        double longitude=reminder.latLng.longitude;
        double radius=reminder.radius;
        Log.d(TAG, "buildGeofence: inside");
        return new Geofence.Builder()
                .setRequestId(reminder.id)
                .setCircularRegion(
                        lat,
                        longitude,
                        (float)radius
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();


    }

    private PendingIntent getGeofencePendingIntent() {

        Log.d(TAG, "PendingIntent : inside");

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }



    @Override
    public void onComplete(@NonNull Task<Void> task) {
        Log.d(TAG, "onComplete: inside");
        if (task.isSuccessful()) {
            List<ReminderDetails> reminderArrayList = Utils.getAll(this);
            reminderArrayList.add(reminder);
            System.out.println("REMINDER LAT LON COMPLETE :: "+reminder.latLng);
            System.out.println("REMINDER RADIOUS COMPLETE:: "+reminder.radius);
            System.out.println("REMINDER ID COMPLETE:: "+reminder.id);
            Utils.saveAll(this, reminderArrayList);
            Log.d(TAG, "onComplete: "+new Gson().toJson(mGeofenceList));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        "ServiceChannel",
                        "Example Service Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );

                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(serviceChannel);
            }


            setResult(Activity.RESULT_OK);
           // finish();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceError.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }

    }


}