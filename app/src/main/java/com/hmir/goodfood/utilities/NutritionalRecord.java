package com.hmir.goodfood.utilities;

import android.media.Image;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.type.DateTime;

import java.util.List;

public class NutritionalRecord {
    private String record_id;
    private List<String> ingredient;
    private Timestamp dateTime;
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
    private DocumentReference image;
    private String user_id;

    public NutritionalRecord(String record_id, double calcium, double calories, double carbs,
                             double cholesterol, Timestamp dateTime, double fat, DocumentReference image,
                             List<String> ingredient, double iron, double potassium, double protein,
                             double sodium, double magnesium, String user_id) {
        this.record_id = record_id;
        this.calcium = calcium;
        this.calories = calories;
        this.carbs = carbs;
        this.magnesium = magnesium;
        this.cholesterol = cholesterol;
        this.dateTime = dateTime;
        this.fat = fat;
        this.image = image;
        this.ingredient = ingredient;
        this.iron = iron;
        this.potassium = potassium;
        this.protein = protein;
        this.sodium = sodium;
        this.user_id = user_id;
    }

    public NutritionalRecord (){
    }

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

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
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

    public List<String>getIngredient() {
        return ingredient;
    }

    public void setIngredient(List<String> ingredient) {
        this.ingredient = ingredient;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
