package com.talentcerebrumhrms.activity;

import android.annotation.SuppressLint;
import android.content.Context;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.DatePickerEditText;
import com.talentcerebrumhrms.utils.OneTimeLocationProvider;
import com.talentcerebrumhrms.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<OneTimeLocationProvider.GPSCoordinates> locations = new ArrayList<>();
    EditText to, from, code;
    Button search;
    AppCompatImageView back;
    DatePickerEditText fromdatepickertext, todatepickertext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        search = (Button) findViewById(R.id.search);
        to = (EditText) findViewById(R.id.date_to);
        from = (EditText) findViewById(R.id.date_from);
        code = (EditText) findViewById(R.id.code);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        back = (AppCompatImageView) findViewById(R.id.back);

        fromdatepickertext = new DatePickerEditText(this, getFragmentManager(), from);
        todatepickertext = new DatePickerEditText(this, getFragmentManager(), to);
        calenderEditTextConfig();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  View view = MapsActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }*/
                if (to.getText().toString().trim().equalsIgnoreCase("") || code.getText().toString().trim().equalsIgnoreCase("") || from.getText().toString().trim().equalsIgnoreCase(""))
                    Toast.makeText(MapsActivity.this, R.string.missing_fields, Toast.LENGTH_SHORT).show();
                else
                    getbeatdata(code.getText().toString().trim(), dateToyyyymmdd(to.getText().toString().trim()), dateToyyyymmdd(from.getText().toString().trim()));
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(28.6508275,77.2187207) , 4.0f) );
        addMarkersToMap();
         }


    /*private void getbeatdata(String employee_id, String to_date, String from_date) {
        AppController.getbeatdata(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {
                Log.d(getClass().getName(), jsonObject.toString());
                try {
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); ++i) {
                        JSONObject loc = data.getJSONObject(i);
                        OneTimeLocationProvider.GPSCoordinates gpsCoordinates =
                                new OneTimeLocationProvider.GPSCoordinates(loc.getDouble("latitude"),
                                        loc.getDouble("longitude"));
                        gpsCoordinates.time = loc.getString("created_at");
                        locations.add(gpsCoordinates);
                    }
                    addMarkersToMap();
                    View view = MapsActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                Log.d(getClass().getName(), "Error: " + volleyError.getMessage());
            }
        }, employee_id, to_date, from_date);
    }*/
    private void getbeatdata(String employee_id, String to_date, String from_date) {
        Call<JsonObject> call = ApiUtil.getServiceClass().beat_logs_detail(employee_id,to_date,from_date);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); ++i) {
                        JSONObject loc = data.getJSONObject(i);
                        OneTimeLocationProvider.GPSCoordinates gpsCoordinates =
                                new OneTimeLocationProvider.GPSCoordinates(loc.getDouble("latitude"),
                                        loc.getDouble("longitude"));
                        gpsCoordinates.time = loc.getString("created_at");
                        locations.add(gpsCoordinates);
                    }
                    addMarkersToMap();
                    View view = MapsActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        assert imm != null;
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
//                Log.d(getClass().getName(), "Error: " + volleyError.getMessage());
            }
        });
    }

    private void addMarkersToMap() {
        if (mMap != null) {
            mMap.clear();
            if (locations.size() > 0) {
                for (int i = 0; i < locations.size(); i++) {
                    LatLng ll = new LatLng(locations.get(i).latitude, locations.get(i).longitude);
                    BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    mMap.addMarker(new MarkerOptions().position(ll).title(locations.get(i).time)
                            .icon(bitmapMarker)).showInfoWindow();
                }
                final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
                if (mapView != null && mapView.getViewTreeObserver().isAlive()) {
                    mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onGlobalLayout() {
                            LatLngBounds.Builder bld = new LatLngBounds.Builder();
                            for (int i = 0; i < locations.size(); i++) {
                                LatLng ll = new LatLng(locations.get(i).latitude, locations.get(i).longitude);
                                bld.include(ll);
                            }
                            LatLngBounds bounds = bld.build();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
                            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                        }
                    });
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.finish_in, R.anim.finish_out);
    }


    private String dateToyyyymmdd(String datetext) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat formatfinal = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return formatfinal.format(format.parse(datetext));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datetext;

    }

    private void calenderEditTextConfig() {
        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!from.getText().toString().trim().equalsIgnoreCase(""))
                    todatepickertext.setMinPickerDate(Utility.dateToMilisec(from.getText().toString()));
                if (!to.getText().toString().trim().equalsIgnoreCase(""))
                    fromdatepickertext.setMaxPickerDate(Utility.dateToMilisec(to.getText().toString()));
            }
        });
        to.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!from.getText().toString().trim().equalsIgnoreCase(""))
                    todatepickertext.setMinPickerDate(Utility.dateToMilisec(from.getText().toString()));
                if (!to.getText().toString().trim().equalsIgnoreCase(""))
                    fromdatepickertext.setMaxPickerDate(Utility.dateToMilisec(to.getText().toString()));
            }
        });
    }
}
