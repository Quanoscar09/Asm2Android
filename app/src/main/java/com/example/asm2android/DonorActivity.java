package com.example.asm2android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DonorActivity extends AppCompatActivity {

    private Button viewSitesButton, registerButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        // Initialize buttons
        viewSitesButton = findViewById(R.id.viewDonationSitesButton);
        registerButton = findViewById(R.id.registerDonationButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Set up button listeners
        viewSitesButton.setOnClickListener(v -> {
            Intent intent = new Intent(DonorActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(DonorActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(DonorActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
