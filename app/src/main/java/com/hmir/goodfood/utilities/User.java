package com.hmir.goodfood.utilities;

import android.annotation.SuppressLint;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class User {
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

      Section 4 : Getters and Setters
                    - All fields are available for Getters
                    - All fields except "email" are available for Setters
     */

    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    //    private final static String email = "test1@gmail.com";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String username;
    private long age;
    private double height;
    private double weight;
    private List<String> health_labels;
    private List<String> favourite_recipes;
    private List<String> nutritional_records;

    public User() {}

    public User(UserCallback callback) {
        fetchUserInfo().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User fetchedUser = task.getResult();
                if (fetchedUser != null) {
                    // Initialize fields with fetched user data
                    this.username = fetchedUser.getUsername();
                    this.age = fetchedUser.getAge();
                    this.height = fetchedUser.getHeight();
                    this.weight = fetchedUser.getWeight();
                    this.health_labels = fetchedUser.getHealth_labels();
                    this.favourite_recipes = fetchedUser.getFavourite_recipes();
                    this.nutritional_records = fetchedUser.getNutritional_records();

                    callback.onSuccess(this);
                } else {
                    callback.onFailure(new Exception("User data is null"));
                }
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    // Fetch user and return a Task of it
    public Task<User> fetchUserInfo() {
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

    // Getter and Setter
    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<String> getFavourite_recipes() {
        return favourite_recipes;
    }

    public void setFavourite_recipes(List<String> favourite_recipes) {
        this.favourite_recipes = favourite_recipes;
    }

    public List<String> getHealth_labels() {
        return health_labels;
    }

    public void setHealth_labels(List<String> health_labels) {
        this.health_labels = health_labels;
    }

    public List<String> getNutritional_records() {
        return nutritional_records;
    }

    public void setNutritional_records(List<String> nutritional_records) {
        this.nutritional_records = nutritional_records;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("User(%s, %s, %d, %f, %f, %s, %s, %s)",
                username, email, age, height, weight,
                health_labels, favourite_recipes, nutritional_records);
    }

    public interface UserCallback {
        void onSuccess(User result);

        void onFailure(Exception e);
    }
}