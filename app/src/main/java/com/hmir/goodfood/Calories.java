package com.hmir.goodfood;

import static com.hmir.goodfood.utilities.FileUtil.readBase64FromFile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.hmir.goodfood.utilities.NetworkUtil;
import com.hmir.goodfood.utilities.UserHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Calories} class is an {@link AppCompatActivity} responsible for displaying the nutritional data
 * of a food item. The activity receives nutritional data in JSON format and displays it in various nutrient categories.
 * It also allows the user to view the associated image of the meal.
 * <p>
 * The activity extracts and parses nutritional data, and dynamically adds nutrient cards to the layout for each nutrient.
 * The data is parsed from a JSON response, and an image (if available) is displayed alongside the nutritional information.
 */
public class Calories extends AppCompatActivity {

    private String cleanedNutritionData; // Declare as an instance variable
    private double totalCalories = 0;
    private double totalProtein = 0;
    private double totalFat = 0;
    private double totalCarbs = 0;
    private double totalSodium = 0;
    private double totalCalcium = 0;
    private double totalIron = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calories);

        Intent intent = getIntent();
        String nutritionData = intent.getStringExtra("nutritionData");
        String ingredients = intent.getStringExtra("ingredients");
        Log.d("Ingredients", nutritionData);

        String filePath = intent.getStringExtra("file_path");

        // Check if the file path is valid before proceeding
        if (filePath != null && !filePath.isEmpty()) {
            String encodedImage = readBase64FromFile(new File(filePath));
            if (encodedImage != null && !encodedImage.isEmpty()) {
                displayImage(encodedImage);
            } else {
                Log.e("Calories", "No Base64 image data found.");
            }
        } else {
            Log.e("Calories", "File path is invalid.");
        }

        LinearLayout nutritionContainer = findViewById(R.id.nutritionContainer);

        if (nutritionContainer != null && !nutritionData.isEmpty()) {
            try {
                // Clean and parse the nutrition data
                cleanedNutritionData = nutritionData.replace("```json\n", "").replace("\n```", "");
                JSONObject jsonObject = new JSONObject(cleanedNutritionData);

                // Split ingredients string into an array (if multiple ingredients)
                String[] ingredientList = ingredients.split(",");

                // Loop through each ingredient and accumulate nutritional values
                for (String ingredient : ingredientList) {
                    // For now, we're just using the same data for all ingredients, but ideally,
                    // you'd call the API for each ingredient to get specific data.
                    totalCalories += jsonObject.optDouble("total_calories", 0);
                    totalProtein += jsonObject.optDouble("total_protein", 0);
                    totalFat += jsonObject.optDouble("total_fat", 0);
                    totalCarbs += jsonObject.optDouble("total_carbs", 0);
                    totalSodium += jsonObject.optDouble("total_sodium", 0);
                    totalCalcium += jsonObject.optDouble("total_calcium", 0);
                    totalIron += jsonObject.optDouble("total_iron", 0);
                }

                // Add each nutrient to the container
                addNutrientCard("Calories", totalCalories, "kcal", nutritionContainer);
                addNutrientCard("Protein", totalProtein, "g", nutritionContainer);
                addNutrientCard("Carbohydrates", totalCarbs, "g", nutritionContainer);
                addNutrientCard("Fat", totalFat, "g", nutritionContainer);
                addNutrientCard("Sodium", totalSodium, "mg", nutritionContainer);
                addNutrientCard("Calcium", totalCalcium, "mg", nutritionContainer);
                addNutrientCard("Iron", totalIron, "mg", nutritionContainer);

            } catch (Exception e) {
                Log.e("Calories", "Error cleaning or parsing nutrition data", e);
                Toast.makeText(this, "Error displaying nutritional data", Toast.LENGTH_SHORT).show();
            }
        }

        FloatingActionButton saveRecordBtn = findViewById(R.id.SaveRecordButton);
        saveRecordBtn.setOnClickListener(view -> {
            UserHelper user = new UserHelper();
            try {
                // Convert the file_path into Uri class object
                if (filePath != null && !filePath.isEmpty()) {
                    File file = new File(filePath);
                    Uri imageUri = Uri.fromFile(file);

                    // Parse the cleaned JSON into a NutritionalRecord object
                    JSONObject jsonObject = new JSONObject(cleanedNutritionData);

                    Map<String, Object> newNutritionalRecord = new HashMap<>();
                    newNutritionalRecord.put("calcium", totalCalcium);
                    newNutritionalRecord.put("calories", totalCalories);
                    newNutritionalRecord.put("carbs", totalCarbs);
                    newNutritionalRecord.put("cholesterol", jsonObject.optDouble("total_cholesterol", 0));
                    newNutritionalRecord.put("fat", totalFat);
                    newNutritionalRecord.put("iron", totalIron);
                    newNutritionalRecord.put("magnesium", jsonObject.optDouble("total_magnesium", 0));
                    newNutritionalRecord.put("potassium", jsonObject.optDouble("total_potassium", 0));
                    newNutritionalRecord.put("protein", totalProtein);
                    newNutritionalRecord.put("sodium", totalSodium);
                    newNutritionalRecord.put("ingredients", ingredients);
                    newNutritionalRecord.put("date_time", new Timestamp(new Date()));

                    if (NetworkUtil.isInternetAvailable(getApplicationContext())) {
                        user.addUserNutritionalRecord(newNutritionalRecord, imageUri);
                        Toast.makeText(Calories.this, "Record saved successfully", Toast.LENGTH_SHORT).show();

                        // End the activity after saving the record
                        finish(); // This will close the current activity
                    } else {
                        Toast.makeText(Calories.this, "Unable to connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Calories", "File path is invalid.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Calories.this, "Error saving record", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays an image from the Base64-encoded string by decoding it and setting it in an {@link ImageView}.
     *
     * @param encodedImage The Base64-encoded image string.
     */
    private void displayImage(String encodedImage) {
        try {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ImageView capturedImageView = findViewById(R.id.MealImage);
            capturedImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("Calories", "Error decoding or displaying image", e);
        }
    }

    /**
     * Helper method to add a nutrient card to the nutrition container.
     * A nutrient card displays the label, value, and unit of a nutrient.
     *
     * @param label    The name of the nutrient (e.g., "Calories").
     * @param value    The value of the nutrient (e.g., 200).
     * @param unit     The unit of the nutrient (e.g., "kcal", "g").
     * @param container The container in which the nutrient card will be added.
     */
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
