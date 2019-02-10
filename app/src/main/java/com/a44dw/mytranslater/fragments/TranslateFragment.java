package com.a44dw.mytranslater.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.a44dw.mytranslater.interfaces.OnDeleteListener;
import com.a44dw.mytranslater.R;
import com.a44dw.mytranslater.adapters.TranslateAdapter;
import com.a44dw.mytranslater.entities.TranslateEntity;
import com.a44dw.mytranslater.model.TranslateViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TranslateFragment extends Fragment implements View.OnClickListener, OnDeleteListener {

    TranslateViewModel mModel;
    LiveData<String> mTranslateFrom;
    LiveData<String> mTranslateTo;
    LiveData<String> mTranslateResult;
    LiveData<List<TranslateEntity>> mMatchesEntities;

    private TextView mTranslateResultField;
    private Switch mTranslateHotSwitch;
    private Button mTranslateToButton;
    private ConstraintLayout mTranslateResultLayout;
    private TextView mTranslateFromHint;
    private TextView mTranslateDictionaryHint;
    private ImageView mTranslateSave;
    private RecyclerView mTranslateRecyclerView;
    private TranslateAdapter mTranslateAdapter;

    private boolean snackIsShown = false;

    public TranslateFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View holder = inflater.inflate(R.layout.fragment_translate, container, false);

        initModelAndData();
        initUI(holder);

        return holder;
    }

    private void initModelAndData() {
        mModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(TranslateViewModel.class);
        mTranslateFrom = mModel.getTranslateFrom();
        mTranslateFrom.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if(!mModel.isAutoFromSetted())
                    mTranslateFromHint.setText(s);
            }
        });
        mTranslateTo = mModel.getTranslateTo();
        mTranslateTo.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mTranslateToButton.setText(s);
            }
        });
        mTranslateResult = mModel.getTranslateResult();
        mTranslateResult.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mTranslateResultLayout.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                mTranslateResultField.setText(s);
            }
        });
        mMatchesEntities = mModel.getMatchesTranslateEntities();
        mMatchesEntities.observe(this, new Observer<List<TranslateEntity>>() {
            @Override
            public void onChanged(@Nullable List<TranslateEntity> translateEntities) {

                mTranslateDictionaryHint.setVisibility(
                        translateEntities.size() > 0 ? View.VISIBLE : View.GONE);

                mTranslateAdapter.setData((ArrayList<TranslateEntity>) translateEntities);
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                mTranslateRecyclerView.setLayoutManager(manager);
                mTranslateRecyclerView.setAdapter(mTranslateAdapter);
                DividerItemDecoration dividerItemDecoration =
                        new DividerItemDecoration(mTranslateRecyclerView.getContext(),
                        manager.getOrientation());
                mTranslateRecyclerView.addItemDecoration(dividerItemDecoration);
            }
        });
    }

    private void initUI(View holder) {
        mTranslateResultField = holder.findViewById(R.id.translateTextField);
        mTranslateToButton = holder.findViewById(R.id.translateToButton);
        mTranslateToButton.setOnClickListener(this);
        mTranslateFromHint = holder.findViewById(R.id.translateFromHint);
        mTranslateResultLayout = holder.findViewById(R.id.translateResultLayout);
        mTranslateDictionaryHint = holder.findViewById(R.id.translateDictionaryHint);
        mTranslateHotSwitch = holder.findViewById(R.id.translateHot);
        mTranslateHotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mModel.setHotTranslateChecked(isChecked);
                mTranslateSave.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if(isChecked) showCheckedChangedInfo();
                else snackIsShown = false;
            }
        });
        mTranslateSave = holder.findViewById(R.id.translateSave);
        mTranslateSave.setOnClickListener(this);
        mTranslateRecyclerView = holder.findViewById(R.id.translateSearchRecyclerView);
        mTranslateAdapter = new TranslateAdapter();
        mTranslateAdapter.setListener(this);
        mModel.setHotTranslateChecked(mTranslateHotSwitch.isChecked());

        clearTranslateResultField();
    }

    private void showCheckedChangedInfo() {
        if(snackIsShown) return;
        Snackbar.make(mTranslateHotSwitch,
                getResources().getText(R.string.hot_translate_disclaimer),
                Snackbar.LENGTH_LONG)
                .show();
        snackIsShown = true;
    }

    private void showSaveInfo(boolean saveResult) {
        String message = getResources().getText(saveResult ?
                R.string.save_success :
                R.string.save_unsuccess).toString();
        Snackbar.make(mTranslateSave,
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }

    public void clearTranslateResultField() {
        mModel.clearResultAndMatches();
        mTranslateResultLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.translateToButton): {
                mModel.provideTranslate();
                break;
            }
            case (R.id.translateSave): {
                showSaveInfo(mModel.insertTranslateResultInDB());
                break;
            }
        }
    }

    @Override
    public void onDelete(View item) {
        TranslateEntity entityToDelete = (TranslateEntity) item.getTag();
        mModel.deleteTranslateEntity(entityToDelete);
        mMatchesEntities.getValue().remove(entityToDelete);
        mTranslateAdapter.notifyDataSetChanged();
    }
}
