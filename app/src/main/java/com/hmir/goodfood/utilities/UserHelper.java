package com.hmir.goodfood.utilities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Helper class for managing user-related operations in the application.
 * This class handles user data management, nutritional records, and favorite recipes.
 * It provides methods for CRUD operations on user data and related entities.
 *
 * The class is organized into four main sections:
 * 1. User Specifics - Basic user operations (fetch, add, update, delete)
 * 2. User Nutritional Records - Managing user's nutritional data
 * 3. User Favourite Recipes - Managing user's favorite recipes
 * 4. Callbacks - Interfaces for handling asynchronous operations
 */
public class UserHelper {
    /*
      In this class, there will be multiple sections of methods relating to a user:
      Section 1 : User Specifics
                    - fetchUser()
                    - isUserExist()
                    - addNewUser()
                    - updateUserInfo()
                    - deleteUser()

                    *  fetch methods are called with callbacks
                        userHelper.fetchMethod(argument, new UserHelper.OnFetchedCallback() {
                            @Override
                            public void onFetched(Object obj) {
                                Log.d("Example", "Fetched: " + obj.toString());
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("Example", "Error fetching: " + e.getMessage());
                            }
                        });
                    * other methods are called the normal way

      Section 2 : User Nutritional Records
                    - fetchUserNutritionalRecord()
                    - fetchAllUserNutritionalRecords()
                    - addUserNutritionalRecord()
                    - updateUserNutritionalRecord()
                    - deleteUserNutritionalRecord()

                    * refer to Section 1 for method calling

      Section 3 : User Favourite Recipes
                    - fetchUserFavouriteRecipe()
                    - fetchAllUserFavouriteRecipes()
                    - searchUserFavouriteRecipesByName()
                    - addUserFavouriteRecipe()
                    - updateUserFavouriteRecipe()
                    - deleteUserFavouriteRecipe()

                    * refer to Section 1 for method calling

       Section 4 : Callbacks
                    - OnRecordFetchedCallback
                    - OnRecipeFetchedCallback
                    - OnRecordListFetchedCallback
                    - OnRecipeListFetchedCallback
                    - OnUserFetchedCallback

                    * do use these if you are calling fetch methods
    */

    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final NutritionalRecordHelper nutritionalRecordHelper = new NutritionalRecordHelper();
    private final FavouriteRecipeHelper favouriteRecipeHelper = new FavouriteRecipeHelper();
    private final Queue<Runnable> pendingOperations = new LinkedList<>();
    public User currentUser;
    //    private final static String email = "test1@gmail.com";
    private boolean isUserLoaded = false;

    public UserHelper() {
        initializeCurrentUser();
    }

    public UserHelper(int indicator) {
    }

    private void initializeCurrentUser() {
        fetchUser().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult();
                if (user != null) {
                    Log.d("UserHelper", "User loaded successfully: " + user.toString());
                    currentUser = user;
                    isUserLoaded = true;
                    processPendingOperations();
                } else {
                    currentUser = null;
                    Log.e("UserHelper", "User not found");
                }
            } else {
                Exception e = task.getException();
                Log.e("UserHelper", "Error loading user: " + (e != null ? e.getMessage() : "Unknown error"));
            }
        });
    }

    // Add an operation to the queue
    private void enqueueOrExecute(Runnable operation) {
        if (isUserLoaded) {
            operation.run();
        } else {
            pendingOperations.add(operation);
        }
    }

    // Process all pending operations
    private void processPendingOperations() {
        while (!pendingOperations.isEmpty()) {
            // Execute each task
            pendingOperations.poll().run();
        }
    }

    // Section 1 : User Specifics

    // Fetch user and return a Task of it
    public Task<User> fetchUser() {
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
                throw task.getException() != null ? task.getException() : new Exception("Failed to fetch user record");
            }
        });
    }

    // Fetch user and check if it exists
    public Task<Boolean> isUserExist() {
        if (email == null || email.isEmpty()) {
            return Tasks.forException(new Exception("Email is null or empty"));
        }

        // Check if document exists
        return db.collection("user").document(email).get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                return documentSnapshot.exists();
            } else {
                throw task.getException() != null ? task.getException() : new Exception("Failed to check user existence");
            }
        });
    }

    // For MainActivity, saving user data to SharedPreferences when user exists in Firestore
    public void saveUserDataFromFirestoreToSharedPreferences(Context context) {
        enqueueOrExecute(() -> {
            // Save data in Shared Preferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            editor.putString("Username", currentUser.getUsername());
            editor.putString("Age", Long.toString(currentUser.getAge()));
            editor.putString("Height", Double.toString(currentUser.getHeight()));
            editor.putString("Weight", Double.toString(currentUser.getWeight()));
            editor.putString("DietTypes", currentUser.getHealth_labels().toString());
            editor.apply();

            Log.d("UserHelper","SharedPreferences saved");
        });
    }

    // Update User document
    public void updateUserInfo(String username, List<String> health_labels, long age, double height, double weight,
                               List<String> favourite_recipes, List<String> nutritional_records) {
        enqueueOrExecute(() -> {
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

            db.collection("user").document(email).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    db.collection("user").document(email).update(userUpdates)
                            .addOnSuccessListener(aVoid ->
                                    Log.d("UserHelper", "User info updated"))
                            .addOnFailureListener(e ->
                                    Log.e("UserHelper", "Error updating user info", e));
                } else {
                    Log.e("UserHelper", "User document not found for email: " + email);
                }
            });
        });
    }

    // Add new user
    public void addNewUser(Map<String, Object> user) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }

        Map<String, Object> defaultUser = new HashMap<>();
        defaultUser.put("username", null);
        defaultUser.put("age", 0);
        defaultUser.put("height", 0);
        defaultUser.put("weight", 0);
        defaultUser.put("health_labels", null);
        defaultUser.put("favourite_recipes", null);
        defaultUser.put("nutritional_records", null);
        defaultUser.putAll(user);

        db.collection("user")
                .document(email)
                .set(defaultUser, SetOptions.merge())  // Overwrite existing data or add new user
                .addOnSuccessListener(aVoid ->
                        Log.d("UserHelper", "User added or updated"))
                .addOnFailureListener(e ->
                        Log.e("UserHelper", "Error adding or updating user", e));

        initializeCurrentUser();
    }

    // Delete user
    public void deleteUser() {
        enqueueOrExecute(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is null or empty");
            }

            db.collection("user")
                    .document(email)
                    .delete()
                    .addOnSuccessListener(aVoid ->
                            Log.d("UserHelper", "User deleted"))
                    .addOnFailureListener(e ->
                            Log.e("UserHelper", "Error deleting user", e));

        });
    }

    // Section 2 : User Nutritional Records

    // Fetch specific nutritional record based on record id and return a callback
    public void fetchUserNutritionalRecord(String record_id, OnRecordFetchedCallback callback) {
        enqueueOrExecute(() -> {
            if (record_id == null || record_id.isEmpty()) {
                callback.onError(new IllegalArgumentException("record_id is null or empty"));
                return;
            }

            nutritionalRecordHelper.fetchNutritionalRecord(record_id)
                    .addOnSuccessListener(record -> {
                        if (record != null) {
                            callback.onRecordFetched(record);
                        } else {
                            callback.onError(new Exception("Nutritional record not found"));
                        }
                    })
                    .addOnFailureListener(callback::onError);
        });
    }


    // Fetch list of nutritional records based on record id and return a callback
    public void fetchAllUserNutritionalRecords(OnRecordListFetchedCallback callback) {
        enqueueOrExecute(() -> {
            if (!isUserLoaded) {
                Log.e("UserHelper", "User is not loaded yet. Aborting operation.");
                return;
            }

            List<String> record_id = currentUser.getNutritional_records();

            if (record_id == null || record_id.isEmpty()) {
                callback.onError(new IllegalArgumentException("record_id(s) are null or empty"));
                return;
            }

            nutritionalRecordHelper.fetchSomeNutritionalRecords(record_id)
                    .addOnSuccessListener(records -> {
                        if (records != null && !records.isEmpty()) {
                            callback.onRecordListFetched(records);
                        } else {
                            callback.onError(new Exception("No nutritional records found"));
                        }
                    })
                    .addOnFailureListener(callback::onError);
        });
    }

    // Add new nutritional record
    public void addUserNutritionalRecord(Map<String, Object> record, Uri imageUri) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }
        // Upload photo first, then proceed to add record
        uploadPhotoToStorage(imageUri, email)
                .addOnSuccessListener(uri -> {
                    // Once the image upload is complete, get the download URL
                    String imageDownloadUrl = uri.toString();

                    // Add the image URL to the nutritional record map
                    record.put("image", imageDownloadUrl);

                    // Proceed with adding the nutritional record
                    enqueueOrExecute(() -> {
                        nutritionalRecordHelper.addNutritionalRecord(record, new NutritionalRecordHelper.OnRecordAddedCallback() {
                            @Override
                            public void onRecordAdded(String recordId) {
                                if(currentUser.getNutritional_records() != null){
                                    currentUser.getNutritional_records().add(recordId);
                                } else {
                                    List<String> nutritional_records = new ArrayList<>();
                                    nutritional_records.add(recordId);
                                    currentUser.setNutritional_records(nutritional_records);
                                }

                                db.collection("user")
                                        .document(email)
                                        .update("nutritional_records", FieldValue.arrayUnion(recordId))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("UserHelper", "Nutritional record added to user successfully." + recordId);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("UserHelper", "Error adding nutritional record to user", e);
                                        });
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("UserHelper", "Error adding nutritional record: " + e.getMessage());
                            }
                        });
                    });

                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Image upload failed", e);
                });
    }

    // Update nutritional record
    public void updateUserNutritionalRecord(String record_id, Map<String, Object> record, Uri imageUri) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }
        // Upload photo first, then proceed to update record
        uploadPhotoToStorage(imageUri, email)
                .addOnSuccessListener(uri -> {
                    // Once the image upload is complete, get the download URL
                    String imageDownloadUrl = uri.toString();

                    // Add the image URL to the nutritional record map if necessary
                    record.put("image", imageDownloadUrl);

                    // Proceed with updating the nutritional record
                    enqueueOrExecute(() -> {
                        nutritionalRecordHelper.addNutritionalRecord(record, new NutritionalRecordHelper.OnRecordAddedCallback() {
                            @Override
                            public void onRecordAdded(String newRecordId) {
                                if (currentUser.getNutritional_records() != null) {
                                    currentUser.getNutritional_records().add(newRecordId);
                                    currentUser.getNutritional_records().remove(record_id);
                                } else {
                                    List<String> nutritional_records = new ArrayList<>();
                                    nutritional_records.add(newRecordId);
                                    currentUser.setNutritional_records(nutritional_records);
                                }

                                db.collection("user")
                                        .document(email)
                                        .update("nutritional_records", FieldValue.arrayRemove(record_id))
                                        .addOnSuccessListener(aVoid -> {
                                            db.collection("user")
                                                    .document(email)
                                                    .update("nutritional_records", FieldValue.arrayUnion(newRecordId))
                                                    .addOnSuccessListener(aVoid2 ->
                                                            Log.d("UserHelper", "Nutritional record updated successfully."))
                                                    .addOnFailureListener(e ->
                                                            Log.e("UserHelper", "Error updating nutritional record", e));
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("UserHelper", "Error removing old nutritional record", e);
                                        });
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("UserHelper", "Error updating nutritional record: " + e.getMessage());
                            }
                        });
                    });

                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Image upload failed", e);
                });
    }

    // Delete nutritional record
    public void deleteUserNutritionalRecord(String record_id) {
        enqueueOrExecute(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is null or empty");
            }

            if (record_id == null || record_id.isEmpty()) {
                throw new IllegalArgumentException("record_id is null or empty");
            }

            if(currentUser.getNutritional_records() == null)
                return;

            if (currentUser.getNutritional_records().contains(record_id)) {
                currentUser.getNutritional_records().remove(record_id);

                db.collection("user")
                        .document(email)
                        .update("nutritional_records", FieldValue.arrayRemove(record_id))
                        .addOnSuccessListener(aVoid -> {
                            Log.d("UserHelper", "Nutritional record removed from user successfully.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("UserHelper", "Error removing nutritional record from user", e);
                        });
            } else {
                Log.d("UserHelper", "Nutritional record not found in user's list.");
            }
        });
    }

    // Section 3 : User Favourite Recipes

    // Fetch specific favourite recipe based on recipe id
    public void fetchUserFavouriteRecipe(String recipe_id, OnRecipeFetchedCallback callback) {
        enqueueOrExecute(() -> {
            if (recipe_id == null || recipe_id.isEmpty()) {
                callback.onError(new IllegalArgumentException("recipe_id is null or empty"));
                return;
            }

            favouriteRecipeHelper.fetchFavouriteRecipe(recipe_id)
                    .addOnSuccessListener(recipe -> {
                        if (recipe != null) {
                            callback.onRecipeFetched(recipe);
                        } else {
                            callback.onError(new Exception("Favourite recipe not found"));
                        }
                    })
                    .addOnFailureListener(callback::onError);
        });
    }

    // Fetch list of favourite recipes based on recipe id
    public void fetchAllUserFavouriteRecipes(OnRecipeListFetchedCallback callback) {
        enqueueOrExecute(() -> {
            if (!isUserLoaded) {
                Log.e("UserHelper", "User is not loaded yet. Aborting operation.");
                return;
            }

            List<String> recipe_id = currentUser.getFavourite_recipes();

            if (recipe_id == null || recipe_id.isEmpty()) {
                callback.onError(new IllegalArgumentException("recipe_id(s) are null or empty"));
                return;
            }

            favouriteRecipeHelper.fetchSomeFavouriteRecipes(recipe_id)
                    .addOnSuccessListener(recipes -> {
                        if (recipes != null && !recipes.isEmpty()) {
                            callback.onRecipeListFetched(recipes);
                        } else {
                            callback.onError(new Exception("No favourite recipes found"));
                        }
                    })
                    .addOnFailureListener(callback::onError);
        });
    }

    // Search favourite recipes by name
    public void searchUserFavouriteRecipesByName(String name, OnRecipeListFetchedCallback callback) {
        enqueueOrExecute(() -> {
            if (name == null || name.isEmpty()) {
                callback.onError(new IllegalArgumentException("Name is null or empty"));
                return;
            }

            favouriteRecipeHelper.searchFavouriteRecipesByName(name)
                    .addOnSuccessListener(recipes -> {
                        if (recipes != null && !recipes.isEmpty()) {
                            callback.onRecipeListFetched(recipes);
                        } else {
                            callback.onError(new Exception("No favourite recipes found by name"));
                        }
                    })
                    .addOnFailureListener(callback::onError);
        });
    }

    // Add new favourite recipe
    public void addUserFavouriteRecipe(Map<String, Object> recipe, Uri imageUri) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }
        // Upload photo first, then proceed to add the recipe
        uploadPhotoToStorage(imageUri, email)
                .addOnSuccessListener(uri -> {
                    // Once the image upload is complete, get the download URL
                    String imageDownloadUrl = uri.toString();

                    // Add the image URL to the recipe map if necessary
                    recipe.put("image", imageDownloadUrl);

                    // Proceed with adding the favourite recipe
                    enqueueOrExecute(() -> {
                        favouriteRecipeHelper.addFavouriteRecipe(recipe, new FavouriteRecipeHelper.OnRecipeAddedCallback() {
                            @Override
                            public void onRecipeAdded(String recipeId) {
                                if(currentUser.getFavourite_recipes() != null){
                                    currentUser.getFavourite_recipes().add(recipeId);
                                } else {
                                    List<String> favourite_recipes = new ArrayList<>();
                                    favourite_recipes.add(recipeId);
                                    currentUser.setFavourite_recipes(favourite_recipes);
                                }

                                db.collection("user")
                                        .document(email)
                                        .update("favourite_recipes", FieldValue.arrayUnion(recipeId))
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("UserHelper", "New favourite recipe added to user successfully.");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("UserHelper", "Error adding new favourite recipe to user", e);
                                        });
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("UserHelper", "Error adding favourite recipe: " + e.getMessage());
                            }
                        });
                    });

                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Image upload failed", e);
                });
    }

    // Update favourite recipe
    public void updateUserFavouriteRecipe(String recipe_id, Map<String, Object> recipe, Uri imageUri, String userEmail) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is null or empty");
        }
        // Upload photo first, then proceed to update the recipe
        uploadPhotoToStorage(imageUri, userEmail)
                .addOnSuccessListener(uri -> {
                    // Once the image upload is complete, get the download URL
                    String imageDownloadUrl = uri.toString();

                    // Add the image URL to the recipe map if necessary
                    recipe.put("image_url", imageDownloadUrl);

                    // Proceed with updating the favourite recipe
                    enqueueOrExecute(() -> {
                        favouriteRecipeHelper.addFavouriteRecipe(recipe, new FavouriteRecipeHelper.OnRecipeAddedCallback() {
                            @Override
                            public void onRecipeAdded(String newRecipeId) {
                                if (currentUser.getFavourite_recipes() != null) {
                                    currentUser.getFavourite_recipes().add(newRecipeId);
                                    currentUser.getFavourite_recipes().remove(recipe_id);
                                } else {
                                    List<String> favourite_recipes = new ArrayList<>();
                                    favourite_recipes.add(newRecipeId);
                                    currentUser.setFavourite_recipes(favourite_recipes);
                                }

                                db.collection("user")
                                        .document(email)
                                        .update("favourite_recipes", FieldValue.arrayRemove(recipe_id))
                                        .addOnSuccessListener(aVoid -> {
                                            db.collection("user")
                                                    .document(email)
                                                    .update("favourite_recipes", FieldValue.arrayUnion(newRecipeId))
                                                    .addOnSuccessListener(aVoid2 ->
                                                            Log.d("UserHelper", "Favourite recipe updated successfully."))
                                                    .addOnFailureListener(e ->
                                                            Log.e("UserHelper", "Error adding new favourite recipe", e));
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("UserHelper", "Error removing old favourite recipe", e);
                                        });
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("UserHelper", "Error updating favourite recipe: " + e.getMessage());
                            }
                        });
                    });

                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Image upload failed", e);
                });
    }


    // Delete favourite recipe
    public void deleteUserFavouriteRecipe(String recipe_id) {
        enqueueOrExecute(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is null or empty");
            }

            if (recipe_id == null || recipe_id.isEmpty()) {
                throw new IllegalArgumentException("recipe_id is null or empty");
            }

            if(currentUser.getFavourite_recipes() == null)
                return;

            if (currentUser.getFavourite_recipes().contains(recipe_id)) {
                currentUser.getFavourite_recipes().remove(recipe_id);

                db.collection("user")
                        .document(email)
                        .update("favourite_recipes", FieldValue.arrayRemove(recipe_id))
                        .addOnSuccessListener(aVoid -> {
                            Log.d("UserHelper", "Favourite recipe removed from user successfully.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("UserHelper", "Error removing favourite recipe from user", e);
                        });
            } else {
                Log.d("UserHelper", "Favourite recipe not found in user's list.");
            }
        });
    }

    // Callback interfaces
    public interface OnRecordFetchedCallback {
        void onRecordFetched(NutritionalRecord record);

        void onError(Exception e);
    }

    public interface OnRecipeFetchedCallback {
        void onRecipeFetched(FavouriteRecipe recipe);

        void onError(Exception e);
    }

    public interface OnRecordListFetchedCallback {
        void onRecordListFetched(List<NutritionalRecord> records);

        void onError(Exception e);
    }

    public interface OnRecipeListFetchedCallback {
        void onRecipeListFetched(List<FavouriteRecipe> recipes);

        void onError(Exception e);
    }

    public interface OnUserFetchedCallback {
        void onUserFetched(User user);

        void onError(Exception e);
    }

    /**
     * Uploads a photo to Firebase Storage and returns a Task containing the download URL.
     *
     * @param imageUri The URI of the image to be uploaded
     * @param userEmail The email of the user uploading the image, used to create a unique file name
     * @return Task<Uri> A task that resolves to the download URL of the uploaded image
     * @throws IllegalArgumentException if imageUri is null
     */
    public Task<Uri> uploadPhotoToStorage(Uri imageUri, String userEmail) {
        if (imageUri == null) {
            Log.e("Firebase", "Image URI is null");
            return Tasks.forException(new IllegalArgumentException("Image URI cannot be null"));
        }

        // 1. Get a reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a unique path for the image
        String fileName = "images/" + userEmail + "_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        // 2. Upload the image to Storage
        return imageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // 3. Get the download URL of the uploaded image
                    return imageRef.getDownloadUrl();
                });
    }

}
