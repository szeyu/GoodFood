package com.hmir.goodfood.models;

/**
 * Represents a request object for nutritional analysis.
 *
 * This class is used to encapsulate the list of ingredients provided by the user,
 * which will be sent to the server for nutritional analysis. Each instance of this
 * class contains the ingredients data as a single string.
 */
public class AnalyzeRequest {

    /**
     * A string containing the ingredients for nutritional analysis.
     * The ingredients should be provided in a format recognized by the server.
     */
    private String ingredients;

    /**
     * Constructs a new {@code AnalyzeRequest} with the specified ingredients.
     *
     * @param ingredients A {@code String} containing the ingredients to analyze.
     *                    The format of the string should align with the server's requirements.
     */
    public AnalyzeRequest(String ingredients) {
        this.ingredients = ingredients;
    }

    /**
     * Returns the ingredients for nutritional analysis.
     *
     * @return A {@code String} containing the ingredients.
     */
    public String getIngredients() {
        return ingredients;
    }

    /**
     * Sets the ingredients for nutritional analysis.
     *
     * @param ingredients A {@code String} containing the ingredients to analyze.
     *                    This will overwrite any existing value.
     */
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
