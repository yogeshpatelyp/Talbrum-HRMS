package com.talentcerebrumhrms;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.talentcerebrumhrms.geofencing.SessionManagerTwo;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button buttonLocation;


    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    SessionManagerTwo session;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
            activity = MainActivity.this;

        session = new SessionManagerTwo(MainActivity.this);
      /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            startService(new Intent(MainActivity.this, GPSService.class));
             }
        else {
           MainActivity.this.startForegroundService(new Intent(MainActivity.this, GPSService.class));
              }*/

        buttonLocation=findViewById(R.id.buttonLocation);


       /* if (isServicesOK()) {
            buttonLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent locationIntent = new Intent(MainActivity.this, ActivityGeoFenceMap.class);
                    startActivity(locationIntent);
                }
            });
        }*/
        HashMap<String, String> mipush = session.getAutostart();
        String sttsmipush = mipush.get(SessionManagerTwo.KEY_AUTOSTART);
        //if(sttsmipush==null) {
            enableAutoStart();
      //  }
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

    private void enableAutoStart() {
        if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
            new MaterialDialog.Builder(activity).title("Enable AutoStart")
                    .content(
                            "Please allow AppName to always run in the background,else our services can't be accessed.")
                    .theme(Theme.LIGHT)
                    .positiveText("ALLOW")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            session.createAutostart("1");
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.securitycenter",
                                    "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivity(intent);
                        }
                    })
                    .show();
        } else if (Build.BRAND.equalsIgnoreCase("Letv")) {
            new MaterialDialog.Builder(activity).title("Enable AutoStart")
                    .content(
                            "Please allow AppName to always run in the background,else our services can't be accessed.")
                    .theme(Theme.LIGHT)
                    .positiveText("ALLOW")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            session.createAutostart("1");
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.letv.android.letvsafe",
                                    "com.letv.android.letvsafe.AutobootManageActivity"));
                            startActivity(intent);
                        }
                    })
                    .show();
        } else if (Build.BRAND.equalsIgnoreCase("Honor")) {
            new MaterialDialog.Builder(activity).title("Enable AutoStart")
                    .content(
                            "Please allow AppName to always run in the background,else our services can't be accessed.")
                    .theme(Theme.LIGHT)
                    .positiveText("ALLOW")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            session.createAutostart("1");
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager",
                                    "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivity(intent);
                        }
                    })
                    .show();
        } else if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
            new MaterialDialog.Builder(activity).title("Enable AutoStart")
                    .content(
                            "Please allow AppName to always run in the background,else our services can't be accessed.")
                    .theme(Theme.LIGHT)
                    .positiveText("ALLOW")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {
                                session.createAutostart("1");
                                startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.FakeActivity")));
                            } catch (Exception e) {
                                try {
                                    session.createAutostart("1");
                                    startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.StartupAppListActivity")));
                                } catch (Exception e1) {
                                    try {
                                        session.createAutostart("1");
                                        startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.StartupAppListActivity")));
                                    } catch (Exception e2) {
                                        try {
                                            session.createAutostart("1");
                                            startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startup.StartupAppListActivity")));
                                        } catch (Exception e3) {
                                            try {
                                                session.createAutostart("1");
                                                startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupapp.StartupAppListActivity")));
                                            } catch (Exception e4) {
                                                try {
                                                    session.createAutostart("1");
                                                    startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupmanager.StartupAppListActivity")));
                                                } catch (Exception e5) {
                                                    try {
                                                        session.createAutostart("1");
                                                        startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startsettings")));
                                                    } catch (Exception e6) {
                                                        try {
                                                            session.createAutostart("1");
                                                            startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.startupmanager")));
                                                        } catch (Exception e7) {
                                                            try {
                                                                session.createAutostart("1");
                                                                startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.startupActivity")));
                                                            } catch (Exception e8) {
                                                                try {
                                                                    session.createAutostart("1");
                                                                    startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.startupapp.startupmanager")));
                                                                } catch (Exception e9) {
                                                                    try {
                                                                        session.createAutostart("1");
                                                                        startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager")));
                                                                    } catch (Exception e10) {
                                                                        try {
                                                                            session.createAutostart("1");
                                                                            startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity")));
                                                                        } catch (Exception e11) {
                                                                            try {
                                                                                session.createAutostart("1");
                                                                                startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.FakeActivity")));
                                                                            } catch (Exception e12) {
                                                                                e12.printStackTrace();
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
                    .show();
        } else if (Build.MANUFACTURER.contains("vivo")) {
            new MaterialDialog.Builder(activity).title("Enable AutoStart")
                    .content(
                            "Please allow AppName to always run in the background.Our app runs in background else our services can't be accesed.")
                    .theme(Theme.LIGHT)
                    .positiveText("ALLOW")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            try {
                                session.createAutostart("1");
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("com.iqoo.secure",
                                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
                                startActivity(intent);
                            } catch (Exception e) {
                                try {
                                    session.createAutostart("1");
                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                                    startActivity(intent);
                                } catch (Exception ex) {
                                    try {
                                        session.createAutostart("1");
                                        Intent intent = new Intent();
                                        intent.setClassName("com.iqoo.secure",
                                                "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                                        startActivity(intent);
                                    } catch (Exception exx) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    })
                    .show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.e("MainActivity", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MainActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("MainActivity", "onRestart");
    }
}
