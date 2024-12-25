package com.hmir.goodfood;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Activity for displaying detailed information about a food item.
 * Shows comprehensive information including food name, image, nutritional content
 * (fat, calories, protein), description, and ingredients.
 * Integrates with local database for data retrieval and supports navigation
 * through the toolbar.
 *
 * @see AppCompatActivity
 * @see DatabaseHelper
 */
public class DisplayActivity extends AppCompatActivity {

    private TextView foodNameTextView;
    private TextView fatTextView;
    private TextView caloriesTextView;
    private TextView proteinTextView;
    private TextView descriptionTextView;
    private TextView ingredientsTextView;
    private ImageView foodImageView;
    private com.hmir.goodfood.DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display); // Ensure this layout is correct

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize the views
        foodNameTextView = findViewById(R.id.foodNameTextView);
        foodImageView = findViewById(R.id.foodImageView);
        fatTextView = findViewById(R.id.fatTextView);
        caloriesTextView = findViewById(R.id.caloriesTextView);
        proteinTextView = findViewById(R.id.proteinTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Retrieve the item data from the Intent
        int foodId = getIntent().getIntExtra("food_id", -1);
        String foodName = getIntent().getStringExtra("food_name");
        String foodImage = getIntent().getStringExtra("food_image");

        Log.d("DisplayActivity", "Received Food ID: " + foodId);
        Log.d("DisplayActivity", "Received Food Name: " + foodName);
        Log.d("DisplayActivity", "Received Food Image: " + foodImage);

        if (foodId != -1) {
            // Fetch the item details from the database by foodId
            Item item = dbHelper.getItemById(foodId); // Use foodId here directly

            if (item != null) {
                // Set the data to the views from the Item object
                foodNameTextView.setText(item.getName());

                // Dynamically load image (with Glide if needed)
                String imageName = item.getImageResourceName();
                int imageResourceId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                if (imageResourceId != 0) {
                    foodImageView.setImageResource(imageResourceId);
                } else {
                    foodImageView.setImageResource(R.drawable.eating_healthy_food_cuate_1); // Default image
                }

                fatTextView.setText(item.getFatContent());
                caloriesTextView.setText(item.getCalories());
                proteinTextView.setText(item.getProteinContent());
                descriptionTextView.setText(item.getDescription());
                ingredientsTextView.setText(item.getIngredients());
            } else {
                Log.d("DisplayActivity", "Item not found in database");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle back button click
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}
