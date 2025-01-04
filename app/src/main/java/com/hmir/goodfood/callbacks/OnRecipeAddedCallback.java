package com.hmir.goodfood.callbacks;

/**
 * Callback interface for recipe addition operations.
 */
public interface OnRecipeAddedCallback {

    /**
     * Called when a recipe is successfully added.
     * @param recipeId ID of the newly added recipe
     */
    void onRecipeAdded(String recipeId);

    /**
     * Called when an error occurs during recipe addition.
     * @param e The exception that occurred
     */
    void onError(Exception e);
}