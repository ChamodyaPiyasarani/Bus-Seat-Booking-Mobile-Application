package com.example.bus_app_go_bus;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login_form extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button login_button;
    ImageView back_button, togglePasswordVisibility;
    boolean isPasswordVisible = false;
    DatabaseHelper databaseHelper; // DatabaseHelper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        // Initialize Views
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
        back_button = findViewById(R.id.back_button);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Toggle Password Visibility
        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.icons8_eye_16);
            } else {
                txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordVisibility.setImageResource(R.drawable.icons8_invisible_16);
            }
            isPasswordVisible = !isPasswordVisible;
            txtPassword.setSelection(txtPassword.getText().length()); // Move cursor to the end
        });

        // Login Button Click Event
        login_button.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            // Check for empty fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login_form.this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate the login credentials
            User user = databaseHelper.checkEmailPassword(email, password);

            if (user != null && user.getPassword().equals(password)) {
                Intent intent;
                if (user.getRole().equals("Passenger_1")) {
                    intent = new Intent(this, MapClass.class);
                } else if (user.getRole().equals("Bus Owner_2")) {
                    intent = new Intent(login_form.this, BusOwnersView.class);
                    intent.putExtra("email", email); // Pass the email
                } else if (user.getRole().equals("Bus Driver_3")) {
                    intent = new Intent(login_form.this, ViewBusDriverActivity.class);
                } else {
                    Toast.makeText(login_form.this, "Invalid role.", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(intent);
            } else {
                Toast.makeText(login_form.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignupChooser.class);
        startActivity(intent);
    }

    public void back_1(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
