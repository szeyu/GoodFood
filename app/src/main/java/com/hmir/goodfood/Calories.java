package com.hmir.goodfood;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Calories extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calories);

        // Get the nutrition data passed from ExtractIngredient activity
        String nutritionData = getIntent().getStringExtra("nutritionData");

        // Display it in a TextView
        TextView nutritionTextView = findViewById(R.id.CaloriesTextView);
        if (nutritionData != null && nutritionData.length() > 10) { // Ensure string is long enough to avoid errors
            String cleanedIngredients = nutritionData.substring(2, nutritionData.length() - 8);
            nutritionTextView.setText(cleanedIngredients);
        } else {
            nutritionTextView.setText("No nutrition data available");
        }
    }
}
