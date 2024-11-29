package com.hmir.goodfood.utilities;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Firestore {

    private final FirebaseFirestore db;
    private final String currentEmail = "test1@gmail.com";  // Hardcoded email

    public Firestore() {
        db = FirebaseFirestore.getInstance();
    }

    // Callback Interface for Async Operations
    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    // Fetch User Information and Return a Task with User
    public Task<User> fetchUserInfo() {
        return db.collection("user")
                .document(currentEmail)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                user.setEmail(documentSnapshot.getId());
                            }
                            return user;
                        } else {
                            throw new Exception("User not found");
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Fetch Favourite Recipes and Return a Task with List of FavouriteRecipe
    public Task<ArrayList<FavouriteRecipe>> fetchFavouriteRecipes() {
        return db.collection("favourite_recipe")
                .whereEqualTo("user_id", currentEmail)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        ArrayList<FavouriteRecipe> recipes = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            FavouriteRecipe recipe = doc.toObject(FavouriteRecipe.class);
                            recipe.setRecipe_id(doc.getId());
                            recipes.add(recipe);
                        }
                        return recipes;
                    } else {
                        throw task.getException();
                    }
                });
    }

    // Fetch Nutritional Records and Return a Task with List of NutritionalRecord
    public Task<ArrayList<NutritionalRecord>> fetchNutritionalRecords() {
        return db.collection("nutritional_record")
                .whereEqualTo("user_id", currentEmail)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        ArrayList<NutritionalRecord> records = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            NutritionalRecord record = doc.toObject(NutritionalRecord.class);
                            record.setRecord_id(doc.getId());
                            records.add(record);
                        }
                        return records;
                    } else {
                        throw task.getException();
                    }
                });
    }


    // Update User Information
    public void updateUserInfo(String username, List<String> healthLabel, long age, long height, long weight) {
        Map<String, Object> userUpdates = Map.of(
                "username", username,
                "health_label", healthLabel,
                "age", age,
                "height", height,
                "weight", weight
        );
        db.collection("user")
                .document(currentEmail)
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User info updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating user info", e));
    }

    // Add New Nutritional Record
    public void addNutritionalRecord(Map<String, Object> record) {
        db.collection("nutritional_record")
                .add(record)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Nutritional record added"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding nutritional record", e));
    }

    // Delete Nutritional Record by Document ID
    public void deleteNutritionalRecord(String recordId) {
        db.collection("nutritional_record")
                .document(recordId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Nutritional record deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting nutritional record", e));
    }

    // Add Favourite Recipe
    public void addFavouriteRecipe(Map<String, Object> recipe) {
        db.collection("favourite_recipe")
                .add(recipe)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Favourite recipe added"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error adding favourite recipe", e));
    }

    // Delete Favourite Recipe by Document ID
    public void deleteFavouriteRecipe(String recipeId) {
        db.collection("favourite_recipe")
                .document(recipeId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Favourite recipe deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting favourite recipe", e));
    }
}

