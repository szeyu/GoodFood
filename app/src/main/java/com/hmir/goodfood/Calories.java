package com.hmir.goodfood;

import static com.hmir.goodfood.utilities.FileUtil.readBase64FromFile;

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

/**
 * The {@code Calories} class is an {@link AppCompatActivity} responsible for displaying the nutritional data
 * of a food item. The activity receives nutritional data in JSON format and displays it in various nutrient categories.
 * It also allows the user to view the associated image of the meal.
 * <p>
 * The activity extracts and parses nutritional data, and dynamically adds nutrient cards to the layout for each nutrient.
 * The data is parsed from a JSON response, and an image (if available) is displayed alongside the nutritional information.
 */
public class Calories extends AppCompatActivity {

    /**
     * Initializes the activity, retrieves and processes the nutritional data, and displays both the data and the image.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the saved state data. Otherwise, it is {@code null}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calories);

        Intent intent = getIntent();
        String nutritionData = intent.getStringExtra("nutritionData");
        Log.d("Ingredients", nutritionData);

        String filePath = intent.getStringExtra("file_path");
        // Check if the file path is valid before proceeding
        if (filePath != null) {
            String encodedImage = readBase64FromFile(new File(filePath));
            if (encodedImage != null) {
                displayImage(encodedImage);
            }
        }

        LinearLayout nutritionContainer = findViewById(R.id.nutritionContainer);

        if (nutritionContainer != null && !nutritionData.isEmpty()) {
            try {
                // Parse the JSON into a NutritionalRecord object
                JSONObject jsonObject = new JSONObject(nutritionData);
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

    /**
     * Displays an image from the Base64-encoded string by decoding it and setting it in an {@link ImageView}.
     *
     * @param encodedImage The Base64-encoded image string.
     */
    private void displayImage(String encodedImage) {
        byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ImageView capturedImageView = findViewById(R.id.MealImage);
        capturedImageView.setImageBitmap(bitmap);
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
