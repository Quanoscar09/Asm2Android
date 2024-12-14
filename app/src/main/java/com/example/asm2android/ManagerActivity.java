package com.example.asm2android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ManagerActivity extends AppCompatActivity {
    private Button createDonationSiteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        createDonationSiteButton = findViewById(R.id.createDonationSiteButton);

        createDonationSiteButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, CreateDonationSiteActivity.class);
            startActivity(intent);
        });
    }
}
