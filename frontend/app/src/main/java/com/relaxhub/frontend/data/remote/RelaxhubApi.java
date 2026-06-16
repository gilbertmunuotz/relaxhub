package com.relaxhub.frontend.data.remote;

import com.relaxhub.frontend.data.model.ApiResponse;
import com.relaxhub.frontend.data.model.AuthResponse;
import com.relaxhub.frontend.data.model.DbHealthResponse;
import com.relaxhub.frontend.data.model.HealthResponse;
import com.relaxhub.frontend.data.model.LoginRequest;
import com.relaxhub.frontend.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RelaxhubApi {

    @GET("api/health")
    Call<HealthResponse> getHealth();

    @GET("api/health/db")
    Call<DbHealthResponse> getDatabaseHealth();

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);
}
