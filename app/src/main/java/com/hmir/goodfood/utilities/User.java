package com.hmir.goodfood.utilities;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

    //    private final static String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private final static String email = "test1@gmail.com";
    private String username;
    private long age;
    private double height;
    private double weight;
    private List<String> health_labels;
    private List<String> favourite_recipes;
    private List<String> nutritional_records;

    public User() {}
    public User(String username, long age, double height, double weight, List<String> health_labels,
                List<String> favourite_recipes, List<String> nutritional_records) {
        this.username = username;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.health_labels = health_labels;
        this.favourite_recipes = favourite_recipes;
        this.nutritional_records = nutritional_records;
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
}