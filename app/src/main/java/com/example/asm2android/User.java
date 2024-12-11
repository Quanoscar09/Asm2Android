package com.example.asm2android;

public class User {
    public String username;
    public String email;
    public String password;
    public String role;

    // Default constructor required for Firebase
    public User() {
    }

    // Parameterized constructor
    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
