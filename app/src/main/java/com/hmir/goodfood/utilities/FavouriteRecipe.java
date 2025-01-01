package com.hmir.goodfood.utilities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Manages favorite recipes in the GoodFood application.
 * This class handles CRUD operations for favorite recipes in Firebase Firestore,
 * including storing nutritional information, ingredients, and recipe details.
 * Implements defensive copying for mutable collections and proper null handling.
 *
 * @see FirebaseFirestore
 */
public class FavouriteRecipe {
    private static final String COLLECTION_NAME = "favourite_recipes";
    private static final String TAG = "FavouriteRecipe";

    private final FirebaseFirestore db;
    private String recipe_id;
    private String name;
    private final List<String> ingredients;
    private final List<String> steps;

    /**
     * Default constructor required for Firebase Firestore deserialization.
     * Initializes empty lists for ingredients and steps.
     */
    public FavouriteRecipe() {
        this.db = FirebaseFirestore.getInstance();
        this.ingredients = new ArrayList<>();
        this.steps = new ArrayList<>();
    }

    /**
     * Creates a new FavouriteRecipe with the specified details.
     *
     * @param recipe_id   The unique identifier for the recipe
     * @param name        The name of the recipe
     * @param ingredients List of ingredients required for the recipe
     * @param steps       List of preparation steps for the recipe
     * @throws IllegalArgumentException if recipe_id or name is null or empty
     */
    public FavouriteRecipe(@NonNull String recipe_id, @NonNull String name,
                           @Nullable List<String> ingredients, @Nullable List<String> steps) {
        if (recipe_id.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe ID cannot be empty");
        }
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe name cannot be empty");
        }

        this.db = FirebaseFirestore.getInstance();
        this.recipe_id = recipe_id;
        this.name = name;
        this.ingredients = ingredients != null ?
                new ArrayList<>(ingredients) : new ArrayList<>();
        this.steps = steps != null ?
                new ArrayList<>(steps) : new ArrayList<>();
    }

    /**
     * Fetches a favorite recipe from Firestore by its ID.
     *
     * @param recipe_id The unique identifier of the recipe to fetch
     * @return A Task containing the FavouriteRecipe if found
     * @throws IllegalArgumentException if recipe_id is null or empty
     */
    public Task<FavouriteRecipe> fetchFavouriteRecipe(@NonNull String recipe_id) {
        if (recipe_id.trim().isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("Recipe ID cannot be empty"));
        }

        return db.collection(COLLECTION_NAME)
                .document(recipe_id)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            return document.toObject(FavouriteRecipe.class);
                        }
                        throw new Exception("Recipe not found");
                    }
                    throw task.getException() != null ? task.getException() :
                            new Exception("Failed to fetch recipe");
                });
    }

    /**
     * Adds the current recipe to Firestore.
     *
     * @return A Task representing the async operation
     * @throws IllegalStateException if recipe_id is null or empty
     */
    public Task<Void> addFavouriteRecipe() {
        if (recipe_id == null || recipe_id.trim().isEmpty()) {
            return Tasks.forException(new IllegalStateException("Recipe ID cannot be null or empty"));
        }

        return db.collection(COLLECTION_NAME)
                .document(recipe_id)
                .set(this)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Recipe added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding recipe", e));
    }

    /**
     * Updates the current recipe in Firestore.
     *
     * @return A Task representing the async operation
     * @throws IllegalStateException if recipe_id is null or empty
     */
    public Task<Void> updateFavouriteRecipe() {
        if (recipe_id == null || recipe_id.trim().isEmpty()) {
            return Tasks.forException(new IllegalStateException("Recipe ID cannot be null or empty"));
        }

        return db.collection(COLLECTION_NAME)
                .document(recipe_id)
                .update(
                        "name", name,
                        "ingredients", new ArrayList<>(ingredients),
                        "steps", new ArrayList<>(steps)
                )
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Recipe updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating recipe", e));
    }

    /**
     * Deletes the current recipe from Firestore.
     *
     * @return A Task representing the async operation
     * @throws IllegalStateException if recipe_id is null or empty
     */
    public Task<Void> deleteFavouriteRecipe() {
        if (recipe_id == null || recipe_id.trim().isEmpty()) {
            return Tasks.forException(new IllegalStateException("Recipe ID cannot be null or empty"));
        }

        return db.collection(COLLECTION_NAME)
                .document(recipe_id)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Recipe deleted successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting recipe", e));
    }

    /**
     * Gets the recipe's unique identifier.
     *
     * @return The recipe ID
     */
    public String getRecipe_id() {
        return recipe_id;
    }

    /**
     * Sets the recipe's unique identifier.
     *
     * @param recipe_id The new recipe ID
     * @throws IllegalArgumentException if recipe_id is empty
     */
    public void setRecipe_id(@NonNull String recipe_id) {
        if (recipe_id.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe ID cannot be empty");
        }
        this.recipe_id = recipe_id;
    }

    /**
     * Gets the recipe's name.
     *
     * @return The name of the recipe
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the recipe's name.
     *
     * @param name The new name for the recipe
     * @throws IllegalArgumentException if name is empty
     */
    public void setName(@NonNull String name) {
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe name cannot be empty");
        }
        this.name = name;
    }

    /**
     * Gets the list of ingredients.
     *
     * @return An unmodifiable view of the ingredients list
     */
    @NonNull
    public List<String> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    /**
     * Sets the list of ingredients.
     *
     * @param ingredients The new list of ingredients
     */
    public void setIngredients(@Nullable List<String> ingredients) {
        this.ingredients.clear();
        if (ingredients != null) {
            this.ingredients.addAll(ingredients);
        }
    }

    /**
     * Gets the list of preparation steps.
     *
     * @return An unmodifiable view of the steps list
     */
    @NonNull
    public List<String> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    /**
     * Sets the list of preparation steps.
     *
     * @param steps The new list of preparation steps
     */
    public void setSteps(@Nullable List<String> steps) {
        this.steps.clear();
        if (steps != null) {
            this.steps.addAll(steps);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavouriteRecipe that = (FavouriteRecipe) o;
        return Objects.equals(recipe_id, that.recipe_id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(ingredients, that.ingredients) &&
                Objects.equals(steps, that.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipe_id, name, ingredients, steps);
    }

    @Override
    @NonNull
    public String toString() {
        return String.format("FavouriteRecipe{recipe_id='%s', name='%s', ingredients=%s, steps=%s}",
                recipe_id, name, ingredients, steps);
    }
}
