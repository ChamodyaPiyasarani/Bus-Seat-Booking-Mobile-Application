package com.example.bus_app_go_bus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Make sure you have a layout for splash screen

        // Delay the opening of MainActivity for 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // After the delay, open MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close SplashActivity so the user can't go back to it
            }
        }, 3000); // 3000 ms (3 seconds) delay
    }
}