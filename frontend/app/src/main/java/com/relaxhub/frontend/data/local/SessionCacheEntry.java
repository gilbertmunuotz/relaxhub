package com.relaxhub.frontend.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "session_cache")
public class SessionCacheEntry {

    @PrimaryKey
    private int id;

    private long userId;
    private String fullName;
    private String email;
    private String token;
    private long loggedInAt;

    public SessionCacheEntry(
            int id,
            long userId,
            String fullName,
            String email,
            String token,
            long loggedInAt
    ) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.token = token;
        this.loggedInAt = loggedInAt;
    }

    public int getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public long getLoggedInAt() {
        return loggedInAt;
    }
}
