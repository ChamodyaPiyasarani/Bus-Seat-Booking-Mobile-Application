package com.example.bus_app_go_bus;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BusDetailsActivity extends AppCompatActivity {

    private EditText busNumberEditText, busModelEditText, numberOfSeatsEditText, routeEditText, busTypeEditText;
    private DatabaseHelper dbHelper;
    private String busNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        busNumberEditText = findViewById(R.id.busNumber);
        busModelEditText = findViewById(R.id.busModel);
        numberOfSeatsEditText = findViewById(R.id.numberOfSeats);
        routeEditText = findViewById(R.id.route);
        busTypeEditText = findViewById(R.id.busType);

        Button updateBusButton = findViewById(R.id.updateBusButton);
        Button deleteBusButton = findViewById(R.id.deleteBusButton);

        // Get the bus number from the intent
        busNumber = getIntent().getStringExtra("bus_number");

        // Load bus details into fields
        loadBusDetails(busNumber);

        // Update button listener
        updateBusButton.setOnClickListener(v -> updateBusDetails());

        // Delete button listener
        deleteBusButton.setOnClickListener(v -> deleteBus(busNumber));
    }

    private void loadBusDetails(String busNumber) {
        Cursor cursor = dbHelper.getBusDetails(busNumber);

        if (cursor != null && cursor.moveToFirst()) {
            busNumberEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("bus_number")));
            busModelEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("bus_model")));
            numberOfSeatsEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("number_of_seats")));
            routeEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("route")));
            busTypeEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("bus_type")));
            cursor.close();
        } else {
            Toast.makeText(this, "Bus details not found", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if details are missing
        }
    }

    private void updateBusDetails() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("bus_number", busNumberEditText.getText().toString());
        contentValues.put("bus_model", busModelEditText.getText().toString());
        contentValues.put("number_of_seats", numberOfSeatsEditText.getText().toString());
        contentValues.put("route", routeEditText.getText().toString());
        contentValues.put("bus_type", busTypeEditText.getText().toString());

        boolean isUpdated = dbHelper.updateBus(busNumber, contentValues);
        if (isUpdated) {
            Toast.makeText(this, "Bus details updated", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);  // Notify BusOwnersView to refresh
            finish(); // Close this activity
        } else {
            Toast.makeText(this, "Failed to update bus details", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBus(String busNumber) {
        boolean isDeleted = dbHelper.deleteBus(busNumber);
        if (isDeleted) {
            Toast.makeText(this, "Bus deleted successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);  // Notify BusOwnersView to refresh
            finish(); // Close this activity
        } else {
            Toast.makeText(this, "Failed to delete bus", Toast.LENGTH_SHORT).show();
        }
    }
}
