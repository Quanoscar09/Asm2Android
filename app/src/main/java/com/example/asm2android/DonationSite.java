package com.example.asm2android;

import java.util.ArrayList;
import java.util.List;

public class DonationSite {
    public String siteId;
    public String name;
    public String address;
    public String bloodTypeNeeded;
    public String date;
    public double latitude;
    public double longitude;
    public String managerId;
    public List<String> registeredDonors;

    public DonationSite() {
        registeredDonors = new ArrayList<>();
    }

    public DonationSite(String siteId, String name, String address, String bloodTypeNeeded,
                        String date, double latitude, double longitude, String managerId) {
        this.siteId = siteId;
        this.name = name;
        this.address = address;
        this.bloodTypeNeeded = bloodTypeNeeded;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.managerId = managerId;
        this.registeredDonors = new ArrayList<>();
    }

    public void addDonor(String donorId) {
        if (!registeredDonors.contains(donorId)) {
            registeredDonors.add(donorId);
        }
    }
}
