package com.example.asm2android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DonationSiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BloodDonation.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_DONATION_SITES = "DonationSites";
    public static final String SITE_ID = "_id";
    public static final String SITE_NAME = "name";
    public static final String SITE_ADDRESS = "address";
    public static final String SITE_HOURS = "hours";
    public static final String SITE_BLOOD_TYPES = "bloodTypes";
    public static final String SITE_LATITUDE = "latitude";
    public static final String SITE_LONGITUDE = "longitude";

    private static final String CREATE_TABLE_SITES =
            "CREATE TABLE " + TABLE_DONATION_SITES + " (" +
                    SITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SITE_NAME + " TEXT NOT NULL, " +
                    SITE_ADDRESS + " TEXT NOT NULL, " +
                    SITE_HOURS + " TEXT NOT NULL, " +
                    SITE_BLOOD_TYPES + " TEXT NOT NULL, " +
                    SITE_LATITUDE + " REAL NOT NULL, " +
                    SITE_LONGITUDE + " REAL NOT NULL);";

    public DonationSiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONATION_SITES);
        onCreate(db);
    }

    public void insertDonationSite(SQLiteDatabase db, String name, String address, String hours, String bloodTypes, double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put(SITE_NAME, name);
        values.put(SITE_ADDRESS, address);
        values.put(SITE_HOURS, hours);
        values.put(SITE_BLOOD_TYPES, bloodTypes);
        values.put(SITE_LATITUDE, latitude);
        values.put(SITE_LONGITUDE, longitude);
        db.insert(TABLE_DONATION_SITES, null, values);
    }

    public Cursor getAllSites(SQLiteDatabase db) {
        return db.query(TABLE_DONATION_SITES, null, null, null, null, null, null);
    }
}
