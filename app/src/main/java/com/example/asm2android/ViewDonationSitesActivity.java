package com.example.asm2android;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewDonationSitesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation_sites);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Load donation sites from Firestore
        db.collection("donationSites").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String siteName = document.getString("siteName");
                        String siteAddress = document.getString("siteAddress");
                        String donationHours = document.getString("donationHours");
                        String requiredBloodTypes = document.getString("requiredBloodTypes");

                        double latitude = document.getDouble("latitude");
                        double longitude = document.getDouble("longitude");

                        // Add markers to the map
                        LatLng siteLocation = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(siteLocation)
                                .title(siteName)
                                .snippet("Address: " + siteAddress +
                                        "\nHours: " + donationHours +
                                        "\nBlood Types: " + requiredBloodTypes));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(siteLocation, 12));
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
