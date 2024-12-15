package com.hmir.goodfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hmir.goodfood.utilities.NutritionalRecord;

import org.json.JSONObject;

import java.io.File;

public class Calories extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calories);

        Intent intent = getIntent();
        String nutritionData = intent.getStringExtra("nutritionData");

        String cleanedIngredients = nutritionData.substring(2, nutritionData.length() - 8);
        Log.d("Ingredients", cleanedIngredients);

        LinearLayout nutritionContainer = findViewById(R.id.nutritionContainer);

        if (nutritionContainer != null && !nutritionData.isEmpty()) {
            try {
                // Parse the JSON into a NutritionalRecord object
                JSONObject jsonObject = new JSONObject(cleanedIngredients);
                NutritionalRecord record = new NutritionalRecord(
                        null,
                        jsonObject.optDouble("total_calcium", 0),
                        jsonObject.optDouble("total_calories", 0),
                        jsonObject.optDouble("total_carbs", 0),
                        jsonObject.optDouble("total_cholesterol", 0),
                        null,
                        jsonObject.optDouble("total_fat", 0),
                        null,
                        null,
                        jsonObject.optDouble("total_iron", 0),
                        jsonObject.optDouble("total_potassium", 0),
                        jsonObject.optDouble("total_protein", 0),
                        jsonObject.optDouble("total_sodium", 0),
                        jsonObject.optDouble("total_magnesium", 0)
                );

                // Add each nutrient to the container
                addNutrientCard("Calories", record.getCalories(), "kcal", nutritionContainer);
                addNutrientCard("Protein", record.getProtein(), "g", nutritionContainer);
                addNutrientCard("Carbohydrates", record.getCarbs(), "g", nutritionContainer);
                addNutrientCard("Fat", record.getFat(), "g", nutritionContainer);
                addNutrientCard("Sodium", record.getSodium(), "mg", nutritionContainer);
                addNutrientCard("Calcium", record.getCalcium(), "mg", nutritionContainer);
                addNutrientCard("Iron", record.getIron(), "mg", nutritionContainer);
                addNutrientCard("Cholesterol", record.getCholesterol(), "mg", nutritionContainer);
                addNutrientCard("Potassium", record.getPotassium(), "mg", nutritionContainer);
                addNutrientCard("Magnesium", record.getMagnesium(), "mg", nutritionContainer);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // Helper method to add a nutrient card
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
