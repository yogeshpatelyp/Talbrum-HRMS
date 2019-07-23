package com.talentcerebrumhrms.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Belal on 1/27/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "DBAttandance";
    public static final String TABLE_NAME = "tblattandance";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ATTANDANCE_STATUS = "attandance_status";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_Address = "Address";
    public static final String COLUMN_Latitude = "Latitude";
    public static final String COLUMN_Longitude = "Longitude";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DATE= "date";
    public static final String COLUMN_TOKEN= "token";
    public static final String COLUMN_IMAGE = "image";

    public static final String TABLE_OUTTIME = "tblouttime";
    public static final String COLUMN_ID_OUTTIME = "idouttime";
    public static final String COLUMN_STATUS_OUTTIME = "statusouttime";
    public static final String COLUMN_Address_OUTTIME = "Addressouttime";
    public static final String COLUMN_Latitude_OUTTIME = "Latitudeouttime";
    public static final String COLUMN_Longitude_OUTTIME = "Longitudeouttime";
    public static final String COLUMN_TIME_OUTTIME = "timeouttime";
    public static final String COLUMN_DATE_OUTTIME= "dateouttime";
    public static final String COLUMN_TOKEN_OUTTIME= "tokenouttime";

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ""
                + COLUMN_ATTANDANCE_STATUS + " VARCHAR, "
                + COLUMN_Address + " VARCHAR, "
                + COLUMN_Latitude+ " VARCHAR, "
                + COLUMN_Longitude + " VARCHAR, "
                + COLUMN_TIME + " VARCHAR, "
                + COLUMN_DATE + " VARCHAR, "
                + COLUMN_TOKEN + " VARCHAR, "
                + COLUMN_IMAGE + " BLOB, "
                + COLUMN_STATUS + " TINYINT);";
        db.execSQL(sql);

        String sql2 = "CREATE TABLE " + TABLE_OUTTIME
                + "(" + COLUMN_ID_OUTTIME + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ""
                + COLUMN_Address_OUTTIME + " VARCHAR, "
                + COLUMN_Latitude_OUTTIME+ " VARCHAR, "
                + COLUMN_Longitude_OUTTIME + " VARCHAR, "
                + COLUMN_TIME_OUTTIME + " VARCHAR, "
                + COLUMN_DATE_OUTTIME + " VARCHAR, "
                + COLUMN_TOKEN_OUTTIME + " VARCHAR, "
                + COLUMN_STATUS_OUTTIME + " TINYINT);";
        db.execSQL(sql2);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(sql);
        String sql2 = "DROP TABLE IF EXISTS "+TABLE_OUTTIME;
        db.execSQL(sql2);
        onCreate(db);
    }

    /*
    * This method is taking two arguments
    * first one is the name that is to be saved
    * second one is the status
    * 0 means the name is synced with the server
    * 1 means the name is not synced with the server
    * */
    public boolean addAttandance(String attandance_status,String address,String lat,String lon,String time,String date,String token,byte[] image, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_ATTANDANCE_STATUS, attandance_status);
        contentValues.put(COLUMN_Address, address);
        contentValues.put(COLUMN_Latitude, lat);
        contentValues.put(COLUMN_Longitude, lon);
        contentValues.put(COLUMN_TIME, time);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_TOKEN, token);
        contentValues.put(COLUMN_IMAGE, image);
        contentValues.put(COLUMN_STATUS, status);


        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean addAttandanceOuttime(String address,String lat,String lon,String time,String date,String token, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_Address_OUTTIME, address);
        contentValues.put(COLUMN_Latitude_OUTTIME, lat);
        contentValues.put(COLUMN_Longitude_OUTTIME, lon);
        contentValues.put(COLUMN_TIME_OUTTIME, time);
        contentValues.put(COLUMN_DATE_OUTTIME, date);
        contentValues.put(COLUMN_TOKEN_OUTTIME, token);

        contentValues.put(COLUMN_STATUS_OUTTIME, status);


        db.insert(TABLE_OUTTIME, null, contentValues);
        db.close();
        return true;
    }
    public void delete(long _id) {
        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + "=" + _id, null);
    }
    public void deleteouttime(long _id) {
        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(DatabaseHelper.TABLE_OUTTIME, DatabaseHelper.COLUMN_ID_OUTTIME + "=" + _id, null);
    }
    /*
    * This method taking two arguments
    * first one is the id of the name for which
    * we have to update the sync status
    * and the second one is the status that will be changed
    * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }
    public boolean updateOuttimeStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS_OUTTIME, status);
        db.update(TABLE_OUTTIME, contentValues, COLUMN_ID_OUTTIME + "=" + id, null);
        db.close();
        return true;
    }

    /*
    * this method will give us all the name stored in sqlite
    * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
    * this method is for getting all the unsynced name
    * so that we can sync it with database
    * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
    public Cursor getUnsyncedOuttime() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_OUTTIME + " WHERE " + COLUMN_STATUS_OUTTIME + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }
}
