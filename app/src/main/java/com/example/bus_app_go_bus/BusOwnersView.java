package com.example.bus_app_go_bus;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BusOwnersView extends AppCompatActivity {

    private static final int REQUEST_CODE_UPDATE_DELETE = 1;
    private DatabaseHelper dbHelper;
    private RecyclerView busListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_owners_view);

        dbHelper = new DatabaseHelper(this);
        busListView = findViewById(R.id.busListView);
        busListView.setLayoutManager(new LinearLayoutManager(this));

        displayBuses();
    }

    public void reg_driver(View view) {
        Intent intent = new Intent(this, busDriverSignup.class);
        startActivity(intent);
    }

    public void reg_bus(View view) {
        Intent intent = new Intent(this, RegisterBus.class);
        startActivity(intent);
    }

    private void displayBuses() {
        Cursor cursor = dbHelper.getAllBuses();
        ArrayList<Bus> busList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String busNumber = cursor.getString(cursor.getColumnIndexOrThrow("bus_number"));
            String route = cursor.getString(cursor.getColumnIndexOrThrow("route"));

            busList.add(new Bus(busNumber, route));
        }
        cursor.close();

        BusAdapter adapter = new BusAdapter(busList);
        busListView.setAdapter(adapter);

        // Handle item click to open BusDetailsActivity
        adapter.setOnItemClickListener(new BusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Bus bus) {
                // Start BusDetailsActivity for result to handle updates or deletions
                Intent intent = new Intent(BusOwnersView.this, BusDetailsActivity.class);
                intent.putExtra("bus_number", bus.getBusNumber());
                startActivityForResult(intent, REQUEST_CODE_UPDATE_DELETE);  // Start activity for result
            }
        });
    }

    // This method handles the result from BusDetailsActivity when a bus is updated or deleted
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from BusDetailsActivity
        if (requestCode == REQUEST_CODE_UPDATE_DELETE) {
            if (resultCode == RESULT_OK) {
                // Refresh the list of buses after update or delete
                displayBuses();  // Reload the bus list
            }
        }
    }
}
