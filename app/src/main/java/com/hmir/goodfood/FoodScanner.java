package com.hmir.goodfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
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

public class FoodScanner extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    private static final int PICK_IMAGE_REQUEST = 1; // Request code for gallery
    private ImageView cameraView;

    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_scanner);

        cameraView = findViewById(R.id.cameraView);

        // Start camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE);

        // Button to extract ingredients from the camera image
        Button extractIngredientButton = findViewById(R.id.extractIngredientButton);
        extractIngredientButton.setOnClickListener(v -> extractIngredientFromImageView(cameraView));


        // Button to select image from the gallery
        Button selectFromGalleryButton = findViewById(R.id.selectFromGalleryButton);
        selectFromGalleryButton.setOnClickListener(v -> selectImageFromGallery());
    }

    // Start the gallery activity to pick an image
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle camera image
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            cameraView.setImageBitmap(photo);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle gallery image
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                cameraView.setImageBitmap(bitmap);

                // Pass the bitmap for ingredient extraction
                extractIngredientFromBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // Convert the Bitmap to base64 and pass to extractIngredient
    private void extractIngredientFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        // Call your existing API logic with the encoded image
        extractIngredient(encodedImage);
    }

    private void extractIngredientFromImageView(ImageView cameraView) {
        cameraView.setDrawingCacheEnabled(true);
        cameraView.buildDrawingCache();
        Bitmap bitmap = cameraView.getDrawingCache();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        // Call your existing API logic with the encoded image
        extractIngredient(encodedImage);
    }

    private void extractIngredient(String encodedImage) {
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

        String prompt = "Based on the food scanned, extract the ingredients used in the food image, but normalize the output to represent just 1 piece or unit of the food. " +
                "Don't provide explanations. Please follow the output format:\n\n" +
                "Example output:\n- 1 piece of Chicken\n\n" +
                "Output format:\n- 1 <unit> <Ingredient>";


        GeminiRequestBody requestBody = createGeminiRequestBody(prompt, encodedImage);
        String jsonBody = new Gson().toJson(requestBody);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        Call<GeminiApiResponse> call = service.callGemini(GEMINI_API_KEY, body);
        call.enqueue(new Callback<GeminiApiResponse>() {
            @Override
            public void onResponse(Call<GeminiApiResponse> call, Response<GeminiApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getCandidates().isEmpty()) {
                    String ingredients = response.body().getCandidates().get(0).getContent().getParts().get(0).getText();

// Normalize the quantity to "1"
                    ingredients = normalizeToSingleUnit(ingredients);
                    Log.d("ExtractIngredient", "Response: " + ingredients);

                    File imageFile = FileUtil.saveBase64ToFile(FoodScanner.this, encodedImage, "temp_image.txt");

                    Intent intent = new Intent(FoodScanner.this, ExtractIngredient.class);
                    intent.putExtra("file_path", imageFile.getAbsolutePath());
                    intent.putExtra("ingredients", ingredients);
                    startActivity(intent);
                } else {
                    Log.d("ExtractIngredient", "Response unsuccessful: " + response.toString());
                    Toast.makeText(FoodScanner.this, "Failed to extract ingredients", Toast.LENGTH_SHORT).show();
                }
            }
            private String normalizeToSingleUnit(String ingredients) {
                // Regex to match quantities at the start of the line (e.g., "6 pieces of Chicken")
                return ingredients.replaceAll("^(\\d+)\\s", "1 ");
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
