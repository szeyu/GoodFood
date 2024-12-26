package com.hmir.goodfood;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hmir.goodfood.utilities.FavouriteRecipeHelper;

public class DisplayActivity extends AppCompatActivity {

    private TextView recipeNameTextView, recipeIngredientsTextView, recipeStepsTextView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display); // Ensure this layout is correct

        // Initialize the views
        recipeNameTextView = findViewById(R.id.recipeName);
        recipeIngredientsTextView = findViewById(R.id.recipeIngredients);
        recipeStepsTextView = findViewById(R.id.recipeSteps);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the recipe ID from the Intent
        String recipeId = getIntent().getStringExtra("recipe_id");
        Log.d("DisplayActivity", "Received recipe_id: " + recipeId);

        if (recipeId != null) {
            // Fetch the recipe details from Firebase
            fetchRecipeDetails(recipeId);
        } else {
            Log.e("DisplayActivity", "No recipe ID provided");
            Toast.makeText(this, "Error: No recipe ID provided", Toast.LENGTH_SHORT).show();
        }


        FloatingActionButton fabDelete = findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(v -> {
            // Get the user's email
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            if (userEmail != null) {
                // Call method to delete the recipe using FavouriteRecipeHelper
                FavouriteRecipeHelper favouriteRecipeHelper = new FavouriteRecipeHelper();
                favouriteRecipeHelper.deleteFavouriteRecipe(recipeId, userEmail, new FavouriteRecipeHelper.OnRecipeDeletedCallback() {
                    @Override
                    public void onRecipeDeleted() {
                        Toast.makeText(DisplayActivity.this, "Recipe deleted", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after deletion
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(DisplayActivity.this, "Error deleting recipe", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(DisplayActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            }
        });
    }

        private void fetchRecipeDetails(String recipeId) {
        // Ensure the recipeId is valid
        if (recipeId == null || recipeId.isEmpty()) {
            Log.e("DisplayActivity", "Invalid recipe ID");
            Toast.makeText(this, "Error: Invalid recipe ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch the recipe document from the 'favourite_recipes' collection using the recipeId
        db.collection("favourite_recipes")
                .document(recipeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the recipe data from the document
                        Item item = documentSnapshot.toObject(Item.class);  // Use the correct class (Item)

                        if (item != null) {
                            // Set the data to the views
                            recipeNameTextView.setText(item.getName());
                            recipeIngredientsTextView.setText(String.join("\n", item.getIngredients()));
                            recipeStepsTextView.setText(String.join("\n", item.getSteps()));

                            // You can set an image here if you have a way to associate images with recipes
                            // For example, if you store image URLs in Firestore:
                            // Glide.with(this).load(item.getImageUrl()).into(foodImageView);
                        }
                    } else {
                        Log.d("DisplayActivity", "Recipe not found");
                        Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DisplayActivity", "Error fetching recipe details", e);
                    Toast.makeText(this, "Error fetching recipe details", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle back button click
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
