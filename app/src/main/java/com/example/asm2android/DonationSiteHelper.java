package com.example.asm2android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DonationSiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BloodDonation.db";
    private static final int DB_VERSION = 2; // Increment version to trigger onUpgrade

    public static final String TABLE_DONATION_SITES = "DonationSites";
    public static final String SITE_ID = "_id";
    public static final String SITE_NAME = "name";
    public static final String SITE_ADDRESS = "address";
    public static final String SITE_HOURS = "hours";
    public static final String SITE_BLOOD_TYPES = "bloodTypes";
    public static final String SITE_LATITUDE = "latitude";
    public static final String SITE_LONGITUDE = "longitude";
    public static final String SITE_EVENT_DATE = "eventDate"; // New column for event date

    private static final String CREATE_TABLE_SITES =
            "CREATE TABLE " + TABLE_DONATION_SITES + " (" +
                    SITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SITE_NAME + " TEXT NOT NULL, " +
                    SITE_ADDRESS + " TEXT NOT NULL, " +
                    SITE_HOURS + " TEXT NOT NULL, " +
                    SITE_BLOOD_TYPES + " TEXT NOT NULL, " +
                    SITE_LATITUDE + " REAL NOT NULL, " +
                    SITE_LONGITUDE + " REAL NOT NULL, " +
                    SITE_EVENT_DATE + " TEXT NOT NULL);"; // Add event date column

    public DonationSiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_DONATION_SITES + " ADD COLUMN " + SITE_EVENT_DATE + " TEXT NOT NULL DEFAULT 'N/A';");
        }
    }

    public void insertDonationSite(SQLiteDatabase db, String name, String address, String hours, String bloodTypes, double latitude, double longitude, String eventDate) {
        ContentValues values = new ContentValues();
        values.put(SITE_NAME, name);
        values.put(SITE_ADDRESS, address);
        values.put(SITE_HOURS, hours);
        values.put(SITE_BLOOD_TYPES, bloodTypes);
        values.put(SITE_LATITUDE, latitude);
        values.put(SITE_LONGITUDE, longitude);
        values.put(SITE_EVENT_DATE, eventDate); // Add event date
        db.insert(TABLE_DONATION_SITES, null, values);
    }

    public Cursor getAllSites(SQLiteDatabase db) {
        return db.query(TABLE_DONATION_SITES, null, null, null, null, null, null);
    }
}
