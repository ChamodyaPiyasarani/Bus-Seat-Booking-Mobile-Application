package com.example.bus_app_go_bus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupChooser extends AppCompatActivity {

    Button passenger_button;
    Button busOwner_button;
    Button busDriver_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_chooser);

        // Set window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        passenger_button = findViewById(R.id.passenger_button);
        busOwner_button = findViewById(R.id.busOwner_button);
        busDriver_button = findViewById(R.id.busDriver_button);


        passenger_button.setOnClickListener(this::passenger_btn);
        busOwner_button.setOnClickListener(this::busowner_btn);
        busDriver_button.setOnClickListener(this::busdriver_btn);
    }

    public void passenger_btn(View view) {
        Intent intent = new Intent(this, passengerSignup.class);
        startActivity(intent);
    }

    public void busowner_btn(View view) {
        Intent intent = new Intent(this, busOwnerSignup.class);
        startActivity(intent);
    }

    public void busdriver_btn(View view) {
        Intent intent = new Intent(this, busDriverSignup.class);
        startActivity(intent);
    }
}
