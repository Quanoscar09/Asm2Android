package com.example.asm2android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private EditText donorNameInput, donorEmailInput, donorPhoneInput;
    private Spinner donationSiteSpinner;
    private Button registerButton;

    private DonorHelper donorHelper;
    private DonationSiteHelper siteHelper;
    private SQLiteDatabase donorDb, siteDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize UI components
        donorNameInput = findViewById(R.id.donorNameInput);
        donorEmailInput = findViewById(R.id.donorEmailInput);
        donorPhoneInput = findViewById(R.id.donorPhoneInput);
        donationSiteSpinner = findViewById(R.id.donationSiteSpinner);
        registerButton = findViewById(R.id.registerButton);

        // Initialize database helpers
        donorHelper = new DonorHelper(this);
        siteHelper = new DonationSiteHelper(this);

        donorDb = donorHelper.getWritableDatabase();
        siteDb = siteHelper.getReadableDatabase();

        // Populate donation site spinner or set the selected site
        populateDonationSiteSpinner();

        // Handle the register button click
        registerButton.setOnClickListener(v -> registerDonor());
    }

    private void populateDonationSiteSpinner() {
        // Check if the activity received a specific site name from the intent
        String selectedSite = getIntent().getStringExtra("siteName");
        if (selectedSite != null) {
            // Populate spinner with only the selected site name and disable the spinner
            List<String> siteNames = new ArrayList<>();
            siteNames.add(selectedSite);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, siteNames);
            donationSiteSpinner.setAdapter(adapter);

            // Disable the spinner to prevent changes
            donationSiteSpinner.setEnabled(false);
        } else {
            // Populate spinner with all site names from the database
            List<String> siteNames = new ArrayList<>();
            Cursor cursor = siteHelper.getAllSites(siteDb);

            while (cursor.moveToNext()) {
                siteNames.add(cursor.getString(cursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_NAME)));
            }
            cursor.close();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, siteNames);
            donationSiteSpinner.setAdapter(adapter);
        }
    }

    private void registerDonor() {
        // Get user inputs
        String donorName = donorNameInput.getText().toString().trim();
        String donorEmail = donorEmailInput.getText().toString().trim();
        String donorPhone = donorPhoneInput.getText().toString().trim();
        String selectedSite = (String) donationSiteSpinner.getSelectedItem();

        // Validate inputs
        if (donorName.isEmpty() || donorEmail.isEmpty() || donorPhone.isEmpty() || selectedSite == null) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save donor data into the database
        donorHelper.registerDonor(donorDb, donorName, donorEmail, donorPhone, selectedSite);
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

        // Close the activity
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connections
        donorDb.close();
        siteDb.close();
    }
}
