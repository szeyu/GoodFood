package com.hmir.goodfood.services;

import com.hmir.goodfood.models.AnalyzeRequest;
import com.hmir.goodfood.models.ExtractRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Service interface for interacting with the server to perform ingredient extraction
 * and nutritional analysis.
 *
 * This interface defines endpoints that allow the application to:
 * - Extract ingredients from input data, such as an image or text.
 * - Analyze the nutritional content of the extracted ingredients.
 *
 * Retrofit is used to facilitate HTTP communication with the defined endpoints.
 */
public interface IngredientService {

    /**
     * Sends a request to extract ingredients from the provided input data.
     *
     * @param request The {@link ExtractRequest} object containing the input data
     *                for ingredient extraction (e.g., image or text).
     * @return A {@link Call} object that, when executed, will send the request
     *         and return the server's response as a {@link String}.
     */
    @POST("/extract_ingredients")
    Call<String> extractIngredients(@Body ExtractRequest request);

    /**
     * Sends a request to analyze the nutritional content of the provided input data.
     *
     * @param request The {@link AnalyzeRequest} object containing the input data
     *                for nutritional analysis.
     * @return A {@link Call} object that, when executed, will send the request
     *         and return the server's response as a {@link String}.
     */
    @POST("/gemini_analyze_nutrition")
    Call<String> analyzeNutrition(@Body AnalyzeRequest request);
}


