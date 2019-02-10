package com.a44dw.mytranslater.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a44dw.mytranslater.interfaces.OnDeleteListener;
import com.a44dw.mytranslater.R;
import com.a44dw.mytranslater.entities.TranslateEntity;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter {

    private static final int MAX_ELEMENTS = 9;
    private ArrayList<TranslateEntity> mData;
    private OnDeleteListener mListener;

    public void setData(ArrayList<TranslateEntity> mData) {
        this.mData = mData;
    }

    public void setListener(OnDeleteListener mListener) {
        this.mListener = mListener;
    }

    static class MainViewHolder extends RecyclerView.ViewHolder {

        CardView mHolder;
        TextView mOriginalLang;
        TextView mTranslatedLang;
        TextView mOriginalText;
        TextView mTranslatedText;
        ImageView mBascket;

        MainViewHolder(CardView layout) {
            super(layout);

            this.mHolder = layout;
            this.mOriginalLang = layout.findViewById(R.id.fromLanguageTextView);
            this.mTranslatedLang = layout.findViewById(R.id.toLanguageTextView);
            this.mOriginalText = layout.findViewById(R.id.originalTextView);
            this.mTranslatedText = layout.findViewById(R.id.translatedTextView);
            this.mBascket = layout.findViewById(R.id.basketImageView);
        }

        void bind(TranslateEntity data, final OnDeleteListener listener) {

            mHolder.setTag(data);
            mOriginalLang.setText(data.getOriginalLanguage());
            mTranslatedLang.setText(data.getTranslatedLanguage());
            mOriginalText.setText(data.getOriginalText());
            mTranslatedText.setText(data.getTranslatedText());
            mBascket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDelete(mHolder);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView listItem = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_list_item, viewGroup, false);
        return new MainViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((MainViewHolder)viewHolder).bind(mData.get(i), mListener);
    }

    @Override
    public int getItemCount() {
        return mData.size() > MAX_ELEMENTS ? MAX_ELEMENTS : mData.size();
    }
}
