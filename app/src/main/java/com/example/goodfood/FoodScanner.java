package com.example.goodfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FoodScanner extends AppCompatActivity {
    private static final int REQUEST_CODE = 22;
    private ImageView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_scanner);

        cameraView = findViewById(R.id.cameraView);
        Button extractIngredientButton = findViewById(R.id.extractIngredientButton);
        extractIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FoodScanner.this, ExtractIngredient.class);
                intent.putExtra("ingredient1", "Salt");
                intent.putExtra("ingredient2", "Sugar");
                intent.putExtra("ingredient3", "Flour");
                startActivity(intent);
            }
        });

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE);
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
}

