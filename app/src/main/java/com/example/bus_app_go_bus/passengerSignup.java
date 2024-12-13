package com.example.bus_app_go_bus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class passengerSignup extends AppCompatActivity {

    private EditText txtName, txtEmail, txtPassword, txtConfPassword, txtPhone;
    private ImageView togglePasswordVisibility, toggleConfirmPasswordVisibility;
    private ShapeableImageView profilePicture;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_signup);

        // Initialize views
        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_password);
        txtConfPassword = findViewById(R.id.txt_conf_password);
        txtPhone = findViewById(R.id.txt_phone);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        toggleConfirmPasswordVisibility = findViewById(R.id.toggleConfirmPasswordVisibility);
        profilePicture = findViewById(R.id.profilePicture);

        dbHelper = new DatabaseHelper(this);

        // Toggle Password Visibility
        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.icons8_eye_16); // Eye open icon
            } else {
                txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.icons8_invisible_16); // Eye closed icon
            }
            isPasswordVisible = !isPasswordVisible;
            txtPassword.setSelection(txtPassword.getText().length()); // Move cursor to the end
        });

        // Toggle Confirm Password Visibility
        toggleConfirmPasswordVisibility.setOnClickListener(v -> {
            if (isConfirmPasswordVisible) {
                txtConfPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.icons8_eye_16); // Eye open icon
            } else {
                txtConfPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.icons8_invisible_16); // Eye closed icon
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            txtConfPassword.setSelection(txtConfPassword.getText().length()); // Move cursor to the end
        });

        // Set Profile Picture
        profilePicture.setOnClickListener(v -> selectImageFromGallery());

        // Handle Sign Up Button Click
        findViewById(R.id.signup_button).setOnClickListener(v -> {
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String confirmPassword = txtConfPassword.getText().toString().trim();
            String phone = txtPhone.getText().toString().trim();

            // Validate empty fields
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
                Toast.makeText(passengerSignup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate email format
            if (!isValidEmail(email)) {
                Toast.makeText(passengerSignup.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("\\d{10}")) {
                Toast.makeText(passengerSignup.this, "Please enter a valid 10-digit phone number.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate phone number format (using a simple pattern, adjust as needed)
            if (!isValidPhone(phone)) {
                Toast.makeText(passengerSignup.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(passengerSignup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the email is already registered
            if (dbHelper.checkEmail(email)) {
                Toast.makeText(passengerSignup.this, "This email is already registered", Toast.LENGTH_SHORT).show();
                return;
            }


            // Convert profile picture to byte array (if selected)
            byte[] profilePicByteArray = null;
            if (profilePicture.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                profilePicByteArray = baos.toByteArray();
            }

            // Insert data into database
            boolean isInserted = dbHelper.insertPassenger(name, email, password, confirmPassword, phone);
            if (isInserted) {
                Toast.makeText(passengerSignup.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                // Redirect to login page or other action
                startActivity(new Intent(passengerSignup.this, login_form.class)); // Optional redirect
                // finish();
            } else {
                Toast.makeText(passengerSignup.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Email validation using regex
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Phone number validation using regex (this is a simple validation, you may adjust as needed)
    private boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && phone.matches("[0-9]{10}"); // 10-digit phone number
    }

    // Function to select an image from the gallery
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Activity result launcher for image picking
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        profilePicture.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}
