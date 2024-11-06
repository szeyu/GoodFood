package com.example.goodfood;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExtractIngredient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_extract_ingredient);
        String ingredient1 = getIntent().getStringExtra("ingredient1");
        String ingredient2 = getIntent().getStringExtra("ingredient2");
        String ingredient3 = getIntent().getStringExtra("ingredient3");

        // Display these ingredients in a TextView (assuming you have a TextView with id ingredientTextView)
        TextView IngredientTextView = findViewById(R.id.IngredientsTextView);
        IngredientTextView.setText("Ingredients:\n" + ingredient1 + "\n" + ingredient2 + "\n" + ingredient3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}