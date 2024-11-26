package com.example.goodfood;
import android.content.Intent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton cameraButton;
    private ImageButton profileButton;
    //private ImageButton profileButton = findViewById(R.id.profileButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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