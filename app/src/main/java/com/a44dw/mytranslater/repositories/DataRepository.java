package com.a44dw.mytranslater.repositories;
import com.a44dw.mytranslater.database.TranslateDatabase;
import com.a44dw.mytranslater.entities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataRepository {

    private TranslateDatabase database;

    public DataRepository(TranslateDatabase database) {
        this.database = database;
    }

    public void insertTranslateEntity(TranslateEntity entity) {
        database.translateEntityDao().insert(entity);
    }

    public List<TranslateEntity> getTranslateEntities() {
        return database.translateEntityDao().getAll();
    }

    public void deleteTranslateEntity(TranslateEntity entityToDelete) {
        database.translateEntityDao().delete(entityToDelete);
    }
}
