package com.talentcerebrumhrms.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
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
    private static final String PREF_NAME = "TalbrumPref";
    private static final String IS_LOGIN = "IsLoggedIn";



    public static final String KEY_USER_EMAIL= "email";
   public static final String KEY_USER_PASS= "password";


    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public void createSaveCredentialSession(String email,String password){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_PASS, password);


        editor.commit();
    }
    public HashMap<String, String> getSaveCredentialSession(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USER_EMAIL, pref.getString(KEY_USER_EMAIL, null));
        user.put(KEY_USER_PASS, pref.getString(KEY_USER_PASS, null));


        return user;
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
           /* Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);*/
        }

    }


    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
       /* Intent i = new Intent(_context,MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);*/

    }


    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
