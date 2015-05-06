package com.acktos.motoparking.controllers;

import android.content.Context;

import com.acktos.motoparking.R;
import com.acktos.motoparking.helpers.HttpRequest;
import com.acktos.motoparking.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Acktos on 3/3/15.
 */
public class ReviewController {

    private Context context;

    public ReviewController(Context context)
    {
        this.context=context;
    }

    public ArrayList<Review> getReviews(String id)
    {
        ArrayList<Review> reviews = new ArrayList<Review>();
        HttpRequest httpRequest = new HttpRequest(context.getString(R.string.url_get_comments));
        httpRequest.setParam("id",id);
        String response = httpRequest.postRequest();

        if(response!=null)
        {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    reviews.add(new Review(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return reviews;
    }

    public String saveReview(String paramId, String paramName, String paramComment, String paramCalification) {

        HttpRequest httpRequest = new HttpRequest(context.getString(R.string.url_save_review));
        httpRequest.setParam("id",paramId);
        httpRequest.setParam("nombre",paramName);
        httpRequest.setParam("comentario",paramComment);
        httpRequest.setParam("calificacion",paramCalification);
        String response = httpRequest.postRequest();
        return response;
    }
}
