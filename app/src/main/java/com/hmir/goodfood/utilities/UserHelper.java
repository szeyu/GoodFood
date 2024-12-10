package com.hmir.goodfood.utilities;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class UserHelper {
    /*
      In this class, there will be multiple sections of methods relating to a user:
      Section 1 : User Specifics
                    - fetchUser()
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
//    private final static String email = "test1@gmail.com";
    private boolean isUserLoaded = false;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final NutritionalRecordHelper nutritionalRecordHelper = new NutritionalRecordHelper();
    private final FavouriteRecipeHelper favouriteRecipeHelper = new FavouriteRecipeHelper();
    private final Queue<Runnable> pendingOperations = new LinkedList<>();
    protected User currentUser;

    public UserHelper() {
        initializeCurrentUser();
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
        enqueueOrExecute(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is null or empty");
            }

            Map<String, Object> defaultUser = new HashMap<>();
            defaultUser.put("username", null);
            defaultUser.put("age", 0);
            defaultUser.put("height", 0);
            defaultUser.put("weight", 0);
            defaultUser.put("health_label", null);
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
        });
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
    public void addUserNutritionalRecord(Map<String, Object> record) {
        enqueueOrExecute(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is null or empty");
            }

            nutritionalRecordHelper.addNutritionalRecord(record, new NutritionalRecordHelper.OnRecordAddedCallback() {
                @Override
                public void onRecordAdded(String recordId) {
                    currentUser.getNutritional_records().add(recordId);

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
    }

    // Update nutritional record
    public void updateUserNutritionalRecord(String record_id, Map<String, Object> record) {
        enqueueOrExecute(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is null or empty");
            }

            nutritionalRecordHelper.addNutritionalRecord(record, new NutritionalRecordHelper.OnRecordAddedCallback() {
                @Override
                public void onRecordAdded(String newRecordId) {
                    if (currentUser.getNutritional_records() != null) {
                        currentUser.getNutritional_records().add(newRecordId);
                        currentUser.getNutritional_records().remove(record_id);
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
                            .addOnFailureListener(e ->
                                    Log.e("UserHelper", "Error removing old nutritional record", e));
                }

                @Override
                public void onError(Exception e) {
                    Log.e("UserHelper", "Error updating nutritional record: " + e.getMessage());
                }
            });
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
    public void addUserFavouriteRecipe(Map<String, Object> recipe) {
        enqueueOrExecute(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is null or empty");
            }

            favouriteRecipeHelper.addFavouriteRecipe(recipe, new FavouriteRecipeHelper.OnRecipeAddedCallback() {
                @Override
                public void onRecipeAdded(String recipeId) {
                    currentUser.getFavourite_recipes().add(recipeId);

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
    }

    // Update favourite recipe
    public void updateUserFavouriteRecipe(String recipe_id, Map<String, Object> recipe) {
        enqueueOrExecute(() -> {
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is null or empty");
            }

            favouriteRecipeHelper.addFavouriteRecipe(recipe, new FavouriteRecipeHelper.OnRecipeAddedCallback() {
                @Override
                public void onRecipeAdded(String newRecipeId) {
                    if (currentUser.getNutritional_records() != null) {
                        currentUser.getNutritional_records().add(newRecipeId);
                        currentUser.getNutritional_records().remove(recipe_id);
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
                            .addOnFailureListener(e ->
                                    Log.e("UserHelper", "Error removing old favourite recipe", e));
                }

                @Override
                public void onError(Exception e) {
                    Log.e("UserHelper", "Error updating favourite recipe: " + e.getMessage());
                }
            });
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
}
