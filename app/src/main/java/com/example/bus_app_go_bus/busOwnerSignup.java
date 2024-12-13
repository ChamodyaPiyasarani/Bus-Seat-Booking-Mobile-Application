package com.example.bus_app_go_bus;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class busOwnerSignup extends AppCompatActivity {

    private EditText txtName, txtEmail, txtPassword, txtConfPassword, txtPhoneNumber, txtNoOfBuses;
    private ImageView togglePasswordVisibility, toggleConfirmPasswordVisibility;
    private ImageView profilePicture;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private DatabaseHelper databaseHelper; // DatabaseHelper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_owner_signup);

        // Initialize Views
        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_password);
        txtConfPassword = findViewById(R.id.txt_conf_password);
        txtPhoneNumber = findViewById(R.id.txt_phone);
        txtNoOfBuses = findViewById(R.id.txt_noOfBuses);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        toggleConfirmPasswordVisibility = findViewById(R.id.toggleConfirmPasswordVisibility);
        profilePicture = findViewById(R.id.profilePicture);
        Button btnSignup = findViewById(R.id.signup_button);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Toggle Password Visibility
        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide Password
                txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.icons8_eye_16); // Set to 'eye open' icon
            } else {
                // Show Password
                txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.icons8_invisible_16); // Set to 'eye closed' icon
            }
            isPasswordVisible = !isPasswordVisible;
        });

        // Toggle Confirm Password Visibility
        toggleConfirmPasswordVisibility.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                // Hide Confirm Password
                txtConfPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.icons8_eye_16);
            } else {
                // Show Confirm Password
                txtConfPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.icons8_invisible_16);
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        });

        // Activity Result Launcher for Profile Picture
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Uri imageUri = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    profilePicture.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        profilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Sign Up Button OnClick
        btnSignup.setOnClickListener(v -> {
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String confPassword = txtConfPassword.getText().toString().trim();
            String phone = txtPhoneNumber.getText().toString().trim();
            String noOfBusesStr = txtNoOfBuses.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confPassword) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(noOfBusesStr)) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPhone(phone)) {
                Toast.makeText(this, "Invalid phone number. It must be 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            int numberOfBuses;
            try {
                numberOfBuses = Integer.parseInt(noOfBusesStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number of buses", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.checkEmail(email)) {
                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert Bus Owner to Database
            boolean isInserted = databaseHelper.insertBusOwner(name, email, password,confPassword ,phone, numberOfBuses);

            if (isInserted) {
                Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, login_form.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phone number validation method
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}"); // Ensures 10 digits
    }
}
