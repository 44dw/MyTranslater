package com.a44dw.mytranslater.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.a44dw.mytranslater.interfaces.OnDeleteListener;
import com.a44dw.mytranslater.R;
import com.a44dw.mytranslater.adapters.MainAdapter;
import com.a44dw.mytranslater.entities.*;
import com.a44dw.mytranslater.model.TranslateViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainFragment extends Fragment implements OnDeleteListener,
                                                      View.OnClickListener{

    private TranslateViewModel mModel;
    private LiveData<List<TranslateEntity>> mTranslateEntityCollection;

    private Spinner mMainLanguageSpinnerFrom;
    private Spinner mMainLanguageSpinnerTo;
    private RecyclerView mMainRecyclerView;
    private MainAdapter mAdapter;

    public MainFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View holder = inflater.inflate(R.layout.fragment_main, container, false);

        initModelAndData();
        initUI(holder);

        return holder;
    }

    private void initModelAndData() {
        mModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(TranslateViewModel.class);
        mTranslateEntityCollection = mModel.getTranslateEntityCollection();
        mTranslateEntityCollection.observe(this, new Observer<List<TranslateEntity>>() {
            @Override
            public void onChanged(@Nullable List<TranslateEntity> translateEntities) {
                updateAndSetAdapter((ArrayList<TranslateEntity>) mTranslateEntityCollection.getValue());
            }
        });
    }

    private void initUI(View holder) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                android.R.layout.simple_spinner_item,
                mModel.getLanguages().toArray(
                        new String[mModel.getLanguages().size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMainLanguageSpinnerFrom = holder.findViewById(R.id.mainLanguageSpinner1);
        mMainLanguageSpinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(selectedItemView != null) {
                    String rawFrom = ((TextView)selectedItemView).getText().toString();
                    mModel.updateTranslateFrom(rawFrom);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mMainLanguageSpinnerTo = holder.findViewById(R.id.mainLanguageSpinner2);
        mMainLanguageSpinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(selectedItemView != null) {
                    String rawTo = ((TextView)selectedItemView).getText().toString();
                    mModel.updateTranslateTo(rawTo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        mMainLanguageSpinnerFrom.setAdapter(adapter);
        mMainLanguageSpinnerTo.setAdapter(adapter);
        setSpinnerSelections();
        ImageButton mMainReverseButton = holder.findViewById(R.id.mainReverseButton);
        mMainReverseButton.setOnClickListener(this);
        mMainRecyclerView = holder.findViewById(R.id.mainRecyclerView);
        mMainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CheckBox mAutoLanguageCheckBox = holder.findViewById(R.id.autoLanguageCheckBox);
        mAutoLanguageCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMainLanguageSpinnerFrom.setEnabled(!isChecked);
                mModel.setAutoFrom(isChecked);
            }
        });
        mAdapter = new MainAdapter();
    }

    private void setSpinnerSelections() {
        String from = (mModel.getTranslateFrom().getValue() == null ? "английский" :
                mModel.getTranslateFrom().getValue());
        String to = (mModel.getTranslateTo().getValue() == null ? "русский" :
                mModel.getTranslateTo().getValue());
        mMainLanguageSpinnerFrom.setSelection(mModel.getLanguages().indexOf(from));
        mMainLanguageSpinnerTo.setSelection(mModel.getLanguages().indexOf(to));
    }

    private void updateAndSetAdapter(ArrayList<TranslateEntity> list) {
        mAdapter.setData(list);
        mAdapter.setListener(this);
        mMainRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDelete(View item) {
        TranslateEntity entityToDelete = (TranslateEntity) item.getTag();
        mModel.deleteTranslateEntity(entityToDelete);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.mainReverseButton): {
                reverseLanguages();
                break;
            }
        }
    }

    private void reverseLanguages() {
        int fromPosition = mMainLanguageSpinnerFrom.getSelectedItemPosition();
        int toPosition = mMainLanguageSpinnerTo.getSelectedItemPosition();
        mMainLanguageSpinnerFrom.setSelection(toPosition);
        mMainLanguageSpinnerTo.setSelection(fromPosition);
    }

}
