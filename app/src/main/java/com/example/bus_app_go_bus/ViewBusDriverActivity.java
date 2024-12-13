package com.example.bus_app_go_bus;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewBusDriverActivity extends AppCompatActivity {

    private EditText txtName, txtPhone, txtEmail, txtLicenceNumber, txtXperience;
    private DatabaseHelper dbHelper;
    private String driverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bus_driver);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        txtName = findViewById(R.id.txt_name);
        txtPhone = findViewById(R.id.txt_phone);
        txtEmail = findViewById(R.id.txt_email);
        txtLicenceNumber = findViewById(R.id.txt_licence_number);
        txtXperience = findViewById(R.id.txt_xperience);

        Button updateDriverButton = findViewById(R.id.update_button);
        Button deleteDriverButton = findViewById(R.id.delete_button);

        // Get the driver's email from the intent
        driverEmail = getIntent().getStringExtra("driverEmail");

        // Load driver details into fields
        loadDriverDetails(driverEmail);

        // Update button listener
        updateDriverButton.setOnClickListener(v -> updateDriverDetails());

        // Delete button listener
        deleteDriverButton.setOnClickListener(v -> deleteDriver(driverEmail));
    }

    private void loadDriverDetails(String driverEmail) {
        Cursor cursor = dbHelper.getDriverDetails(driverEmail);

        if (cursor != null && cursor.moveToFirst()) {
            txtName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            txtPhone.setText(cursor.getString(cursor.getColumnIndexOrThrow("phone_number")));
            txtEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            txtLicenceNumber.setText(cursor.getString(cursor.getColumnIndexOrThrow("license_number")));
            txtXperience.setText(cursor.getString(cursor.getColumnIndexOrThrow("years_of_experience")));
            cursor.close();
        } else {
            Toast.makeText(this, "Driver details not found", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if details are missing
        }
    }

    private void updateDriverDetails() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", txtName.getText().toString());
        contentValues.put("phone_number", txtPhone.getText().toString());
        contentValues.put("email", txtEmail.getText().toString());
        contentValues.put("license_number", txtLicenceNumber.getText().toString());
        contentValues.put("years_of_experience", txtXperience.getText().toString());

        boolean isUpdated = dbHelper.updateDriver(driverEmail,
                txtName.getText().toString(),
                txtPhone.getText().toString(),
                txtEmail.getText().toString(),
                txtLicenceNumber.getText().toString(),
                txtXperience.getText().toString());
        if (isUpdated) {
            Toast.makeText(this, "Driver details updated", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);  // Notify other views to refresh if needed
            finish(); // Close this activity
        } else {
            Toast.makeText(this, "Failed to update driver details", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDriver(String driverEmail) {
        boolean isDeleted = dbHelper.deleteDriver(driverEmail);
        if (isDeleted) {
            Toast.makeText(this, "Driver deleted successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);  // Notify other views to refresh if needed
            finish(); // Close this activity
        } else {
            Toast.makeText(this, "Failed to delete driver", Toast.LENGTH_SHORT).show();
        }
    }
}
