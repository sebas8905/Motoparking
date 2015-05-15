package com.acktos.motoparking.controllers;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.acktos.motoparking.R;
import com.acktos.motoparking.helpers.HttpRequest;
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
    private Uri uri;
    private Cursor c;
    private final static String FILE_PARKINGS="com.acktos.motoparking.PARKINGS";
    private static final Uri URI_CP = Uri.parse("content://content.provider.parkings/parkings");

    private int id;
    private String address;
    private String coordinates;
    private String schedule;
    private int price_minute;
    private int price_hour;
    private int price_standard;
    private String image;
    private String comments;
    private String creation_date;

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
            ContentResolver CR = context.getContentResolver();
            //String content = storage.readFile(FILE_PARKINGS);
            //Log.i("Content", content);

            try {
                JSONArray jsonArray = new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    parkings.add(new Parking(jsonObject));
                }
                if(parkings.size()>0) {
                    for (int i = 0; i < parkings.size(); i++) {
                        Integer id = Integer.valueOf(parkings.get(i).id);
                        String address = parkings.get(i).address;
                        String coordinates = parkings.get(i).coordinates;
                        String schedule = parkings.get(i).schedule;
                        Integer price_minute = Integer.valueOf(parkings.get(i).price_minute);
                        Integer price_hour = Integer.valueOf(parkings.get(i).price_hour);
                        Integer price_standard = Integer.valueOf(parkings.get(i).price_standard);
                        String image = parkings.get(i).image;
                        String comments = parkings.get(i).comments;
                        String creation_date = parkings.get(i).creation_date;
                        // Insertamos registros
                        try{
                            uri = CR.insert(URI_CP, setVALORES(id, address, coordinates, schedule, price_minute, price_hour, price_standard, image, comments, creation_date));
                            Log.i("REGISTRO INSERTADO", uri.toString());
                            uri = Uri.parse("content://content.provider.parkings/parkings/"+id+"");
                            CR.update(uri, setVALORES(id, address, coordinates, schedule, price_minute, price_hour, price_standard, image, comments, creation_date),null,null);
                            Log.i("REGISTRO ACTUALIZADO", String.valueOf(id));
                        }catch (SQLException e)
                        {
                            Log.i("Error",e.getMessage());
                        }

                    }
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

        ContentResolver CR = context.getContentResolver();

        String[] valores_recuperar = {"_id", "address", "coordinates", "schedule", "price_minute", "price_hour", "price_standard", "image", "comments", "creation_date"};
        c = CR.query(URI_CP, valores_recuperar, null, null, null);
        c.moveToFirst();

        if(c.getCount()>0) {
            String jsonparking = "[";

            do {
                id = c.getInt(0);
                address = c.getString(1);
                coordinates = c.getString(2);
                schedule = c.getString(3);
                price_minute = c.getInt(4);
                price_hour = c.getInt(5);
                price_standard = c.getInt(6);
                image = c.getString(7);
                comments = c.getString(8);
                creation_date = c.getString(9);
                jsonparking += "{\"price_standard\": \"" + price_standard + "\", \"price_hour\": \"" + price_hour + "\", \"schedule\": \"" + schedule + "\", \"price_minute\": \"" + price_minute + "\", \"coordinates\": \"" + coordinates + "\", \"creation_date\": \"" + creation_date + "\", \"comments\": \"" + comments + "\", \"image\": \"" + image + "\", \"address\": \"" + address + "\", \"id\": " + id + "},";
            } while (c.moveToNext());

            jsonparking = jsonparking.substring(0, jsonparking.length() - 1);
            jsonparking += "]";

            try {
                JSONArray jsonArray = new JSONArray(jsonparking);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    parkings.add(new Parking(jsonObject));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    public ContentValues setVALORES(int id, String address, String coordinates, String schedule, int price_minute, int price_hour, int price_standard, String image, String comments, String creation_date) {
        ContentValues valores = new ContentValues();
        valores.put("_id", id);
        valores.put("address", address);
        valores.put("coordinates", coordinates);
        valores.put("schedule", schedule);
        valores.put("price_minute", price_minute);
        valores.put("price_hour", price_hour);
        valores.put("price_standard", price_standard);
        valores.put("image", image);
        valores.put("comments", comments);
        valores.put("creation_date", creation_date);
        return valores;
    }
}
