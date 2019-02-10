package com.a44dw.mytranslater.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

public class TranslateAdapter extends RecyclerView.Adapter {

    private ArrayList<TranslateEntity> mData;
    private OnDeleteListener mListener;

    public void setData(ArrayList<TranslateEntity> mData) {
        this.mData = mData;
    }

    public void setListener(OnDeleteListener mListener) {
        this.mListener = mListener;
    }

    static class TranslateViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mHolder;
        TextView mOriginalText;
        TextView mTranslatedText;
        ImageView mBascket;

        public TranslateViewHolder(ConstraintLayout layout) {
            super(layout);

            this.mHolder = layout;
            this.mOriginalText = layout.findViewById(R.id.translateFrom);
            this.mTranslatedText = layout.findViewById(R.id.translateTo);
            this.mBascket = layout.findViewById(R.id.basketImageView);
        }

        void bind(TranslateEntity data, final OnDeleteListener listener) {
            mHolder.setTag(data);
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
        ConstraintLayout listItem = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.translate_list_item, viewGroup, false);
        return new TranslateViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((TranslateViewHolder)viewHolder).bind(mData.get(i), mListener);
    }

    @Override
    public int getItemCount() {return mData.size();}
}
