package com.acktos.motoparking;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.acktos.motoparking.controllers.ParkingController;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddParkingDataActivity extends Activity {


    private EditText field_address;
    private EditText timepicker_initial;
    private EditText timepicker_final;
    private EditText price_minute;
    private EditText price_hour;
    private EditText price_standard;
    private EditText field_comment;
    private String coordinates;
    private TextView imagetext;
    private Switch switch_free;

    private static final String TAG = "upload";
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    File photoFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        coordinates = getIntent().getExtras().getString("coordinates");
        String address = getIntent().getExtras().getString("address");

        setContentView(R.layout.activity_add_parking_data);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        EditText parking_address = ((EditText) findViewById(R.id.field_address));
        parking_address.setText(address);

        switch_free = (Switch) findViewById(R.id.switch_free);

        switch_free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout linear_prices = (LinearLayout) findViewById(R.id.linear_prices);
                if(isChecked){
                    linear_prices.setVisibility(View.GONE);
                    price_minute = (EditText)findViewById(R.id.price_minute);
                    price_hour = (EditText)findViewById(R.id.price_hour);
                    price_standard = (EditText)findViewById(R.id.price_standard);
                    price_minute.setText("");
                    price_hour.setText("");
                    price_standard.setText("");
                }else{

                    linear_prices.setVisibility(View.VISIBLE);
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_parking_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                field_address = (EditText)findViewById(R.id.field_address);
                timepicker_initial = (EditText)findViewById(R.id.timepicker_initial);
                timepicker_final = (EditText)findViewById(R.id.timepicker_final);
                price_minute = (EditText)findViewById(R.id.price_minute);
                price_hour = (EditText)findViewById(R.id.price_hour);
                price_standard = (EditText)findViewById(R.id.price_standard);
                field_comment = (EditText)findViewById(R.id.field_comment);
                imagetext = (TextView)findViewById(R.id.imagetext);

                if( field_address.getText().toString().length() == 0 ) {
                    field_address.setError(getString(R.string.error_direction));
                    return false;
                }

                if( timepicker_initial.getText().toString().equals(getString(R.string.label_initial)) ) {
                    timepicker_initial.setError(getString(R.string.error_initial));
                    return false;
                }

                if( timepicker_final.getText().toString().equals(getString(R.string.label_final)) ) {
                    timepicker_final.setError(getString(R.string.error_final));
                    return false;
                }

                String field_address_string = field_address.getText().toString();
                String timepicker_initial_string = timepicker_initial.getText().toString();
                String timepicker_final_string = timepicker_final.getText().toString();
                String price_minute_string = price_minute.getText().toString();
                String price_hour_string = price_hour.getText().toString();
                String price_standard_string = price_standard.getText().toString();
                String field_comment_string = field_comment.getText().toString();
                String coordinates_string = coordinates;
                String imagetext_string = imagetext.getText().toString();

                if(price_minute_string.length() == 0){ price_minute_string="0";  }
                if(price_hour_string.length() == 0){ price_hour_string="0";  }
                if(price_standard_string.length() == 0){ price_standard_string="0";  }

                saveParking(field_address_string,timepicker_initial_string,timepicker_final_string,price_minute_string,price_hour_string,price_standard_string,field_comment_string,coordinates_string,imagetext_string);

                return true;

            case android.R.id.home:
                coordinates = getIntent().getExtras().getString("coordinates");
                Intent intent_name = new Intent(getApplicationContext(),AddMapActivity.class);
                intent_name.putExtra("coordinates",coordinates);
                startActivity(intent_name);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveParking(String field_address, String timepicker_initial, String timepicker_final, String price_minute, String price_hour, String price_standard, String field_comment, String coordinates, String imagetext) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String field_address = params[0];
                String timepicker_initial = params[1];
                String timepicker_final = params[2];
                String price_minute = params[3];
                String price_hour = params[4];
                String price_standard = params[5];
                String field_comment = params[6];
                String coordinates = params[7];
                String imagetext;

                ParkingController parkingController = new ParkingController(AddParkingDataActivity.this);

                ImageView mImageView = (ImageView) findViewById(R.id.imageview);

                if(mImageView.getDrawable() == null) {
                    imagetext = "";
                }
                else
                {
                    Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                    imagetext = parkingController.savePicture(bitmap);
                    imagetext = R.string.url_images+imagetext;
                }

                String response = parkingController.saveParking(field_address, timepicker_initial, timepicker_final, price_minute, price_hour, price_standard, field_comment, coordinates, imagetext);
                parkingController.getParkings();

                return response;
            }

            @Override
            protected void onPostExecute(String response) {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                Intent intent_name = new Intent(getApplicationContext(),MapActivity.class);
                startActivity(intent_name);
                finish();
                super.onPostExecute(response);
            }

            @Override
            protected void onPreExecute() {
                setProgressBarIndeterminateVisibility(true);
                super.onPreExecute();
            }

        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(field_address,timepicker_initial,timepicker_final,price_minute,price_hour,price_standard,field_comment,coordinates,imagetext);

    }

    public class TimePickerInitialFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            String hora = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
            EditText editText =  ((EditText) findViewById(R.id.timepicker_initial));
            editText.setText(hora);
        }
    }

    public class TimePickerFinalFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            String hora = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
            EditText editText =  ((EditText) findViewById(R.id.timepicker_final));
            editText.setText(hora);
        }
    }

    public void showTimePickerInitialDialog(View v) {
        DialogFragment newFragment = new TimePickerInitialFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTimePickerFinalDialog(View v) {
        DialogFragment newFragment = new TimePickerFinalFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void takePhoto(View v) {
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.i(TAG, "onActivityResult: " + this);
        setPic();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/picupload";
        File dir = new File(storageDir);
        if (!dir.exists())
            dir.mkdir();

        File image = new File(storageDir + "/" + imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        //Log.i(TAG, "photo path = " + mCurrentPhotoPath);
        return image;
    }

    private void setPic() {
        ImageView mImageView = (ImageView) findViewById(R.id.imageview);

        /*if(mImageView.getDrawable() == null) {

        }
        else
        {*/
            // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor << 1;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            Matrix mtx = new Matrix();
            mtx.postRotate(0);
            // Rotating Bitmap
            Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

            if (rotatedBMP != bitmap)
                bitmap.recycle();

            mImageView.setImageBitmap(rotatedBMP);
        //}

    }

}
