package com.acktos.motoparking.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 3/3/15.
 */
public class Review {

    public String id;
    public String author;
    public String calification;
    public String review;
    public String date;

    public final static String KEY_ID="id";
    public final static String KEY_AUTHOR="author";
    public final static String KEY_CALIFICATION="calification";
    public final static String KEY_REVIEW="review";
    public final static String KEY_DATE="date";

    public Review(JSONObject jsonObject)
    {
        try {
            this.id = jsonObject.getString(KEY_ID);
            this.author = jsonObject.getString(KEY_AUTHOR);
            this.calification = jsonObject.getString(KEY_CALIFICATION);
            this.review = jsonObject.getString(KEY_REVIEW);
            this.date = jsonObject.getString(KEY_DATE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
