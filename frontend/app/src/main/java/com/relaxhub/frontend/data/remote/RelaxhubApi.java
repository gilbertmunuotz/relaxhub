package com.relaxhub.frontend.data.remote;

import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.CreateComplaintRequest;
import com.relaxhub.frontend.data.model.CreateFeedbackRequest;
import com.relaxhub.frontend.data.model.CreateReceiptRequest;
import com.relaxhub.frontend.data.model.AuthResponse;
import com.relaxhub.frontend.data.model.DbHealthResponse;
import com.relaxhub.frontend.data.model.HealthResponse;
import com.relaxhub.frontend.data.model.LoginRequest;
import com.relaxhub.frontend.data.model.RegisterRequest;
import com.relaxhub.frontend.data.model.ResetPasswordRequest;
import com.relaxhub.frontend.data.model.ReceiptResponse;
import com.relaxhub.frontend.data.model.SpotResponse;
import com.relaxhub.frontend.data.model.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RelaxhubApi {

    @GET("api/health")
    Call<HealthResponse> getHealth();

    @GET("api/health/db")
    Call<DbHealthResponse> getDatabaseHealth();

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("api/auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest request);

    @GET("api/auth/me")
    Call<ApiResponse<UserResponse>> getProfile();

    @POST("api/feedback")
    Call<ApiResponse<Object>> submitFeedback(@Body CreateFeedbackRequest request);

    @POST("api/complaints")
    Call<ApiResponse<Object>> submitComplaint(@Body CreateComplaintRequest request);

    @POST("api/receipts")
    Call<ApiResponse<ReceiptResponse>> createReceipt(@Body CreateReceiptRequest request);

    @GET("api/receipts")
    Call<ApiResponse<List<ReceiptResponse>>> getReceipts();

    @GET("api/spots")
    Call<ApiResponse<List<SpotResponse>>> getSpots(@Query("type") String type);

    @GET("api/spots/nearby")
    Call<ApiResponse<List<SpotResponse>>> getNearbySpots(
            @Query("lat") double lat,
            @Query("lng") double lng,
            @Query("radiusKm") double radiusKm,
            @Query("type") String type
    );

    @GET("api/spots/{id}")
    Call<ApiResponse<SpotResponse>> getSpot(@Path("id") long id);
}
