package com.hmir.goodfood;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

public class Item {
    private String name;
    private List<String> ingredients;
    private List<String> steps;
    private String recipe_id;  // This will store the recipe document ID from Firestore

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public String getRecipeId() {
        return recipe_id;
    }

    public void setRecipeId(String recipe_id) {
        this.recipe_id = recipe_id;
    }
}
