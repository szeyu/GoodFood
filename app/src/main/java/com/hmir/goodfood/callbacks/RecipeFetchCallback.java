package com.hmir.goodfood.callbacks;

import com.hmir.goodfood.Item;

import java.util.List;

/**
 * Callback interface for handling recipe fetch operations.
 * Provides methods to handle both successful fetches and error scenarios.
 */
public interface RecipeFetchCallback {
    /**
     * Called when recipes are successfully fetched from the database.
     *
     * @param items List of fetched recipe items
     */
    void onRecipesFetched(List<Item> items);

    /**
     * Called when an error occurs during the fetch operation.
     *
     * @param e The exception that occurred during fetching
     */
    void onError(Exception e);
}