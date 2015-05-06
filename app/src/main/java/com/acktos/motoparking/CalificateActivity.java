package com.acktos.motoparking;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.acktos.motoparking.controllers.ReviewController;
import com.acktos.motoparking.models.Parking;

public class CalificateActivity extends Activity {

    private EditText name;
    private EditText comment;
    private RatingBar calification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle(getString(R.string.label_calificate));
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_calificate);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calificate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:

                String data = getIntent().getExtras().getString("data_to_sent");
                Parking parking = new Parking(data);
                String parking_id = parking.id;

                name   = (EditText)findViewById(R.id.field_name);
                comment   = (EditText)findViewById(R.id.field_comment);
                calification   = (RatingBar)findViewById(R.id.field_calification);

                Float calification_float = calification.getRating();

                Integer calification_integer = Math.round(calification_float);

                String calification_string = calification_integer.toString();

                if( name.getText().toString().length() == 0 ) {
                    name.setError(getString(R.string.error_name));
                    return false;
                }

                if( comment.getText().toString().length() == 0 ) {
                    comment.setError(getString(R.string.error_comment));
                    return false;
                }

                saveReview(parking_id,name.getText().toString(), comment.getText().toString(), calification_string);

                return true;

            case android.R.id.home:
                data = getIntent().getExtras().getString("data_to_sent");
                Intent intent_name = new Intent(getApplicationContext(),DetailActivity.class);
                intent_name.putExtra("data_to_sent",data);
                startActivity(intent_name);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveReview(String id, String name, String comment, String calification_string) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String paramId = params[0];
                String paramName = params[1];
                String paramComment = params[2];
                String paramCalification = params[3];

                ReviewController reviewController = new ReviewController(CalificateActivity.this);
                String response = reviewController.saveReview(paramId,paramName, paramComment, paramCalification);
                return response;
            }

            @Override
            protected void onPostExecute(String response) {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                String data = getIntent().getExtras().getString("data_to_sent");
                Intent intent_name = new Intent(getApplicationContext(),DetailActivity.class);
                intent_name.putExtra("data_to_sent",data);
                startActivity(intent_name);
                super.onPostExecute(response);
            }

            @Override
            protected void onPreExecute() {
                setProgressBarIndeterminateVisibility(true);
                super.onPreExecute();
            }

        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(id, name, comment, calification_string);

    }
}
