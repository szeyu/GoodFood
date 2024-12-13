package com.hmir.goodfood.models;

public class AnalyzeRequest {
    private String ingredients;

    // Constructor
    public AnalyzeRequest(String ingredients) {
        this.ingredients = ingredients;
    }

    // Getter
    public String getIngredients() {
        return ingredients;
    }

    // Setter
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
