package com.hmir.goodfood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;

public class HomePage extends AppCompatActivity {
    private TextView tvCalorieIntake;
    private ProgressBar progressCalorieIntake;
    private TextView caloriePercentageText;
    private ImageView exceedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

//        tvCalorieIntake = findViewById(R.id.tv_calorie_intake);
//        progressCalorieIntake = findViewById(R.id.todayProgressBar);
//        exceedImageView = findViewById(R.id.dizzyface);
//        caloriePercentageText = findViewById(R.id.percentage_cal);
//        //Retrieve the totalCalories value from SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("CaloriePrefs", Context.MODE_PRIVATE);
//        int totalCalories = sharedPreferences.getInt("totalCalories", 0);
//
//        updateProgressBar(totalCalories);
    }

    private void updateProgressBar(int totalCalories) {
        // Check if totalCalories exceeds 1000 and adjust the progress bar and percentage text accordingly
        int progress;
        String percentageText;

        if (totalCalories > 1000) {
            progress = 100; // Set progress to 100%
            // Display the image when exceeding 100%
            exceedImageView.setVisibility(View.VISIBLE); // Make the image visible
            caloriePercentageText.setVisibility(View.GONE);
        } else {
            progress = (int) ((totalCalories / 1000) * 100); // Calculate the progress percentage
            percentageText = progress + "%"; // Display the actual progress
            caloriePercentageText.setText(percentageText);
            exceedImageView.setVisibility(View.GONE); // Hide the image
        }
        tvCalorieIntake.setText(String.format("Calories: %.0f/1000", totalCalories));
        progressCalorieIntake.setProgress(progress);
    }

    public void toSearchPage(View view) {
        Intent intent = new Intent(HomePage.this, SearchActivity.class);
        startActivity(intent);
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