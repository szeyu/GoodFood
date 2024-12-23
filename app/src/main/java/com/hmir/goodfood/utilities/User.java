package com.hmir.goodfood.utilities;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class User {
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
        return String.format("User(%s, %s, %d, %.2f, %.2f, %s, %s, %s)",
                username, email, age, height, weight,
                health_labels, favourite_recipes, nutritional_records);
    }
}