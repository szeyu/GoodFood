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

    public FavouriteRecipe() {
    }

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

    // Getters and Setters
    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return new ArrayList<>(steps);
    }

    public void setSteps(List<String> steps) {
        this.steps = new ArrayList<>(steps);
    }

    @Override
    public String toString() {
        List<String> safeDietLabels = new ArrayList<>(diet_labels);
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
