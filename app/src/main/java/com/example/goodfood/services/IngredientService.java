package com.example.goodfood.services;

import com.example.goodfood.models.ExtractRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IngredientService {
    @POST("/extract_ingredients")
    Call<String> extractIngredients(@Body ExtractRequest request);
}

