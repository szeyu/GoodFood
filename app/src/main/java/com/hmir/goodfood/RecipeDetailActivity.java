package com.hmir.goodfood;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hmir.goodfood.callbacks.OnRecipeAddedCallback;
import com.hmir.goodfood.utilities.FavouriteRecipeHelper;

import java.util.List;
import java.util.Map;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity for displaying detailed information about a recipe.
 * This activity shows the recipe's name, ingredients, and preparation steps.
 * It also provides functionality to save the recipe to the user's favorites.
 *
 * The activity receives a {@link Recipe} object through the intent extra with key "recipe".
 * If no recipe is provided or if the recipe is invalid, appropriate error messages are displayed.
 *
 * Features:
 * <ul>
 *     <li>Displays recipe name, ingredients, and preparation steps</li>
 *     <li>Provides a back button for navigation</li>
 *     <li>Allows saving the recipe to favorites</li>
 *     <li>Handles missing or incomplete recipe data gracefully</li>
 * </ul>
 *
 * Usage:
 * <pre>
 * Intent intent = new Intent(context, RecipeDetailActivity.class);
 * intent.putExtra("recipe", recipeObject);
 * startActivity(intent);
 * </pre>
 *
 * @see Recipe
 * @see FavouriteRecipeHelper
 * @see AppCompatActivity
 */
public class RecipeDetailActivity extends AppCompatActivity {
    private FavouriteRecipeHelper favouriteRecipeHelper;

    /**
     * Initializes the activity, sets up the UI components and event handlers.
     * Retrieves the recipe from the intent and populates the UI accordingly.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                          being shut down, this contains the data it most recently
     *                          supplied in onSaveInstanceState(Bundle).
     */
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

    /**
     * Saves the given recipe to the user's favorites list.
     * Uses Firebase Authentication to get the current user's ID and
     * stores the recipe data in the database.
     *
     * @param recipe The recipe to be saved to favorites
     */
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