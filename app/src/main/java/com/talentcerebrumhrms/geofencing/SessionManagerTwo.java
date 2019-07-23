package com.talentcerebrumhrms.geofencing;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManagerTwo {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;
    Activity activity;
    // Sharedpref file name
    private static final String PREF_NAME = "GeoFencingprefrence";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
      public static final String KEY_UID = "user_id";
    public static final String KEY_IMAGEN = "imagen";
    public static final String KEY_UNAME = "user_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONE= "phone";
    public static final String KEY_VSTTS= "vstatus";
    public static final String KEY_UIMAGE= "uimage";
    public static final String KEY_UPARENTSCONTACT= "uparentcontact";


    public static final String KEY_APIKEY = "api_key";

    public static final String KEY_FIRSTTIME= "firsttime";

    public static final String KEY_AUTOSTART= "auto_start";


    // public static final String KEY_= "VideoidSession";
    public SessionManagerTwo(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void createfirsttime(String id){
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_FIRSTTIME, id);

        editor.commit();
    }
    public HashMap<String, String> getfirsttime(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_FIRSTTIME, pref.getString(KEY_FIRSTTIME, null));
        //user.put(KEY_UNAME, pref.getString(KEY_UNAME, null));
        // user email id

        return user;
    }
    public void createAutostart(String id){
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_AUTOSTART, id);

        editor.commit();
    }
    public HashMap<String, String> getAutostart(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_AUTOSTART, pref.getString(KEY_AUTOSTART, null));
        //user.put(KEY_UNAME, pref.getString(KEY_UNAME, null));
        // user email id

        return user;
    }

    public void createLoginSession(String id,String uname, String branchid, String companyname,String stts,String image,String parentscontact,String apikey){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UID, id);
        editor.putString(KEY_UNAME, uname);
        editor.putString(KEY_EMAIL, branchid);
        editor.putString(KEY_PHONE, companyname);
        editor.putString(KEY_VSTTS, stts);
        editor.putString(KEY_UIMAGE, image);
        editor.putString(KEY_UPARENTSCONTACT,parentscontact);
        editor.putString(KEY_APIKEY,apikey);
        editor.commit();
    }


    public HashMap<String, String> getLoginSession(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_UID, pref.getString(KEY_UID, null));
        user.put(KEY_UNAME, pref.getString(KEY_UNAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));
        user.put(KEY_UIMAGE ,pref.getString(KEY_UIMAGE, null));
        user.put(KEY_UPARENTSCONTACT ,pref.getString(KEY_UPARENTSCONTACT, null));
        user.put(KEY_APIKEY,pref.getString(KEY_APIKEY, null));
        // return user
        return user;
    }


    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
