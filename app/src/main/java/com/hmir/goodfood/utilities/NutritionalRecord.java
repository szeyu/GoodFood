package com.hmir.goodfood.utilities;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.google.firebase.Timestamp;

public class NutritionalRecord {
    private String record_id;
    private String ingredients;
    private Timestamp date_time;
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double sodium;
    private double calcium;
    private double iron;
    private double cholesterol;
    private double potassium;
    private double magnesium;
    private String image;

    public NutritionalRecord(String record_id, double calcium, double calories, double carbs,
                             double cholesterol, Timestamp date_time, double fat, String image,
                             String ingredients, double iron, double potassium, double protein,
                             double sodium, double magnesium) {
        this.record_id = record_id;
        this.calcium = calcium;
        this.calories = calories;
        this.carbs = carbs;
        this.magnesium = magnesium;
        this.cholesterol = cholesterol;
        this.date_time = date_time;
        this.fat = fat;
        this.image = image;
        this.ingredients = ingredients;
        this.iron = iron;
        this.potassium = potassium;
        this.protein = protein;
        this.sodium = sodium;
    }

    // Getter and Setter
    public NutritionalRecord (){
        // empty constructor
    }

    // Getter and Setter
    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(double magnesium) {
        this.magnesium = magnesium;
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

    public double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }

    public Timestamp getDate_time() {
        return date_time;
    }

    public void setDate_time(Timestamp date_time) {
        this.date_time = date_time;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
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

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format(
                "NutritionalRecord { " +
                        "record_id='%s', ingredients='%s', date_time='%s', calories=%.2f, " +
                        "protein=%.2f, carbs=%.2f, fat=%.2f, sodium=%.2f, calcium=%.2f, " +
                        "iron=%.2f, cholesterol=%.2f, potassium=%.2f, magnesium=%.2f, image='%s' }",
                record_id,
                ingredients,
                date_time != null ? date_time.toDate().toString() : "null",
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
                image != null ? image : "null"
        );
    }
}
