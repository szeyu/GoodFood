package com.hmir.goodfood.utilities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private List<String> diet_labels = new ArrayList<>();
    private int servings;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double sodium;
    private double calcium;
    private double magnesium;
    private double cholesterol;
    private double potassium;
    private double iron;
    private Uri image;

    public FavouriteRecipe (){
    }

    public FavouriteRecipe(double calcium, double calories, double carbs, List<String> diet_labels,
                           double fat, Uri image, double iron, double magnesium, String name,
                           double potassium, double protein, int servings, double sodium,
                           double cholesterol, String recipe_id) {
        this.recipe_id = recipe_id;
        this.calcium = calcium;
        this.calories = calories;
        this.carbs = carbs;
        this.cholesterol = cholesterol;
        this.diet_labels = diet_labels != null ? new ArrayList<>(diet_labels) : new ArrayList<>();
        this.fat = fat;
        this.image = image;
        this.iron = iron;
        this.magnesium = magnesium;
        this.name = name;
        this.potassium = potassium;
        this.protein = protein;
        this.servings = servings;
        this.sodium = sodium;
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
    public void addFavouriteRecipe(Map<String, Object> recipe) {
        Map<String, Object> defaultRecipe = new HashMap<>();
        defaultRecipe.put("calcium", null);
        defaultRecipe.put("calories", null);
        defaultRecipe.put("carbs", null);
        defaultRecipe.put("name", null);
        defaultRecipe.put("magnesium", null);
        defaultRecipe.put("cholesterol", null);
        defaultRecipe.put("diet_label", null);
        defaultRecipe.put("fat", null);
        defaultRecipe.put("image", null);
        defaultRecipe.put("servings", null);
        defaultRecipe.put("iron", null);
        defaultRecipe.put("potassium", null);
        defaultRecipe.put("protein", null);
        defaultRecipe.put("sodium", null);
        defaultRecipe.putAll(recipe);

        db.collection("favourite_recipes")
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)  // Order by document ID
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    String newRecipeId = "recipe-1";  // Default ID if no recipes exist

                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot latestRecipe = querySnapshot.getDocuments().get(0);
                        String latestRecipeId = latestRecipe.getId();

                        if (latestRecipeId.startsWith("recipe-")) {
                            String[] parts = latestRecipeId.split("-");
                            try {
                                int currentId = Integer.parseInt(parts[1]);
                                newRecipeId = "recipe-" + (currentId + 1);
                            } catch (NumberFormatException e) {
                                Log.e("Firestore", "Error parsing recipe ID: " + latestRecipeId, e);
                            }
                        }
                    }

                    // Add new recipe with the incremented ID
                    db.collection("favourite_recipes")
                            .document(newRecipeId)
                            .set(defaultRecipe)
                            .addOnSuccessListener(aVoid ->
                                    Log.d("Firestore", "Favourite recipe added"))
                            .addOnFailureListener(e ->
                                    Log.e("Firestore", "Error adding favourite recipe", e));
                })
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error fetching latest recipe", e));
    }

    // Update Recipe Record
    public void updateFavouriteRecipe(double calcium, double calories, double carbs, String name, double cholesterol, double magnesium,
                                      double fat, DocumentReference image, List<String> diet_labels, double iron, long servings,
                                      double potassium, double protein, double sodium) {
        // Create defensive copy of diet_labels
        List<String> safeDietLabels = diet_labels != null ?
                new ArrayList<>(diet_labels) : new ArrayList<>();

        Map<String, Object> recipeUpdates = Map.ofEntries(
                Map.entry("calcium", calcium),
                Map.entry("calories", calories),
                Map.entry("carbs", carbs),
                Map.entry("cholesterol", cholesterol),
                Map.entry("magnesium", magnesium),
                Map.entry("name", name),
                Map.entry("fat", fat),
                Map.entry("image", image),
                Map.entry("diet_labels", safeDietLabels),
                Map.entry("servings", servings),
                Map.entry("iron", iron),
                Map.entry("potassium", potassium),
                Map.entry("protein", protein),
                Map.entry("sodium", sodium)
        );
        db.collection("favourite_recipes")
                .document(recipe_id)
                .update(recipeUpdates)
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

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public List<String> getDiet_labels() {
        return new ArrayList<>(diet_labels);
    }

    public void setDiet_labels(List<String> diet_labels) {
        this.diet_labels = diet_labels != null ? new ArrayList<>(diet_labels) : new ArrayList<>();
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(double magnesium) {
        this.magnesium = magnesium;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        List<String> safeDietLabels = new ArrayList<>(diet_labels);
        return String.format(
                "FavouriteRecipe { " +
                        "recipe_id='%s', name='%s', diet_labels='%s', calories=%.2f, " +
                        "protein=%.2f, carbs=%.2f, fat=%.2f, sodium=%.2f, calcium=%.2f, " +
                        "iron=%.2f, cholesterol=%.2f, potassium=%.2f, magnesium=%.2f, servings=%d, image='%s' }",
                recipe_id,
                name,
                safeDietLabels,
                calories,
                protein,
                carbs,
                fat,
                sodium,
                calcium,
                iron,
                cholesterol,
                potassium,
                magnesium,
                servings,
                image != null ? image.getPath() : "null"
        );
    }
}
