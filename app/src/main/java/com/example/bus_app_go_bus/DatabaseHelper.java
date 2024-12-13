package com.example.bus_app_go_bus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "busapp.db";
    public static final int DATABASE_VERSION = 2;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL("PRAGMA table_info(bookings);");


        db.execSQL("CREATE TABLE passenger (" +
                "name TEXT, " +
                "email TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "confirm_password TEXT, " +
                "phone_number TEXT)"
        );

        db.execSQL("CREATE TABLE bus_owner (" +
                "name TEXT, " +
                "email TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "confirm_password TEXT, " +
                "phone_number TEXT, " +
                "number_of_buses INTEGER)"
        );

        db.execSQL("CREATE TABLE bus_driver (" +
                "name TEXT, " +
                "email TEXT PRIMARY KEY, " +
                "password TEXT, " +
                "confirm_password TEXT, " +
                "phone_number TEXT, " +
                "license_number TEXT, " +
                "years_of_experience INTEGER)"
        );

        db.execSQL("CREATE TABLE buses (" +
                "bus_number TEXT PRIMARY KEY NOT NULL, " +
                "bus_model TEXT NOT NULL, " +
                "number_of_seats INTEGER NOT NULL, " +
                "route TEXT NOT NULL, " +
                "bus_type TEXT NOT NULL, " +
                "bus_owner_id INTEGER, " +
                "FOREIGN KEY(bus_owner_id) REFERENCES bus_owner(email))"
        );

        // Create table for seat bookings
        db.execSQL("CREATE TABLE seats (" +
                "seat_number INTEGER NOT NULL, " +
                "bus_number TEXT NOT NULL, " +
                "email TEXT, " +
                "PRIMARY KEY (seat_number, bus_number), " +
                "FOREIGN KEY (bus_number) REFERENCES buses(bus_number), " +
                "FOREIGN KEY (email) REFERENCES passenger(email))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS passenger");
            db.execSQL("DROP TABLE IF EXISTS bus_owner");
            db.execSQL("DROP TABLE IF EXISTS bus_driver");
            db.execSQL("DROP TABLE IF EXISTS buses");
            db.execSQL("DROP TABLE IF EXISTS seats");
            onCreate(db);
        }
    }


    public Boolean insertPassenger(String name, String email, String password, String confirmPassword, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("confirm_password", confirmPassword);
        contentValues.put("phone_number", phoneNumber);
        long result = db.insert("passenger", null, contentValues);
        return result != -1;
    }

    public Boolean insertBusOwner(String name, String email, String password, String confirmPassword, String phoneNumber, int numberOfBuses) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("confirm_password", confirmPassword);
        contentValues.put("phone_number", phoneNumber);
        contentValues.put("number_of_buses", numberOfBuses);
        long result = db.insert("bus_owner", null, contentValues);
        return result != -1;
    }

    public Boolean insertBusDriver(String name, String email, String password, String confirmPassword, String phoneNumber, String licenseNumber, int yearsOfExperience) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("confirm_password", confirmPassword);
        contentValues.put("phone_number", phoneNumber);
        contentValues.put("license_number", licenseNumber);
        contentValues.put("years_of_experience", yearsOfExperience);
        long result = db.insert("bus_driver", null, contentValues);
        return result != -1;
    }

    public Boolean insertBus(String busNumber, String busModel, int numberOfSeats, String route, String busType, String busOwnerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if bus number already exists
        Cursor cursor = db.rawQuery("SELECT * FROM buses WHERE bus_number = ?", new String[]{busNumber});
        if (cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put("bus_number", busNumber);
        contentValues.put("bus_model", busModel);
        contentValues.put("number_of_seats", numberOfSeats);
        contentValues.put("route", route);
        contentValues.put("bus_type", busType);
        contentValues.put("bus_owner_id", busOwnerId);

        long result = db.insert("buses", null, contentValues);
        return result != -1;
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorPassenger = db.rawQuery("SELECT * FROM passenger WHERE email = ?", new String[]{email});
        Cursor cursorBusOwner = db.rawQuery("SELECT * FROM bus_owner WHERE email = ?", new String[]{email});
        Cursor cursorBusDriver = db.rawQuery("SELECT * FROM bus_driver WHERE email = ?", new String[]{email});

        boolean exists = cursorPassenger.getCount() > 0 || cursorBusOwner.getCount() > 0 || cursorBusDriver.getCount() > 0;

        cursorPassenger.close();
        cursorBusOwner.close();
        cursorBusDriver.close();

        return exists;
    }

    public User checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorPassenger = db.rawQuery("SELECT * FROM passenger WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursorPassenger.moveToFirst()) {
            cursorPassenger.close();
            return new User(email, password, "Passenger_1");
        }

        Cursor cursorBusOwner = db.rawQuery("SELECT * FROM bus_owner WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursorBusOwner.moveToFirst()) {
            cursorBusOwner.close();
            return new User(email, password, "Bus Owner_2");
        }

        Cursor cursorBusDriver = db.rawQuery("SELECT * FROM bus_driver WHERE email = ? AND password = ?", new String[]{email, password});
        if (cursorBusDriver.moveToFirst()) {
            cursorBusDriver.close();
            return new User(email, password, "Bus Driver_3");
        }

        cursorPassenger.close();
        cursorBusOwner.close();
        cursorBusDriver.close();

        return null;
    }

    public Cursor getAllBuses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT bus_number, route FROM buses", null);
    }

    public Cursor getBusDetails(String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM buses WHERE bus_number = ?", new String[]{busNumber});
    }

    public boolean updateBus(String busNumber, ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update("buses", contentValues, "bus_number = ?", new String[]{busNumber});

        return rowsAffected > 0;
    }

    public boolean deleteBus(String busNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("buses", "bus_number = ?", new String[]{busNumber});

        return rowsDeleted > 0;
    }

    public Cursor getDriverDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bus_driver WHERE email = ?", new String[]{email});
        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("DatabaseHelper", "No driver found with email: " + email);
        }
        return cursor;
    }


    public boolean updateDriver(String email, String name, String phone, String newEmail, String licenseNumber, String experience) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Prepare updated values
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("phone_number", phone);
        values.put("email", newEmail);
        values.put("license_number", licenseNumber);
        values.put("years_of_experience", experience);

        // Update the driver record where email matches the given email
        int rowsAffected = db.update("bus_driver", values, "email = ?", new String[]{email});

        return rowsAffected > 0; // Return true if at least one row was updated
    }


    public boolean deleteDriver(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("bus_driver", "email = ?", new String[]{email});

        return rowsDeleted > 0;
    }

    // In your DatabaseHelper
    public Cursor getBookingDetails(String seatName, String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        int seatNumber = Integer.parseInt(seatName.replace("Seat", ""));
        return db.rawQuery("SELECT email FROM bookings WHERE seat_number = ? AND bus_number = ?",
                new String[]{String.valueOf(seatNumber), busNumber});
    }

    // Example of booking a seat
    public boolean bookSeat(int seatNumber, String busNumber, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the seat is already booked
        Cursor cursor = db.rawQuery("SELECT * FROM bookings WHERE seat_number = ? AND bus_number = ?",
                new String[]{String.valueOf(seatNumber), busNumber});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return false; // Seat already booked
        }

        // Insert the new booking record
        ContentValues values = new ContentValues();
        values.put("seat_number", seatNumber);
        values.put("bus_number", busNumber);
        values.put("email", email);

        long result = db.insert("bookings", null, values);
        cursor.close();

        return result != -1; // If the result is -1, the insert failed
    }




    // Retrieve booked seats for a bus
    public Cursor getBookedSeats(String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT seat_number FROM bookings WHERE bus_number = ? AND email IS NOT NULL", new String[]{busNumber});

    }



    // Check if a seat is booked
    public boolean isSeatBooked(int seatNumber, String busNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM seats WHERE seat_number = ? AND bus_number = ?",
                new String[]{String.valueOf(seatNumber), busNumber});
        boolean booked = cursor.moveToFirst(); // If cursor moves to the first result, it means the seat is booked
        cursor.close();
        return booked;
    }

    // Cancel a seat booking
    public boolean cancelBooking(int seatNumber, String busNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("seats", "seat_number = ? AND bus_number = ?",
                new String[]{String.valueOf(seatNumber), busNumber});
        return rowsDeleted > 0; // Return true if the booking was successfully canceled
    }



}

