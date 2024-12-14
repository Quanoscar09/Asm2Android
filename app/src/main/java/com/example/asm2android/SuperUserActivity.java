package com.example.asm2android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SuperUserActivity extends AppCompatActivity {

    private Button viewAllSitesButton, generateReportsButton, logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_superuser);

        // Initialize UI components
        viewAllSitesButton = findViewById(R.id.viewAllSitesButton);
        generateReportsButton = findViewById(R.id.generateReportsButton);
        logOutButton = findViewById(R.id.logOutButton);

        // Set button listeners
        viewAllSitesButton.setOnClickListener(v -> {
            // Navigate to View All Sites Activity
            Intent intent = new Intent(SuperUserActivity.this, ViewDonationSitesActivity.class);
            startActivity(intent);
        });

        logOutButton.setOnClickListener(v -> {
            // Log out and navigate to LoginActivity
            Intent intent = new Intent(SuperUserActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
