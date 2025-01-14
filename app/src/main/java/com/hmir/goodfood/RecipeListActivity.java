package com.hmir.goodfood;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This activity displays a list of recipes passed from the previous activity.
 * It sets up a RecyclerView with a RecipeAdapter to display the recipes in a list format.
 * It also handles clicks on individual recipes, navigating to the RecipeDetailActivity to display more details.
 */
public class RecipeListActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Retrieve the list of recipes passed from the previous activity
        List<Recipe> recipes = getIntent().getParcelableArrayListExtra("recipes");

        if (recipes != null && !recipes.isEmpty()) {
            RecyclerView recyclerView = findViewById(R.id.recipeRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Set up the adapter with the list of recipes
            RecipeAdapter adapter = new RecipeAdapter(recipes, this);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No recipes found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // Navigate to the RecipeDetailActivity
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}
