package com.hmir.goodfood;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.hmir.goodfood.callbacks.OnRecipeAddedCallback;
import com.hmir.goodfood.utilities.FavouriteRecipeHelper;

import java.util.List;
import java.util.Map;

/**
 * Activity for displaying detailed information about a recipe.
 * This activity shows the recipe's name, ingredients, and steps,
 * and allows users to save the recipe to their favorites.
 */


import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class RecipeDetailActivity extends AppCompatActivity {
    private FavouriteRecipeHelper favouriteRecipeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        favouriteRecipeHelper = new FavouriteRecipeHelper();
        Recipe recipe = getIntent().getParcelableExtra("recipe");

        TextView recipeNameTextView = findViewById(R.id.recipeName);
        TextView recipeIngredientsTextView = findViewById(R.id.recipeIngredients);
        TextView recipeStepsTextView = findViewById(R.id.recipeSteps);
        FloatingActionButton saveRecipeButton = findViewById(R.id.saveRecipeButton);

        if (recipe != null) {
            recipeNameTextView.setText(recipe.getName() != null ? recipe.getName() : "Recipe Name Not Available");

            if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                recipeIngredientsTextView.setText(String.join("\n", recipe.getIngredients()));
            } else {
                recipeIngredientsTextView.setText("Ingredients Not Available");
            }

            if (recipe.getSteps() != null && !recipe.getSteps().isEmpty()) {
                String joinedSteps = String.join(" ", recipe.getSteps());
                List<String> splitSteps = favouriteRecipeHelper.splitSteps(joinedSteps);
                recipeStepsTextView.setText(String.join("\n", splitSteps));
            } else {
                recipeStepsTextView.setText("Steps Not Available");
            }

            saveRecipeButton.setOnClickListener(v -> saveRecipeToFavorites(recipe));
        } else {
            recipeNameTextView.setText("Recipe Not Found");
            recipeIngredientsTextView.setText("");
            recipeStepsTextView.setText("");
        }
    }

    private void saveRecipeToFavorites(Recipe recipe) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> splitSteps = favouriteRecipeHelper.splitSteps(String.join(" ", recipe.getSteps()));

        Map<String, Object> recipeData = Map.of(
                "name", recipe.getName(),
                "ingredients", recipe.getIngredients(),
                "steps", splitSteps
        );

        favouriteRecipeHelper.addFavouriteRecipe(recipeData, new OnRecipeAddedCallback() {
            @Override
            public void onRecipeAdded(String recipeId) {
                Log.d("RecipeDetailActivity", "Recipe added with ID: " + recipeId);
            }

            @Override
            public void onError(Exception e) {
                Log.e("RecipeDetailActivity", "Error adding recipe", e);
            }
        });
    }
}