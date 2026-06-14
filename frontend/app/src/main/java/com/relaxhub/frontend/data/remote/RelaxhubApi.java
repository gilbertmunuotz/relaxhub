package com.relaxhub.frontend.data.remote;

import com.relaxhub.frontend.data.model.HealthResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RelaxhubApi {

    @GET("api/health")
    Call<HealthResponse> getHealth();
}
