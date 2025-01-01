package com.hmir.goodfood.utilities;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manages favorite recipes in the GoodFood application.
 * This class handles CRUD operations for favorite recipes in Firebase Firestore,
 * including storing nutritional information, ingredients, and recipe details.
 * Implements defensive copying for mutable collections and proper null handling.
 *
 * @see FirebaseFirestore
 */
public class FavouriteRecipe {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String recipe_id;
    private String name;
    private List<String> ingredients;
    private List<String> steps;

    /**
     * Default constructor required for Firebase Firestore deserialization.
     */
    public FavouriteRecipe() {
    }

    /**
     * Creates a new FavouriteRecipe with the specified details.
     *
     * @param recipe_id   The unique identifier for the recipe
     * @param name        The name of the recipe
     * @param ingredients List of ingredients required for the recipe
     * @param steps       List of preparation steps for the recipe
     */
    public FavouriteRecipe(String recipe_id, String name, List<String> ingredients, List<String> steps) {
        this.recipe_id = recipe_id;
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
        this.steps = new ArrayList<>(steps);
    }

    /**
     * Fetches a favorite recipe from Firestore by its ID.
     *
     * @param recipe_id The unique identifier of the recipe to fetch
     * @return A Task containing the FavouriteRecipe if found
     * @throws Exception if the recipe is not found or if the fetch operation fails
     */
    public Task<FavouriteRecipe> fetchFavouriteRecipe(String recipe_id) {
        return db.collection("favourite_recipes")
                .document(recipe_id)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            return documentSnapshot.toObject(FavouriteRecipe.class);
                        } else {
                            throw new Exception("Favourite recipe not found");
                        }
                    } else {
                        throw task.getException() != null ? task.getException() :
                                new Exception("Failed to fetch favourite recipe");
                    }
                });
    }

    // Add new favourite recipe
    public void addFavouriteRecipe() {
        db.collection("favourite_recipes")
                .document(recipe_id)
                .set(this)
                .addOnSuccessListener(aVoid ->
                        Log.d("Firestore", "Favourite recipe added"))
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error adding favourite recipe", e));
    }

    // Update Recipe Record
    public void updateFavouriteRecipe() {
        db.collection("favourite_recipes")
                .document(recipe_id)
                .update("name", name, "ingredients", ingredients, "steps", steps)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Recipe info updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating recipe info", e));
    }

    // Delete Favourite Recipe by Document ID
    public void deleteFavouriteRecipe() {
        db.collection("favourite_recipes")
                .document(recipe_id)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Favourite recipe deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting favourite recipe", e));
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
     */
    public void setRecipe_id(String recipe_id) {
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
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of ingredients.
     *
     * @return A defensive copy of the ingredients list
     */
    public List<String> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    /**
     * Sets the list of ingredients.
     *
     * @param ingredients The new list of ingredients
     */
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Gets the list of preparation steps.
     *
     * @return A defensive copy of the steps list
     */
    public List<String> getSteps() {
        return new ArrayList<>(steps);
    }

    /**
     * Sets the list of preparation steps.
     *
     * @param steps The new list of preparation steps
     */
    public void setSteps(List<String> steps) {
        this.steps = new ArrayList<>(steps);
    }

    @Override
    public String toString() {
        return String.format(
                "FavouriteRecipe { " +
                        "recipe_id='%s', name='%s', ingredients='%s', steps='%s', image='%s' }",
                recipe_id,
                name,
                ingredients,
                steps


        );
    }
}
