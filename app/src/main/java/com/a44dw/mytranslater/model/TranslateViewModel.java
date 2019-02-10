package com.a44dw.mytranslater.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.a44dw.mytranslater.database.TranslateDatabase;
import com.a44dw.mytranslater.entities.TranslateEntity;
import com.a44dw.mytranslater.repositories.DataRepository;
import com.a44dw.mytranslater.repositories.TranslaterRepository;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TranslateViewModel extends AndroidViewModel implements TranslaterRepository.TranslaterResponseListener {

    private MutableLiveData<String> mTranslateFrom;
    private MutableLiveData<String> mTranslateTo;
    private MutableLiveData<String> mTranslateResult;
    private MutableLiveData<Boolean> mHotTranslateChecked;
    private MutableLiveData<List<TranslateEntity>> mTranslateEntityCollection;
    private MutableLiveData<List<TranslateEntity>> mMatchesTranslateEntities;
    private String mTextToTranslate;
    private ExecutorService mExecutor;
    private boolean mAutoFrom = false;

    DataRepository mDataRepository;
    TranslaterRepository mTranslaterRepository;

    public TranslateViewModel(Application application) {
        super(application);
        this.mDataRepository = new DataRepository(TranslateDatabase.getDatabase(application));
        this.mTranslaterRepository = new TranslaterRepository(application);
        this.mTranslaterRepository.setResponseListener(this);
        this.mExecutor = Executors.newSingleThreadExecutor();
        loadTranslateEntityCollection();
    }

    public void setHotTranslateChecked(boolean hotTranslateChecked) {
        if (mHotTranslateChecked == null) mHotTranslateChecked = new MutableLiveData<>();
        mHotTranslateChecked.setValue(hotTranslateChecked);
    }

    public LiveData<String> getTranslateResult() {
        if (mTranslateResult == null) mTranslateResult = new MutableLiveData<>();
        return mTranslateResult;
    }

    private void updateTranslateResult(String result) {
        if (mTranslateResult == null) mTranslateResult = new MutableLiveData<>();
        mTranslateResult.setValue(result);
    }

    public LiveData<String> getTranslateFrom() {
        if (mTranslateFrom == null) mTranslateFrom = new MutableLiveData<>();
        return mTranslateFrom;
    }

    public LiveData<String> getTranslateTo() {
        if (mTranslateTo == null) mTranslateTo = new MutableLiveData<>();
        return mTranslateTo;
    }

    public void updateTranslateFrom(String from) {
        if (mTranslateFrom == null) mTranslateFrom = new MutableLiveData<>();
        mTranslateFrom.setValue(from);
    }

    public void updateTranslateTo(String to) {
        if (mTranslateTo == null) mTranslateTo = new MutableLiveData<>();
        mTranslateTo.setValue(to);
    }

    public LiveData<List<TranslateEntity>> getTranslateEntityCollection() {
        if (mTranslateEntityCollection == null) mTranslateEntityCollection = new MutableLiveData<>();
        return mTranslateEntityCollection;
    }

    public void setTranslateEntityCollection(List<TranslateEntity> entities) {
        if (mTranslateEntityCollection == null) mTranslateEntityCollection = new MutableLiveData<>();
        mTranslateEntityCollection.setValue(entities);
    }

    public LiveData<List<TranslateEntity>> getMatchesTranslateEntities() {
        if (mMatchesTranslateEntities == null) mMatchesTranslateEntities = new MutableLiveData<>();
        return mMatchesTranslateEntities;
    }

    private void loadTranslateEntityCollection() {
        TranslateEntitiesLoader translateEntitiesLoader =
                new TranslateEntitiesLoader(new WeakReference<>(this));
        translateEntitiesLoader.execute();
    }

    public boolean isAutoFromSetted() {
        return mAutoFrom;
    }

    public void prepareTranslate(String text) {
        mTextToTranslate = text.toLowerCase();
        findMatchesEntities();
        if(mHotTranslateChecked.getValue())
            provideTranslate();
    }

    private void findMatchesEntities() {
        if(mTextToTranslate.length() == 0) return;
        MatchesEntitiesLoader loader = new MatchesEntitiesLoader(new WeakReference<>(this));
        loader.execute(mTextToTranslate, mTranslateEntityCollection.getValue());
    }

    public void provideTranslate() {
        if(mTextToTranslate.length() == 0) return;
        String translateFrom = mAutoFrom ? null : mTranslateFrom.getValue();
        mTranslaterRepository.translate(mTextToTranslate,
                translateFrom,
                mTranslateTo.getValue());
    }

    public boolean insertTranslateResultInDB() {
        final TranslateEntity entity = new TranslateEntity(mTextToTranslate,
                mTranslateResult.getValue(),
                mTranslateFrom.getValue(),
                mTranslateTo.getValue());
        if(checkTranslateResultExist(entity)) return false;
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDataRepository.insertTranslateEntity(entity);
                loadTranslateEntityCollection();
            }
        });
        return true;
    }

    private boolean checkTranslateResultExist(TranslateEntity entity) {
        ArrayList<TranslateEntity> entities = (ArrayList<TranslateEntity>) getTranslateEntityCollection().getValue();
        return entities.contains(entity);
    }

    @Override
    public void onTranslate(final String translateResult, int status) {
        if(status == TranslaterRepository.RESULT_OK) {
            updateTranslateResult(translateResult);
            if(mHotTranslateChecked.getValue()) return;
            insertTranslateResultInDB();
        }
    }

    public void deleteTranslateEntity(final TranslateEntity entityToDelete) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDataRepository.deleteTranslateEntity(entityToDelete);
                loadTranslateEntityCollection();
            }
        });
    }

    public void setAutoFrom(boolean autoFrom) {
        this.mAutoFrom = autoFrom;
    }

    public void clearResultAndMatches() {
        mTranslateResult.setValue("");
        mMatchesTranslateEntities.setValue(new ArrayList<TranslateEntity>());
    }

    public List<String> getLanguages() {
        return new ArrayList<>(mTranslaterRepository.getLanguages().keySet());
    }

    private static class TranslateEntitiesLoader extends AsyncTask<Void, Void, List<TranslateEntity>> {
        private WeakReference<TranslateViewModel> wrModel;

        TranslateEntitiesLoader(WeakReference<TranslateViewModel> wrModel) {
            this.wrModel = wrModel;
        }

        @Override
        protected List<TranslateEntity> doInBackground(Void... voids) {
            return wrModel.get().mDataRepository.getTranslateEntities();
        }

        @Override
        protected void onPostExecute(List<TranslateEntity> translateEntities) {
            Collections.sort(translateEntities, new Comparator<TranslateEntity>() {
                @Override
                public int compare(TranslateEntity o1, TranslateEntity o2) {
                    return o1.getId() > o2.getId() ? -1 : 1;
                }
            });
            wrModel.get().setTranslateEntityCollection(translateEntities);
        }
    }

    private static class MatchesEntitiesLoader extends AsyncTask<Object, Void, List<TranslateEntity>> {
        private WeakReference<TranslateViewModel> wrModel;

        MatchesEntitiesLoader(WeakReference<TranslateViewModel> wrModel) {
            this.wrModel = wrModel;
        }

        @Override
        protected List<TranslateEntity> doInBackground(Object... args) {
            String text = (String) args[0];
            ArrayList<TranslateEntity> allEntities = (ArrayList<TranslateEntity>) args[1];
            ArrayList<TranslateEntity> matchesEntities = new ArrayList<>();
            for (TranslateEntity entity : allEntities) {
                if((entity.getOriginalText().startsWith(text)||
                        (entity.getTranslatedText().startsWith(text)))) {
                    matchesEntities.add(entity);
                }
            }
            return matchesEntities;
        }

        @Override
        protected void onPostExecute(List<TranslateEntity> newMatchesEntities) {
            TranslateViewModel model = wrModel.get();

            List<TranslateEntity> originalMatchesEntities = model.mMatchesTranslateEntities.getValue();
            if(!(originalMatchesEntities.containsAll(newMatchesEntities)&&
                    newMatchesEntities.containsAll(originalMatchesEntities))) {
                model.mMatchesTranslateEntities.setValue(newMatchesEntities);
            }
        }
    }
}
