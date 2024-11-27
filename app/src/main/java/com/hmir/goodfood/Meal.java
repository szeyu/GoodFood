package com.hmir.goodfood;

public class Meal {
    // Temporary meal class for data
    public String name;
    public int calories;

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public Meal(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }
}
