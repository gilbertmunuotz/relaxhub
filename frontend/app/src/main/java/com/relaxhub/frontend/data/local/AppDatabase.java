package com.relaxhub.frontend.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SessionCacheEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
}
