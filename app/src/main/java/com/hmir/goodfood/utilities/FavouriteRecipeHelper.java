package com.hmir.goodfood.utilities;

import android.util.Log;

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

    // Helper Method
    // Fetch specific favourite recipe and return a Task of it
    private Task<FavouriteRecipe> fetchFavouriteRecipeTask(String recipe_id) {
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

    // Return FavouriteRecipe object
    public FavouriteRecipe fetchFavouriteRecipe(String recipe_id) throws Exception {
        if (recipe_id == null || recipe_id.isEmpty()) {
            throw new IllegalArgumentException("recipe_id is null or empty");
        }

        try {
            Task<FavouriteRecipe> task = fetchFavouriteRecipeTask(recipe_id);
            return Tasks.await(task); // This blocks until the task completes
        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error fetching favourite recipe information", e);
        }
    }

    // Helper Method
    // Fetch selected favourite recipes and return a Task of a List of them
    private Task<List<FavouriteRecipe>> fetchSomeFavouriteRecipesTask(List<String> recipe_id) {
        if (recipe_id == null || recipe_id.isEmpty()) {
            return Tasks.forException(new Exception("recipe IDs list is null or empty"));
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

    // Return selected FavouriteRecipe objects List
    public List<FavouriteRecipe> fetchSomeFavouriteRecipes(List<String> recipe_id) throws Exception {
        if (recipe_id == null || recipe_id.isEmpty()) {
            throw new Exception("recipe IDs list is null or empty");
        }

        try {
            Task<List<FavouriteRecipe>> task = fetchSomeFavouriteRecipesTask(recipe_id);
            return Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error fetching selected favourite recipes", e);
        }
    }

    // Helper Method
    // Fetch all favourite recipes with no restrictions and return a Task of a List of them
    private Task<List<FavouriteRecipe>> fetchAllFavouriteRecipesTask() {
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

    // Return all FavouriteRecipe objects List
    public List<FavouriteRecipe> fetchAllFavouriteRecipes() throws Exception {
        try {
            Task<List<FavouriteRecipe>> task = fetchAllFavouriteRecipesTask();
            return Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error fetching all favourite recipes", e);
        }
    }

    // Helper Method
    // Search favourite recipes by name (inclusive - contains)
    private Task<List<FavouriteRecipe>> searchFavouriteRecipesByNameTask(String name) {
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

    // Return searched FavouriteRecipe objects List
    public List<FavouriteRecipe> searchFavouriteRecipesByName(String name) throws Exception {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is null or empty");
        }

        try {
            Task<List<FavouriteRecipe>> task = searchFavouriteRecipesByNameTask(name);
            return Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error fetching favourite recipe information", e);
        }
    }

    // Function 2 : Add

    // Add new favourite recipe
    public String addFavouriteRecipe(Map<String, Object> recipe) throws Exception {
        Map<String, Object> defaultRecipe = new HashMap<>();
        defaultRecipe.put("calcium", null);
        defaultRecipe.put("calories", null);
        defaultRecipe.put("carbs", null);
        defaultRecipe.put("magnesium", null);
        defaultRecipe.put("cholesterol", null);
        defaultRecipe.put("diet_labels", null);
        defaultRecipe.put("fat", null);
        defaultRecipe.put("image", null);
        defaultRecipe.put("name", null);
        defaultRecipe.put("iron", null);
        defaultRecipe.put("potassium", null);
        defaultRecipe.put("protein", null);
        defaultRecipe.put("sodium", null);
        defaultRecipe.put("servings", null);
        defaultRecipe.putAll(recipe);

        try {
            // Fetch the latest recipe ID
            Task<QuerySnapshot> queryTask = db.collection("favourite_recipes")
                    .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                    .limit(1)
                    .get();

            QuerySnapshot querySnapshot = Tasks.await(queryTask);

            // Generate the new recipe ID
            String newrecipeId = "recipe-1";
            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot latestrecipe = querySnapshot.getDocuments().get(0);
                String latestrecipeId = latestrecipe.getId();
                if (latestrecipeId.startsWith("recipe-")) {
                    String[] parts = latestrecipeId.split("-");
                    try {
                        int currentId = Integer.parseInt(parts[1]);
                        newrecipeId = "recipe-" + (currentId + 1);
                    } catch (NumberFormatException e) {
                        Log.e("Firestore", "Failed to parse recipe ID: " + latestrecipeId, e);
                    }
                }
            }

            // Add the new recipe to Firestore
            Task<Void> addTask = db.collection("favourite_recipes")
                    .document(newrecipeId)
                    .set(defaultRecipe);

            Tasks.await(addTask);

            // Return the new recipe ID
            return newrecipeId;

        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error adding favourite recipe", e);
        }
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
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "recipe info updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating recipe info", e));
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
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "favourite recipe deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting favourite recipe", e));
    }
}
