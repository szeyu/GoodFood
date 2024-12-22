package com.hmir.goodfood;

public class NutritionalRecord {
    private int id;
    private String name; // Item name
    private String calories;
    private String protein;
    private String carbs;
    private String fat;
    private String sodium;
    private String calcium;
    private String iron;
    private String cholesterol;
    private String potassium;
    private String magnesium;

    // Constructor
    public NutritionalRecord(int id, String name, String calories, String protein, String carbs, String fat,
                             String sodium, String calcium, String iron, String cholesterol, String potassium, String magnesium) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.sodium = sodium;
        this.calcium = calcium;
        this.iron = iron;
        this.cholesterol = cholesterol;
        this.potassium = potassium;
        this.magnesium = magnesium;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCalories() {
        return calories;
    }

    public String getProtein() {
        return protein;
    }

    public String getCarbs() {
        return carbs;
    }

    public String getFat() {
        return fat;
    }

    public String getSodium() {
        return sodium;
    }

    public String getCalcium() {
        return calcium;
    }

    public String getIron() {
        return iron;
    }

    public String getCholesterol() {
        return cholesterol;
    }

    public String getPotassium() {
        return potassium;
    }

    public String getMagnesium() {
        return magnesium;
    }
}