package com.example.goodfood;

import android.content.Intent;
import android.graphics.Bitmap;
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

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goodfood.models.ExtractRequest;
import com.example.goodfood.services.IngredientService;

public class FoodScanner extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    private ImageView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_scanner);

        cameraView = findViewById(R.id.cameraView);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE);

        Button extractIngredientButton = findViewById(R.id.extractIngredientButton);
        extractIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractIngredient(cameraView);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            cameraView.setImageBitmap(photo);
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void extractIngredient(ImageView cameraView) {
        // Convert ImageView to Bitmap
        cameraView.setDrawingCacheEnabled(true);
        cameraView.buildDrawingCache();
        Bitmap bitmap = cameraView.getDrawingCache();

        // Convert Bitmap to Base64
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        // Create Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.22.96.1:8080/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IngredientService service = retrofit.create(IngredientService.class);
        ExtractRequest request = new ExtractRequest(encodedImage);

        // Make the network request
        Call<String> call = service.extractIngredients(request);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Log the entire response body
                    Log.d("ExtractIngredient", "Response: " + response.body());

                    // Get the ingredients from the response
                    String ingredients = String.valueOf(response.body());

                    // Start the ExtractIngredient activity
                    Intent intent = new Intent(FoodScanner.this, ExtractIngredient.class);
                    intent.putExtra("ingredients", ingredients);
                    startActivity(intent);
                } else {
                    Log.d("ExtractIngredient", "Response unsuccessful: " + response.toString());
                    Toast.makeText(FoodScanner.this, "Failed to extract ingredients", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(FoodScanner.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ExtractIngredient", "Error: " + t.getMessage(), t);
                TextView description = findViewById(R.id.adviceText);
                description.setText(t.getMessage());
            }
        });
    }
}

