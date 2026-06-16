package com.relaxhub.frontend.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SessionDao {

    @Query("SELECT * FROM session_cache WHERE id = 1 LIMIT 1")
    SessionCacheEntry getSession();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSession(SessionCacheEntry entry);

    @Query("DELETE FROM session_cache")
    void clearSession();
}
