package com.hmir.goodfood;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hmir.goodfood.callbacks.OnRecipeDeletedCallback;
import com.hmir.goodfood.utilities.FavouriteRecipeHelper;

/**
 * Activity for displaying detailed information about a recipe.
 * Shows recipe name, ingredients, steps, and provides deletion functionality.
 */
public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "DisplayActivity";
    private static final String COLLECTION_NAME = "favourite_recipes";

    private TextView recipeNameTextView;
    private TextView recipeIngredientsTextView;
    private TextView recipeStepsTextView;
    private FloatingActionButton fabDelete;
    private FirebaseFirestore db;
    private String recipeId;
    private FavouriteRecipeHelper favouriteRecipeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        initializeViews();
        setupToolbar();
        initializeFirebase();
        handleRecipeId();
        setupDeleteButton();
    }

    /**
     * Initializes view references from layout.
     * Finds and assigns all necessary view components.
     */
    private void initializeViews() {
        recipeNameTextView = findViewById(R.id.recipeName);
        recipeIngredientsTextView = findViewById(R.id.recipeIngredients);
        recipeStepsTextView = findViewById(R.id.recipeSteps);
        fabDelete = findViewById(R.id.fab_delete);
    }

    /**
     * Sets up the toolbar with proper configuration.
     * Enables the back button and sets the title.
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.recipe_details);
        }
    }

    /**
     * Initializes Firebase components.
     * Sets up Firestore and FavouriteRecipeHelper instances.
     */
    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        favouriteRecipeHelper = new FavouriteRecipeHelper();
    }

    /**
     * Handles the recipe ID received from the intent.
     * Fetches recipe details if ID is present, shows error otherwise.
     */
    private void handleRecipeId() {
        recipeId = getIntent().getStringExtra("recipe_id");
        if (recipeId != null) {
            fetchRecipeDetails(recipeId);
        } else {
            showError(getString(R.string.error_no_recipe_id));
            finish();
        }
    }

    /**
     * Sets up the delete button with click listener.
     * Initializes the floating action button for recipe deletion.
     */
    private void setupDeleteButton() {
        fabDelete.setOnClickListener(v -> deleteRecipe());
    }

    /**
     * Fetches recipe details from Firestore.
     *
     * @param recipeId The ID of the recipe to fetch
     */
    private void fetchRecipeDetails(@NonNull String recipeId) {
        if (recipeId.isEmpty()) {
            showError(getString(R.string.error_invalid_recipe_id));
            return;
        }

        db.collection(COLLECTION_NAME)
                .document(recipeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Item item = documentSnapshot.toObject(Item.class);
                        if (item != null) {
                            displayRecipeDetails(item);
                        } else {
                            showError(getString(R.string.error_invalid_data));
                        }
                    } else {
                        showError(getString(R.string.error_recipe_not_found));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching recipe details", e);
                    showError(getString(R.string.error_fetching_details));
                });
    }

    /**
     * Displays recipe details in the UI.
     *
     * @param item The recipe item to display
     */
    private void displayRecipeDetails(@NonNull Item item) {
        recipeNameTextView.setText(item.getName());

        String ingredients = String.join("\n", item.getIngredients());
        recipeIngredientsTextView.setText(getString(R.string.ingredients_format, ingredients));

        String steps = String.join("\n", item.getSteps());
        recipeStepsTextView.setText(getString(R.string.steps_format, steps));
    }

    /**
     * Handles recipe deletion.
     */
    private void deleteRecipe() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showError(getString(R.string.error_not_authenticated));
            return;
        }

        favouriteRecipeHelper.deleteFavouriteRecipe(recipeId, currentUser.getEmail(),
                new OnRecipeDeletedCallback() {
                    @Override
                    public void onRecipeDeleted() {
                        showSuccess(getString(R.string.success_recipe_deleted));
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error deleting recipe", e);
                        showError(getString(R.string.error_deleting_recipe));
                    }
                });
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a success message to the user.
     *
     * @param message The success message to display
     */
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}