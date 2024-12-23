package com.hmir.goodfood.utilities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FavouriteRecipeHelper {
    /*
      In this class, there will be multiple methods relating to favourite recipe(s):

      * Note :  The following Functions 1 - 4 are mostly covered in UserHelper, so these usually are not being used / called directly from
                FavouriteRecipeHelper.

      Function 1 : Fetch and Search
                    - fetchFavouriteRecipe()
                    - fetchSomeFavouriteRecipes()
                    - fetchAllFavouriteRecipes()
                    - searchFavouriteRecipesByName()

      Function 2 : Add
                    - addFavouriteRecipe()

      Function 3 : Update
                    - updateFavouriteRecipe()

       Function 4 : Delete
                    - deleteFavouriteRecipe()
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FavouriteRecipeHelper() {}

    // Function 1 : Fetch and Search

    // Fetch specific favourite recipe and return a Task of it
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

    // Fetch selected favourite recipes and return a Task of a List of them
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
                // Start of the range for lexicographical order
                .whereLessThanOrEqualTo("name", name.toLowerCase() + "\uf8ff")
                // End of the range (wide range character)
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

    // Add new favourite recipe
    public void addFavouriteRecipe(Map<String, Object> recipe, OnRecipeAddedCallback callback) {
        Map<String, Object> defaultRecipe = new HashMap<>();
        defaultRecipe.put("calcium", 0);
        defaultRecipe.put("calories", 0);
        defaultRecipe.put("carbs", 0);
        defaultRecipe.put("magnesium", 0);
        defaultRecipe.put("cholesterol", 0);
        defaultRecipe.put("diet_labels", null);
        defaultRecipe.put("fat", 0);
        defaultRecipe.put("image", null);
        defaultRecipe.put("name", null);
        defaultRecipe.put("iron", 0);
        defaultRecipe.put("potassium", 0);
        defaultRecipe.put("protein", 0);
        defaultRecipe.put("sodium", 0);
        defaultRecipe.put("servings", 0);
        defaultRecipe.putAll(recipe);

        db.collection("favourite_recipes")
                .add(defaultRecipe)
                .addOnSuccessListener(documentReference -> {
                    String newRecipeId = documentReference.getId();
                    callback.onRecipeAdded(newRecipeId);
                })
                .addOnFailureListener(e -> {
                    callback.onError(new Exception("Error adding favourite recipe", e));
                });
    }

    // Function 3 : Update

    // Update favourite recipe
    public void updateFavouriteRecipe(double calcium, double calories, double carbs, double cholesterol,
                                        double magnesium, List<String> diet_labels, double fat, int servings,
                                        DocumentReference image, String name, double iron,
                                        double potassium, double protein, double sodium, String recipe_id) {
        if (recipe_id == null || recipe_id.isEmpty()) {
            throw new IllegalArgumentException("recipe_id is null or empty");
        }

        Map<String, Object> recipeUpdates = Map.ofEntries(
                Map.entry("calcium", calcium),
                Map.entry("calories", calories),
                Map.entry("carbs", carbs),
                Map.entry("cholesterol", cholesterol),
                Map.entry("magnesium", magnesium),
                Map.entry("diet_labels", diet_labels),
                Map.entry("fat", fat),
                Map.entry("image", image),
                Map.entry("name", name),
                Map.entry("iron", iron),
                Map.entry("potassium", potassium),
                Map.entry("protein", protein),
                Map.entry("sodium", sodium),
                Map.entry("servings", servings)
        );
        db.collection("favourite_recipes")
                .document(recipe_id)
                .update(recipeUpdates)
                .addOnSuccessListener(aVoid -> Log.d("FavouriteRecipeHelper", "recipe info updated"))
                .addOnFailureListener(e -> Log.e("FavouriteRecipeHelper", "Error updating recipe info", e));
    }

    // Function 4 : Delete

    // Delete favourite recipe
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

    public interface OnRecipeAddedCallback {
        void onRecipeAdded(String recipeId);
        void onError(Exception e);
    }
}
