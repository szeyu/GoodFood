package com.hmir.goodfood.callbacks;

/**
 * Callback interface for handling recipe deletion operations.
 * This interface provides methods to handle both successful deletion
 * and error scenarios when deleting a favorite recipe.
 */
public interface OnRecipeDeletedCallback {

    /**
     * Called when a recipe is successfully deleted from the database.
     * This method is invoked after the deletion operation completes without errors.
     */
    void onRecipeDeleted();

    /**
     * Called when a recipe is successfully deleted from the database.
     * This method is invoked after the deletion operation completes without errors.
     */
    void onError(Exception e);
}
