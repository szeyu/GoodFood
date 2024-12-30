package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hmir.goodfood.utilities.NutritionalRecord;
import com.hmir.goodfood.utilities.UserHelper;
import com.hmir.goodfood.utilities.UserHelper.OnRecordFetchedCallback;
import com.hmir.goodfood.utilities.UserHelper.OnRecordListFetchedCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class Mealhistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealhistory);

        // Find the ImageView by its ID
        ImageView mealImageView = findViewById(R.id.MHMealImage);
        // Find the container for displaying nutritional data
        LinearLayout nutritionContainer = findViewById(R.id.MHnutritionContainer);
        // Retrieve the image URL passed from the TodayFragment
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        Double calories = intent.getDoubleExtra("calories", 0.0);
        Double protein = intent.getDoubleExtra("protein",0.0);
        Double carbs = intent.getDoubleExtra("carbs", 0.0);
        Double fat = intent.getDoubleExtra("fat", 0.0);
        Double sodium = intent.getDoubleExtra("sodium", 0.0);
        Double iron = intent.getDoubleExtra("iron", 0.0);
        Double calcium = intent.getDoubleExtra("calcium", 0.0);
        Double cholesterol = intent.getDoubleExtra("cholesterol", 0.0);
        Double magnesium = intent.getDoubleExtra("magnesium", 0.0);
        Double potassium = intent.getDoubleExtra("potassium", 0.0);
        // Use Glide to load the image into the ImageView
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl) // Load the image from the URL
                    .into(mealImageView); // Display it in the ImageView
        }
        // Check if a valid record is passed and display its nutritional information
        // Display the nutritional information by calling addNutrientCard for each nutrient
        if (calories != null) {
            addNutrientCard("Calories", calories, "kcal", nutritionContainer);
        }
        if (protein != null) {
            addNutrientCard("Protein", protein, "g", nutritionContainer);
        }
        if (carbs != null) {
            addNutrientCard("Carbohydrates", carbs, "g", nutritionContainer);
        }
        if (fat != null) {
            addNutrientCard("Fat", fat, "g", nutritionContainer);
        }
        if (sodium != null) {
            addNutrientCard("Sodium", sodium, "mg", nutritionContainer);
        }
        if (calcium != null) {
            addNutrientCard("Calcium", calcium, "mg", nutritionContainer);
        }
        if (iron != null) {
            addNutrientCard("Iron", iron, "mg", nutritionContainer);
        }
        if (cholesterol != null) {
            addNutrientCard("Cholesterol", cholesterol, "mg", nutritionContainer);
        }
        if (potassium != null) {
            addNutrientCard("Potassium", potassium, "mg", nutritionContainer);
        }
        if (magnesium != null) {
            addNutrientCard("Magnesium", magnesium, "mg", nutritionContainer);
        }
    }

    private void addNutrientCard(String label, double value, String unit, LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View card = inflater.inflate(R.layout.fragment_nutritional_detail, container, false);

        TextView labelView = card.findViewById(R.id.nutrientLabel);
        TextView valueView = card.findViewById(R.id.nutrientValue);

        labelView.setText(label);
        valueView.setText(String.format("%.2f %s", value, unit));

        container.addView(card);
    }
}

