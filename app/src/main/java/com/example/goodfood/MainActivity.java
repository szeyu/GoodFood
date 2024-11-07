package com.example.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton cameraButton;
    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // camera button functionality
        cameraButton = findViewById(R.id.cameraButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FoodScanner.class);
                startActivity(intent);
            }
        });

        // navigation buttons functionality
        homeButton = findViewById(R.id.homeButton);
        profileButton = findViewById(R.id.profileButton);

        homeButton.setImageResource(R.drawable.home_colored);

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FoodScanner.class);
            startActivity(intent);
            finish();
        });
//
//        profileButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//            startActivity(intent);
//            overridePendingTransition(0, 0);
//            finish();
//        });
    }

    public void toTodayStats(View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("code", "today");
        startActivity(intent);
    }

    public void toThisMonthStats (View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("code", "month");
        startActivity(intent);
    }
}