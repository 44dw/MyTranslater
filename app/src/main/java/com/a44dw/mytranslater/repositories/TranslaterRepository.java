package com.a44dw.mytranslater.repositories;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TranslaterRepository {

    private static final String TRANSTATE_KEY =
            "trnsl.1.1.20190206T175808Z.634259c03aae2564.997ca9e90c780d7febb1b4b7364432d4ed27f745";
    private static final String TRANSLATE_URL =
            "https://translate.yandex.net/api/v1.5/tr.json/translate?";
    public static final int RESULT_OK = 1;
    public static final int RESULT_ERROR = 2;

    private static HashMap<String, String> mLanguageMap;
    private TranslaterResponseListener mListener;
    private StringBuilder mTranslateBuilder;
    private RequestQueue mQueue;

    public TranslaterRepository(Context context) {
        initLanguageMap();
        initRequestQueue(context);
    }

    private void initRequestQueue(Context context) {
        mQueue = Volley.newRequestQueue(context);
    }

    private void initLanguageMap() {
        mLanguageMap = new HashMap<>();
        mLanguageMap.put("английский", "en");
        mLanguageMap.put("русский", "ru");
        mLanguageMap.put("немецкий", "de");
        mLanguageMap.put("французский", "fr");
        mLanguageMap.put("испанский", "es");
        mLanguageMap.put("польский", "pl");
    }

    public void setResponseListener(TranslaterResponseListener mListener) {
        this.mListener = mListener;
    }

    public Map<String, String> getLanguages() {
        if(mLanguageMap == null)
            initLanguageMap();
        return mLanguageMap;
    }

    public void translate(String textToTranslate, @Nullable String from, String to) {

        String url = concatUrl(textToTranslate,
                mLanguageMap.get(from),
                mLanguageMap.get(to));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String resp) {
                        try {
                            JSONObject object = new JSONObject(resp);
                            mListener.onTranslate(object.getString("text").substring(2,
                                    object.getString("text").length()-2),
                                    RESULT_OK);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mListener.onTranslate("Некорректный ответ от сервиса!",
                                    RESULT_ERROR);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mListener.onTranslate("Нет связи с сервисом!",
                                RESULT_ERROR);
                    }
        });
        mQueue.add(stringRequest);
    }

    private String concatUrl(String textToTranslate, @Nullable String from, String to) {
        final String txt = "&text=";
        final String lng = "&lang=";
        final String fromTo = (from != null ? from + "-" + to
                : to);
        initTranslateBuilder();
        mTranslateBuilder.append(txt);
        mTranslateBuilder.append(textToTranslate);
        mTranslateBuilder.append(lng);
        mTranslateBuilder.append(fromTo);

        return mTranslateBuilder.toString();
    }

    private void initTranslateBuilder() {
        mTranslateBuilder = new StringBuilder(TRANSLATE_URL);
        mTranslateBuilder.append("key=");
        mTranslateBuilder.append(TRANSTATE_KEY);
    }

    public interface TranslaterResponseListener {
        void onTranslate(String translateResult, int status);
    }
}
