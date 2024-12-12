package com.example.asm2android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameInput, emailInput, passwordInput, phoneInput;
    private RadioGroup roleGroup;
    private Button registerButton;
    private TextView loginRedirect;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize UI components
        fullNameInput = findViewById(R.id.registerName);
        emailInput = findViewById(R.id.registerEmail);
        passwordInput = findViewById(R.id.registerPassword);
        phoneInput = findViewById(R.id.registerPhone);
        roleGroup = findViewById(R.id.roleGroup);
        registerButton = findViewById(R.id.registerBtn);
        loginRedirect = findViewById(R.id.loginRedirect);

        // Set up register button click listener
        registerButton.setOnClickListener(v -> registerUser());

        // Set up login redirect click listener
        loginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        int selectedRoleId = roleGroup.getCheckedRadioButtonId();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || selectedRoleId == -1) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        String role;
        if (selectedRoleId == R.id.radioDonor) {
            role = "Donor";
        } else if (selectedRoleId == R.id.radioManager) {
            role = "Manager";
        } else {
            role = "SuperUser";
        }

        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = fAuth.getCurrentUser();
                    if (user != null) {
                        DocumentReference df = fStore.collection("Users").document(user.getUid());
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("FullName", fullName);
                        userInfo.put("Email", email);
                        userInfo.put("PhoneNumber", phone);
                        userInfo.put("Role", role);

                        df.set(userInfo).addOnSuccessListener(aVoid -> {
                            Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            navigateToRoleActivity(role);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(RegisterActivity.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToRoleActivity(String role) {
        Intent intent;
        if ("Donor".equals(role)) {
            intent = new Intent(RegisterActivity.this, DonorActivity.class);
        } else if ("Manager".equals(role)) {
            intent = new Intent(RegisterActivity.this, ManagerActivity.class);
        } else {
            intent = new Intent(RegisterActivity.this, SuperUserActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
