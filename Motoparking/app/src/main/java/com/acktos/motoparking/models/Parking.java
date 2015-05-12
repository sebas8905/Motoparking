package com.acktos.motoparking.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acktos on 2/19/15.
 */
public class Parking {

    public String id;
    public String address;
    public String coordinates;
    public String schedule;
    public String price_minute;
    public String price_hour;
    public String price_standard;
    public String image;
    public String comments;
    public String creation_date;

    public final static String KEY_ID="id";
    public final static String KEY_ADDRESS="address";
    public final static String KEY_COORDINATES="coordinates";
    public final static String KEY_SCHEDULE="schedule";
    public final static String KEY_PRICE_MINUTE="price_minute";
    public final static String KEY_PRICE_HOUR="price_hour";
    public final static String KEY_PRICE_STANDARD="price_standard";
    public final static String KEY_IMAGE="image";
    public final static String KEY_COMMENTS="comments";
    public final static String KEY_CREATION_DATE="creation_date";

    public Parking(JSONObject jsonObject)
    {
        try {
            this.id = jsonObject.getString(KEY_ID);
            this.address = jsonObject.getString(KEY_ADDRESS);
            this.coordinates = jsonObject.getString(KEY_COORDINATES);
            this.schedule = jsonObject.getString(KEY_SCHEDULE);
            this.price_minute = jsonObject.getString(KEY_PRICE_MINUTE);
            this.price_hour = jsonObject.getString(KEY_PRICE_HOUR);
            this.price_standard = jsonObject.getString(KEY_PRICE_STANDARD);
            this.image = jsonObject.getString(KEY_IMAGE);
            this.comments = jsonObject.getString(KEY_COMMENTS);
            this.creation_date = jsonObject.getString(KEY_CREATION_DATE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Parking(String string)
    {
        try {
            JSONObject jsonObject = new JSONObject(string);
            this.id = jsonObject.getString(KEY_ID);
            this.address = jsonObject.getString(KEY_ADDRESS);
            this.coordinates = jsonObject.getString(KEY_COORDINATES);
            this.schedule = jsonObject.getString(KEY_SCHEDULE);
            this.price_minute = jsonObject.getString(KEY_PRICE_MINUTE);
            this.price_hour = jsonObject.getString(KEY_PRICE_HOUR);
            this.price_standard = jsonObject.getString(KEY_PRICE_STANDARD);
            this.image = jsonObject.getString(KEY_IMAGE);
            this.comments = jsonObject.getString(KEY_COMMENTS);
            this.creation_date = jsonObject.getString(KEY_CREATION_DATE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String toJson()
    {
        String json="";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_ID,this.id);
            jsonObject.put(KEY_ADDRESS,this.address);
            jsonObject.put(KEY_COORDINATES,this.coordinates);
            jsonObject.put(KEY_SCHEDULE,this.schedule);
            jsonObject.put(KEY_PRICE_MINUTE,this.price_minute);
            jsonObject.put(KEY_PRICE_HOUR,this.price_hour);
            jsonObject.put(KEY_PRICE_STANDARD,this.price_standard);
            jsonObject.put(KEY_IMAGE,this.image);
            jsonObject.put(KEY_COMMENTS,this.comments);
            jsonObject.put(KEY_CREATION_DATE,this.creation_date);
            json=jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
