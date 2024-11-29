package com.hmir.goodfood.utilities;

import android.media.Image;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class FavouriteRecipe {
    private String recipe_id;
    private String name;
    private List<String> diet_label;
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
    private DocumentReference image;
    private String user_id;

    public FavouriteRecipe (){
    }

    public FavouriteRecipe(double calcium, double calories, double carbs, List<String> diet_label,
                           double fat, DocumentReference image, double iron, double magnesium, String name,
                           double potassium, double protein, int servings, double sodium,
                           double cholesterol, String user_id, String recipe_id) {
        this.recipe_id = recipe_id;
        this.calcium = calcium;
        this.calories = calories;
        this.carbs = carbs;
        this.cholesterol = cholesterol;
        this.diet_label = diet_label;
        this.fat = fat;
        this.image = image;
        this.iron = iron;
        this.magnesium = magnesium;
        this.name = name;
        this.potassium = potassium;
        this.protein = protein;
        this.servings = servings;
        this.sodium = sodium;
        this.user_id = user_id;
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

    public List<String> getDiet_label() {
        return diet_label;
    }

    public void setDiet_label(List<String> diet_label) {
        this.diet_label = diet_label;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public DocumentReference getImage() {
        return image;
    }

    public void setImage(DocumentReference image) {
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
