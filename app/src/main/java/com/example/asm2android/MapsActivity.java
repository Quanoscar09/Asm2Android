package com.example.asm2android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

    private Button backButton, searchButton;
    private EditText bloodTypeInput, eventDateInput;

    private HashMap<Marker, String> siteIdMap = new HashMap<>();
    private LatLng currentLatLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize database helper and location client
        dbHelper = new DonationSiteHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize UI elements
        backButton = findViewById(R.id.backButton);
        searchButton = findViewById(R.id.filterButton);
        bloodTypeInput = findViewById(R.id.bloodTypeInput);
        eventDateInput = findViewById(R.id.eventDateInput);

        // Set button click listeners
        backButton.setOnClickListener(v -> onBackPressed());
        searchButton.setOnClickListener(v -> searchDonationSites());

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

        // Enable map controls
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Check and enable location permissions
        if (checkLocationPermissions()) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }

        // Load markers for donation sites
        loadSitesFromDatabase();

        // Marker click listener to show details
        mMap.setOnMarkerClickListener(marker -> {
            String siteId = siteIdMap.get(marker);
            if (siteId != null) {
                showMarkerDetailsDialog(siteId);
            }
            return false;
        });
    }

    private boolean checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void getCurrentLocation() {
        if (!checkLocationPermissions()) return;

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("Your Location"));
            } else {
                Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSitesFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.getAllSites(db);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                addMarkerForSite(cursor);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void addMarkerForSite(Cursor cursor) {
        int siteId = cursor.getInt(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_ID));
        String siteName = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_NAME));
        String siteAddress = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_ADDRESS));
        double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_LONGITUDE));

        LatLng location = new LatLng(latitude, longitude);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(siteName)
                .snippet(siteAddress));
        if (marker != null) {
            siteIdMap.put(marker, String.valueOf(siteId));
        }
    }

    private void showMarkerDetailsDialog(String siteId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DonationSiteHelper.TABLE_DONATION_SITES, null,
                DonationSiteHelper.SITE_ID + "=?", new String[]{siteId},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String siteName = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_NAME));
            String siteAddress = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_ADDRESS));
            String bloodTypes = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_BLOOD_TYPES));
            String eventDate = cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_EVENT_DATE));
            cursor.close();

            new AlertDialog.Builder(this)
                    .setTitle(siteName)
                    .setMessage("Address: " + siteAddress + "\n" +
                            "Blood Types Needed: " + bloodTypes + "\n" +
                            "Event Date: " + eventDate)
                    .setPositiveButton("Register", (dialog, which) -> {
                        // Open Registration Activity with the selected site name
                        Intent intent = new Intent(MapsActivity.this, RegistrationActivity.class);
                        intent.putExtra("siteName", siteName);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            Toast.makeText(this, "Unable to fetch site details.", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchDonationSites() {
        String bloodType = bloodTypeInput.getText().toString().trim();
        String eventDate = eventDateInput.getText().toString().trim();

        if (bloodType.isEmpty() && eventDate.isEmpty()) {
            Toast.makeText(this, "Please enter blood type or event date to search.", Toast.LENGTH_SHORT).show();
            return;
        }

        mMap.clear();
        if (currentLatLng != null) {
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your Location"));
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = "";
        String[] selectionArgs = null;

        if (!bloodType.isEmpty() && !eventDate.isEmpty()) {
            selection = DonationSiteHelper.SITE_BLOOD_TYPES + " LIKE ? AND " +
                    DonationSiteHelper.SITE_EVENT_DATE + " = ?";
            selectionArgs = new String[]{"%" + bloodType + "%", eventDate};
        } else if (!bloodType.isEmpty()) {
            selection = DonationSiteHelper.SITE_BLOOD_TYPES + " LIKE ?";
            selectionArgs = new String[]{"%" + bloodType + "%"};
        } else {
            selection = DonationSiteHelper.SITE_EVENT_DATE + " = ?";
            selectionArgs = new String[]{eventDate};
        }

        Cursor cursor = db.query(DonationSiteHelper.TABLE_DONATION_SITES, null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            LatLng firstLocation = null;
            do {
                addMarkerForSite(cursor);
                if (firstLocation == null) {
                    firstLocation = new LatLng(
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_LATITUDE)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_LONGITUDE)));
                }
            } while (cursor.moveToNext());
            cursor.close();

            if (firstLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15));
            }
        } else {
            Toast.makeText(this, "No matching donation sites found.", Toast.LENGTH_SHORT).show();
        }
    }
}
