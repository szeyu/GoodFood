package com.hmir.goodfood;

import static com.hmir.goodfood.utilities.FileUtil.readBase64FromFile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hmir.goodfood.models.AnalyzeRequest;
import com.hmir.goodfood.services.IngredientService;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The {@code ExtractIngredient} class is an {@link AppCompatActivity} that handles the display and analysis
 * of ingredients extracted from a food image. It receives ingredients and image data from the {@link FoodScanner}
 * activity, displays the ingredients, and allows users to analyze the nutritional content.
 * <p>
 * This activity uses Retrofit to communicate with a backend service for nutrition analysis.
 */
public class ExtractIngredient extends AppCompatActivity {

    /**
     * Initializes the activity, extracts ingredients, and handles image display and nutritional analysis.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the saved state data. Otherwise, it is {@code null}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_extract_ingredient);

        // Display these ingredients in a TextView (assuming you have a TextView with id ingredientTextView
        TextView IngredientTextView = findViewById(R.id.IngredientsTextView);

        Intent intent = getIntent();
        String ingredients = intent.getStringExtra("ingredients");
        String filePath = intent.getStringExtra("file_path");

        if (ingredients != null && ingredients.length() > 10) { // Ensure string is long enough to avoid errors
            String cleanedIngredients = ingredients.substring(2, ingredients.length() - 8);
            IngredientTextView.setText(cleanedIngredients);
        } else {
            IngredientTextView.setText("Invalid ingredients data");
        }

        // Check if the file path is valid before proceeding
        if (filePath != null) {
            String encodedImage = readBase64FromFile(new File(filePath));
            if (encodedImage != null) {
                displayImage(encodedImage);
            }
        }

        // Set up the button listener for analyzing nutrition
        Button calCalorieBtn = findViewById(R.id.CalcCalorieBtn);
        calCalorieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ExtractIngredient", ingredients);

                if (ingredients != null && !ingredients.isEmpty()) {
                    analyzeNutrition(ingredients, filePath);
                } else {
                    Toast.makeText(ExtractIngredient.this, "No ingredients to analyze", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
     * Analyzes the nutritional content of the ingredients by making a network request to the backend.
     * The nutritional data is then passed to the {@link Calories} activity.
     *
     * @param ingredients The ingredients string to analyze.
     * @param filePath    The path of the image file associated with the ingredients.
     */
    private void analyzeNutrition(String ingredients, String filePath) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") // Use Android Emulator's localhost
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IngredientService service = retrofit.create(IngredientService.class);

        // Create the request
        AnalyzeRequest request = new AnalyzeRequest(ingredients);

        // Call the nutrition analysis endpoint
        Call<String> call = service.analyzeNutrition(request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Pass the nutrition analysis result to the Calories activity
                    Intent intent = new Intent(ExtractIngredient.this, Calories.class);
                    intent.putExtra("nutritionData", response.body());
                    intent.putExtra("file_path", filePath);
                    startActivity(intent);
                } else {
                    Toast.makeText(ExtractIngredient.this, "Failed to analyze nutrition", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ExtractIngredient.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}