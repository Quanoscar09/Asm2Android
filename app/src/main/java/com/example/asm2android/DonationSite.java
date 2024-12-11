package com.example.asm2android;

public class DonationSite {
    public String name;
    public String address;
    public String bloodTypeNeeded;
    public String date;
    public double latitude;
    public double longitude;

    public DonationSite() { }

    public DonationSite(String name, String address, String bloodTypeNeeded, String date, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.bloodTypeNeeded = bloodTypeNeeded;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

