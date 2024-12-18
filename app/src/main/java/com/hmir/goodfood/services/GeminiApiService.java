package com.hmir.goodfood.services;

import com.hmir.goodfood.models.GeminiApiResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface for the Gemini API service.
 * Provides methods to interact with the Gemini API.
 */
public interface GeminiApiService {
    /**
     * Calls the Gemini API to generate content.
     *
     * @param apiKey The API key for authentication.
     * @param requestBody The request body containing the data to be sent.
     * @return A Call object to execute the request.
     */
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    Call<GeminiApiResponse> callGemini(
            @Query("key") String apiKey,
            @Body RequestBody requestBody
    );
}
