package com.a44dw.mytranslater.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.a44dw.mytranslater.dao.TranslateEntityDao;
import com.a44dw.mytranslater.entities.TranslateEntity;

@Database(entities = {TranslateEntity.class}, version = 1, exportSchema = false)
public abstract class TranslateDatabase extends RoomDatabase {
    public abstract TranslateEntityDao translateEntityDao();

    private static TranslateDatabase instance;

    public static TranslateDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TranslateDatabase.class, "TranslateDatabase")
                    .build();
        }
        return instance;
    }
}
