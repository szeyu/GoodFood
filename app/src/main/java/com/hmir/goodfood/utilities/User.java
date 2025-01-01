package com.hmir.goodfood.utilities;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user entity in the GoodFood application.
 * This class maintains user-specific information including personal details,
 * health preferences, and related data collections.
 *
 * The class is designed to work with Firebase Authentication and Firestore,
 * providing seamless integration with the backend services.
 */
public class User {
    // Firebase-related constants
    @Exclude
    private final static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // User identification
    private final static String photoUrl = currentUser != null && currentUser.getPhotoUrl() != null
            ? currentUser.getPhotoUrl().toString()
            : null;

    // User personal information
    private final String email;
    private String username;
    private long age;
    private double height;  // in centimeters
    private double weight;  // in kilograms

    // User preferences and collections
    private List<String> health_labels;
    private List<String> favourite_recipes;
    private List<String> nutritional_records;

    /**
     * Default constructor required for Firestore serialization.
     */
    public User() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.email = currentUser != null ? currentUser.getEmail() : null;

        // Initialize lists to prevent null pointer exceptions
        this.health_labels = new ArrayList<>();
        this.favourite_recipes = new ArrayList<>();
        this.nutritional_records = new ArrayList<>();
    }

    /**
     * Parameterized constructor for creating a new user with basic information.
     *
     * @param username The user's display name
     * @param age The user's age
     * @param height The user's height in centimeters
     * @param weight The user's weight in kilograms
     */
    public User(String username, long age, double height, double weight) {
        this();
        this.username = username;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    // Getters and Setters with validation

    /**
     * Gets the user's age.
     * @return The age of the user
     */
    public long getAge() {
        return age;
    }

    /**
     * Sets the user's age with validation.
     * @param age The age to set (must be positive)
     * @throws IllegalArgumentException if age is negative
     */
    public void setAge(long age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        this.age = age;
    }

    /**
     * Gets the user's email address.
     * @return The email address or null if not authenticated
     */
    @Nullable
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's height in centimeters.
     * @return The height in centimeters
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the user's height with validation.
     * @param height The height in centimeters (must be positive)
     * @throws IllegalArgumentException if height is negative or unrealistic
     */
    public void setHeight(double height) {
        if (height <= 0 || height > 300) {
            throw new IllegalArgumentException("Height must be between 0 and 300 cm");
        }
        this.height = height;
    }

    /**
     * Gets the user's username.
     * @return The username
     */
    @Nullable
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username with validation.
     * @param username The username to set (cannot be empty)
     * @throws IllegalArgumentException if username is empty or null
     */
    public void setUsername(@NonNull String username) {
        if (username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username.trim();
    }

    /**
     * Gets the user's weight in kilograms.
     * @return The weight in kilograms
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the user's weight with validation.
     * @param weight The weight in kilograms (must be positive)
     * @throws IllegalArgumentException if weight is negative or unrealistic
     */
    public void setWeight(double weight) {
        if (weight <= 0 || weight > 500) {
            throw new IllegalArgumentException("Weight must be between 0 and 500 kg");
        }
        this.weight = weight;
    }

    /**
     * Gets the user's favorite recipes.
     * @return An unmodifiable list of favorite recipe IDs
     */
    @NonNull
    public List<String> getFavourite_recipes() {
        return Collections.unmodifiableList(favourite_recipes);
    }

    /**
     * Sets the user's favorite recipes.
     * @param recipes The list of favorite recipe IDs
     */
    public void setFavourite_recipes(@Nullable List<String> recipes) {
        favourite_recipes.clear();
        if (recipes != null) {
            favourite_recipes.addAll(recipes);
        }
    }

    /**
     * Gets the user's health labels.
     * @return An unmodifiable list of health labels
     */
    @NonNull
    public List<String> getHealth_labels() {
        return Collections.unmodifiableList(health_labels);
    }

    /**
     * Sets the user's health labels.
     * @param labels The list of health labels
     */
    public void setHealth_labels(@Nullable List<String> labels) {
        health_labels.clear();
        if (labels != null) {
            health_labels.addAll(labels);
        }
    }

    /**
     * Gets the user's nutritional records.
     * @return An unmodifiable list of nutritional record IDs
     */
    @NonNull
    public List<String> getNutritional_records() {
        return Collections.unmodifiableList(nutritional_records);
    }

    /**
     * Sets the user's nutritional records.
     * @param records The list of nutritional record IDs
     */
    public void setNutritional_records(@Nullable List<String> records) {
        nutritional_records.clear();
        if (records != null) {
            nutritional_records.addAll(records);
        }
    }

    /**
     * Gets the user's profile photo URL.
     * @return The URL of the user's profile photo or null if not set
     */
    @Nullable
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Calculates the user's BMI (Body Mass Index).
     * @return The calculated BMI value
     */
    @Exclude
    public double calculateBMI() {
        if (height <= 0) return 0;
        double heightInMeters = height / 100;
        return weight / (heightInMeters * heightInMeters);
    }

    /**
     * Returns a string representation of the User object.
     * @return A formatted string containing all user details
     */
    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("User(%s, %s, %d, %.2f, %.2f, %s, %s, %s, %s)",
                username, email, age, height, weight,
                health_labels, favourite_recipes, nutritional_records, photoUrl);
    }

    /**
     * Checks if two User objects are equal.
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getEmail() != null && email.equals(user.getEmail());
    }

    /**
     * Generates a hash code for the User object.
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }
}