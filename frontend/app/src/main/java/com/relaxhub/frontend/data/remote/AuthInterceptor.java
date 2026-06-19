package com.relaxhub.frontend.data.remote;

import android.content.Context;

import com.relaxhub.frontend.data.local.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = SessionManager.getInstance(context).getToken();
        if (token != null && !token.isEmpty()) {
            return chain.proceed(
                    chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build()
            );
        }
        return chain.proceed(chain.request());
    }
}
