package com.example.bus_app_go_bus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterBus extends AppCompatActivity {

    // Declare UI elements
    private EditText edtBusNumber, edtBusModel, edtNumberOfSeats, edtRoute;
    private Spinner busTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bus); // Assuming your layout is activity_register_bus.xml

        // Initialize UI elements
        edtBusNumber = findViewById(R.id.edtBusNumber);
        edtBusModel = findViewById(R.id.edtBusModel);
        edtNumberOfSeats = findViewById(R.id.edtNumberOfSeats);
        edtRoute = findViewById(R.id.edtRoute);
        busTypeSpinner = findViewById(R.id.bustype);

        // Set up the bus type spinner with an adapter
        setUpBusTypeSpinner();
    }

    // Method to set up the bus type spinner
    private void setUpBusTypeSpinner() {
        // Load bus types from strings.xml
        String[] busTypes = getResources().getStringArray(R.array.bus_type); // Using the bus_type array from strings.xml

        // Create an ArrayAdapter and set it to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, busTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Style for dropdown
        busTypeSpinner.setAdapter(adapter);
    }

    // Method called when the "Register Bus" button is clicked
    public void save(View view) {
        // Get input values from EditText and Spinner
        String busNumber = edtBusNumber.getText().toString().trim();
        String busModel = edtBusModel.getText().toString().trim();
        String numberOfSeatsStr = edtNumberOfSeats.getText().toString().trim();
        String route = edtRoute.getText().toString().trim();
        String busType = busTypeSpinner.getSelectedItem().toString(); // Get selected bus type

        // Validate inputs
        if (busNumber.isEmpty() || busModel.isEmpty() || numberOfSeatsStr.isEmpty() || route.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int numberOfSeats;
        try {
            numberOfSeats = Integer.parseInt(numberOfSeatsStr); // Convert number of seats to integer
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number of seats", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming the bus owner email is passed or retrieved from a session (as a placeholder example)
        String busOwnerEmail = "example_bus_owner_email@example.com";  // You need to replace this with actual email of the bus owner

        // Initialize DatabaseHelper and insert the bus
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean isInserted = dbHelper.insertBus(busNumber, busModel, numberOfSeats, route, busType, busOwnerEmail);

        // Show appropriate message based on success or failure
        if (isInserted) {
            Toast.makeText(this, "Bus registered successfully!", Toast.LENGTH_SHORT).show();
            // Redirect to BusOwnerView Activity
            Intent intent = new Intent(RegisterBus.this, BusOwnersView.class);
            startActivity(intent);
            finish(); // Optionally finish the current activity so the user can't return here by pressing back
        } else {
            Toast.makeText(this, "Error in registering bus", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to clear input fields after successful registration
    private void clearFields() {
        edtBusNumber.setText("");
        edtBusModel.setText("");
        edtNumberOfSeats.setText("");
        edtRoute.setText("");
        busTypeSpinner.setSelection(0); // Reset spinner to the first option
    }
}
