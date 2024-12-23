package com.hmir.goodfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.hmir.goodfood.models.GeminiApiResponse;
import com.hmir.goodfood.models.GeminiRequestBody;
import com.hmir.goodfood.services.GeminiApiService;
import com.hmir.goodfood.utilities.FileUtil;

/**
 * Activity for scanning food and extracting ingredients.
 */
public class FoodScanner extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    private ImageView cameraView;

    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/";

    private static final int MAX_RETRIES = 3;
    private int retryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_scanner);

        Log.d("Gemini", GEMINI_API_KEY);
        Log.d("Gemini", GEMINI_API_BASE_URL);

        cameraView = findViewById(R.id.cameraView);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE);

        ImageButton ExtractIngredientButton = findViewById(R.id.ExtractIngredientButton);
        ExtractIngredientButton.setOnClickListener(v -> extractIngredient(cameraView));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            cameraView.setImageBitmap(photo);
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Extracts ingredients from the captured image.
     *
     * @param cameraView The ImageView containing the captured image.
     */
    private void extractIngredient(ImageView cameraView) {
        cameraView.setDrawingCacheEnabled(true);
        cameraView.buildDrawingCache();
        Bitmap bitmap = cameraView.getDrawingCache();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

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

        String prompt = "Based on the food scanned, extract the quantity and ingredients used in the food image. " +
                "Don't provide explanations. Please follow the output format:\n\n" +
                "Example output:\n- 4 pieces of Wedges\n\n" +
                "Output format:\n- <quantity> <unit> <Ingredient>";

        GeminiRequestBody requestBody = createGeminiRequestBody(prompt, encodedImage);
        String jsonBody = new Gson().toJson(requestBody);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Call<GeminiApiResponse> call = service.callGemini(GEMINI_API_KEY, body);
        call.enqueue(new Callback<GeminiApiResponse>() {
            @Override
            public void onResponse(Call<GeminiApiResponse> call, Response<GeminiApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GeminiApiResponse.Candidate> candidates = response.body().getCandidates();
                    if (candidates != null && !candidates.isEmpty()) {
                        String ingredients = candidates.get(0).getContent().getParts().get(0).getText();
                        Log.d("ExtractIngredient", "Response: " + ingredients);

                        File imageFile = FileUtil.saveBase64ToFile(FoodScanner.this, encodedImage, "temp_image.txt");

                        Intent intent = new Intent(FoodScanner.this, ExtractIngredient.class);
                        intent.putExtra("file_path", imageFile.getAbsolutePath());
                        intent.putExtra("ingredients", ingredients);
                        startActivity(intent);
                    } else {
                        // Handle the case when no recipe is found
                        Log.d("ExtractIngredient", "No recipes found.");
                        Toast.makeText(FoodScanner.this, "No recipe found for the image.", Toast.LENGTH_SHORT).show();

                        // Retry logic if no results are found
                        if (retryCount < MAX_RETRIES) {
                            retryCount++;
                            Log.d("ExtractIngredient", "Retrying... Attempt " + retryCount);
                            // Retry after a short delay (e.g., 2 seconds)
                            new android.os.Handler().postDelayed(() -> extractIngredient(cameraView), 2000);
                        } else {
                            Log.d("ExtractIngredient", "Failed after " + MAX_RETRIES + " retries.");
                            Toast.makeText(FoodScanner.this, "Failed to extract ingredients after retries.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d("ExtractIngredient", "Response unsuccessful: " + response.toString());
                    Toast.makeText(FoodScanner.this, "Failed to extract ingredients", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiApiResponse> call, Throwable t) {
                Toast.makeText(FoodScanner.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ExtractIngredient", "Error: " + t.getMessage(), t);
                TextView description = findViewById(R.id.adviceText);
                description.setText(t.getMessage());
            }
        });
    }

    /**
     * Creates a GeminiRequestBody with the specified prompt and encoded image.
     *
     * @param prompt The prompt to be included in the request.
     * @param encodedImage The base64 encoded image to be included in the request.
     * @return A GeminiRequestBody object containing the prompt and image.
     */
    private GeminiRequestBody createGeminiRequestBody(String prompt, String encodedImage) {
        List<GeminiRequestBody.Part> parts = new ArrayList<>();
        parts.add(new GeminiRequestBody.Part(prompt));
        parts.add(new GeminiRequestBody.Part(encodedImage, "image/jpeg"));

        List<GeminiRequestBody.Content> contents = new ArrayList<>();
        contents.add(new GeminiRequestBody.Content(parts));

        return new GeminiRequestBody(contents);
    }
}
