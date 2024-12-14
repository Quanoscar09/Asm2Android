package com.example.asm2android;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ManagerActivity extends AppCompatActivity {
    private Button createDonationSiteButton, logOutButton, viewDonorListButton, deleteDonorButton, viewMapButton;
    private DonationSiteHelper siteHelper;
    private DonorHelper donorHelper;
    private SQLiteDatabase siteDb, donorDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        // Initialize UI components
        createDonationSiteButton = findViewById(R.id.createDonationSiteButton);
        logOutButton = findViewById(R.id.logOutButton);
        viewDonorListButton = findViewById(R.id.viewDonorListButton);
        deleteDonorButton = findViewById(R.id.deleteDonorButton);
        viewMapButton = findViewById(R.id.viewMapButton);

        // Initialize database helpers
        siteHelper = new DonationSiteHelper(this);
        donorHelper = new DonorHelper(this);

        siteDb = siteHelper.getReadableDatabase();
        donorDb = donorHelper.getWritableDatabase();

        // Navigate to Create Donation Site screen
        createDonationSiteButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, CreateDonationSiteActivity.class);
            startActivity(intent);
        });

        // Handle View Donor List button
        viewDonorListButton.setOnClickListener(v -> viewDonors());

        // Handle Delete Donor button
        deleteDonorButton.setOnClickListener(v -> deleteDonor());

        // Handle View Map button
        viewMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        // Handle Log Out button
        logOutButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void viewDonors() {
        // Get all donation sites from the database
        Cursor siteCursor = siteHelper.getAllSites(siteDb);

        if (siteCursor.getCount() == 0) {
            Toast.makeText(this, "No donation sites found.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder donorListBuilder = new StringBuilder();

        while (siteCursor.moveToNext()) {
            String siteName = siteCursor.getString(siteCursor.getColumnIndexOrThrow(DonationSiteHelper.SITE_NAME));

            // Get donors for this site
            Cursor donorCursor = donorDb.query(
                    DonorHelper.TABLE_DONORS,
                    null,
                    DonorHelper.DONOR_SITE_NAME + "=?",
                    new String[]{siteName},
                    null,
                    null,
                    null
            );

            donorListBuilder.append("Site: ").append(siteName).append("\n");

            if (donorCursor.getCount() > 0) {
                while (donorCursor.moveToNext()) {
                    String donorName = donorCursor.getString(donorCursor.getColumnIndexOrThrow(DonorHelper.DONOR_NAME));
                    String donorEmail = donorCursor.getString(donorCursor.getColumnIndexOrThrow(DonorHelper.DONOR_EMAIL));
                    String donorPhone = donorCursor.getString(donorCursor.getColumnIndexOrThrow(DonorHelper.DONOR_PHONE));

                    donorListBuilder.append("- ").append(donorName)
                            .append(" (Email: ").append(donorEmail)
                            .append(", Phone: ").append(donorPhone).append(")\n");
                }
            } else {
                donorListBuilder.append("No donors registered.\n");
            }
            donorCursor.close();
            donorListBuilder.append("\n");
        }

        siteCursor.close();

        // Show donors in an alert dialog
        new AlertDialog.Builder(this)
                .setTitle("Donor List")
                .setMessage(donorListBuilder.toString())
                .setPositiveButton("OK", null)
                .setNegativeButton("Download", (dialog, which) -> downloadDonorList(donorListBuilder.toString()))
                .show();
    }

    private void deleteDonor() {
        // Prompt manager to input the donor's email
        final EditText emailInput = new EditText(this);
        emailInput.setHint("Enter Donor Email");

        new AlertDialog.Builder(this)
                .setTitle("Delete Donor")
                .setMessage("Enter the donor's email to delete their record.")
                .setView(emailInput)
                .setPositiveButton("Delete", (dialog, which) -> {
                    String donorEmail = emailInput.getText().toString().trim();

                    if (donorEmail.isEmpty()) {
                        Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int rowsDeleted = donorDb.delete(
                            DonorHelper.TABLE_DONORS,
                            DonorHelper.DONOR_EMAIL + "=?",
                            new String[]{donorEmail}
                    );

                    if (rowsDeleted > 0) {
                        Toast.makeText(this, "Donor deleted successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No donor found with the provided email.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void downloadDonorList(String data) {
        // Get the Downloads directory
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, "donor_list.txt");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes());
            Toast.makeText(this, "Donor list saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save donor list.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        siteDb.close();
        donorDb.close();
    }
}
