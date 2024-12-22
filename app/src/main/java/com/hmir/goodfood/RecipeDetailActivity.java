package com.hmir.goodfood;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Recipe recipe = getIntent().getParcelableExtra("recipe");

        TextView recipeNameTextView = findViewById(R.id.recipeName);
        TextView recipeIngredientsTextView = findViewById(R.id.recipeIngredients);
        TextView recipeStepsTextView = findViewById(R.id.recipeSteps);

        if (recipe != null) {
            // Set recipe name
            recipeNameTextView.setText(recipe.getName() != null ? recipe.getName() : "Recipe Name Not Available");

            // Set recipe ingredients
            if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                recipeIngredientsTextView.setText("Ingredients:\n" + String.join("\n", recipe.getIngredients()));
            } else {
                recipeIngredientsTextView.setText("Ingredients Not Available");
            }

            // Set recipe steps
            if (recipe.getSteps() != null && !recipe.getSteps().isEmpty()) {
                recipeStepsTextView.setText("Steps:\n" + String.join("\n", recipe.getSteps()));
            } else {
                recipeStepsTextView.setText("Steps Not Available");
            }
        } else {
            recipeNameTextView.setText("Recipe Not Found");
            recipeIngredientsTextView.setText("");
            recipeStepsTextView.setText("");
        }
    }
}
