package com.hmir.goodfood.callbacks;

/**
 * Interface for handling nutrition data retrieval callbacks.
 * Provides methods to handle successful and failed nutrition data operations.
 */
public interface NutritionCallback {
    /**
     * Called when nutrition data is successfully retrieved.
     *
     * @param nutritionData The retrieved nutrition data as a String
     */
    void onSuccess(String nutritionData);

    /**
     * Called when an error occurs during nutrition data retrieval.
     *
     * @param errorMessage The error message describing what went wrong
     */
    void onError(String errorMessage);
}
