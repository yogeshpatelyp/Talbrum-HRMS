package com.talentcerebrumhrms.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.adapter.Draweradapter;
import com.talentcerebrumhrms.adapter.FeedbackAdapter;
import com.talentcerebrumhrms.attandanceoffline.Name;
import com.talentcerebrumhrms.attandanceoffline.OutTime;
import com.talentcerebrumhrms.chatbot.Chat;
import com.talentcerebrumhrms.chatbot.ChatAdapter;
import com.talentcerebrumhrms.datatype.AlertDataType;
import com.talentcerebrumhrms.datatype.HolidayDatatype;
import com.talentcerebrumhrms.datatype.LeaveApproveDatatype;
import com.talentcerebrumhrms.datatype.LeaveTypeDatatype;
import com.talentcerebrumhrms.fragment.AttendanceFragment;
import com.talentcerebrumhrms.fragment.LeaveFragment;
import com.talentcerebrumhrms.fragment.List_Holidays_Fragment;
import com.talentcerebrumhrms.fragment.PeopleFragment;
import com.talentcerebrumhrms.fragment.RecruitmentFragment;
import com.talentcerebrumhrms.fragment.ReimbursementsFragment;
import com.talentcerebrumhrms.fragment.TimesheetFragment;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.AppController;
import com.talentcerebrumhrms.utils.DatabaseHelper;
import com.talentcerebrumhrms.utils.DrawerButtonListerner;
import com.talentcerebrumhrms.utils.NetworkStateChecker;
import com.talentcerebrumhrms.utils.NetworkStateCheckerOutTime;
import com.talentcerebrumhrms.utils.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fabric.sdk.android.Fabric;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.alert_data_array;
import static com.talentcerebrumhrms.utils.AppController.alert_size;
import static com.talentcerebrumhrms.utils.AppController.apiVersion;
import static com.talentcerebrumhrms.utils.AppController.appVersion;
import static com.talentcerebrumhrms.utils.AppController.approve_size;
import static com.talentcerebrumhrms.utils.AppController.attendanceFragmentDataType;
import static com.talentcerebrumhrms.utils.AppController.attendance_dialog;
import static com.talentcerebrumhrms.utils.AppController.holidays;
import static com.talentcerebrumhrms.utils.AppController.lat;
import static com.talentcerebrumhrms.utils.AppController.leave_approve_array;
import static com.talentcerebrumhrms.utils.AppController.leave_reason_array;
import static com.talentcerebrumhrms.utils.AppController.leave_type_array;
import static com.talentcerebrumhrms.utils.AppController.leaves_array;
import static com.talentcerebrumhrms.utils.AppController.loc;
import static com.talentcerebrumhrms.utils.AppController.lon;
import static com.talentcerebrumhrms.utils.AppController.people_data_array;
import static com.talentcerebrumhrms.utils.AppController.reimbursement_array;
import static com.talentcerebrumhrms.utils.AppController.serverUrl;
import static com.talentcerebrumhrms.utils.AppController.timesheet_approved_array;
import static com.talentcerebrumhrms.utils.AppController.timesheet_rejected_array;
import static com.talentcerebrumhrms.utils.AppController.timesheet_upload_array;
import static com.talentcerebrumhrms.utils.AppController.TAG;
public class MainActivity extends AppCompatActivity implements DrawerButtonListerner, LocationListener, RecyclerView.OnItemTouchListener, AIListener {

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    Fragment leaveFragment = new LeaveFragment();
    Fragment attendanceFragment = new AttendanceFragment();
    Fragment reimbursementsFragment = new ReimbursementsFragment();
    Fragment peopleFragment = new PeopleFragment();
    Fragment timesheetFragment = new TimesheetFragment();
    Fragment recruitmentFragment = new RecruitmentFragment();
    Boolean activityrunnning = false;
    Boolean attandancecheck = false;
    SharedPreferences sharedpreferences;
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as profile_username_label linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    private static Boolean doubleBackToExitPressedOnce = false;
    ActionBarDrawerToggle mDrawerToggle;  // Declaring Action Bar Drawer Toggle

    LocationManager manager;
    ProgressDialog location_pd;
    boolean mark_in_time_present, TRACK_ME_STATUS = false;
    public static Dialog dialog;
    String overflowMenuItem;
    ArrayList<String> TITLES;
    ArrayList<Integer> ICONS;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION_OUT_TIME = 99;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION_TRACK_ME = 105;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 101;
    public static final String LOCATION_REQUEST_TYPE_ATTENDANCE = "timeout";
    public static final String LOCATION_REQUEST_TYPE_TRACKME = "track_me";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final int RECORD_REQUEST_CODE = 111;
    private static final int REQUEST_CODE_SPEECH_INPUT = 505 ;
    Button listen, send;
    EditText textInput;
    TextToSpeech t1;
    private ChatAdapter mChatAdapter;
    private  List<Chat> mChat = new ArrayList<>();
    RecyclerView recyclerView;
    Dialog dialogchatbot;
    AIService aiService;
    FragmentTransaction ft;
    private DatabaseHelper db;
    private List<Name> names;
    //1 means data is synced and 0 means data is not synced
    public static final int IN_TIME_SYNCED_WITH_SERVER = 1;
    public static final int IN_TIME_NOT_SYNCED_WITH_SERVER = 0;

    public static final int OUT_TIME_SYNCED_WITH_SERVER = 1;
    public static final int OUT_TIME_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "com.talentcerebrum.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;
    public static final String DATA_SAVED_BROADCAST_OUTTIME = "com.talentcerebrum.datasavedouttime";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver_OUTTIME;
    public static boolean isoffline=false;
    boolean offlineclicked = false;
    public static boolean isintimemarked = false;
    public static boolean isouttimemarked = false;
    private List<OutTime> outTime;
    String currentVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Answers());
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //removing test location thus preventing mock locations.

        sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);


        // Log.v("dummy test",sharedpreferences.getString("email", ""));
        System.out.println("ON CREAE CALLED");
        if (savedInstanceState == null) {
            System.out.println("ON SAVEDINSTANCE NULL");
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, attendanceFragment);
            ft.commit();
        }


        //activityrunnning = true;
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.overview));

        invalidateOptionsMenu();

        TITLES = createDrawerTitles();
        ICONS = createDrawerIcons(TITLES);

        db = new DatabaseHelper(this);
        names = new ArrayList<>();
        outTime = new ArrayList<>();

        mRecyclerView = findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new Draweradapter(this, TITLES, ICONS, sharedpreferences.getString("name", ""), sharedpreferences.getString("email", ""), sharedpreferences.getString("role", ""), sharedpreferences.getString("company_name", ""), sharedpreferences.getString("passport_image", ""), sharedpreferences.getString("company_logo_url", ""));       // Creating the Adapter of Draweradapter class(which we are going to see in profile_username_label bit)
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mRecyclerView.addOnItemTouchListener(this);
        mLayoutManager = new LinearLayoutManager(this);                 // Creating profile_username_label layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        Drawer = findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }


        }; // Drawer Toggle Object Made
        Drawer.addDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new GetVersionCode().execute();
        selectLeaveApiCall();
        if (sharedpreferences.getString("allow_attendance", "").equalsIgnoreCase("true")) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = dateformat.format(c.getTime());
            StringTokenizer tk = new StringTokenizer(datetime);

            final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
            final String mtime = tk.nextToken();
            Cursor cursor = db.getUnsyncedNames();

            if (cursor.moveToFirst()) {
                do {
                    String DATE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                    if(DATE.equalsIgnoreCase(mdate)){
                        Log.v("onResume DATE", "IF");
                        int  ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                       /* MainActivity.this.registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                        broadcastReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {

                                //loading the names again
                                // loadNames();
                            }
                        };
                        // saveNameToLocalStorage("present","khandwa",String.valueOf("21.8090"),String.valueOf("76.3691"),"12:20",null, NAME_NOT_SYNCED_WITH_SERVER);
                        MainActivity.this.registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));*/
                        //markAttendanceCheckApiCall();

                    }else{
                        Log.v("onResume DATE", "ELSE");
                        int  ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                        db.delete(ID);
                        markAttendanceCheckApiCall();
                    }

                } while (cursor.moveToNext());
            }
            else {
                markAttendanceCheckApiCall();
            }
            approveLeaveApiCall();
            getAlertsApiCall();
            holidayListApiCall();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("onResume", "MainActivity");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateformat.format(c.getTime());
        StringTokenizer tk = new StringTokenizer(datetime);

        final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
        final String mtime = tk.nextToken();
        Cursor cursor = db.getUnsyncedNames();

        if (cursor.moveToFirst()) {
            do {
                String DATE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                if(DATE.equalsIgnoreCase(mdate)){
                    Log.v("onResume DATE", "IF");
                    int  ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                    MainActivity.this.registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                    broadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            //loading the names again
                            // loadNames();
                        }
                    };
                    // saveNameToLocalStorage("present","khandwa",String.valueOf("21.8090"),String.valueOf("76.3691"),"12:20",null, NAME_NOT_SYNCED_WITH_SERVER);
                    MainActivity.this.registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
                    //markAttendanceCheckApiCall();

                }else{
                    Log.v("onResume DATE", "ELSE");
                 int  ID = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
                    db.delete(ID);
                    markAttendanceCheckApiCall();
                }

            } while (cursor.moveToNext());
        }

        Cursor cursorouttime = db.getUnsyncedOuttime();

        if (cursorouttime.moveToFirst()) {
            do {
                String DATE = cursorouttime.getString(cursorouttime.getColumnIndex(DatabaseHelper.COLUMN_DATE_OUTTIME));
                if(DATE.equalsIgnoreCase(mdate)){
                    Log.v("onResume DATE", "IF");
                    int  ID = cursorouttime.getInt(cursorouttime.getColumnIndex(DatabaseHelper.COLUMN_ID_OUTTIME));
                    MainActivity.this.registerReceiver(new NetworkStateCheckerOutTime(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                    broadcastReceiver_OUTTIME = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            //loading the names again
                            // loadNames();
                        }
                    };
                    // saveNameToLocalStorage("present","khandwa",String.valueOf("21.8090"),String.valueOf("76.3691"),"12:20",null, NAME_NOT_SYNCED_WITH_SERVER);
                    MainActivity.this.registerReceiver(broadcastReceiver_OUTTIME, new IntentFilter(DATA_SAVED_BROADCAST_OUTTIME));
                    //markAttendanceCheckApiCall();

                }else{
                    Log.v("onResume DATE", "ELSE");
                    int  ID = cursorouttime.getInt(cursorouttime.getColumnIndex(DatabaseHelper.COLUMN_ID_OUTTIME));
                    db.deleteouttime(ID);
                    //markAttendanceCheckApiCall();
                }

            } while (cursorouttime.moveToNext());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.alert);
        menuItem.setIcon(buildCounterDrawable(AppController.alert_size, R.drawable.alert));

        MenuItem menuItem1 = menu.findItem(R.id.leave_approve_toolbar_button);
        menuItem1.setIcon(buildCounterDrawable(AppController.approve_size, R.drawable.approve));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Answers.getInstance().logCustom(new CustomEvent(item.getTitle() + " Clicked"));
        switch (id) {

            case R.id.alert:
                Bundle bndlanimatio = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                Intent intent = new Intent(MainActivity.this, AlertActivity.class);
                startActivity(intent, bndlanimatio);
                break;

            case R.id.leave_approve_toolbar_button:
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                Intent i = new Intent(MainActivity.this, ApproveLeavesActivity.class);
                startActivity(i, bndlanimation);

                //  invalidateOptionsMenu(); to refresh menu
                break;

            case R.id.timeout:
                overflowMenuItem = LOCATION_REQUEST_TYPE_ATTENDANCE;
                mark_in_time_present = false;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION_OUT_TIME);
                else if (!isGPSEnabled())
                    showSettingsAlert();
                else
                    outTimeDialog();
                break;

            case R.id.settings:

                Intent intent2 = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent2);
                break;
            case R.id.chatbot:
               // Intent intentchatbot = new Intent(MainActivity.this, ActivityChatbot.class);
               // startActivity(intentchatbot);
                showCustomDialog(MainActivity.this,"msg");
                break;
            case R.id.track_me:
                overflowMenuItem = LOCATION_REQUEST_TYPE_TRACKME;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION_TRACK_ME);
                else if (!isGPSEnabled())
                    showSettingsAlert();
                else
                    trackMeDialog();
                break;

            case R.id.tracking_map:
                Intent mapIntent = new Intent(this, MapsActivity.class);
                startActivity(mapIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }

        return true;
    }

    public Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.toolbar_button_approve, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public void onBackPressed() {

        if (Drawer.isDrawerOpen(GravityCompat.START)) {
            Drawer.closeDrawers();
            //drawer is open
        } else {
            Draweradapter.mSelectedPositionTitle = getString(R.string.overview);
            mRecyclerView.getAdapter().notifyDataSetChanged();
            FragmentManager frm = getSupportFragmentManager();
            Log.e("backstackcount", String.valueOf(frm.getBackStackEntryCount()));
            Log.v("double exit 1", String.valueOf(doubleBackToExitPressedOnce));
            if (frm.getBackStackEntryCount() == 0) {
                if (doubleBackToExitPressedOnce) {
                    Log.v("double exit 2", String.valueOf(doubleBackToExitPressedOnce));
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                   // finish();
                    //onQuitPressed();
                    return;
                   // System.exit(1);
                    //finish();
                }
                Log.v("double exit 3", String.valueOf(doubleBackToExitPressedOnce));
                doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                        Log.v("double exit 4", String.valueOf(doubleBackToExitPressedOnce));
                    }
                }, 3000);
            } else {
                Fragment currentFragment = frm.findFragmentByTag("people");
                if (currentFragment instanceof PeopleFragment) {
                    if (toolbar.getVisibility() == View.GONE) {
                        Draweradapter.mSelectedPositionTitle = "people";
                        if (!((PeopleFragment) currentFragment).slidepanelcheck()) {
                            ((PeopleFragment) currentFragment).backPressPeople();
                        } else {
                            hamburgerEnabled(true);
                            ((PeopleFragment) currentFragment).showToolbar();
                        }
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    } else if (!((PeopleFragment) currentFragment).slidepanelcheck()) {
                        Draweradapter.mSelectedPositionTitle = "people";
                        ((PeopleFragment) currentFragment).backPressPeople();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    } else {
                        Draweradapter.mSelectedPositionTitle = getString(R.string.overview);
                        //TODO could be changed to going to first screen if back pressed instead of the previous one.
                        frm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        FragmentTransaction ft = frm.beginTransaction();
                        getSupportActionBar().setTitle(getString(R.string.overview));
                        ft.replace(R.id.content_frame, attendanceFragment);
                        ft.commit();
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                    }

                } else {
                    Draweradapter.mSelectedPositionTitle = getString(R.string.overview);
                    //TODO could be changed to going to first screen if back pressed instead of the previous one.
                    frm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    FragmentTransaction ft = frm.beginTransaction();
                    getSupportActionBar().setTitle(getString(R.string.overview));
                    ft.replace(R.id.content_frame, attendanceFragment);
                    ft.commit();
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }
    }
    public void onQuitPressed() {

        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
    @Override
    public void hamburgerEnabled(boolean value) {
        mDrawerToggle.setDrawerIndicatorEnabled(value);
        toolbar.getMenu().getItem(0).setEnabled(value);
        toolbar.getMenu().getItem(1).setEnabled(value);
        toolbar.getMenu().getItem(2).setEnabled(value);
        if (value) Drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        else
            Drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    // to be used later to manage fragment and memory.
    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Log.v("current fragment", fragmentTag);
        return fragmentManager.findFragmentByTag(fragmentTag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: //Camera
                try {
                    if (data != null) {
                        Bitmap pic = (Bitmap) data.getExtras().get("data");
                        //new UploadImageWithData(this, serverUrl + "/api/v2/attendances/mark_attendance", pic, "").execute();
                        Log.v("upload test", "test");
                        if(offlineclicked){
                            attendance_dialog = true;
                            isoffline =true;
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String datetime = dateformat.format(c.getTime());
                            StringTokenizer tk = new StringTokenizer(datetime);

                            final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
                            final String mtime = tk.nextToken();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            pic.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
                            byte[] picdata = stream.toByteArray();
                            String userlat ="0.0";
                            String userlon="0.0";


                            if(sharedpreferences.getString("latitude", "")!=null) {
                                if (sharedpreferences.getString("latitude", "").equalsIgnoreCase("null")) {
                                    userlat = "0.0";
                                } else {
                                    userlat = sharedpreferences.getString("latitude", "");
                                }
                                if (sharedpreferences.getString("longitude", "").equalsIgnoreCase("null")) {
                                    userlon = "0.0";
                                } else {
                                    userlon = sharedpreferences.getString("longitude", "");
                                }
                            }else{
                                userlon = "0.0";
                                userlat = "0.0";
                            }
                            System.out.println("USER LAT "+userlat);
                            System.out.println("USER LON "+userlon);
                           if(meterDistanceBetweenPoints(Float.valueOf(String.valueOf(lat)), Float.valueOf(String.valueOf(lon)),
                                    Float.valueOf(String.valueOf(userlat)), Float.valueOf(String.valueOf(userlon)))<=100){
                               saveNameToLocalStorage("present",loc,String.valueOf(lat),String.valueOf(lon),mtime,mdate,sharedpreferences.getString("token", ""),picdata, IN_TIME_NOT_SYNCED_WITH_SERVER);

                               Toast.makeText(MainActivity.this,"Attendance Marked in Offline Mode",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this,"You are out of range for marking attendance",Toast.LENGTH_SHORT).show();
                            }

                            System.out.println("DISTANCE " + meterDistanceBetweenPoints(Float.valueOf(String.valueOf(lat)), Float.valueOf(String.valueOf(lon)),
                                    Float.valueOf(String.valueOf(userlat)), Float.valueOf(String.valueOf(userlon))));
                            ;
                            System.out.println("DISTANCE lat lon " + lat + " " + lon);

                        }else {
                            new UploadTask().execute(pic);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 100: //Location
                if (!isGPSEnabled())
                    showSettingsAlert();
                break;
            case REQUEST_CODE_SPEECH_INPUT : {
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuery = result.get(0);
                    //  userText.setText(userQuery);
                    sendMessage("11","00",userQuery);
                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute(userQuery);

                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION_OUT_TIME:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isGPSEnabled())
                        showSettingsAlert();
                    else {
                        if (mark_in_time_present) {
                            location_pd = new ProgressDialog(MainActivity.this);
                            location_pd.setTitle(R.string.getting_location);
                            location_pd.setMessage(getString(R.string.please_wait));
                            location_pd.setCancelable(false);
                            location_pd.show();

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)  // always true
                            {
                                if (isMockLocations())
                                    Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " possible mock location"));
                                overflowMenuItem = LOCATION_REQUEST_TYPE_ATTENDANCE;

                                if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                                if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
                            }
                        } else
                            outTimeDialog();
                    }
                }
                break;

            case MY_PERMISSIONS_REQUEST_LOCATION_TRACK_ME:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isGPSEnabled())
                        showSettingsAlert();
                    else {
                        location_pd = new ProgressDialog(MainActivity.this);
                        location_pd.setTitle(R.string.getting_location);
                        location_pd.setMessage(getString(R.string.please_wait));
                        location_pd.setCancelable(false);
                        location_pd.show();

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)  // always true
                        {
                            if (isMockLocations())
                                Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " possible mock location"));
                            overflowMenuItem = LOCATION_REQUEST_TYPE_TRACKME;
                            if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                            if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);

                        }
                    }
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, 0);
                } else
                    Toast.makeText(this, R.string.camera_access, Toast.LENGTH_SHORT).show();
                break;
            case RECORD_REQUEST_CODE : {
                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Permission denied by User");
                }
                else
                {
                    Log.d(TAG, "Permission granted by user");
                }
                return;
            }
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(R.string.gps_settings);

        // Setting Dialog Message
        alertDialog.setMessage(R.string.gps_not_enabled);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);
        alertDialog.setCancelable(true);

        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 100);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {

        AppController.lat = location.getLatitude();
        AppController.lon = location.getLongitude();

        Log.v("changed_latitude", "" + AppController.lat);
        Log.v("changed_longitude", "" + AppController.lon);

        if (lat != 0 && lon != 0) {

            manager.removeUpdates(this);

            Geocoder coder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> address = coder.getFromLocation(lat, lon, 1);
                if (address.size() != 0) {
                    String addr = address.get(0).getAddressLine(0);
                    String city = address.get(0).getLocality();
                    loc = addr + ", " + city;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (location_pd.isShowing() || !activityrunnning)
                location_pd.cancel();

            switch (overflowMenuItem) {
                case "timeout":
                    Log.v("case in location change", overflowMenuItem);
                    if (mark_in_time_present) {


                        Log.v("location changed", String.valueOf(AppController.lon));

                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            startActivityForResult(intent, 0);
                        } else
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                    } else {
                        if (isTimeAutomatic(MainActivity.this)) {
                            outTimeApiCall();
                        }
                        else {
                            final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                            //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            pDialog.setTitleText("Set Automatic Time");
                            pDialog.setContentText("Please set Automatic Time and Date,without automatic time you can't mark attandance");
                            pDialog.setConfirmText("OK");
                            pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    pDialog.cancel();
                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                                }
                            })
                                    .show();
                        }
                    }
                    break;
                case "track_me":
                    if (!TRACK_ME_STATUS) {
                        TRACK_ME_STATUS = true;
                        createBeatLog((float) lat, (float) lon);

                    }
                    break;
                default:

                    break;
            }

        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.v("Location", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.v("Location", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.v("Location", "onProviderDisabled");
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int child_position = recyclerView.getChildLayoutPosition(child);
        Log.v("child_position_drawer", "" + child_position);
        if (child_position >= 0) {
            if (child_position == 0)
                child.setTag("Header");
            else {
                child.setTag(TITLES.get(child_position - 1));
                Draweradapter.mSelectedPositionTitle = TITLES.get(child_position - 1);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
            Log.v("child name", "" + child.getTag());
            Answers.getInstance().logCustom(new CustomEvent("Drawer " + child.getTag() + " Clicked"));

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Drawer.closeDrawers();
                FragmentManager fragmentManager = getSupportFragmentManager();
                ft = fragmentManager.beginTransaction();
                switch (child.getTag().toString()) {
                    case "Header":
                        Log.v("header", "clicked");
                        break;
                    case "Overview":
                        getSupportActionBar().setTitle(child.getTag().toString());
                        ft.replace(R.id.content_frame, attendanceFragment, "attendance").addToBackStack("attendance");
                        break;
                    case "Leaves":
                        getSupportActionBar().setTitle(child.getTag().toString());
                        ft.replace(R.id.content_frame, leaveFragment, "leaves").addToBackStack("leaves");
                        break;
                    case "Reimbursements":
                        getSupportActionBar().setTitle(child.getTag().toString());
                        ft.replace(R.id.content_frame, reimbursementsFragment, "reimbursements").addToBackStack("reimbursements");
                        break;
                    case "Timesheet":
                        getSupportActionBar().setTitle(child.getTag().toString());
                        ft.replace(R.id.content_frame, timesheetFragment, "timesheet").addToBackStack("timesheet");
                        break;
                    case "People":
                        getSupportActionBar().setTitle(child.getTag().toString());
                        ft.replace(R.id.content_frame, peopleFragment, "people").addToBackStack("people");

                        break;
                    case "Recruitment":
                        getSupportActionBar().setTitle(child.getTag().toString());
                        ft.replace(R.id.content_frame, recruitmentFragment, "recruitment").addToBackStack("recruitment");
                        break;

                    case "VFS Darpan":

                        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                        Intent i = new Intent(MainActivity.this, ActivityPDFView.class);
                        //  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i, bndlanimation);

                        break;
                    case "Logout":
                        boolean isdata = false;
                        Cursor cursor2 = db.getUnsyncedNames();
                        Cursor cursor3 = db.getUnsyncedOuttime();
                        if (cursor2.moveToFirst()) {
                            do {
                                    isdata=true;
                               // Toast.makeText(MainActivity.this,"Your mark attandance data will be lose if you logout",Toast.LENGTH_SHORT).show();
                                //logOutApiCall();
                            } while (cursor2.moveToNext());
                        }else{
                            isdata=false;
                            if (cursor3.moveToFirst()) {
                                do {
                                    isdata=true;
                                    // Toast.makeText(MainActivity.this,"Your mark attandance data will be lose if you logout",Toast.LENGTH_SHORT).show();
                                    //logOutApiCall();
                                } while (cursor3.moveToNext());
                            }else{
                                isdata=false;
                                logOutApiCall();
                            }
                        }
                        if(isdata){
                            showLogoutAlert();
                        }

                        break;
                    case "Mark In Time Offline":
                        System.out.println("isintimemarked "+isintimemarked);
                        if(isintimemarked) {

                            final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                            //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            pDialog.setTitleText("Already Marked");
                            pDialog.setContentText("Your Attendance is already marked");
                            pDialog.setConfirmText("OK");
                            pDialog.show();
                        }else{
                            if (isTimeAutomatic(MainActivity.this)) {
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String datetime = dateformat.format(c.getTime());
                                StringTokenizer tk = new StringTokenizer(datetime);

                                final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
                                final String mtime = tk.nextToken();
                                Cursor cursor = db.getUnsyncedNames();



                                            if (cursor.moveToFirst()) {
                                                do {
                                                    String DATE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                                                    if (DATE.equalsIgnoreCase(mdate)) {
                                                        Log.v("onResume DATE", "IF");
                                                        Toast.makeText(MainActivity.this, "You have already marked offline attandance", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.v("Offline intime clicked", "ELSE");
                                                        offlineclicked = true;
                                                        mark_in_time_present = true;
                                                        Answers.getInstance().logCustom(new CustomEvent("present marked"));
                                                        Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " present click"));
                                                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                                            ActivityCompat.requestPermissions(
                                                                    MainActivity.this,
                                                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                                                    MY_PERMISSIONS_REQUEST_LOCATION_OUT_TIME);
                                                        else if (!isGPSEnabled())
                                                            showSettingsAlert();
                                                        else {
                                                            location_pd = new ProgressDialog(MainActivity.this);
                                                            location_pd.setTitle(R.string.getting_location);
                                                            location_pd.setMessage(getString(R.string.please_wait));
                                                            location_pd.setCancelable(false);
                                                            location_pd.show();

                                                            if (isMockLocations())
                                                                Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " possible mock location"));
                                                            overflowMenuItem = LOCATION_REQUEST_TYPE_ATTENDANCE;
                                                            if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                                                                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                                                            if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                                                                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);


                                                        }
                                                    }

                                                } while (cursor.moveToNext());
                                            } else {
                                                Log.v("Offline intime clicked", "ELSE");
                                                offlineclicked = true;
                                                mark_in_time_present = true;
                                                Answers.getInstance().logCustom(new CustomEvent("present marked"));
                                                Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " present click"));
                                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                                    ActivityCompat.requestPermissions(
                                                            MainActivity.this,
                                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                                            MY_PERMISSIONS_REQUEST_LOCATION_OUT_TIME);
                                                else if (!isGPSEnabled())
                                                    showSettingsAlert();
                                                else {
                                                    location_pd = new ProgressDialog(MainActivity.this);
                                                    location_pd.setTitle(R.string.getting_location);
                                                    location_pd.setMessage(getString(R.string.please_wait));
                                                    location_pd.setCancelable(false);
                                                    location_pd.show();

                                                    if (isMockLocations())
                                                        Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " possible mock location"));
                                                    overflowMenuItem = LOCATION_REQUEST_TYPE_ATTENDANCE;
                                                    if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                                                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                                                    if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                                                        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
                                                }
                                            }

                            } else {
                                final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                                //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                pDialog.setTitleText("Set Automatic Time");
                                pDialog.setContentText("Please set Automatic Time and Date,without automatic time you can't mark attandance");
                                pDialog.setConfirmText("OK");
                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        pDialog.cancel();
                                        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                                    }
                                })
                                        .show();
                            }
                        }

                        break;
                    default:
                        Log.v("default", "clicked");
                        break;
                }
                ft.commit();
                return true;
            }
        }
        return false;
    }
public void showLogoutAlert(){
    final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
    //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
    pDialog.setTitleText("Logout");
    pDialog.setContentText("Your attandance will be lose.Do you want Logout?");
    pDialog.setConfirmText("Yes");
    pDialog.setCancelText("No");
    pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            pDialog.cancel();
            MainActivity.this.deleteDatabase(DatabaseHelper.DB_NAME);
            logOutApiCall();
        }
    })
            .show();
}
    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }
    private class UploadTask extends AsyncTask<Bitmap, Integer, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected Void doInBackground(Bitmap... bitmaps) {
            if (bitmaps[0] == null)
                return null;
            Bitmap bitmap = bitmaps[0];
            Log.v("pic size before", "" + bitmap.getByteCount() + " B");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            byte[] data = stream.toByteArray();
            Log.v("pic size after", "" + data.length + " B");
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = dateformat.format(c.getTime());
            StringTokenizer tk = new StringTokenizer(datetime);

            final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
            final String mtime = tk.nextToken();
            System.out.println("mdate : "+mdate);
            System.out.println("mtime : "+mtime);
             //saveNameToLocalStorage("present",loc,String.valueOf(lat),String.valueOf(lon),mtime,mdate,sharedpreferences.getString("token", ""),data, NAME_NOT_SYNCED_WITH_SERVER);
             //  saveNameToLocalStorage("present",loc,String.valueOf(lat),String.valueOf(lon),"10:30:21","2019-05-21",sharedpreferences.getString("token", ""),data, NAME_NOT_SYNCED_WITH_SERVER);
            //  saveNameToLocalStorage("present",loc,String.valueOf(lat),String.valueOf(lon),"10:00:05","2019-05-22",sharedpreferences.getString("token", ""),data, NAME_NOT_SYNCED_WITH_SERVER);
            // attendanceFragmentDataType.setInTime("Your Attandnce in offline mode");
          // isoffline = true;
          //  offlineclicked = true;
            try {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                ByteArrayBody bab = new ByteArrayBody(data, System.currentTimeMillis() + ".png");
                builder.addPart("myFile", bab);
                builder.addPart("status", new StringBody("present", ContentType.TEXT_PLAIN));

                // Log.v("location GPS",loc);

                builder.addPart("Address", new StringBody(loc, ContentType.TEXT_PLAIN));
                builder.addPart("Latitude", new StringBody(String.valueOf(lat), ContentType.TEXT_PLAIN));
                builder.addPart("Longitude", new StringBody(String.valueOf(lon), ContentType.TEXT_PLAIN));
                builder.addPart("date", new StringBody(mdate, ContentType.TEXT_PLAIN));
                builder.addPart("time", new StringBody(mtime, ContentType.TEXT_PLAIN));

                HttpEntity entity = builder.build();
                Log.v("http_entity", "" + entity);

                URL url = new URL(serverUrl + "/api/v2/attendances/mark_attendance");
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("token", sharedpreferences.getString("token", ""));
                conn.setRequestProperty("appVersion", appVersion);
                conn.setRequestProperty("apiVersion", apiVersion);
                conn.setRequestProperty("Accept", "application/json");
//                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Length", "" + reqEntity.getContentLength());
//                conn.setRequestProperty("Content-Language", "en-US");
                conn.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //conn.connect();

                long len = entity.getContentLength();
                Log.v("Full Data Size", "" + len + " B");
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    Log.v("total", "" + total);
                    publishProgress((int) ((total * 100) / len));
                }

                entity.writeTo(conn.getOutputStream());

                final int responseCode = conn.getResponseCode();
                Log.v("response Code", "" + responseCode);
                Log.v("response error", "" + conn.getErrorStream());
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder("");
                    String line;
                    while ((line = in.readLine()) != null)
                        sb.append(line);
                    in.close();
                    final JSONObject response_server = new JSONObject(sb.toString());

                    Log.v("response String", "" + sb);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (response_server.getString("status").equalsIgnoreCase("true")) {
                                    isoffline = false;
                                    Toast.makeText(MainActivity.this, R.string.attendance_marked, Toast.LENGTH_SHORT).show();
                                    Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " present successful"));
                                    // saveNameToLocalStorage("present",loc,String.valueOf(lat),String.valueOf(lon),time,date,sharedpreferences.getString("token", ""),data, NAME_SYNCED_WITH_SERVER);


                                }
                                else {
                                    isoffline = false;
                                    Toast.makeText(MainActivity.this, ""+response_server.getString("message"), Toast.LENGTH_SHORT).show();
                                    // Log.v("response error", "" + conn.getErrorStream());
                                    attendance_dialog = true;
                                    //saveNameToLocalStorage("present",loc,String.valueOf(lat),String.valueOf(lon),mtime,mdate,sharedpreferences.getString("token", ""),data, NAME_NOT_SYNCED_WITH_SERVER);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }
                else {
                    // Toast.makeText(MainActivity.this,"Your attandance in offline mode due to server issue" , Toast.LENGTH_SHORT).show();
                    attendance_dialog = true;
                    isoffline =true;
                    offlineclicked = true;
                    saveNameToLocalStorage("present",loc,String.valueOf(lat),String.valueOf(lon),mtime,mdate,sharedpreferences.getString("token", ""),data, IN_TIME_NOT_SYNCED_WITH_SERVER);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (progressDialog != null)
                if (progressDialog.isShowing())
                    progressDialog.cancel();
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.cancel();
            inTimeApiCall();
            if(isoffline){
                Toast.makeText(MainActivity.this,"Your attandance in offline mode due to server issue" , Toast.LENGTH_SHORT).show();
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Offline Marked")
                        .setContentText("Your attandance in offline mode due to server issue")
                        .setConfirmText("OK")
                        .show();
            }else{
                // Toast.makeText(MainActivity.this,"else Your attandance in offline mode due to server issue" , Toast.LENGTH_SHORT).show();

            }
        }


    }
    private void saveNameToLocalStorage(String attandance_status,String address,String lat,String lon,String time,String date,String token,byte[] image, int status) {
        db.addAttandance(attandance_status,address,lat ,lon,time,date,token,image,status);
        Name n = new Name(attandance_status,address,lat ,lon,time,image,status);
        names.add(n);
        //refreshList();
    }

    private void saveOutTimeToLocalStorage(String address,String lat,String lon,String time,String date,String token, int status) {
        db.addAttandanceOuttime(address,lat ,lon,time,date,token,status);
        System.out.println("outtime local storage >> "+address+lat +lon+time+date+token+status);
        OutTime n = new OutTime(address,lat ,lon,time,status);
        outTime.add(n);
        //refreshList();
    }
    private ArrayList<String> createDrawerTitles() {

        ArrayList<String> title = new ArrayList<>();
        title.add(getString(R.string.overview));
        title.add(getString(R.string.leaves));
        title.add(getString(R.string.reimbursements));
        if (sharedpreferences.getString("allow_employee_directory", "").equalsIgnoreCase("true"))
            title.add(getString(R.string.people));
        if (sharedpreferences.getString("allow_timesheet", "").equalsIgnoreCase("true"))
            title.add(getString(R.string.timesheet));
        title.add("Mark In Time Offline");
       // title.add("Mark Out Time Offline");

        //title.add(getString(R.string.recruitment));
        if(sharedpreferences.getString("darpan_url", "")!=null) {
            if (sharedpreferences.getString("darpan_url", "").contains("pdf")) {
                title.add("VFS Darpan");

            }
        }
        title.add(getString(R.string.menu_tem_logout));

        return title;
    }

    private ArrayList<Integer> createDrawerIcons(ArrayList<String> titles) {

        ArrayList<Integer> icon = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            switch (titles.get(i)) {
                case "Overview":
                    icon.add(R.drawable.globe);
                    break;
                case "Leaves":
                    icon.add(R.drawable.leaves);
                    break;
                case "Reimbursements":
                    icon.add(R.drawable.reimbursement);
                    break;
                case "People":
                    icon.add(R.drawable.people);
                    break;
                case "Timesheet":
                    icon.add(R.drawable.timesheet);
                    break;
                case "Mark In Time Offline":
                    icon.add(R.drawable.attendance_mark);
                    break;
                case "VFS Darpan":
                    if(sharedpreferences.getString("darpan_url", "")!=null) {
                        if (sharedpreferences.getString("darpan_url", "").contains("pdf")) {
                            icon.add(R.drawable.pdf);
                        }
                    }
                    break;
                case "Logout":
                    icon.add(R.drawable.logout);
                    break;
                default:
                    icon.add(android.R.color.transparent);
                    break;
            }
        }
        return icon;
    }

    private void userNotFound() {

        Bundle bndlanimation = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i, bndlanimation);
        AppController.getInstance().cancelPendingRequests(TAG);

    }

    private boolean isGPSEnabled() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void outTimeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.Timeout));
        alertDialog.setMessage(getString(R.string.mark_outtime));
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                location_pd = new ProgressDialog(MainActivity.this);
                location_pd.setTitle(getString(R.string.getting_location));
                location_pd.setMessage(getString(R.string.please_wait));
                location_pd.setCancelable(false);
                location_pd.show();

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)  // always true
                {
                    if (isMockLocations())
                        Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " possible mock location"));
                    overflowMenuItem = LOCATION_REQUEST_TYPE_ATTENDANCE;
                    if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                    if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
                }
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void trackMeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.track_me));
        alertDialog.setMessage(getString(R.string.track_me_message));
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                location_pd = new ProgressDialog(MainActivity.this);
                location_pd.setTitle(getString(R.string.getting_location));
                location_pd.setMessage(getString(R.string.please_wait));
                location_pd.setCancelable(false);
                location_pd.show();
                TRACK_ME_STATUS = false;

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)  // always true
                {
                    if (isMockLocations())
                        Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " possible mock location"));
                    overflowMenuItem = LOCATION_REQUEST_TYPE_TRACKME;
                    if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                    if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
                }
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void askForFeedback() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.dialog_ask_for_feedback, null);
        builder.setView(convertView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button yes = convertView.findViewById(R.id.yes);
        Button no = convertView.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                feedbackForm();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void feedbackForm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.dialog_feedback_form, null);
        builder.setView(convertView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final ArrayList<String> feedback_titles = createFeedbackTitles();
        final ArrayList<Integer> feedback_icons = createFeedbackIcons(feedback_titles);
        FeedbackAdapter adapter = new FeedbackAdapter(this, feedback_titles, feedback_icons);
        GridView grid = convertView.findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.v("clicked feedback", feedback_titles.get(position));
            }
        });
    }

    private ArrayList<String> createFeedbackTitles() {

        ArrayList<String> title = new ArrayList<>();
        title.add(getString(R.string.excellent));
        title.add(getString(R.string.good));
        title.add(getString(R.string.poor));
        title.add(getString(R.string.horrible));

        return title;
    }

    private ArrayList<Integer> createFeedbackIcons(ArrayList<String> titles) {

        ArrayList<Integer> icon = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            switch (titles.get(i)) {
                case "Excellent":
                    icon.add(R.drawable.excellent);
                    break;
                case "Good":
                    icon.add(R.drawable.good);
                    break;
                case "Poor":
                    icon.add(R.drawable.poor);
                    break;
                case "Horrible":
                    icon.add(R.drawable.horrible);
                    break;
                default:
                    icon.add(android.R.color.transparent);
                    break;
            }
        }
        return icon;
    }

    /*private void logOutApiCall() {

        AppController.logout(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("loggedin", false);
                        editor.putString("username", "");
                        editor.putString("password", "");
                        editor.putString("role", "");
                        editor.putString("email", "");
                        editor.putString("name", "");
                        editor.putString("token", "");
                        editor.putString("passport_image", "");
                        editor.putString("allow_attendance", "");
                        editor.putString("allow_employee_directory", "");
                        editor.putString("allow_timesheet", "");
                        //editor.putString("last_updated_time", "");
                        editor.apply();

                        approve_size = 0;
                        alert_size = 0;
                        holidays.clear();
                        reimbursement_array.clear();
                        leaves_array.clear();
                        timesheet_approved_array.clear();
                        timesheet_rejected_array.clear();
                        timesheet_upload_array.clear();
                        leave_type_array.clear();
                        leave_reason_array.clear();
                        leave_reason_array.clear();
                        alert_data_array.clear();
                        people_data_array.clear();

                        userNotFound();
                    } else {
                        Draweradapter.mSelectedPositionTitle = getString(R.string.overview);
                        // dialoglogin.dismiss();
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Draweradapter.mSelectedPositionTitle = getString(R.string.overview);
                errorFunction(MainActivity.this, volleyError);

            }
        });

    }*/
    private void logOutApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().logout();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                    if(jObject!=null){
                        if(jObject.code()==200){

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("loggedin", false);
                        editor.putString("username", "");
                        editor.putString("password", "");
                        editor.putString("role", "");
                        editor.putString("email", "");
                        editor.putString("name", "");
                        editor.putString("token", "");
                        editor.putString("passport_image", "");
                        editor.putString("allow_attendance", "");
                        editor.putString("allow_employee_directory", "");
                        editor.putString("allow_timesheet", "");
                        //editor.putString("last_updated_time", "");
                        editor.apply();

                        approve_size = 0;
                        alert_size = 0;
                        holidays.clear();
                        reimbursement_array.clear();
                        leaves_array.clear();
                        timesheet_approved_array.clear();
                        timesheet_rejected_array.clear();
                        timesheet_upload_array.clear();
                        leave_type_array.clear();
                        leave_reason_array.clear();
                        leave_reason_array.clear();
                        alert_data_array.clear();
                        people_data_array.clear();

                        userNotFound();
                    } else {
                        Draweradapter.mSelectedPositionTitle = getString(R.string.overview);
                        // dialoglogin.dismiss();
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                        }
                    }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
                Draweradapter.mSelectedPositionTitle = getString(R.string.overview);
               // errorFunction(MainActivity.this, volleyError);

            }
        });

    }
   /* private void selectLeaveApiCall() {

        AppController.select_leave(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    leave_type_array.clear();
                    leave_reason_array.clear();
                    String message = jsonObject.getString("message");
                    Log.v("select_leave_message", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("leave_type").length(); i++) {
                            LeaveTypeDatatype a = new LeaveTypeDatatype();
                            a.setType(jsonObject.getJSONObject("data").getJSONArray("leave_type").getJSONObject(i).getString("leave_type"));
                            a.setId(jsonObject.getJSONObject("data").getJSONArray("leave_type").getJSONObject(i).getString("leave_id"));
                            leave_type_array.add(a);
                        }
                        for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("leave_reasons").length(); i++) {
                            LeaveTypeDatatype b = new LeaveTypeDatatype();
                            b.setType(jsonObject.getJSONObject("data").getJSONArray("leave_reasons").getJSONObject(i).getString("reason_type"));
                            b.setId(jsonObject.getJSONObject("data").getJSONArray("leave_reasons").getJSONObject(i).getString("reason_id"));
                            leave_reason_array.add(b);
                        }
                        //can go null if view not available handled in adapter and here.
                        //other way would be use a boolean to handle it.
                        LeaveFragment.notifychange();
                    } else if (message.equalsIgnoreCase("User not found")) {
                        userNotFound();
                    } else {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        selectLeaveApiCall();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }*/

   private void selectLeaveApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().select_leave();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null) {
                    if (jObject.code() == 200) {

                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());

                            leave_type_array.clear();
                            leave_reason_array.clear();
                            String message = jsonObject.getString("message");
                            Log.v("select_leave_message", message);
                            if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                                for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("leave_type").length(); i++) {
                                    LeaveTypeDatatype a = new LeaveTypeDatatype();
                                    a.setType(jsonObject.getJSONObject("data").getJSONArray("leave_type").getJSONObject(i).getString("leave_type"));
                                    a.setId(jsonObject.getJSONObject("data").getJSONArray("leave_type").getJSONObject(i).getString("leave_id"));
                                    leave_type_array.add(a);
                                }
                                for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("leave_reasons").length(); i++) {
                                    LeaveTypeDatatype b = new LeaveTypeDatatype();
                                    b.setType(jsonObject.getJSONObject("data").getJSONArray("leave_reasons").getJSONObject(i).getString("reason_type"));
                                    b.setId(jsonObject.getJSONObject("data").getJSONArray("leave_reasons").getJSONObject(i).getString("reason_id"));
                                    leave_reason_array.add(b);
                                }
                                //can go null if view not available handled in adapter and here.
                                //other way would be use a boolean to handle it.
                                LeaveFragment.notifychange();
                            } else if (message.equalsIgnoreCase("User not found")) {
                                userNotFound();
                            } else {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        selectLeaveApiCall();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

   /* private void markAttendanceCheckApiCall() {

        AppController.mark_attendance_check(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    Log.v("mark_attendance_check", message);
                    attendance_dialog = jsonObject.getBoolean("status");
                    if (jsonObject.getBoolean("status")) {
                        if (!jsonObject.getJSONObject("data").getBoolean("check_attendance_status")) {

                            Log.v("current_lon", "" + lon);
                            Log.v("current_lat", "" + lat);

                            //attendance dialog
                            dialog = new Dialog(MainActivity.this);
                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            Window window = dialog.getWindow();
                            if (window != null) {
                                lp.copyFrom(window.getAttributes());
                            }

                            //This makes the dialog take up the full width
                            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            lp.dimAmount = 0.5f;
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            dialog.setContentView(R.layout.dialog_mark_attendance);

                            dialog.setCancelable(true);  //false to set cancelable false
                            //todo set false

                            dialog.show();

                            if (window != null) {
                                window.setAttributes(lp);
                                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                window.setBackgroundDrawableResource(android.R.color.transparent);
                            }
                            Button present = dialog.findViewById(R.id.present);
                            //  Button cancel = (Button) dialog.findViewById(R.id.cancel);
                            Button outdoor = dialog.findViewById(R.id.outdoor);

                            present.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mark_in_time_present = true;
                                    Answers.getInstance().logCustom(new CustomEvent("present marked"));
                                    Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " present click"));
                                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                        ActivityCompat.requestPermissions(
                                                MainActivity.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_LOCATION_OUT_TIME);
                                    else if (!isGPSEnabled())
                                        showSettingsAlert();
                                    else {
                                        location_pd = new ProgressDialog(MainActivity.this);
                                        location_pd.setTitle(R.string.getting_location);
                                        location_pd.setMessage(getString(R.string.please_wait));
                                        location_pd.setCancelable(false);
                                        location_pd.show();

                                        if (isMockLocations())
                                            Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " possible mock location"));
                                        overflowMenuItem = LOCATION_REQUEST_TYPE_ATTENDANCE;
                                        if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                                            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                                        if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                                            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
                                    }
                                }
                            });

                            outdoor.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //TODO instead of absent convert to outdoor with reasons.
                                    Answers.getInstance().logCustom(new CustomEvent("absent marked"));
                                    Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " absent click"));
                                    markAttendanceApiCall("absent");
                                }
                            });

                        }
                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "attendance_check");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                errorFunction(MainActivity.this, volleyError);
            }
        });

    }*/

    private void markAttendanceCheckApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().mark_attendance_check();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null) {
                    if (jObject.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());
                            Log.v("atendnce_chk", jsonObject.toString());
                            String message = jsonObject.getString("message");
                            Log.v("mark_attendance_check", message);
                            attendance_dialog = jsonObject.getBoolean("status");
                            attandancecheck = jsonObject.getJSONObject("data").getBoolean("check_attendance_status");
                           // isintimemarked = jsonObject.getJSONObject("data").getBoolean("check_attendance_status");
                            if (jsonObject.getBoolean("status")) {
                                if (!jsonObject.getJSONObject("data").getBoolean("check_attendance_status")) {

                                    Log.v("current_lon", "" + lon);
                                    Log.v("current_lat", "" + lat);

                                    //attendance dialog
                                    dialog = new Dialog(MainActivity.this);
                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    Window window = dialog.getWindow();
                                    if (window != null) {
                                        lp.copyFrom(window.getAttributes());
                                    }

                                    //This makes the dialog take up the full width
                                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    lp.dimAmount = 0.5f;
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                    dialog.setContentView(R.layout.dialog_mark_attendance);

                                    dialog.setCancelable(true);  //false to set cancelable false
                                    //todo set false

                                    dialog.show();

                                    if (window != null) {
                                        window.setAttributes(lp);
                                        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                        window.setBackgroundDrawableResource(android.R.color.transparent);
                                    }
                                    Button present = dialog.findViewById(R.id.present);
                                    //  Button cancel = (Button) dialog.findViewById(R.id.cancel);
                                    Button outdoor = dialog.findViewById(R.id.outdoor);

                                    present.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (isTimeAutomatic(MainActivity.this)) {
                                                offlineclicked = false;
                                                mark_in_time_present = true;
                                                Answers.getInstance().logCustom(new CustomEvent("present marked"));
                                                Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " present click"));
                                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                                    ActivityCompat.requestPermissions(
                                                            MainActivity.this,
                                                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                                            MY_PERMISSIONS_REQUEST_LOCATION_OUT_TIME);
                                                else if (!isGPSEnabled())
                                                    showSettingsAlert();
                                                else {
                                                    location_pd = new ProgressDialog(MainActivity.this);
                                                    location_pd.setTitle(R.string.getting_location);
                                                    location_pd.setMessage(getString(R.string.please_wait));
                                                    location_pd.setCancelable(false);
                                                    location_pd.show();

                                                    if (isMockLocations())
                                                        Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " possible mock location"));
                                                    overflowMenuItem = LOCATION_REQUEST_TYPE_ATTENDANCE;
                                                    if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                                                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                                                    if (manager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                                                        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);
                                                }
                                            } else {
                                                final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                                                //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                pDialog.setTitleText("Set Automatic Time");
                                                pDialog.setContentText("Please set Automatic Time and Date,without automatic time you can't mark attandance");
                                                pDialog.setConfirmText("OK");
                                                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        pDialog.cancel();
                                                        startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0);
                                                    }
                                                })
                                                        .show();
                                            }
                                        }
                                    });

                                    outdoor.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //TODO instead of absent convert to outdoor with reasons.
                                            Answers.getInstance().logCustom(new CustomEvent("absent marked"));
                                            Answers.getInstance().logCustom(new CustomEvent(serverUrl + " " + sharedpreferences.getString("email", "") + " absent click"));
                                            markAttendanceApiCall("absent");
                                        }
                                    });

                                }
                            }
                            if (message.equalsIgnoreCase("User not found")) {

                                userNotFound();
                                //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "attendance_check");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

            //    errorFunction(MainActivity.this, volleyError);
            }
        });

    }

   /* private void approveLeaveApiCall() {

        AppController.leave_approve(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    leave_approve_array.clear();
                    String message = jsonObject.getString("message");
                    Log.v("leave_approve_message", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            LeaveApproveDatatype a = new LeaveApproveDatatype();
                            a.setLeave_id(jsonObject.getJSONArray("data").getJSONObject(i).getString("leave_id"));
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("user_name"));
                            a.setReason(jsonObject.getJSONArray("data").getJSONObject(i).getString("reason"));
                            a.setDate_from(jsonObject.getJSONArray("data").getJSONObject(i).getString("date_from"));
                            a.setDate_to(jsonObject.getJSONArray("data").getJSONObject(i).getString("date_to"));
                            a.setProfile(jsonObject.getJSONArray("data").getJSONObject(i).getString("passport_photo"));
                            leave_approve_array.add(a);
                        }
                        approve_size = jsonObject.getJSONArray("data").length();
                        Log.d("leave_approve_size", String.valueOf(approve_size));

                    } else if (message.equalsIgnoreCase("User not found")) {

                        approve_size = 0;
                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leave_approve");

                    } else {

                        approve_size = 0;


                    }

                    try {
                        invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    ApproveLeavesActivity.updateadapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                errorFunction(MainActivity.this, volleyError);
            }
        });

    }*/
   private void approveLeaveApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().leave_approve();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null) {
                    if (jObject.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());

                            leave_approve_array.clear();
                            String message = jsonObject.getString("message");
                            Log.v("leave_approve_message", message);
                            if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                                for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                                    LeaveApproveDatatype a = new LeaveApproveDatatype();
                                    a.setLeave_id(jsonObject.getJSONArray("data").getJSONObject(i).getString("leave_id"));
                                    a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("user_name"));
                                    a.setReason(jsonObject.getJSONArray("data").getJSONObject(i).getString("reason"));
                                    a.setDate_from(jsonObject.getJSONArray("data").getJSONObject(i).getString("date_from"));
                                    a.setDate_to(jsonObject.getJSONArray("data").getJSONObject(i).getString("date_to"));
                                    a.setProfile(jsonObject.getJSONArray("data").getJSONObject(i).getString("passport_photo"));
                                    leave_approve_array.add(a);
                                }
                                approve_size = jsonObject.getJSONArray("data").length();
                                Log.d("leave_approve_size", String.valueOf(approve_size));

                            } else if (message.equalsIgnoreCase("User not found")) {

                                approve_size = 0;
                                userNotFound();
                                //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leave_approve");

                            } else {

                                approve_size = 0;


                            }

                            try {
                                invalidateOptionsMenu();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //can go null if view not available handled in adapter.
                            //other way would be use a boolean to handle it.
                            ApproveLeavesActivity.updateadapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
               // errorFunction(MainActivity.this, volleyError);
            }
        });

    }

    /*private void getAlertsApiCall() {

        AppController.get_alerts(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    alert_data_array.clear();
                    String message = jsonObject.getString("message");
                    Log.v("leave_approve_message", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("message_alert").length(); i++) {
                            AlertDataType a = new AlertDataType();
                            if (!jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getBoolean("read")) {
                                a.setRead(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("read"));
                                a.setSender_name(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_name"));
                                a.setReceiver_name(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("receiver_name"));
                                a.setCreated_date(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("created_at"));
                                a.setAlert_type(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("alert_type"));
                                a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
                                a.setFlag(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("flag"));
                                a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
                                a.setDescription(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("description"));
                                a.setId(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("id"));
                                alert_data_array.add(a);
                            }
                        }
                        alert_size = alert_data_array.size();

                    } else if (message.equalsIgnoreCase("User not found")) {

                        alert_size = 0;
                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leave_approve");

                    } else
                        alert_size = 0;

                    try {
                        invalidateOptionsMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //can go null if view not available handled in adapter.
                    //other way would be use a boolean to handle it.
                    AlertActivity.updateadapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                errorFunction(MainActivity.this, volleyError);
            }
        });

    }*/

    private void getAlertsApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().get_alerts();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                if(jObject!=null) {
                    if (jObject.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());
                            alert_data_array.clear();
                            String message = jsonObject.getString("message");
                            Log.v("leave_approve_message", message);
                            if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                                for (int i = 0; i < jsonObject.getJSONObject("data").getJSONArray("message_alert").length(); i++) {
                                    AlertDataType a = new AlertDataType();
                                    if (!jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getBoolean("read")) {
                                        a.setRead(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("read"));
                                        a.setSender_name(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_name"));
                                        a.setReceiver_name(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("receiver_name"));
                                        a.setCreated_date(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("created_at"));
                                        a.setAlert_type(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("alert_type"));
                                        a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
                                        a.setFlag(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("flag"));
                                        a.setSender_image(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("sender_image"));
                                        a.setDescription(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("description"));
                                        a.setId(jsonObject.getJSONObject("data").getJSONArray("message_alert").getJSONObject(i).getString("id"));
                                        alert_data_array.add(a);
                                    }
                                }
                                alert_size = alert_data_array.size();

                            } else if (message.equalsIgnoreCase("User not found")) {

                                alert_size = 0;
                                userNotFound();
                                //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "leave_approve");

                            } else
                                alert_size = 0;

                            try {
                                invalidateOptionsMenu();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //can go null if view not available handled in adapter.
                            //other way would be use a boolean to handle it.
                            AlertActivity.updateadapter();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

               // errorFunction(MainActivity.this, volleyError);
            }
        });

    }

   /* private void markAttendanceApiCall(final String value) {

        AppController.mark_attendance(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    Log.v("mark_attendance_msg", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        if (jsonObject.getJSONObject("data").getBoolean("mark_attendance_status")) {
                            Toast.makeText(MainActivity.this, R.string.attendance_marked, Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            inTimeApiCall();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        markAttendanceApiCall(value);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        }, value);

    }*/
   private void markAttendanceApiCall(final String value) {
        JSONObject attendance_details = new JSONObject();
        try {
            attendance_details.put("status", value);
            //attendance_details.put("Address", loc);
            //attendance_details.put("Latitude", lat);
            //attendance_details.put("Longitude", lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(attendance_details))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "attendance_details", String.valueOf(attendance_details));
        Call<JsonObject> call = ApiUtil.getServiceClass().mark_attendance(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    Log.v("mark_attendance_msg", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {

                        if (jsonObject.getJSONObject("data").getBoolean("mark_attendance_status")) {
                            Toast.makeText(MainActivity.this, R.string.attendance_marked, Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                            inTimeApiCall();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.try_again, Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        markAttendanceApiCall(value);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

    }

    /*private void inTimeApiCall() {

        AppController.in_time(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        if (AppController.attendanceFragmentDataType != null) {
                            attendanceFragmentDataType.setInTime(jsonObject.getJSONObject("data").getString("in_time"));
                            attendanceFragmentDataType.setOutTime(jsonObject.getJSONObject("data").getString("out_time"));
                        }
                        //null when calling from mark attendance

                        //null when calling from mark attendance
                        Log.v("check in_time", "" + jsonObject.getJSONObject("data").getString("in_time"));
                        Log.v("check out_time", "" + jsonObject.getJSONObject("data").getString("out_time"));
                        AttendanceFragment.refresh();

                        //CALL GEOFENCE HERE
                        if (isServicesOK()) {

                            Intent locationIntent = new Intent(MainActivity.this, ActivityGeoFenceMap.class);
                            startActivity(locationIntent);

                        }
                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                    //attendanceFragment null when calling from mark attendance no need to call salary slip then.
                } catch (JSONException e) {
                    salarySlipApiCall();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                errorFunction(MainActivity.this, volleyError);
            }
        });

    }*/

    private void inTimeApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().in_time();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {


                if(jObject!=null) {
                    if (jObject.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(jObject.body().toString());
                            String message = jsonObject.getString("message");
                            if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                                if (AppController.attendanceFragmentDataType != null) {
                                    attendanceFragmentDataType.setInTime(jsonObject.getJSONObject("data").getString("in_time"));
                                    attendanceFragmentDataType.setOutTime(jsonObject.optJSONObject("data").optString("out_time"));
                                    isoffline = false;
                                    isintimemarked = true;
                                    if(jsonObject.optJSONObject("data").has("out_time")){
                                        isouttimemarked = true;
                                        System.out.println("ISOUTTIMEMARKED : "+jsonObject.toString());
                                    }
                                }

                                //null when calling from mark attendance

                                //null when calling from mark attendance
                                Log.v("check in_time", "" + jsonObject.getJSONObject("data").getString("in_time"));
                                Log.v("check out_time", "" + jsonObject.optJSONObject("data").optString("out_time"));
                                AttendanceFragment.refresh();

                                //CALL GEOFENCE HERE
                      /*  if (isServicesOK()) {

                            Intent locationIntent = new Intent(MainActivity.this, ActivityGeoFenceMap.class);
                            startActivity(locationIntent);

                        }*/
                            }
                            if (message.equalsIgnoreCase("User not found")) {

                                userNotFound();
                                // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                            }
                            //attendanceFragment null when calling from mark attendance no need to call salary slip then.
                        } catch (JSONException e) {
                            salarySlipApiCall();
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
              //  errorFunction(MainActivity.this, volleyError);
            }
        });

    }


    /*private void salarySlipApiCall() {

        AppController.salary_slip(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        if (AppController.attendanceFragmentDataType != null) {
                            attendanceFragmentDataType.setIncome_tax(jsonObject.getJSONObject("data").getString("income_tax"));
                            attendanceFragmentDataType.setProfessional_tax(jsonObject.getJSONObject("data").getString("professional_tax"));
                            attendanceFragmentDataType.setTotal_deduction(jsonObject.getJSONObject("data").getString("total_deduction_amount"));
                            attendanceFragmentDataType.setNet_credit(jsonObject.getJSONObject("data").getString("net_credit"));
                            attendanceFragmentDataType.setSalaryRange(jsonObject.getJSONObject("data").getString("from_date")+" - "+jsonObject.getJSONObject("data").getString("to_date"));
                        }
                        AttendanceFragment.refresh();
                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                errorFunction(MainActivity.this, volleyError);
            }
        });
    }*/

    private void salarySlipApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().salary_slip();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        if (AppController.attendanceFragmentDataType != null) {
                            attendanceFragmentDataType.setIncome_tax(jsonObject.getJSONObject("data").getString("income_tax"));
                            attendanceFragmentDataType.setProfessional_tax(jsonObject.getJSONObject("data").getString("professional_tax"));
                            attendanceFragmentDataType.setTotal_deduction(jsonObject.getJSONObject("data").getString("total_deduction_amount"));
                            attendanceFragmentDataType.setNet_credit(jsonObject.getJSONObject("data").getString("net_credit"));
                            attendanceFragmentDataType.setSalaryRange(jsonObject.getJSONObject("data").getString("from_date")+" - "+jsonObject.getJSONObject("data").getString("to_date"));
                        }
                        AttendanceFragment.refresh();
                    }
                    if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "mark_attendance");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

               // errorFunction(MainActivity.this, volleyError);
            }
        });
    }
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    /*private void holidayListApiCall() {

        AppController.list_holidays(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                try {
                    holidays.clear();
                    String message = jsonObject.getString("message");
                    Log.v("list_holidays_message", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            HolidayDatatype a = new HolidayDatatype();
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("name"));
                            a.setDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("date"));
                            a.setDay(jsonObject.getJSONArray("data").getJSONObject(i).getString("day"));
                            holidays.add(a);
                        }

                    } else if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "list_holidays");
                    } else {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    }
                    //    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //   editor.putString("token", jsonObject.getString("token"));
                    //   editor.apply();
                    List_Holidays_Fragment.notifychange();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                errorFunction(MainActivity.this, volleyError);
            }
        });
    }*/

    private void holidayListApiCall() {

        Call<JsonObject> call = ApiUtil.getServiceClass().list_holidays();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    holidays.clear();
                    String message = jsonObject.getString("message");
                    Log.v("listholiday", message);
                    if (jsonObject.getString("status").equalsIgnoreCase("true")) {
                        for (int i = 0; i < jsonObject.getJSONArray("data").length(); i++) {
                            HolidayDatatype a = new HolidayDatatype();
                            a.setName(jsonObject.getJSONArray("data").getJSONObject(i).getString("name"));
                            a.setDate(jsonObject.getJSONArray("data").getJSONObject(i).getString("date"));
                            a.setDay(jsonObject.getJSONArray("data").getJSONObject(i).getString("day"));
                            holidays.add(a);
                        }

                    } else if (message.equalsIgnoreCase("User not found")) {

                        userNotFound();
                        // login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "list_holidays");
                    } else {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    }
                    //    SharedPreferences.Editor editor = sharedpreferences.edit();
                    //   editor.putString("token", jsonObject.getString("token"));
                    //   editor.apply();
                    List_Holidays_Fragment.notifychange();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

            }
        });
    }

   /* private void outTimeApiCall() {

        AppController.outTime(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {
                try {
                    String message = jsonObject.getString("message");
                    Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();

                    if (jsonObject.getBoolean("status")) {
                        if (AppController.attendanceFragmentDataType != null)
                            AppController.attendanceFragmentDataType.setOutTime(jsonObject.getJSONObject("data").getString("out_time"));
                        AttendanceFragment.refresh();

                        //todo add when API ready
                        //askForFeedback();
                        if (isServicesOK()) {

                            Intent locationIntent = new Intent(MainActivity.this, ActivityGeoFenceMap.class);
                            startActivity(locationIntent);
                        }
                    }
                    if (message.equalsIgnoreCase("User not found")) {
                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "attendance_check");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {

                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        outTimeApiCall();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }*/

    private void outTimeApiCall() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = dateformat.format(c.getTime());
        StringTokenizer tk = new StringTokenizer(datetime);

        final String mdate = tk.nextToken();  // <---  yyyy-mm-dd
        final String mtime = tk.nextToken();
        JSONObject timeout_details = new JSONObject();
        try {
            timeout_details.put("Address", loc);
            timeout_details.put("Latitude", lat);
            timeout_details.put("Longitude", lon);
            timeout_details.put("date", mdate);
            timeout_details.put("time", mtime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(timeout_details))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "timeout_details", String.valueOf(timeout_details));
        Call<JsonObject> call = ApiUtil.getServiceClass().outTime(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {
                System.out.println("jObject.code() :: "+jObject.code());
                System.out.println("jObject.message() :: "+jObject.message());
                if(jObject!=null){
                    if(jObject.code()==200){

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());
                    String message = jsonObject.getString("message");
                    System.out.println("OUT TIME API CALL  "+jsonObject.toString());
                    Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();

                    if (jsonObject.getBoolean("status")) {
                        if (AppController.attendanceFragmentDataType != null)
                            AppController.attendanceFragmentDataType.setOutTime(jsonObject.getJSONObject("data").getString("out_time"));

                        AttendanceFragment.refresh();
                        if(jsonObject.optJSONObject("data").has("out_time")){
                            isouttimemarked = true;
                            System.out.println("ISOUTTIMEMARKED : "+jsonObject.toString());
                        }
                        //todo add when API ready
                        //askForFeedback();
                      /* if (isServicesOK()) {

                            Intent locationIntent = new Intent(MainActivity.this, ActivityGeoFenceMap.class);
                            startActivity(locationIntent);
                        }*/

                    }
                    if (message.equalsIgnoreCase("User not found")) {
                        userNotFound();
                        //login(context, sharedpreferences.getString("username", ""), sharedpreferences.getString("password", ""), "attendance_check");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                    }
                    else{

                        if (isouttimemarked) {
                            Toast.makeText(MainActivity.this, "Out time already present", Toast.LENGTH_SHORT).show();

                        }

                        else {
                            String userlat = "0.0";
                            String userlon = "0.0";


                            if (sharedpreferences.getString("latitude", "") != null) {
                                if (sharedpreferences.getString("latitude", "").equalsIgnoreCase("null")) {
                                    userlat = "0.0";
                                } else {
                                    userlat = sharedpreferences.getString("latitude", "");
                                }
                                if (sharedpreferences.getString("longitude", "").equalsIgnoreCase("null")) {
                                    userlon = "0.0";
                                } else {
                                    userlon = sharedpreferences.getString("longitude", "");
                                }
                            } else {
                                userlon = "0.0";
                                userlat = "0.0";
                            }
                            System.out.println("USER LAT " + userlat);
                            System.out.println("USER LON " + userlon);
                            if (meterDistanceBetweenPoints(Float.valueOf(String.valueOf(lat)), Float.valueOf(String.valueOf(lon)),
                                    Float.valueOf(String.valueOf(userlat)), Float.valueOf(String.valueOf(userlon))) <= 100) {


                                System.out.println("isintimemarked :: " + isintimemarked);

                                if (!isintimemarked) {
                                    Cursor cursor = db.getUnsyncedNames();

                                    if (cursor.moveToFirst()) {
                                        do {
                                            String DATE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                                            if (DATE.equalsIgnoreCase(mdate)) {
                                                Log.v("outtime DATE", "IF");
                                                Cursor cursorouttime = db.getUnsyncedOuttime();

                                                if (cursorouttime.moveToFirst()) {
                                                    do {
                                                        String DATEOUTTIME = cursorouttime.getString(cursorouttime.getColumnIndex(DatabaseHelper.COLUMN_DATE_OUTTIME));
                                                        if (DATEOUTTIME.equalsIgnoreCase(mdate)) {
                                                            Log.v("outtime DATE", DATEOUTTIME);
                                                            Toast.makeText(MainActivity.this, "You have already marked out time attandance in offline mode", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "outtime marked successfully in offline mode", Toast.LENGTH_SHORT).show();
                                                            saveOutTimeToLocalStorage(loc, String.valueOf(lat), String.valueOf(lon), mtime, mdate, sharedpreferences.getString("token", ""), OUT_TIME_NOT_SYNCED_WITH_SERVER);

                                                        }

                                                    } while (cursorouttime.moveToNext());
                                                } else {
                                                    Toast.makeText(MainActivity.this, "outtime marked successfully in offline mode", Toast.LENGTH_SHORT).show();
                                                    saveOutTimeToLocalStorage(loc, String.valueOf(lat), String.valueOf(lon), mtime, mdate, sharedpreferences.getString("token", ""), OUT_TIME_NOT_SYNCED_WITH_SERVER);

                                                }

                                            } else {
                                                final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                                                //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                pDialog.setTitleText("Mark In Time Today");
                                                pDialog.setContentText("Please mark intime First");
                                                pDialog.setConfirmText("OK");
                                                pDialog.show();
                                            }

                                        } while (cursor.moveToNext());
                                    } else {
                                        final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                                        //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        pDialog.setTitleText("Mark In Time");
                                        pDialog.setContentText("Please mark intime First");
                                        pDialog.setConfirmText("OK");
                                        pDialog.show();
                                    }

                                } else {

                                    Cursor cursor = db.getUnsyncedOuttime();

                                    if (cursor.moveToFirst()) {
                                        do {
                                            String DATE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_OUTTIME));
                                            if (DATE.equalsIgnoreCase(mdate)) {
                                                Log.v("onResume DATE", "IF");
                                                Toast.makeText(MainActivity.this, "You have already marked out time attandance in offline mode", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(MainActivity.this, "out time marked in offline mode", Toast.LENGTH_SHORT).show();
                                                saveOutTimeToLocalStorage(loc, String.valueOf(lat), String.valueOf(lon), mtime, mdate, sharedpreferences.getString("token", ""), OUT_TIME_NOT_SYNCED_WITH_SERVER);

                                            }

                                        } while (cursor.moveToNext());
                                    } else {
                                        // Toast.makeText(MainActivity.this, "Please mark In time first 2899", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(MainActivity.this, "out time marked in offline mode", Toast.LENGTH_SHORT).show();
                                        saveOutTimeToLocalStorage(loc, String.valueOf(lat), String.valueOf(lon), mtime, mdate, sharedpreferences.getString("token", ""), OUT_TIME_NOT_SYNCED_WITH_SERVER);

                                    }


                                }
                            } else {
                                Toast.makeText(MainActivity.this, "You are out of range for marking out time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else{
                    if (isouttimemarked) {
                        Toast.makeText(MainActivity.this, "Out time already present", Toast.LENGTH_SHORT).show();

                    }

                    else {
                        String userlat = "0.0";
                        String userlon = "0.0";


                        if (sharedpreferences.getString("latitude", "") != null) {
                            if (sharedpreferences.getString("latitude", "").equalsIgnoreCase("null")) {
                                userlat = "0.0";
                            } else {
                                userlat = sharedpreferences.getString("latitude", "");
                            }
                            if (sharedpreferences.getString("longitude", "").equalsIgnoreCase("null")) {
                                userlon = "0.0";
                            } else {
                                userlon = sharedpreferences.getString("longitude", "");
                            }
                        } else {
                            userlon = "0.0";
                            userlat = "0.0";
                        }
                        System.out.println("USER LAT " + userlat);
                        System.out.println("USER LON " + userlon);
                        if (meterDistanceBetweenPoints(Float.valueOf(String.valueOf(lat)), Float.valueOf(String.valueOf(lon)),
                                Float.valueOf(String.valueOf(userlat)), Float.valueOf(String.valueOf(userlon))) <= 100) {


                            if (!isintimemarked) {
                                Cursor cursor = db.getUnsyncedNames();

                                if (cursor.moveToFirst()) {
                                    do {
                                        String DATE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                                        if (DATE.equalsIgnoreCase(mdate)) {
                                            Log.v("outtime DATE", "IF");
                                            Cursor cursorouttime = db.getUnsyncedOuttime();

                                            if (cursorouttime.moveToFirst()) {
                                                do {
                                                    String DATEOUTTIME = cursorouttime.getString(cursorouttime.getColumnIndex(DatabaseHelper.COLUMN_DATE_OUTTIME));
                                                    if (DATEOUTTIME.equalsIgnoreCase(mdate)) {
                                                        Log.v("outtime DATE", DATEOUTTIME);
                                                        Toast.makeText(MainActivity.this, "You have already marked out time attandance ", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "outtime marked successfully in offline mode", Toast.LENGTH_SHORT).show();
                                                        saveOutTimeToLocalStorage(loc, String.valueOf(lat), String.valueOf(lon), mtime, mdate, sharedpreferences.getString("token", ""), OUT_TIME_NOT_SYNCED_WITH_SERVER);

                                                    }

                                                } while (cursorouttime.moveToNext());
                                            } else {
                                                Toast.makeText(MainActivity.this, "outtime marked successfully in offline mode", Toast.LENGTH_SHORT).show();
                                                saveOutTimeToLocalStorage(loc, String.valueOf(lat), String.valueOf(lon), mtime, mdate, sharedpreferences.getString("token", ""), OUT_TIME_NOT_SYNCED_WITH_SERVER);

                                            }

                                        } else {
                                            final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                                            //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            pDialog.setTitleText("Mark In Time Today");
                                            pDialog.setContentText("Please mark intime First");
                                            pDialog.setConfirmText("OK");
                                            pDialog.show();
                                        }

                                    } while (cursor.moveToNext());
                                } else {
                                    final SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                                    //new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    pDialog.setTitleText("Mark In Time");
                                    pDialog.setContentText("Please mark intime First");
                                    pDialog.setConfirmText("OK");
                                    pDialog.show();
                                }

                            } else {

                                Cursor cursor = db.getUnsyncedOuttime();

                                if (cursor.moveToFirst()) {
                                    do {
                                        String DATE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE_OUTTIME));
                                        if (DATE.equalsIgnoreCase(mdate)) {
                                            Log.v("onResume DATE", "IF");
                                            Toast.makeText(MainActivity.this, "You have already marked out time attandance ", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "out time marked in offline mode", Toast.LENGTH_SHORT).show();
                                            saveOutTimeToLocalStorage(loc, String.valueOf(lat), String.valueOf(lon), mtime, mdate, sharedpreferences.getString("token", ""), OUT_TIME_NOT_SYNCED_WITH_SERVER);

                                        }

                                    } while (cursor.moveToNext());
                                } else {
                                    // Toast.makeText(MainActivity.this, "Please mark In time first 2899", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(MainActivity.this, "out time marked in offline mode", Toast.LENGTH_SHORT).show();
                                    saveOutTimeToLocalStorage(loc, String.valueOf(lat), String.valueOf(lon), mtime, mdate, sharedpreferences.getString("token", ""), OUT_TIME_NOT_SYNCED_WITH_SERVER);

                                }

                            }
                        } else {
                            Toast.makeText(MainActivity.this, "You are out of range for marking out time", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        outTimeApiCall();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private boolean isMockLocations() {

       /* if (manager != null)
            try {
                Log.d(TAG, "Removing Test providers");
                //  manager.clearTestProviderEnabled(LocationManager.GPS_PROVIDER);
                // manager.clearTestProviderLocation(LocationManager.GPS_PROVIDER);
                // manager.clearTestProviderStatus(LocationManager.GPS_PROVIDER);
                manager.removeTestProvider(LocationManager.GPS_PROVIDER);


                // manager.clearTestProviderEnabled(LocationManager.NETWORK_PROVIDER);
                //manager.clearTestProviderLocation(LocationManager.NETWORK_PROVIDER);
                //manager.clearTestProviderStatus(LocationManager.NETWORK_PROVIDER);
                manager.removeTestProvider(LocationManager.NETWORK_PROVIDER);
            } catch (IllegalArgumentException error) {
                Log.d(TAG, "Got exception in removing test  provider");
            }
        Log.v("mock settings on", String.valueOf(isMockSettingsON(MainActivity.this)));
        Log.v("mock settings apps", String.valueOf(Utility.areThereMockPermissionApps(MainActivity.this)));

*/
        return isMockSettingsON(MainActivity.this) || Utility.areThereMockPermissionApps(MainActivity.this);

    }

    public boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        boolean isMocknetwork = false;
        boolean isMockGPS = false;
        Location locationGPS = new Location(LocationManager.GPS_PROVIDER);
        Location locationNetwork = new Location(LocationManager.NETWORK_PROVIDER);
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMocknetwork = locationGPS.isFromMockProvider();
            isMockGPS = locationNetwork.isFromMockProvider();
        } else {
            isMocknetwork = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            isMockGPS = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
        return isMockGPS || isMocknetwork;

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.v("view mainactivity", "detached");
    }

    private void createBeatLog(final float latitude, float longitude) {
        TRACK_ME_STATUS = true;
        JSONObject request = new JSONObject();
        try {
            //request.put("user_id", "140110");
            request.put("lat", latitude);
            request.put("long", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(request))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG + "request", String.valueOf(request));
        Call<JsonObject> call = ApiUtil.getServiceClass().beatlog(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                Log.d(getClass().getName(), jsonObject.toString());

                Toast.makeText(MainActivity.this, R.string.location_marked, Toast.LENGTH_SHORT).show();

                    statusDialog(
                            getAddress(jsonObject.getJSONObject("data").getDouble("lat"),
                                    jsonObject.getJSONObject("data").getDouble("long"))
                                    + "\n at " + jsonObject.getJSONObject("data").getString("created_at"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());

                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        createBeatLog(latitude, latitude);
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
               // Log.d(getClass().getName(), "Error: " + volleyError.getMessage());
            }
        });
    }

   /* private void createBeatLog(final float latitude, float longitude) {
        TRACK_ME_STATUS = true;

        AppController.beatlog(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {

                Log.d(getClass().getName(), jsonObject.toString());

                Toast.makeText(MainActivity.this, R.string.location_marked, Toast.LENGTH_SHORT).show();
                try {
                    statusDialog(
                            getAddress(jsonObject.getJSONObject("data").getDouble("lat"),
                                    jsonObject.getJSONObject("data").getDouble("long"))
                                    + "\n at " + jsonObject.getJSONObject("data").getString("created_at"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(R.string.noconnection).setCancelable(false);

                alertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        createBeatLog(latitude, latitude);
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Log.d(getClass().getName(), "Error: " + volleyError.getMessage());
            }
        }, latitude, longitude);
    }*/


    private void statusDialog(String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.location_marked));
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.US);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = "Coordinates : " + lat + ", " + lng;
            add = add + "\n\n" + obj.getAddressLine(0);
            // add = add + "\n" + obj.getCountryName();
            // add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            // add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            return add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            //  Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "Coordinates : " + lat + ", " + lng + "\n" + getString(R.string.address_not_found);
        }
    }

    public void showCustomDialog(final Activity context, String message) {
        dialogchatbot = new Dialog(context);


        dialogchatbot.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogchatbot.setContentView(R.layout.fragment_chatbot);
        dialogchatbot.setCancelable(true);
       // dialogchatbot.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogchatbot.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //    ((TextView) dialog.findViewById(R.id.content)).setText(message);
        listen = dialogchatbot.findViewById(R.id.listen);
        send = dialogchatbot.findViewById(R.id.send);
        textInput = dialogchatbot.findViewById(R.id.textInput);

        recyclerView = dialogchatbot.findViewById(R.id.recycler_view);
        mChatAdapter = new ChatAdapter(getApplicationContext(),mChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mChatAdapter);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    t1.setLanguage(Locale.UK);

                }
            }
        });
        int permission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if(permission != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreate: "+ "permission to record denied ");
            makeRequest();
        }
        final AIConfiguration config = new AIConfiguration("09e76e401a25409e88187deb99223215",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(getApplicationContext(), config);
        aiService.setListener(this);

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
                aiService.startListening();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = textInput.getText().toString();

                if(input.isEmpty()){

                }else {
                    sendMessage("11", "00", input);
                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute(input);
                    textInput.setText("");
                }
            }
        });

        dialogchatbot.show();
        dialogchatbot.getWindow().setAttributes(lp);
    }
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something");

        try{
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException e ){
            Toast.makeText(getApplicationContext(),"Your device doesn't supports speech input",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String s, String s1, String userQuery)
    {
        Chat newChat = new Chat();
        newChat.setSender(s);
        newChat.setReciever(s1);
        newChat.setMessage(userQuery);
        mChat.add(newChat);

        mChatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition((mChat.size()));

    }


    private void makeRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
    }

    public String getText(String query)  throws UnsupportedEncodingException {
        String text = "";
        BufferedReader reader  = null;

        try {
            URL url = new URL("https://api.dialogflow.com/v1/query?v=20150910");

            //send POST Data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "Bearer 09e76e401a25409e88187deb99223215");
            conn.setRequestProperty("Content-Type", "application/json");

            //create JSON Object here

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("query",query);
            jsonParam.put("lang","en");
            jsonParam.put("sessionId","1234567890");

            //use Output Stream to send data with this post request
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.d(TAG, "getText: after conversation is "+ jsonParam.toString());
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d(TAG, "karma "+ " json is "+ jsonParam);

            //Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder ab = new StringBuilder();
            String line = null;

            //Read server response
            while ((line = reader.readLine()) != null){
                ab.append(line + "\n");
            }

            text = ab.toString();


            //access
            JSONObject jsonObject = new JSONObject(text);
            JSONObject object  = jsonObject.getJSONObject("result");
            JSONObject fullfillment  = null;
            String speech = null;

            fullfillment = object.getJSONObject("fulfillment");

            speech = fullfillment.optString("speech");

            Log.d(TAG, "karma "+ "response is : "+ text);



            return  speech;


        }

        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try{
                reader.close();
            }
            catch (Exception ex){

            }
        }
        return  null;
    }

    class RetrieveFeedTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String s = null;
            try
            {
                Log.d(TAG, "doInBackground: called");
                s = getText(strings[0]);
                Log.d(TAG, "doInBackground: after called");
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
                Log.d(TAG, "doInBackground: "+ e);
            }
            return s;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            t1.speak(s,TextToSpeech.QUEUE_FLUSH,null);
            Log.d(TAG, "onPostExecute: "+ s);
            //   responseText.setText(s);
            Chat newChat = new Chat();
            newChat.setSender("00");
            newChat.setReciever("11");
            newChat.setMessage(s);
            mChat.add(newChat);
            //    readMessages("00","11",s);
            mChatAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition((mChat.size()));
            if(s.contains("redirect")){
                /*Intent in = new Intent(getApplicationContext(),SplashScreenActivity.class);
                startActivity(in);
                getApplicationContext().finish();*/
                dialogchatbot.dismiss();
                if(attandancecheck){
                    Toast.makeText(MainActivity.this,"You have already marked Attandance",Toast.LENGTH_SHORT).show();
                   // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                   // StrictMode.setThreadPolicy(policy);
                   // aiService.resetContexts();

                }else{
                   // Toast.makeText(MainActivity.this,"Status false",Toast.LENGTH_SHORT).show();

                    markAttendanceCheckApiCall();
                }

            }

            else if(s.contains("opening apply leave")){
                dialogchatbot.dismiss();
                FragmentManager fragmentManager = getSupportFragmentManager();
                ft = fragmentManager.beginTransaction();
                getSupportActionBar().setTitle(getString(R.string.leaves));
                ft.replace(R.id.content_frame, leaveFragment, "leaves").addToBackStack("leaves");
                    ft.commit();
            }

            else if(s.contains("opening Approve leaves List")){
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                Intent i = new Intent(MainActivity.this, ApproveLeavesActivity.class);
                startActivity(i, bndlanimation);

            }
            else if(s.contains("opening Timesheet")){
                dialogchatbot.dismiss();
                if (sharedpreferences.getString("allow_timesheet", "").equalsIgnoreCase("true")) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ft = fragmentManager.beginTransaction();
                    getSupportActionBar().setTitle(getString(R.string.timesheet));
                    ft.replace(R.id.content_frame, timesheetFragment, "timesheet").addToBackStack("timesheet");
                    ft.commit();
                }
                else{
                    Toast.makeText(MainActivity.this,"Timesheet not available",Toast.LENGTH_SHORT).show();
                }
            }
            else if(s.contains("opening People")){
                dialogchatbot.dismiss();
                if (sharedpreferences.getString("allow_employee_directory", "").equalsIgnoreCase("true")){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ft = fragmentManager.beginTransaction();
                    getSupportActionBar().setTitle(getString(R.string.people));
                    ft.replace(R.id.content_frame, peopleFragment, "people").addToBackStack("people");
                    ft.commit();
                }
                else{
                    Toast.makeText(MainActivity.this,"People not available",Toast.LENGTH_SHORT).show();
                }
            }
            else if(s.contains("opening Reimbursements")){
                dialogchatbot.dismiss();
                FragmentManager fragmentManager = getSupportFragmentManager();
                ft = fragmentManager.beginTransaction();
                getSupportActionBar().setTitle(getString(R.string.leaves));
                ft.replace(R.id.content_frame, reimbursementsFragment, "reimbursements").addToBackStack("reimbursements");
                ft.commit();
            }
            else if(s.contains("opening Alert")){
                dialogchatbot.dismiss();
                Bundle bndlanimatio = ActivityOptions.makeCustomAnimation(MainActivity.this, R.anim.anim_out, R.anim.anim_in).toBundle();
                Intent intent = new Intent(MainActivity.this, AlertActivity.class);
                startActivity(intent, bndlanimatio);
            }
            else if(s.contains("Opening Track me")){
                dialogchatbot.dismiss();
                overflowMenuItem = LOCATION_REQUEST_TYPE_TRACKME;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION_TRACK_ME);
                else if (!isGPSEnabled())
                    showSettingsAlert();
                else
                    trackMeDialog();
            }
            else if(s.contains("Opening Tracking Map")){
                dialogchatbot.dismiss();
                Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(mapIntent);
            }
            else{
                //    Toast.makeText(MainActivity.this,"NOT FOUND ",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readMessages(String sender,String receiver,String message){

        Chat myChat = new Chat(sender,receiver,message);
        mChat.add(myChat);
        mChatAdapter.notifyDataSetChanged();



    }

    @Override
    public void onResult(AIResponse result) {
        Log.d(TAG, "onResult: "+ result.toString());
        Result result1 = result.getResult();

        //     textView.setText("Query "+ result1.getResolvedQuery()+" Action: "+ result1.getAction());


    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override

        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName()  + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }


        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                if (Float.valueOf(currentVersion) <Float.valueOf(onlineVersion)) {
                    //show anything
                    //Toast.makeText(MainActivity.this,"Please update app",Toast.LENGTH_SHORT).show();
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("New Version Found")
                            .setContentText("Please update App")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    // reuse previous dialog instance
                                    try {
                                        Intent viewIntent =
                                                new Intent("android.intent.action.VIEW",
                                                        Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                                        startActivity(viewIntent);
                                    }catch(Exception e) {
                                        Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                                                Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                    sDialog.dismiss();
                                }
                            })
                            .show();

                }

            }

            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);

        }
    }
}
