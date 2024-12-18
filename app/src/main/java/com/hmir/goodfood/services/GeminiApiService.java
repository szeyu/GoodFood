package com.hmir.goodfood.services;

import com.hmir.goodfood.models.GeminiApiResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    Call<GeminiApiResponse> callGemini(
            @Query("key") String apiKey,
            @Body RequestBody requestBody
    );
}