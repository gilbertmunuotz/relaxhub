package com.relaxhub.frontend.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "session_cache")
public class SessionCacheEntry {

    @PrimaryKey
    private int id;

    private String value;

    public SessionCacheEntry(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
