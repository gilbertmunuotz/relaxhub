package com.relaxhub.frontend.data.remote;

import android.content.Context;

import com.relaxhub.frontend.util.ApiConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static RelaxhubApi api;

    private ApiClient() {
    }

    public static void init(Context context) {
        if (api != null) {
            return;
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(RelaxhubApi.class);
    }

    public static RelaxhubApi getApi() {
        if (api == null) {
            throw new IllegalStateException("ApiClient.init(context) must be called first");
        }
        return api;
    }
}
