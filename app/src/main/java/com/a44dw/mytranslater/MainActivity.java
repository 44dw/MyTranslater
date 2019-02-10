package com.a44dw.mytranslater;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.a44dw.mytranslater.fragments.MainFragment;
import com.a44dw.mytranslater.fragments.TranslateFragment;
import com.a44dw.mytranslater.model.TranslateViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String FROM_LANGUAGE_PREFS = "fromLanguagePrefs";
    private static final String TO_LANGUAGE_PREFS = "toLanguagePrefs";
    private TranslateViewModel mModel;

    private MainFragment mMainFragment;
    private TranslateFragment mTranslateFragment;
    private EditText mMainTranslateField;
    private ImageView mMainClear;

    private final static String MAIN_FRAGMENT = "main_fragment";
    private final static String TRANSLATE_FRAGMENT = "translate_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initModelAndData();
        initUI();
        initLanguagePrefs();

        showMainFragment();
    }

    private void initLanguagePrefs() {
        SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
        if((preferences.contains(FROM_LANGUAGE_PREFS))&&
                preferences.contains(TO_LANGUAGE_PREFS)) {
            mModel.updateTranslateFrom(preferences.getString(FROM_LANGUAGE_PREFS, "английский"));
            mModel.updateTranslateTo(preferences.getString(TO_LANGUAGE_PREFS, "русский"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FROM_LANGUAGE_PREFS, mModel.getTranslateFrom().getValue());
        editor.putString(TO_LANGUAGE_PREFS, mModel.getTranslateTo().getValue());
        editor.apply();
    }

    private void initModelAndData() {
        mModel = ViewModelProviders.of(this).get(TranslateViewModel.class);
    }

    private void initUI() {
        mMainFragment = new MainFragment();
        mTranslateFragment = new TranslateFragment();
        mMainTranslateField = findViewById(R.id.mainTranslateField);
        mMainTranslateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    showTranslateFragment();
                    ((EditText)v).setHint(R.string.input_translate_field_on);
                }
                else {
                    showMainFragment();
                    ((EditText)v).setHint(R.string.input_translate_field_off);
                }
            }
        });

        mMainTranslateField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                prepareTranslate(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mMainClear = findViewById(R.id.mainClear);
        mMainClear.setOnClickListener(this);
    }

    private void prepareTranslate(String text) {
        mModel.prepareTranslate(text);
    }

    private void showMainFragment() {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragmentHolder, mMainFragment, MAIN_FRAGMENT)
                                   .commit();
        mMainClear.setVisibility(View.GONE);
    }

    private void showTranslateFragment() {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragmentHolder, mTranslateFragment, TRANSLATE_FRAGMENT)
                                   .commit();
        mMainClear.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainClear: {
                if(mMainTranslateField.getText().length() > 0) {
                    mMainTranslateField.getText().clear();
                    mTranslateFragment.clearTranslateResultField();
                } else {
                    mMainTranslateField.clearFocus();
                    showMainFragment();
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentByTag(TRANSLATE_FRAGMENT) != null) {
            mMainTranslateField.clearFocus();
            showMainFragment();
        } else super.onBackPressed();
    }
}
