package com.hmir.goodfood;

import android.os.Bundle;
import android.util.Log;
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
public class RecipeDetailActivity extends AppCompatActivity {

    private FavouriteRecipeHelper favouriteRecipeHelper;

    /**
     * Initializes the activity, sets up the UI, and populates recipe details.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                          being shut down, this contains the data it most recently
     *                          supplied in onSaveInstanceState(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Initialize the FavouriteRecipeHelper
        favouriteRecipeHelper = new FavouriteRecipeHelper();

        Recipe recipe = getIntent().getParcelableExtra("recipe");

        TextView recipeNameTextView = findViewById(R.id.recipeName);
        TextView recipeIngredientsTextView = findViewById(R.id.recipeIngredients);
        TextView recipeStepsTextView = findViewById(R.id.recipeSteps);
        AppCompatImageButton saveRecipeButton = findViewById(R.id.saveRecipeButton); // Assuming a button to add to favorites

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
                // Join the steps into a single string
                String joinedSteps = String.join(" ", recipe.getSteps());
                // Use the splitSteps method from FavouriteRecipeHelper
                List<String> splitSteps = favouriteRecipeHelper.splitSteps(joinedSteps);
                recipeStepsTextView.setText("Steps:\n" + String.join("\n", splitSteps));
            } else {
                recipeStepsTextView.setText("Steps Not Available");
            }

            // Handle add to favorites button click
            saveRecipeButton.setOnClickListener(v -> saveRecipeToFavorites(recipe));
        } else {
            recipeNameTextView.setText("Recipe Not Found");
            recipeIngredientsTextView.setText("");
            recipeStepsTextView.setText("");
        }
    }

    /**
     * Saves the current recipe to the user's favorites collection in Firebase.
     * Requires user to be authenticated.
     *
     * @param recipe The Recipe object to be saved to favorites
     */
    private void saveRecipeToFavorites(Recipe recipe) {
        // Get the current user's UID (ensure the user is logged in)
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Prepare the recipe data
        List<String> splitSteps = favouriteRecipeHelper.splitSteps(String.join(" ", recipe.getSteps())); // Join steps into a single string and split properly

        Map<String, Object> recipeData = Map.of(
                "name", recipe.getName(),
                "ingredients", recipe.getIngredients(),
                "steps", splitSteps
        );

        // Add the recipe to the user's favourite_recipes subcollection
        favouriteRecipeHelper.addFavouriteRecipe(recipeData, new OnRecipeAddedCallback() {
            @Override
            public void onRecipeAdded(String recipeId) {
                // Handle success (e.g., show a message or update UI)
                Log.d("RecipeDetailActivity", "Recipe added with ID: " + recipeId);
            }

            @Override
            public void onError(Exception e) {
                // Handle error (e.g., show an error message)
                Log.e("RecipeDetailActivity", "Error adding recipe", e);
            }
        });
    }
}
