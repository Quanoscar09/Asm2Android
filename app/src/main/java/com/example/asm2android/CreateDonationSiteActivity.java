package com.example.asm2android;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CreateDonationSiteActivity extends AppCompatActivity {

    private EditText siteNameInput, siteAddressInput, donationHoursInput, requiredBloodTypesInput, latitudeInput, longitudeInput, siteIdInput, eventDateInput;
    private Button addSiteButton, showSitesButton, deleteSiteButton, deleteByIdButton;

    private DonationSiteHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_donation_site);

        // Initialize UI components
        siteNameInput = findViewById(R.id.siteNameInput);
        siteAddressInput = findViewById(R.id.siteAddressInput);
        donationHoursInput = findViewById(R.id.donationHoursInput);
        requiredBloodTypesInput = findViewById(R.id.requiredBloodTypesInput);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        siteIdInput = findViewById(R.id.siteIdInput);
        eventDateInput = findViewById(R.id.eventDateInput); // New input field for event date
        addSiteButton = findViewById(R.id.addSiteButton);
        showSitesButton = findViewById(R.id.showSitesButton);
        deleteSiteButton = findViewById(R.id.deleteSiteButton);
        deleteByIdButton = findViewById(R.id.deleteByIdButton);

        // Initialize database helper
        dbHelper = new DonationSiteHelper(this);
        database = dbHelper.getWritableDatabase();

        // Button actions
        addSiteButton.setOnClickListener(v -> addDonationSite());
        showSitesButton.setOnClickListener(v -> showAllSites());
        deleteSiteButton.setOnClickListener(v -> deleteDonationSiteByName());
        deleteByIdButton.setOnClickListener(v -> deleteDonationSiteById());
    }

    private void addDonationSite() {
        // Retrieve and trim inputs
        String name = siteNameInput.getText().toString().trim();
        String address = siteAddressInput.getText().toString().trim();
        String hours = donationHoursInput.getText().toString().trim();
        String bloodTypes = requiredBloodTypesInput.getText().toString().trim();
        String latitudeStr = latitudeInput.getText().toString().trim();
        String longitudeStr = longitudeInput.getText().toString().trim();
        String eventDate = eventDateInput.getText().toString().trim(); // Retrieve event date

        // Check for empty fields
        if (name.isEmpty() || address.isEmpty() || hours.isEmpty() || bloodTypes.isEmpty() ||
                latitudeStr.isEmpty() || longitudeStr.isEmpty() || eventDate.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse latitude and longitude
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);

            // Validate event date format (yyyy-MM-dd)
            if (!eventDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert data into the database
            dbHelper.insertDonationSite(database, name, address, hours, bloodTypes, latitude, longitude, eventDate);

            // Notify user
            Toast.makeText(this, "Site added successfully!", Toast.LENGTH_SHORT).show();

            // Clear inputs after successful addition
            clearInputs();
        } catch (NumberFormatException e) {
            // Handle invalid latitude/longitude values
            Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAllSites() {
        Cursor cursor = dbHelper.getAllSites(database);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No sites found", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) {
            builder.append("ID: ").append(cursor.getInt(0)).append("\n");
            builder.append("Name: ").append(cursor.getString(1)).append("\n");
            builder.append("Address: ").append(cursor.getString(2)).append("\n");
            builder.append("Hours: ").append(cursor.getString(3)).append("\n");
            builder.append("Blood Types: ").append(cursor.getString(4)).append("\n");
            builder.append("Latitude: ").append(cursor.getDouble(5)).append("\n");
            builder.append("Longitude: ").append(cursor.getDouble(6)).append("\n");
            builder.append("Event Date: ").append(cursor.getString(7)).append("\n\n");
        }

        cursor.close();

        new AlertDialog.Builder(this)
                .setTitle("Donation Sites")
                .setMessage(builder.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void deleteDonationSiteByName() {
        String name = siteNameInput.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Enter site name to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        int rowsDeleted = database.delete(DonationSiteHelper.TABLE_DONATION_SITES, DonationSiteHelper.SITE_NAME + "=?", new String[]{name});
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Site deleted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No site found with the provided name", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDonationSiteById() {
        String idStr = siteIdInput.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Enter site ID to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            int rowsDeleted = database.delete(DonationSiteHelper.TABLE_DONATION_SITES, DonationSiteHelper.SITE_ID + "=?", new String[]{String.valueOf(id)});
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Site deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No site found with the provided ID", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid site ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        siteNameInput.setText("");
        siteAddressInput.setText("");
        donationHoursInput.setText("");
        requiredBloodTypesInput.setText("");
        latitudeInput.setText("");
        longitudeInput.setText("");
        siteIdInput.setText("");
        eventDateInput.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }
}
