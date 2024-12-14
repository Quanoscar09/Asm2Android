package com.example.asm2android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DonorHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Donors.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_DONORS = "Donors";
    public static final String DONOR_ID = "_id";
    public static final String DONOR_NAME = "name";
    public static final String DONOR_EMAIL = "email";
    public static final String DONOR_PHONE = "phone";
    public static final String DONOR_SITE_NAME = "siteName";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_DONORS + " (" +
                    DONOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DONOR_NAME + " TEXT NOT NULL, " +
                    DONOR_EMAIL + " TEXT NOT NULL, " +
                    DONOR_PHONE + " TEXT NOT NULL, " +
                    DONOR_SITE_NAME + " TEXT NOT NULL);";

    public DonorHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONORS);
        onCreate(db);
    }

    public void registerDonor(SQLiteDatabase db, String name, String email, String phone, String siteName) {
        ContentValues values = new ContentValues();
        values.put(DONOR_NAME, name);
        values.put(DONOR_EMAIL, email);
        values.put(DONOR_PHONE, phone);
        values.put(DONOR_SITE_NAME, siteName);
        db.insert(TABLE_DONORS, null, values);
    }
}
