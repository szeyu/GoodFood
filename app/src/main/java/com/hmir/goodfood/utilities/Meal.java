package com.hmir.goodfood.utilities;

public class Meal {
    // this class will be terminated at the final stage
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
