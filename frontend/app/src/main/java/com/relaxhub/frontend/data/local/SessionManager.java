package com.relaxhub.frontend.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.relaxhub.frontend.data.model.AuthResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionManager {

    private static final String PREFS_NAME = "relaxhub_session";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final int SESSION_ROW_ID = 1;

    private static SessionManager instance;

    private final SharedPreferences preferences;
    private final SessionDao sessionDao;
    private final ExecutorService executor;

    private SessionManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sessionDao = AppDatabase.getInstance(context).sessionDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_LOGGED_IN, false)
                && preferences.getString(KEY_TOKEN, "") != null
                && !preferences.getString(KEY_TOKEN, "").isEmpty();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, "");
    }

    public String getFullName() {
        return preferences.getString(KEY_FULL_NAME, "");
    }

    public String getEmail() {
        return preferences.getString(KEY_EMAIL, "");
    }

    public void saveSession(AuthResponse authResponse) {
        preferences.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_TOKEN, authResponse.getToken())
                .putLong(KEY_USER_ID, authResponse.getUser().getId())
                .putString(KEY_FULL_NAME, authResponse.getUser().getFullName())
                .putString(KEY_EMAIL, authResponse.getUser().getEmail())
                .apply();

        executor.execute(() -> sessionDao.saveSession(new SessionCacheEntry(
                SESSION_ROW_ID,
                authResponse.getUser().getId(),
                authResponse.getUser().getFullName(),
                authResponse.getUser().getEmail(),
                authResponse.getToken(),
                System.currentTimeMillis()
        )));
    }

    public void clearSession() {
        preferences.edit().clear().apply();
        executor.execute(sessionDao::clearSession);
    }
}
