package com.example.bus_app_go_bus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class busDriverSignup extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profilePicture, togglePasswordVisibility, toggleConfirmPasswordVisibility;
    private EditText txtName, txtEmail, txtPassword, txtConfPassword, txtPhoneNumber, txtLicenseNumber, txtYearsOfExperience;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private DatabaseHelper databaseHelper; // DatabaseHelper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_driver_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        // Initialize Views
        profilePicture = findViewById(R.id.profilePicture);
        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_password);
        txtConfPassword = findViewById(R.id.txt_conf_password);
        txtPhoneNumber = findViewById(R.id.txt_phone);
        txtLicenseNumber = findViewById(R.id.licence_number);
        txtYearsOfExperience = findViewById(R.id.xperience);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        toggleConfirmPasswordVisibility = findViewById(R.id.toggleConfirmPasswordVisibility);
        Button btnSignup = findViewById(R.id.signup_button);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Set OnClickListener for Profile Picture
        profilePicture.setOnClickListener(v -> openImageChooser());

        // Toggle Password Visibility
        togglePasswordVisibility.setOnClickListener(v -> togglePasswordVisibility(txtPassword, togglePasswordVisibility));

        // Toggle Confirm Password Visibility
        toggleConfirmPasswordVisibility.setOnClickListener(v -> togglePasswordVisibility(txtConfPassword, toggleConfirmPasswordVisibility));

        // Set OnClickListener for Signup Button
        btnSignup.setOnClickListener(v -> {
            // Collect data from input fields
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String confirmPassword = txtConfPassword.getText().toString().trim();
            String phoneNumber = txtPhoneNumber.getText().toString().trim();
            String licenseNumber = txtLicenseNumber.getText().toString().trim();
            String yearsOfExperienceString = txtYearsOfExperience.getText().toString().trim();

            // Validate fields
            if (name.isEmpty()) {
                Toast.makeText(busDriverSignup.this, "Name is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(busDriverSignup.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(busDriverSignup.this, "Password is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (confirmPassword.isEmpty()) {
                Toast.makeText(busDriverSignup.this, "Please confirm your password.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(busDriverSignup.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phoneNumber.isEmpty()) {
                Toast.makeText(busDriverSignup.this, "Phone number is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phoneNumber.matches("\\d{10}")) {
                Toast.makeText(busDriverSignup.this, "Please enter a valid 10-digit phone number.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (licenseNumber.isEmpty()) {
                Toast.makeText(busDriverSignup.this, "License number is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (yearsOfExperienceString.isEmpty()) {
                Toast.makeText(busDriverSignup.this, "Years of experience are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            int yearsOfExperience;
            try {
                yearsOfExperience = Integer.parseInt(yearsOfExperienceString);
            } catch (NumberFormatException e) {
                Toast.makeText(busDriverSignup.this, "Please enter a valid number for years of experience.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.checkEmail(email)) {
                Toast.makeText(busDriverSignup.this, "This email is already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert data into the database
            boolean isInserted = databaseHelper.insertBusDriver(name, email, password, confirmPassword,phoneNumber, licenseNumber, yearsOfExperience);

            if (isInserted) {
                Toast.makeText(busDriverSignup.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                // Redirect to another activity or perform any desired action
                startActivity(new Intent(busDriverSignup.this, login_form.class));
            } else {
                Toast.makeText(busDriverSignup.this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to Open Image Chooser
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle Image Chooser Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profilePicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Function to Toggle Password Visibility
    private void togglePasswordVisibility(EditText passwordField, ImageView toggleIcon) {
        if (passwordField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            // Show Password
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            toggleIcon.setImageResource(R.drawable.icons8_invisible_16); // Change to eye closed icon
        } else {
            // Hide Password
            passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            toggleIcon.setImageResource(R.drawable.icons8_eye_16); // Change to eye open icon
        }
        passwordField.setSelection(passwordField.getText().length()); // Move cursor to the end
    }
}
