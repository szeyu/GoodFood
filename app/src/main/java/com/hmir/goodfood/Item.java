package com.hmir.goodfood;

import com.hmir.goodfood.R;

public class Item {
    private int id;
    private String name;
    private String imageResourceName; // Assuming this is the name of the drawable resource
    private String fatContent;
    private String calories;
    private String proteinContent;
    private String description;
    private String ingredients;

    // Constructor
    public Item(int id, String name, String imageResourceName, String fatContent, String calories, String proteinContent, String description, String ingredients) {
        this.id = id;
        this.name = name;
        this.imageResourceName = imageResourceName; // Store the image resource name as a string
        this.fatContent = fatContent;
        this.calories = calories;
        this.proteinContent = proteinContent;
        this.description = description;
        this.ingredients = ingredients;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageResourceName() {
        return imageResourceName;
    }

    public String getFatContent() {
        return fatContent;
    }

    public String getCalories() {
        return calories;
    }

    public String getProteinContent() {
        return proteinContent;
    }

    public String getDescription() {
        return description;
    }

    public String getIngredients() {
        return ingredients;
    }

    // New method to get the image resource ID
    public int getImageResId(android.content.Context context) {
        int resId = context.getResources().getIdentifier(imageResourceName, "drawable", context.getPackageName());
        if (resId == 0) {
            resId = R.drawable.eating_healthy_food_cuate_1;  // Default image if resource is not found
        }
        return resId;
    }

}
