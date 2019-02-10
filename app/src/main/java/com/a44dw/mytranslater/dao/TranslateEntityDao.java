package com.a44dw.mytranslater.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.a44dw.mytranslater.entities.TranslateEntity;

import java.util.List;
import java.util.Set;

@Dao
public interface TranslateEntityDao {
    @Query("SELECT * FROM TranslateEntity")
    List<TranslateEntity> getAll();

    @Insert
    long insert(TranslateEntity translateEntity);

    @Delete
    void delete(TranslateEntity translateEntity);

    @Update
    void update(TranslateEntity translateEntity);
}
