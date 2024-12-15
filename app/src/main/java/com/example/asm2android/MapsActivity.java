package com.example.asm2android;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private GoogleMap mMap;
    private DonationSiteHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;

    private Button backButton;

    // HashMap to store site details with markers
    private HashMap<Marker, String> siteIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize database helper and location client
        dbHelper = new DonationSiteHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the Back Button
        backButton = findViewById(R.id.backButton);

        // Set Back Button click listener
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Enable the user's current location
        mMap.setMyLocationEnabled(true);

        // Load donation sites from the SQLite database and display them as markers on the map
        loadSitesFromDatabase();

        // Display the user's current location
        getCurrentLocation();

        // Handle marker clicks
        mMap.setOnMarkerClickListener(marker -> {
            String siteId = siteIdMap.get(marker);
            if (siteId != null) {
                // Retrieve the site details from the database and show the dialog
                showSiteDetailsDialog(siteId);
            }
            return false;
        });
    }

    private void loadSitesFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getAllSites(db);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve site details
                int siteId = cursor.getInt(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_ID));
                String siteName = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_NAME));
                String siteAddress = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_ADDRESS));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_LONGITUDE));

                // Create a LatLng object for the site's location
                LatLng location = new LatLng(latitude, longitude);

                // Add a marker for the site on the map
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(siteName)
                        .snippet(siteAddress));

                if (marker != null) {
                    // Map the marker to the site ID
                    siteIdMap.put(marker, String.valueOf(siteId));
                }
            } while (cursor.moveToNext());

            cursor.close();
        } else {
            // Notify the user if no sites are found
            Toast.makeText(this, "No donation sites found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCurrentLocation() {
        // Check location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Retrieve the last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                // Create a LatLng object for the user's current location
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Add a marker for the user's current location
                mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("Your Location"));

                // Move the camera to the user's current location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            } else {
                // Notify the user if the current location is unavailable
                Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSiteDetailsDialog(String siteId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DonationSiteHelper.TABLE_DONATION_SITES,
                null,
                DonationSiteHelper.SITE_ID + "=?",
                new String[]{siteId},
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            String siteName = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_NAME));
            String siteAddress = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_ADDRESS));
            String bloodTypes = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_BLOOD_TYPES));
            String donationHours = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_HOURS));

            cursor.close();

            // Show a dialog with site details
            new AlertDialog.Builder(this)
                    .setTitle(siteName)
                    .setMessage("Address: " + siteAddress + "\n" +
                            "Donation Hours: " + donationHours + "\n" +
                            "Blood Types Needed: " + bloodTypes)
                    .setPositiveButton("Register", (dialog, which) -> {
                        // Placeholder for registration logic
                        Toast.makeText(MapsActivity.this, "Register for " + siteName, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retrieve the current location
                getCurrentLocation();
            } else {
                // Notify the user that location permission is required
                Toast.makeText(this, "Location permission is required to display your current location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
