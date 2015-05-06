package com.acktos.motoparking.controllers;

import android.content.Context;
import android.graphics.Bitmap;

import com.acktos.motoparking.R;
import com.acktos.motoparking.helpers.HttpRequest;
import com.acktos.motoparking.helpers.InternalStorage;
import com.acktos.motoparking.helpers.MultipartEntity;
import com.acktos.motoparking.models.Parking;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Acktos on 2/19/15.
 */
public class ParkingController extends Controller {

    private Context context;
    private final static String FILE_PARKINGS="com.acktos.motoparking.PARKINGS";

    public ParkingController(Context context)
    {
        this.context=context;
    }

    public ArrayList<Parking> getParkings()
    {
        ArrayList<Parking> parkings = new ArrayList<Parking>();
        HttpRequest httpRequest = new HttpRequest(context.getString(R.string.url_get_parkings));
        String response = httpRequest.postRequest();

        if(response!=null)
        {
            //Ponerle 200

            InternalStorage storage = new InternalStorage(context);
            storage.saveFile(FILE_PARKINGS,response);
            //String content = storage.readFile(FILE_PARKINGS);
            //Log.i("Content", content);

            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    parkings.add(new Parking(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return parkings;
    }

    public ArrayList<Parking> getFile()
    {
        ArrayList<Parking> parkings = new ArrayList<Parking>();
        InternalStorage storage = new InternalStorage(context);
        String response = storage.readFile(FILE_PARKINGS);
        try {
            JSONArray jsonArray = new JSONArray(response);
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                parkings.add(new Parking(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parkings;
    }

    public String savePicture(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
        InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream
        String mypicture = "parqueadero_"+System.currentTimeMillis()+".jpg";

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost(context.getString(R.string.url_save_image)); // server

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("myFile",mypicture,in);
            httppost.setEntity(reqEntity);

            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                if (response != null)
                    return mypicture;
            } finally {

            }
        } finally {

        }

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mypicture;
    }

    public String saveParking(String field_address, String timepicker_initial, String timepicker_final, String price_minute, String price_hour, String price_standard, String field_comment, String coordinates, String imagetext) {
        HttpRequest httpRequest = new HttpRequest(context.getString(R.string.url_save_parking));
        httpRequest.setParam("field_address",field_address);
        httpRequest.setParam("timepicker_initial",timepicker_initial);
        httpRequest.setParam("timepicker_final",timepicker_final);
        httpRequest.setParam("price_minute",price_minute);
        httpRequest.setParam("price_hour",price_hour);
        httpRequest.setParam("price_standard",price_standard);
        httpRequest.setParam("field_comment",field_comment);
        httpRequest.setParam("coordinates",coordinates);
        httpRequest.setParam("imagetext",imagetext);
        String response = httpRequest.postRequest();
        return response;
    }
}
