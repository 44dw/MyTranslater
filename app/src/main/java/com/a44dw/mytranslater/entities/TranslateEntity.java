package com.a44dw.mytranslater.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity
public class TranslateEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String originalText;
    private String translatedText;
    private String originalLanguage;
    private String translatedLanguage;

    public TranslateEntity(String originalText, String translatedText, String originalLanguage, String translatedLanguage) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.originalLanguage = originalLanguage;
        this.translatedLanguage = translatedLanguage;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTranslatedLanguage() {
        return translatedLanguage;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranslateEntity entity = (TranslateEntity) o;
        return Objects.equals(originalText, entity.originalText) &&
                Objects.equals(translatedText, entity.translatedText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalText, translatedText);
    }
}
