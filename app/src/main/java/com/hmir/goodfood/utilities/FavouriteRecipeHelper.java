package com.hmir.goodfood.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for managing favorite recipes in Firebase Firestore.
 * This class provides functionality for CRUD operations (Create, Read, Update, Delete)
 * on favorite recipes, including searching and managing user-specific favorite recipes.
 */
public class FavouriteRecipeHelper {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Default constructor for FavouriteRecipeHelper.
     */
    public FavouriteRecipeHelper() {}

    // Function 1 : Fetch and Search
    /**
     * Fetches a specific favorite recipe by its ID.
     *
     * @param recipe_id The unique identifier of the recipe
     * @return Task containing the FavouriteRecipe object
     */
    public Task<FavouriteRecipe> fetchFavouriteRecipe(@NonNull String recipe_id) {
        return db.collection("favourite_recipes")
                .document(recipe_id)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            FavouriteRecipe recipe = documentSnapshot.toObject(FavouriteRecipe.class);
                            if (recipe != null) {
                                // Set the recipe_id field to the document ID
                                recipe.setRecipe_id(documentSnapshot.getId());
                            }
                            return recipe;
                        } else {
                            throw new Exception("Favourite recipe not found");
                        }
                    } else {
                        throw task.getException() != null ? task.getException() :
                                new Exception("Failed to fetch favourite recipe");
                    }
                });
    }

    /**
     * Fetches multiple favorite recipes based on provided recipe IDs.
     *
     * @param recipe_id List of recipe IDs to fetch
     * @return Task containing a List of FavouriteRecipe objects
     */
    public Task<List<FavouriteRecipe>> fetchSomeFavouriteRecipes(List<String> recipe_id) {
        if (recipe_id == null || recipe_id.isEmpty()) {
            return Tasks.forException(new Exception("recipe_id(s) list is null or empty"));
        }

        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String id : recipe_id) {
            Task<DocumentSnapshot> task = db.collection("favourite_recipes").document(id).get();
            tasks.add(task);
        }

        return Tasks.whenAllSuccess(tasks).continueWith(task -> {
            List<FavouriteRecipe> recipes = new ArrayList<>();
            for (Object result : task.getResult()) {
                DocumentSnapshot documentSnapshot = (DocumentSnapshot) result;
                if (documentSnapshot.exists()) {
                    FavouriteRecipe recipe = documentSnapshot.toObject(FavouriteRecipe.class);
                    if (recipe != null) {
                        recipe.setRecipe_id(documentSnapshot.getId());
                        recipes.add(recipe);
                    }
                }
            }
            return recipes;
        });
    }

    // Fetch all favourite recipes with no restrictions and return a Task of a List of them
    private Task<List<FavouriteRecipe>> fetchAllFavouriteRecipes() {
        return db.collection("favourite_recipes")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<FavouriteRecipe> recipes = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            if (documentSnapshot.exists()) {
                                FavouriteRecipe recipe = documentSnapshot.toObject(FavouriteRecipe.class);
                                if (recipe != null) {
                                    recipe.setRecipe_id(documentSnapshot.getId());
                                    recipes.add(recipe);
                                }
                            }
                        }
                        return recipes;
                    } else {
                        throw task.getException() != null ? task.getException() :
                                new Exception("Failed to fetch all favourite recipes");
                    }
                });
    }

    // Search favourite recipes by name (inclusive - contains)
    public Task<List<FavouriteRecipe>> searchFavouriteRecipesByName(String name) {
        if (name == null || name.isEmpty()) {
            return Tasks.forException(new IllegalArgumentException("Name is null or empty"));
        }

        return db.collection("favourite_recipes")
                .whereGreaterThanOrEqualTo("name", name.toLowerCase())
                .whereLessThanOrEqualTo("name", name.toLowerCase() + "\uf8ff")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        List<FavouriteRecipe> recipes = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            if (documentSnapshot.exists()) {
                                FavouriteRecipe recipe = documentSnapshot.toObject(FavouriteRecipe.class);
                                if (recipe != null) {
                                    recipe.setRecipe_id(documentSnapshot.getId());
                                    recipes.add(recipe);
                                }
                            }
                        }
                        return recipes;
                    } else {
                        throw task.getException() != null ? task.getException() :
                                new Exception("Failed to fetch favourite recipes by name");
                    }
                });
    }

    // Function 2 : Add

    /**
     * Adds a new favorite recipe for the currently authenticated user.
     *
     * @param recipe Recipe data as a Map
     * @param callback Callback to handle success or failure of the operation
     */
    public void addFavouriteRecipe(Map<String, Object> recipe, OnRecipeAddedCallback callback) {
        // Get the current user's email from Firebase Authentication
        String userEmail = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getEmail() : null;

        if (userEmail == null) {
            callback.onError(new Exception("User is not authenticated"));
            return;
        }

        // Prepare the recipe data
        Map<String, Object> defaultRecipe = new HashMap<>(); // Use a mutable map here
        defaultRecipe.put("name", recipe.get("name"));  // Add name from the passed data
        defaultRecipe.put("ingredients", recipe.get("ingredients"));  // Add ingredients from the passed data

        // Get the steps from the recipe
        Object stepsObj = recipe.get("steps");
        List<String> splitSteps = new ArrayList<>();

        if (stepsObj instanceof String) {
            // If steps are a String, split them into individual steps
            String steps = (String) stepsObj;
            splitSteps = splitSteps(steps);  // Split the steps into a list of strings
        } else if (stepsObj instanceof List) {
            // If steps are already a List, just cast it
            splitSteps = (List<String>) stepsObj;
        }

        // Add the list of steps to the recipe data
        defaultRecipe.put("steps", splitSteps);

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the user's document using email as the document ID
        DocumentReference userDocRef = db.collection("user").document(userEmail);

        // Reference to the `favourite_recipes` collection to store the recipe data
        CollectionReference favRecipesRef = db.collection("favourite_recipes");

        // Add the recipe to the `favourite_recipes` collection and get the recipe ID
        favRecipesRef.add(defaultRecipe)
                .addOnSuccessListener(documentReference -> {
                    String newRecipeId = documentReference.getId();  // Get the new recipe ID

                    // Now, update the user's document to store the recipe ID in the `favourite_recipes` field
                    userDocRef.update("favourite_recipes", FieldValue.arrayUnion(newRecipeId))
                            .addOnSuccessListener(aVoid -> {
                                callback.onRecipeAdded(newRecipeId);  // Call the callback with the new recipe ID
                            })
                            .addOnFailureListener(e -> {
                                callback.onError(new Exception("Error updating user's favourite recipes", e));  // Handle error
                            });
                })
                .addOnFailureListener(e -> {
                    callback.onError(new Exception("Error adding favourite recipe", e));  // Handle error
                });
    }

    // Helper method to split the steps string into individual steps
    public List<String> splitSteps(String steps) {
        List<String> splitSteps = new ArrayList<>();
        if (steps != null && !steps.isEmpty()) {
            // Split by period followed by a space or newline
            String[] parts = steps.split("(?<=\\.)\\s*");

            // Variable to track whether to split or not (skip every other period)
            boolean shouldSplit = false;

            StringBuilder currentStep = new StringBuilder();

            for (String part : parts) {
                currentStep.append(part.trim()); // Append the current part

                // If the current part ends with a period, decide whether to split or not
                if (currentStep.toString().endsWith(".")) {
                    if (shouldSplit) {
                        splitSteps.add(currentStep.toString().trim()); // Add the current step
                        currentStep.setLength(0); // Reset for the next step
                    }
                    shouldSplit = !shouldSplit; // Toggle the split flag
                } else {
                    currentStep.append(" "); // Add space between steps if not splitting
                }
            }

            // Add any remaining text as a final step
            if (currentStep.length() > 0) {
                splitSteps.add(currentStep.toString().trim());
            }
        }
        return splitSteps;
    }

    // Function 3 : Update
    /**
     * Updates an existing favorite recipe.
     *
     * @param name New name for the recipe
     * @param ingredients New list of ingredients
     * @param steps New list of steps
     * @param recipe_id ID of the recipe to update
     * @throws IllegalArgumentException if recipe_id is null or empty
     */
    public void updateFavouriteRecipe(String name, List<String> ingredients, List<String> steps, String recipe_id) {
        if (recipe_id == null || recipe_id.isEmpty()) {
            throw new IllegalArgumentException("recipe_id is null or empty");
        }

        Map<String, Object> recipeUpdates = Map.ofEntries(
                Map.entry("name", name),
                Map.entry("ingredients", ingredients),
                Map.entry("steps", steps)
        );
        db.collection("favourite_recipes")
                .document(recipe_id)
                .update(recipeUpdates)
                .addOnSuccessListener(aVoid -> Log.d("FavouriteRecipeHelper", "recipe info updated"))
                .addOnFailureListener(e -> Log.e("FavouriteRecipeHelper", "Error updating recipe info", e));
    }

    // Function 4 : Delete
    /**
     * Deletes a favorite recipe.
     *
     * @param recipe_id ID of the recipe to delete
     * @throws IllegalArgumentException if recipe_id is null or empty
     */
    public void deleteFavouriteRecipe(String recipe_id) {
        if (recipe_id == null || recipe_id.isEmpty()) {
            throw new IllegalArgumentException("recipe_id is null or empty");
        }

        db.collection("favourite_recipes")
                .document(recipe_id)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("FavouriteRecipeHelper", "favourite recipe deleted"))
                .addOnFailureListener(e -> Log.e("FavouriteRecipeHelper", "Error deleting favourite recipe", e));
    }

    /**
     * Callback interface for recipe addition operations.
     */
    public interface OnRecipeAddedCallback {
        /**
         * Called when a recipe is successfully added.
         * @param recipeId ID of the newly added recipe
         */
        void onRecipeAdded(String recipeId);

        /**
         * Called when an error occurs during recipe addition.
         * @param e The exception that occurred
         */
        void onError(Exception e);
    }
}
