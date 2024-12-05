package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExtractIngredient extends AppCompatActivity {

    Button CalcCalorieBtn;
    Button backFromExtractIngredientBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_extract_ingredient);
        String ingredients = getIntent().getStringExtra("ingredients");

        CalcCalorieBtn = findViewById(R.id.CalcCalorieBtn);
        backFromExtractIngredientBtn = findViewById(R.id.backFromExtractIngredientButton);

        CalcCalorieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExtractIngredient.this, Calories.class);
                startActivity(intent);
            }
        });

        backFromExtractIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Display these ingredients in a TextView (assuming you have a TextView with id ingredientTextView)
        TextView IngredientTextView = findViewById(R.id.IngredientsTextView);
        IngredientTextView.setText("Ingredients:\n" + ingredients + "\n");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ProfilePageMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
}