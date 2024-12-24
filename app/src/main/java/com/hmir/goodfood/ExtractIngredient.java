package com.hmir.goodfood;

import static com.hmir.goodfood.utilities.FileUtil.readBase64FromFile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.hmir.goodfood.callbacks.NutritionCallback;
import com.hmir.goodfood.models.GeminiApiResponse;
import com.hmir.goodfood.models.GeminiRequestBody;
import com.hmir.goodfood.services.GeminiApiService;
import com.hmir.goodfood.utilities.NetworkUtil;
import com.hmir.goodfood.utilities.UserHelper;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Activity for extracting ingredients and analyzing nutrition information from a scanned food image.
 * This activity displays the ingredients extracted from an image and allows the user to analyze
 * the nutritional content of those ingredients.
 */
public class ExtractIngredient extends AppCompatActivity {

    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/";
    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_extract_ingredient);

        TextView IngredientTextView = findViewById(R.id.IngredientsTextView);

        Intent intent = getIntent();
        String ingredients = intent.getStringExtra("ingredients");
        String filePath = intent.getStringExtra("file_path");

        if (ingredients != null) {
            IngredientTextView.setText(ingredients);
        } else {
            IngredientTextView.setText("Unable to Analyse Ingredients");
        }

        if (filePath != null) {
            String encodedImage = readBase64FromFile(new File(filePath));
            if (encodedImage != null) {
                displayImage(encodedImage);
            }
            setupSearchRecipeBtn(IngredientTextView);

        }

        analyzeNutrition(ingredients, new NutritionCallback() {
            @Override
            public void onSuccess(String nutritionData) {
                ImageButton CalculateCaloriesButton = findViewById(R.id.CalculateCaloriesButton);
                CalculateCaloriesButton.setOnClickListener(view -> {
                    if (ingredients != null && !ingredients.isEmpty()) {
                        startCalorieActivity(nutritionData, ingredients);
                    } else {
                        Toast.makeText(ExtractIngredient.this, "No ingredients to analyze", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("NutritionError", errorMessage);
            }
        });
    }

    /**
     * Displays the image from a base64 encoded string.
     *
     * @param encodedImage The base64 encoded image string.
     */
    private void displayImage(String encodedImage) {
        byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        ImageView capturedImageView = findViewById(R.id.MealImage);
        capturedImageView.setImageBitmap(bitmap);
    }

    /**
     * Analyzes the nutrition information based on the provided ingredients.
     *
     * @param ingredients The ingredients to analyze.
     */
    private void analyzeNutrition(String ingredients, NutritionCallback callback) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GEMINI_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GeminiApiService service = retrofit.create(GeminiApiService.class);

        String prompt = "Based on the " + ingredients + ", Give me the nutrition information in JSON format ONLY. " +
                "Don't provide explanation. Please follow the output format. " +
                "Just output with your assumption. " +
                "Don't output backquote character. " +
                "Example output: " +
                "{ " +
                "\"total_calories\": 100, " +
                "\"total_protein\": 20, " +
                "\"total_fat\": 10, " +
                "\"total_carbs\": 20, " +
                "\"total_cholesterol\": 50, " +
                "\"total_sodium\": 1000, " +
                "\"total_calcium\": 10, " +
                "\"total_iron\": 5, " +
                "\"total_magnesium\": 100, " +
                "\"total_potassium\": 200 " +
                "} " +
                "Output format: " +
                "{ " +
                "\"total_calories\": <cal unit>, " +
                "\"total_protein\": <gram unit>, " +
                "\"total_fat\": <gram unit>, " +
                "\"total_carbs\": <gram unit>, " +
                "\"total_cholesterol\": <gram unit>, " +
                "\"total_sodium\": <milligram unit>, " +
                "\"total_calcium\": <milligram unit>, " +
                "\"total_iron\": <milligram unit>, " +
                "\"total_magnesium\": <milligram unit>, " +
                "\"total_potassium\": <milligram unit> " +
                "}";

        GeminiRequestBody requestBody = createGeminiRequestBody(prompt);
        String jsonBody = new Gson().toJson(requestBody);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Call<GeminiApiResponse> call = service.callGemini(GEMINI_API_KEY, body);
        call.enqueue(new Callback<GeminiApiResponse>() {
            @Override
            public void onResponse(Call<GeminiApiResponse> call, Response<GeminiApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getCandidates().isEmpty()) {
                    String nutritionData = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                    callback.onSuccess(nutritionData);
                } else {
                    Toast.makeText(ExtractIngredient.this, "Failed to analyze nutrition", Toast.LENGTH_SHORT).show();
                    callback.onError("Failed to analyze nutrition");
                }
            }

            @Override
            public void onFailure(Call<GeminiApiResponse> call, Throwable t) {
                Toast.makeText(ExtractIngredient.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Creates a GeminiRequestBody with the specified prompt.
     *
     * @param prompt The prompt to be included in the request.
     * @return A GeminiRequestBody object containing the prompt.
     */
    private GeminiRequestBody createGeminiRequestBody(String prompt) {
        List<GeminiRequestBody.Part> parts = new ArrayList<>();
        parts.add(new GeminiRequestBody.Part(prompt));

        List<GeminiRequestBody.Content> contents = new ArrayList<>();
        contents.add(new GeminiRequestBody.Content(parts));

        return new GeminiRequestBody(contents);
    }

    /**
     * Calls an Intent and start Calorie activity.
     *
     * @param nutritionData The analyzed nutrition data from image.
     */

    private void startCalorieActivity(String nutritionData, String ingredients) {
        Intent intent = new Intent(ExtractIngredient.this, Calories.class);
        intent.putExtra("nutritionData", nutritionData);
        intent.putExtra("ingredients", ingredients);
        intent.putExtra("file_path", getIntent().getStringExtra("file_path"));
        startActivity(intent);
    }

    private void setupSearchRecipeBtn(TextView IngredientTextView) {
        // ImageButton for searching recipes
        ImageButton searchRecipeBtn = findViewById(R.id.searchRecipeBtn);
        searchRecipeBtn.setOnClickListener(view -> {
            // Get the ingredients from the IngredientTextView
            String ingredientsFromTextView = IngredientTextView.getText().toString().trim();

            if (!ingredientsFromTextView.isEmpty()) {
                searchRecipes(ingredientsFromTextView);  // Call the method to search for recipes with the ingredients from the TextView
            } else {
                Toast.makeText(ExtractIngredient.this, "No ingredients to search recipes for", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Searches for recipes based on the ingredients provided.
     *
     * @param ingredients The ingredients to search for recipes.
     */
    private void searchRecipes(String ingredients) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GEMINI_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GeminiApiService service = retrofit.create(GeminiApiService.class);

        // Improved prompt with ingredients explicitly mentioned
        String prompt = "Based on the following ingredients: " + ingredients + ", suggest a list of recipes. " +
                "For each recipe, provide the name, ingredients, and cooking steps in this format:\n" +
                "- Recipe Name: <recipe_name>\n" +
                "  Ingredients: <ingredient_1, ingredient_2, ...>\n" +
                "  Steps: <step_1, step_2, ...>";

        GeminiRequestBody requestBody = createGeminiRequestBody(prompt);
        String jsonBody = new Gson().toJson(requestBody);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Call<GeminiApiResponse> call = service.callGemini(GEMINI_API_KEY, body);
        call.enqueue(new Callback<GeminiApiResponse>() {
            @Override
            public void onResponse(Call<GeminiApiResponse> call, Response<GeminiApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getCandidates().isEmpty()) {
                    String recipesText = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();
                    Log.d("GeminiResponse", "Raw response: " + recipesText);  // Log raw response

                    // Clean up unwanted parts, like "-Recipe Name:" and "**"
                    recipesText = recipesText.replaceAll("^-Recipe Name: ", "").replaceAll("\\*\\*", "").trim();

                    // Now you can parse the recipe name or use it as is
                    List<Recipe> recipes = parseRecipes(recipesText);

                    // Pass the cleaned recipe list to the next activity
                    Intent intent = new Intent(ExtractIngredient.this, RecipeListActivity.class);
                    intent.putParcelableArrayListExtra("recipes", new ArrayList<>(recipes));
                    startActivity(intent);
                } else {
                    Toast.makeText(ExtractIngredient.this, "Failed to search for recipes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiApiResponse> call, Throwable t) {
                Toast.makeText(ExtractIngredient.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Parses a string of recipes text into a list of Recipe objects.
     *
     * @param recipesText The text containing recipe information.
     * @return A list of Recipe objects parsed from the text.
     */
    private List<Recipe> parseRecipes(String recipesText) {
        List<Recipe> recipes = new ArrayList<>();
        // Example of parsing the text into Recipe objects (you can adjust the parsing logic as needed)
        String[] recipeStrings = recipesText.split("\n- ");
        for (String recipeString : recipeStrings) {
            if (recipeString.trim().isEmpty()) {
                continue;  // Skip any empty recipe strings
            }

            String[] parts = recipeString.split("\n  ");
            if (parts.length >= 3) {  // Ensure there are at least 3 parts (name, ingredients, steps)
                String name = parts[0].replace("Recipe Name: ", "").replaceAll("\\*\\*", "").trim();  // Remove ** around the name
                List<String> ingredients = Arrays.asList(parts[1].replace("Ingredients: ", "").split(", "));
                List<String> steps = Arrays.asList(parts[2].replace("Steps: ", "").split(", "));
                recipes.add(new Recipe(name, ingredients, steps));
            }
        }
        return recipes;
    }

    /**
     * Creates a GeminiRequestBody with the specified prompt.
     *
     * @param prompt The prompt to be included in the request.
     * @return A GeminiRequestBody object containing the prompt.
     */

}