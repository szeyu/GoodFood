package com.example.goodfood;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

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