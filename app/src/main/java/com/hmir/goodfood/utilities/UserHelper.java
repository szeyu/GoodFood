package com.hmir.goodfood.utilities;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserHelper {
    /*
      In this class, there will be multiple sections of methods relating to a user:
      Section 1 : User Specifics
                    - fetchUserInfo()
                    - addNewUser()
                    - updateUserInfo()
                    - deleteUser()

      Section 2 : User Nutritional Records
                    - fetchUserNutritionalRecord()
                    - fetchAllUserNutritionalRecords()
                    - addUserNutritionalRecord()
                    - updateUserNutritionalRecord()
                    - deleteUserNutritionalRecord()

      Section 3 : User Favourite Recipes
                    - fetchUserFavouriteRecipe()
                    - fetchAllUserFavouriteRecipes()
                    - addUserFavouriteRecipe()
                    - updateUserFavouriteRecipe()
                    - deleteUserFavouriteRecipe()
    */

    //    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private final static String email = "test1@gmail.com";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final NutritionalRecordHelper nutritionalRecordHelper = new NutritionalRecordHelper();
    private final FavouriteRecipeHelper favouriteRecipeHelper = new FavouriteRecipeHelper();
    private User currentUser;
    public UserHelper () {
        try{
            currentUser = fetchUserInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Section 1 : User Specifics

    // Helper Method
    // Fetch user and return a Task of it
    private Task<User> fetchUserInfoTask() {
        if (email == null || email.isEmpty()) {
            return Tasks.forException(new Exception("Email is null or empty"));
        }

        return db.collection("user").document(email).get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    return documentSnapshot.toObject(User.class);
                } else {
                    throw new Exception("User not found");
                }
            } else {
                throw task.getException() != null ?
                        task.getException() : new Exception("Failed to fetch user record");
            }
        });
    }

    // Return User object
    public User fetchUserInfo() throws Exception {
        try {
            Task<User> task = fetchUserInfoTask();
            return Tasks.await(task); // This blocks until the task completes
        } catch (ExecutionException | InterruptedException e) {
            throw new Exception("Error fetching user information", e);
        }
    }

    // Update User document
    public void updateUserInfo(String username, List<String> health_labels, long age, long height, long weight,
                               List<String> favourite_recipes, List<String> nutritional_records) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        Map<String, Object> userUpdates = Map.of(
                "username", username,
                "health_labels", health_labels,
                "age", age,
                "height", height,
                "weight", weight,
                "favourite_recipes", favourite_recipes,
                "nutritional_records", nutritional_records
        );

        db.collection("user").document(email).update(userUpdates)
                .addOnSuccessListener(aVoid ->
                        Log.d("Firestore", "User info updated"))
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error updating user info", e));
    }

    // Add new user
    public void addNewUser(Map<String, Object> user) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        Map<String, Object> defaultUser = new HashMap<>();
        defaultUser.put("username", null);
        defaultUser.put("age", null);
        defaultUser.put("height", null);
        defaultUser.put("weight", null);
        defaultUser.put("health_label", null);
        defaultUser.put("favourite_recipes", null);
        defaultUser.put("nutritional_records", null);
        defaultUser.putAll(user);

        db.collection("user")
                .document(email)
                .set(defaultUser, SetOptions.merge())  // Overwrite existing data or add new user
                .addOnSuccessListener(aVoid ->
                        Log.d("Firestore", "User added or updated with email as document ID"))
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error adding or updating user", e));
    }

    // Delete user
    public void deleteUser() {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        db.collection("user")
                .document(email)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d("Firestore", "User deleted"))
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error deleting user", e));
    }

    // Section 2 : User Nutritional Records

    // Fetch specific nutritional record based on record id
    public NutritionalRecord fetchUserNutritionalRecord(String record_id) {
        if (record_id == null || record_id.isEmpty()) {
            throw new IllegalArgumentException("record_id is null or empty");
        }

        try {
            return nutritionalRecordHelper.fetchNutritionalRecord(record_id);
        } catch (Exception e) {
            System.err.println("Error fetching nutritional record: " + e.getMessage());
            return null;
        }
    }

    // Fetch list of nutritional records based on record id
    public List<NutritionalRecord> fetchAllUserNutritionalRecords() {
        List<String> record_id = currentUser.getNutritional_records();
        if (record_id == null || record_id.isEmpty()) {
            throw new IllegalArgumentException("record_id is null or empty");
        }

        try {
            return nutritionalRecordHelper.fetchSomeNutritionalRecords(record_id);
        } catch (Exception e) {
            System.err.println("Error fetching nutritional record: " + e.getMessage());
            return null;
        }
    }

    // Add new nutritional record
    public void addUserNutritionalRecord(Map<String, Object> record) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        String newRecordId = null;
        try {
            newRecordId = nutritionalRecordHelper.addNutritionalRecord(record);
        } catch (Exception e) {
            System.err.println("Error adding nutritional record: " + e.getMessage());
        }

        if (newRecordId != null) {
            currentUser.getNutritional_records().add(newRecordId);

            db.collection("user")
                    .document(email)
                    .update("nutritional_records", FieldValue.arrayUnion(newRecordId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "New nutritional record ID added to user successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error adding new nutritional record ID to user", e);
                    });
        }
    }

    // Update nutritional record
    public void updateUserNutritionalRecord(String record_id, Map<String, Object> record) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        String newRecordId = null;
        try {
            newRecordId = nutritionalRecordHelper.addNutritionalRecord(record);
        } catch (Exception e) {
            System.err.println("Error adding nutritional record: " + e.getMessage());
            return;  // exit early
        }

        if (newRecordId != null) {
            currentUser.getNutritional_records().add(newRecordId);
            currentUser.getNutritional_records().remove(record_id);

            db.collection("user")
                    .document(email)
                    .update(
                            "nutritional_records", FieldValue.arrayRemove(record_id),
                            "nutritional_records", FieldValue.arrayUnion(newRecordId)
                    )
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Nutritional record updated successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error updating nutritional record", e);
                    });
        }
    }

    // Delete nutritional record
    public void deleteUserNutritionalRecord(String record_id) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        if (record_id == null || record_id.isEmpty()) {
            throw new IllegalArgumentException("record_id is null or empty");
        }

        if (currentUser.getNutritional_records().contains(record_id)) {
            currentUser.getNutritional_records().remove(record_id);

            db.collection("user")
                    .document(email)
                    .update("nutritional_records", FieldValue.arrayRemove(record_id))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Nutritional record removed from user successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error removing nutritional record from user", e);
                    });
        } else {
            Log.d("Firestore", "Nutritional record not found in user's list.");
        }
    }

    // Section 3 : User Favourite Recipes

    // Fetch specific favourite recipe based on recipe id
    public FavouriteRecipe fetchUserFavouriteRecipe(String recipe_id) {
        if (recipe_id == null || recipe_id.isEmpty()) {
            throw new IllegalArgumentException("recipe_id is null or empty");
        }

        try {
            return favouriteRecipeHelper.fetchFavouriteRecipe(recipe_id);
        } catch (Exception e) {
            System.err.println("Error fetching favourite recipe: " + e.getMessage());
            return null;
        }
    }

    // Fetch list of favourite recipes based on recipe id
    public List<FavouriteRecipe> fetchAllUserFavouriteRecipes() {
        List<String> recipe_id = currentUser.getFavourite_recipes();
        if (recipe_id == null || recipe_id.isEmpty()) {
            throw new IllegalArgumentException("recipe_id is null or empty");
        }

        try {
            return favouriteRecipeHelper.fetchSomeFavouriteRecipes(recipe_id);
        } catch (Exception e) {
            System.err.println("Error fetching favourite recipe: " + e.getMessage());
            return null;
        }
    }

    // Search favourite recipes by name
    public List<FavouriteRecipe> searchUserFavouriteRecipesByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }

        try {
            return favouriteRecipeHelper.searchFavouriteRecipesByName(name);
        } catch (Exception e) {
            System.err.println("Error searching favourite recipe: " + e.getMessage());
            return null;
        }
    }

    // Add new nutritional record
    public void addUserFavouriteRecipe(Map<String, Object> recipe) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        String newRecipeId = null;
        try {
            newRecipeId = favouriteRecipeHelper.addFavouriteRecipe(recipe);
        } catch (Exception e) {
            System.err.println("Error adding nutritional record: " + e.getMessage());
        }

        if (newRecipeId != null) {
            currentUser.getFavourite_recipes().add(newRecipeId);

            db.collection("user")
                    .document(email)
                    .update("favourite_recipes", FieldValue.arrayUnion(newRecipeId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "New favourite recipe ID added to user successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error adding new favourite recipe ID to user", e);
                    });
        }
    }

    // Update favourite recipe
    public void updateUserFavouriteRecipe(String recipe_id, Map<String, Object> recipe) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        String newRecipeId = null;
        try {
            newRecipeId = favouriteRecipeHelper.addFavouriteRecipe(recipe);
        } catch (Exception e) {
            System.err.println("Error adding favourite recipe: " + e.getMessage());
            return;  // exit early
        }

        if (newRecipeId != null) {
            currentUser.getFavourite_recipes().add(newRecipeId);
            currentUser.getFavourite_recipes().remove(recipe_id);

            db.collection("user")
                    .document(email)
                    .update(
                            "favourite_recipes", FieldValue.arrayRemove(recipe_id),
                            "favourite_recipes", FieldValue.arrayUnion(newRecipeId)
                    )
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Favourite recipe updated successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error updating favourite recipe", e);
                    });
        }
    }

    // Delete favourite recipe
    public void deleteUserFavouriteRecipe(String recipe_id) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        if (recipe_id == null || recipe_id.isEmpty()) {
            throw new IllegalArgumentException("recipe_id is null or empty");
        }

        if (currentUser.getFavourite_recipes().contains(recipe_id)) {
            currentUser.getFavourite_recipes().remove(recipe_id);

            db.collection("user")
                    .document(email)
                    .update("favourite_recipes", FieldValue.arrayRemove(recipe_id))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Favourite recipe removed from user successfully.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error removing favourite recipe from user", e);
                    });
        } else {
            Log.d("Firestore", "Favourite recipe not found in user's list.");
        }
    }

    // Callback Interface for Asynchronous Operations
    public interface Callback<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }
}
