package com.hmir.goodfood;

import static com.hmir.goodfood.utilities.FileUtil.readBase64FromFile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hmir.goodfood.callbacks.NutritionCallback;
import com.hmir.goodfood.models.GeminiApiResponse;
import com.hmir.goodfood.models.GeminiRequestBody;
import com.hmir.goodfood.services.GeminiApiService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        // If ingredients from the image (intent) are present, set them to the TextView
        if (ingredients != null && !ingredients.isEmpty()) {
            IngredientTextView.setText(ingredients);
        } else {
            IngredientTextView.setText("Unable to Analyse Ingredients");
        }

        // If filePath is not null, display the image
        if (filePath != null) {
            String encodedImage = readBase64FromFile(new File(filePath));
            if (encodedImage != null) {
                displayImage(encodedImage);
            }
        }

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

        // ImageButton for calculating calories
        ImageButton calCalorieBtn = findViewById(R.id.CalculateCaloriesButton);
        calCalorieBtn.setOnClickListener(view -> {
            // Get the ingredients from the IngredientTextView
            String ingredientsFromTextView = IngredientTextView.getText().toString().trim();

            if (!ingredientsFromTextView.isEmpty()) {
                // Call analyzeNutrition with the ingredients from the IngredientTextView
                analyzeNutrition(ingredientsFromTextView, new NutritionCallback() {
                    @Override
                    public void onSuccess(String nutritionData) {
                        // Pass the nutritional data and ingredients to the next activity
                        startCalorieActivity(nutritionData, ingredientsFromTextView);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("NutritionError", errorMessage);
                        // Optionally show a toast for the error
                        Toast.makeText(ExtractIngredient.this, "Failed to analyze nutrition", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Show a toast if no ingredients are provided
                Toast.makeText(ExtractIngredient.this, "No ingredients to analyze", Toast.LENGTH_SHORT).show();
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

        // Improved prompt with ingredients explicitly mentioned
        String prompt = "Based on the following ingredients: " + ingredients + ", provide the nutritional information in JSON format. Output only the nutritional data, without any explanations. The format should be: {\"total_calories\": <value>, \"total_protein\": <value>, \"total_fat\": <value>, \"total_carbs\": <value>, ...}";

        GeminiRequestBody requestBody = createGeminiRequestBody(prompt);
        String jsonBody = new Gson().toJson(requestBody);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Log.d("GeminiRequest", "Request Body: " + jsonBody);  // Log the request body for debugging

        Call<GeminiApiResponse> call = service.callGemini(GEMINI_API_KEY, body);
        call.enqueue(new Callback<GeminiApiResponse>() {
            @Override
            public void onResponse(Call<GeminiApiResponse> call, Response<GeminiApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getCandidates().isEmpty()) {
                    String nutritionData = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();

                    // Debugging the raw response data
                    Log.d("GeminiResponse", "Raw response: " + nutritionData);

                    // Clean the nutrition data to remove the disclaimer
                    nutritionData = nutritionData.replaceAll("(?s)^.*?\\{", "{");  // Remove everything before the JSON part
                    nutritionData = nutritionData.replaceAll("(?s)\\}.*$", "}");  // Remove everything after the JSON part
                    nutritionData = nutritionData.trim();

                    // Log the cleaned nutrition data
                    Log.d("GeminiResponse", "Cleaned Nutrition Data: " + nutritionData);

                    // Attempt to parse the nutrition data as JSON
                    try {
                        JsonObject nutritionJson = new Gson().fromJson(nutritionData, JsonObject.class);

                        // Handle missing values by assigning defaults (0 or placeholder)
                        JsonObject combinedNutrition = new JsonObject();
                        combinedNutrition.addProperty("total_calories", nutritionJson.has("total_calories") && !nutritionJson.get("total_calories").isJsonNull() ? nutritionJson.get("total_calories").getAsDouble() : 0);
                        combinedNutrition.addProperty("total_protein", nutritionJson.has("total_protein") && !nutritionJson.get("total_protein").isJsonNull() ? nutritionJson.get("total_protein").getAsDouble() : 0);
                        combinedNutrition.addProperty("total_fat", nutritionJson.has("total_fat") && !nutritionJson.get("total_fat").isJsonNull() ? nutritionJson.get("total_fat").getAsDouble() : 0);
                        combinedNutrition.addProperty("total_carbs", nutritionJson.has("total_carbs") && !nutritionJson.get("total_carbs").isJsonNull() ? nutritionJson.get("total_carbs").getAsDouble() : 0);
                        combinedNutrition.addProperty("total_sugar", nutritionJson.has("total_sugar") && !nutritionJson.get("total_sugar").isJsonNull() ? nutritionJson.get("total_sugar").getAsDouble() : 0);
                        combinedNutrition.addProperty("total_fiber", nutritionJson.has("total_fiber") && !nutritionJson.get("total_fiber").isJsonNull() ? nutritionJson.get("total_fiber").getAsDouble() : 0);

                        // Return the combined nutrition data
                        callback.onSuccess(combinedNutrition.toString());
                    } catch (Exception e) {
                        Log.e("NutritionError", "Failed to parse nutrition data: " + e.getMessage());
                        callback.onError("Failed to parse nutrition data");
                    }
                } else {
                    Log.e("GeminiResponse", "Response failed or no candidates found.");
                    Toast.makeText(ExtractIngredient.this, "Failed to analyze nutrition", Toast.LENGTH_SHORT).show();
                    callback.onError("Failed to analyze nutrition");
                }
            }

            @Override
            public void onFailure(Call<GeminiApiResponse> call, Throwable t) {
                Log.e("GeminiRequest", "Request failed: " + t.getMessage());
                Toast.makeText(ExtractIngredient.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
    private GeminiRequestBody createGeminiRequestBody(String prompt) {
        List<GeminiRequestBody.Part> parts = new ArrayList<>();
        parts.add(new GeminiRequestBody.Part(prompt));

        List<GeminiRequestBody.Content> contents = new ArrayList<>();
        contents.add(new GeminiRequestBody.Content(parts));

        return new GeminiRequestBody(contents);
    }

    /**
     * Starts the Calorie Activity with the provided nutrition data and ingredients.
     *
     * @param nutritionData The nutrition data to be passed to the activity.
     * @param ingredients The ingredients to be passed to the activity.
     */
    private void startCalorieActivity(String nutritionData, String ingredients) {
        Intent intent = new Intent(ExtractIngredient.this, Calories.class);
        intent.putExtra("nutritionData", nutritionData);
        intent.putExtra("ingredients", ingredients);
        startActivity(intent);
    }
}

