package com.example.bus_app_go_bus;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class SeatBookingActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private HashMap<String, Boolean> seatStatus; // Stores the booking status of seats
    private final Handler handler = new Handler(); // For polling updates
    private final int POLL_INTERVAL = 3000; // 3 seconds
    private String currentUserEmail; // Dynamically set to the logged-in user's email
    private String busNumber; // Dynamically set to the selected bus number
    private String oldSeatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_book);

        databaseHelper = new DatabaseHelper(this);
        seatStatus = new HashMap<>();

        // Get user email and bus number dynamically
        currentUserEmail = getIntent().getStringExtra("user_email"); // Passed from login or main activity
        busNumber = getIntent().getStringExtra("bus_number"); // Passed when user selects a bus



        // Load initial seat booking status
        loadSeatStatus();

        // Start polling for real-time updates
        handler.postDelayed(this::pollSeatStatus, POLL_INTERVAL);

        // Loop through all seat TextViews
        for (int i = 1; i <= 41; i++) {
            String seatId = "seat" + i; // Dynamic ID (e.g., seat1, seat2, ..., seat41)
            int resId = getResources().getIdentifier(seatId, "id", getPackageName());
            TextView seat = findViewById(resId);

            if (seat != null) { // Ensure the seat exists
                String seatName = "Seat" + i; // Example: Seat1, Seat2...

                // Update the seat color based on the current status
                updateSeatColor(seat, seatName);

                // Handle seat click
                seat.setOnClickListener(view -> {
                    if (seatStatus.containsKey(seatName) && Boolean.TRUE.equals(seatStatus.get(seatName))) {
                        // If seat is already booked
                        showBookingOptionsDialog(seatName, seat);
                    } else {
                        // If seat is available
                        showBookingDialog(seatName, seat);
                    }
                });
            }
        }
    }

    private void loadSeatStatus() {
        // Fetch booked seats from the database for the selected bus
        Cursor cursor = databaseHelper.getBookedSeats(busNumber);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int seatNumber = cursor.getInt(cursor.getColumnIndex("seat_number")); // Assume column is seat_number
                String seatName = "Seat" + seatNumber;
                seatStatus.put(seatName, true); // Mark seat as booked
            }
            cursor.close();
        }
    }

    private void updateSeatColor(TextView seatView, String seatName) {
        if (seatStatus.containsKey(seatName) && Boolean.TRUE.equals(seatStatus.get(seatName))) {
            // Seat is already booked
            seatView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark)); // Booked (Blue)
        } else {
            // Seat is available
            seatView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray)); // Available (Gray)
        }
    }

    private void showBookingDialog(String seatName, TextView seatView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seat Booking");
        builder.setMessage("Do you want to book " + seatName + "?");

        // Book button
        builder.setPositiveButton("Book", (dialog, which) -> {
            // Update the database
            boolean success = databaseHelper.bookSeat(Integer.parseInt(seatName.replace("Seat", "")), busNumber, currentUserEmail);
            if (success) {
                // Mark the seat as booked
                seatStatus.put(seatName, true); // Update status in the map
                seatView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light)); // Yellow
                Toast.makeText(this, "Booking is successful for " + seatName + "!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("SeatBooking", "Failed to book seat: " + seatName);
                Toast.makeText(this, seatName + " could not be booked. Please try again.", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        // Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }


    private void showBookingOptionsDialog(String seatName, TextView seatView) {
        // Check if the current user is the one who booked the seat
        Cursor cursor = databaseHelper.getBookingDetails(seatName, busNumber);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String bookedBy = cursor.getString(cursor.getColumnIndex("email"));
            cursor.close();
            if (!bookedBy.equals(currentUserEmail)) {
                Toast.makeText(this, "You cannot modify this booking.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Seat is booked, allow the user to cancel or edit
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Booking Options");
        builder.setMessage(seatName + " is already booked. Choose an option:");

        // Cancel Booking
        builder.setNeutralButton("Cancel Booking", (dialog, which) -> {
            boolean success = databaseHelper.cancelBooking(Integer.parseInt(seatName.replace("Seat", "")), busNumber);
            if (success) {
                seatStatus.put(seatName, false);
                updateSeatColor(seatView, seatName);
                Toast.makeText(this, "Booking canceled for " + seatName + ".", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to cancel booking for " + seatName + ".", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        // Edit Booking
        builder.setNegativeButton("Edit Booking", (dialog, which) -> {
            // Logic for editing booking (moving to a new seat)
            showEditBookingDialog(seatName);
            dialog.dismiss();
        });

        // Close dialog
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showEditBookingDialog(String oldSeatName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Booking");

        // Input for new seat
        final EditText input = new EditText(this);
        input.setHint("Enter new seat number");
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String newSeatNumber = input.getText().toString().trim();
            if (!newSeatNumber.isEmpty()) {
                boolean success = updateBooking(oldSeatName, newSeatNumber);
                if (success) {
                    Toast.makeText(this, "Booking updated to Seat " + newSeatNumber, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to update booking. Try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a valid seat number.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private boolean updateBooking(String oldSeatName, String newSeatNumber) {
        int oldSeatNumber = Integer.parseInt(oldSeatName.replace("Seat", ""));
        int newSeatNum = Integer.parseInt(newSeatNumber);

        boolean success = databaseHelper.cancelBooking(oldSeatNumber, busNumber);
        if (success) {
            return databaseHelper.bookSeat(newSeatNum, busNumber, currentUserEmail);
        }
        return false;
    }

    private void pollSeatStatus() {
        //loadSeatStatus(); // Reload the booking status
        for (int i = 1; i <= 41; i++) {
            String seatId = "seat" + i;
            int resId = getResources().getIdentifier(seatId, "id", getPackageName());
            TextView seat = findViewById(resId);
            if (seat != null) {
                String seatName = "Seat" + i;
                updateSeatColor(seat, seatName); // Update the seat colors dynamically
            }
        }
        handler.postDelayed(this::pollSeatStatus, POLL_INTERVAL); // Schedule the next poll
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Stop polling when the activity is destroyed
    }
}
