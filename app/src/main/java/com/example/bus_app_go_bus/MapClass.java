package com.example.bus_app_go_bus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class MapClass extends AppCompatActivity {

    private MapView mapView;
    private BusAdapter busAdapter;
    private ArrayList<Bus> busList;
    private static final int REQUEST_CODE_UPDATE_DELETE = 1;
    private DatabaseHelper dbHelper;
    private RecyclerView busListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check permissions
        checkPermissions();

        // Initialize OSMDroid configuration
        Configuration.getInstance().setUserAgentValue(getApplicationContext().getPackageName());

        // Set the content view
        setContentView(R.layout.mapeka);

        // Initialize the MapView
        mapView = findViewById(R.id.map);
        if (mapView == null) {
            throw new RuntimeException("MapView not found in layout file.");
        }

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Set fixed location coordinates
        double latitude = 6.9094292188763555;
        double longitude = 79.8632807247173;

        // Create a GeoPoint for the fixed location
        GeoPoint fixedLocation = new GeoPoint(latitude, longitude);

        // Set the map center and zoom level
        mapView.getController().setCenter(fixedLocation);
        mapView.getController().setZoom(15.0);

        // Create a Marker for the fixed location
        Marker locationMarker = new Marker(mapView);
        locationMarker.setPosition(fixedLocation);
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        locationMarker.setIcon(getDrawable(R.drawable.location_mark)); // Ensure this drawable exists
        locationMarker.setTitle("Fixed Location");
        locationMarker.setSnippet("This is a fixed location marker.");

        // Add the marker to the map
        mapView.getOverlays().add(locationMarker);

        // Set up RecyclerView for buses
        dbHelper = new DatabaseHelper(this);
        busListView = findViewById(R.id.busListView);
        if (busListView != null) {
            busListView.setLayoutManager(new LinearLayoutManager(this));
            displayBuses();
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void displayBuses() {
        Cursor cursor = dbHelper.getAllBuses();
        busList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String busNumber = cursor.getString(cursor.getColumnIndexOrThrow("bus_number"));
            String route = cursor.getString(cursor.getColumnIndexOrThrow("route"));
            busList.add(new Bus(busNumber, route));
        }
        cursor.close();

        busAdapter = new BusAdapter(busList);
        busListView.setAdapter(busAdapter);

        busAdapter.setOnItemClickListener(bus -> {
            Intent intent = new Intent(MapClass.this, SeatBookingActivity.class);
            intent.putExtra("bus_number", bus.getBusNumber());
            startActivityForResult(intent, REQUEST_CODE_UPDATE_DELETE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_DELETE && resultCode == RESULT_OK) {
            displayBuses();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume(); // Resume map view
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause(); // Pause map view
        }
    }
}
