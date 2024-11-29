package com.hmir.goodfood;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageButton searchButton = findViewById(R.id.searchButton);

        // Set OnClickListener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to SearchActivity
                Intent intent = new Intent(com.hmir.goodfood.MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

    }

        public void toTodayStats(View view){
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("code", "today");
            startActivity(intent);
        }

        public void toThisMonthStats (View view){
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("code", "month");
            startActivity(intent);
        }


}