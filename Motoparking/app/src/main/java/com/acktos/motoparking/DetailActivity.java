package com.acktos.motoparking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.acktos.motoparking.controllers.ReviewController;
import com.acktos.motoparking.models.Parking;
import com.acktos.motoparking.models.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends Activity {

    private TextView parking_title;
    private TextView parking_schedule;
    private TextView parking_price;
    private TextView parking_price_values;
    private TextView parking_label_price;
    private TextView parking_comments;
    private ImageView parking_image;
    private ProgressBar parking_loading;
    private String price;
    private String price_label;
    private List<String> array_prices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String data = getIntent().getExtras().getString("data_to_sent");
        Parking parking = new Parking(data);

        setTitle(parking.address);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        parking_image = ((ImageView) findViewById(R.id.parking_image));

        parking_title = ((TextView) findViewById(R.id.parking_address));
        parking_title.setText(parking.address);

        parking_schedule = ((TextView) findViewById(R.id.parking_schedule));
        parking_schedule.setText(parking.schedule);

        parking_comments = ((TextView) findViewById(R.id.parking_comments));
        parking_comments.setText(parking.comments);

        parking_price = ((TextView) findViewById(R.id.parking_price));
        parking_label_price = ((TextView) findViewById(R.id.parking_label_price));
        parking_price_values = ((TextView) findViewById(R.id.parking_price_value));

        price =  getString(R.string.label_free);

        if (Integer.parseInt(parking.price_standard) > 0) {
            price = parking.price_standard;
            price_label = getString(R.string.label_standard);
            array_prices.add(price+" "+getString(R.string.label_standard));
        }
        if (Integer.parseInt(parking.price_hour) > 0) {
            price = parking.price_hour;
            price_label = getString(R.string.label_hour);
            array_prices.add(price+" "+getString(R.string.label_hour));
        }
        if (Integer.parseInt(parking.price_minute) > 0) {
            price =  parking.price_minute;
            price_label =  getString(R.string.label_minute);
            array_prices.add(price+" "+getString(R.string.label_minute));
        }

        if(array_prices.size()>1)
        {
            parking_price_values.setText(array_prices.toString());
        }
        else
        {
            TextView parking_label_prices = (TextView)findViewById(R.id.parking_label_prices);
            View separator3 = findViewById(R.id.separator3);

            parking_price_values.setVisibility(View.GONE);
            parking_label_prices.setVisibility(View.GONE);
            separator3.setVisibility(View.GONE);
        }

        if(price!=getString(R.string.label_free)) {
            price = price.format( "%,d", Integer.parseInt(price) );
            parking_price.setText("$"+price);
            parking_label_price.setText(price_label);
        }
        else
        {
            parking_price.setText(price);
        }

        if(parking_comments.length()<=0)
        {
            TextView parking_label_comments = (TextView)findViewById(R.id.parking_label_comments);
            View separator4 = findViewById(R.id.separator4);
            parking_label_comments.setVisibility(View.GONE);
            separator4.setVisibility(View.GONE);
        }

        String url_site = getString(R.string.url_site_images);
        //Log.i("Imagen", url_site + parking.image);
        String image = url_site+parking.image;

        Picasso.with(getApplicationContext()).load(image).into(parking_image);
        GetReview(parking.id);
        //(new GetReviews()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_calificate:
                String data = getIntent().getExtras().getString("data_to_sent");
                Intent intent_name = new Intent();
                intent_name.setClass(getApplicationContext(),CalificateActivity.class);
                intent_name.putExtra("data_to_sent",data);
                startActivity(intent_name);
                return true;

            case R.id.action_share:
                data = getIntent().getExtras().getString("data_to_sent");
                Parking parking = new Parking(data);

                price = getString(R.string.label_free);
                price_label="";

                if (Integer.parseInt(parking.price_standard) > 0) {
                    price = parking.price_standard;
                    price_label = getString(R.string.label_standard);
                    price = price.format( "%,d", Integer.parseInt(price) );
                    price = " $"+price;
                }
                if (Integer.parseInt(parking.price_hour) > 0) {
                    price = parking.price_hour;
                    price_label = getString(R.string.label_hour);
                    price = price.format( "%,d", Integer.parseInt(price) );
                    price = " $"+price;
                }
                if (Integer.parseInt(parking.price_minute) > 0) {
                    price =  parking.price_minute;
                    price_label =  getString(R.string.label_minute);
                    price = price.format( "%,d", Integer.parseInt(price) );
                    price = " $"+price;
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Parqueadero en "+parking.address+" - "+price+" "+price_label);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Motoparking.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void GetReview(String id)
    {
    class GetReviews extends AsyncTask<String,Void,ArrayList<Review>>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            super.onPostExecute(reviews);
            ListView parking_reviews = ((ListView) findViewById(R.id.parking_reviews));

            parking_loading = ((ProgressBar) findViewById(R.id.parking_loading));
            parking_loading.setVisibility(View.GONE);
            if(reviews.isEmpty()) {
                TextView parking_label_reviews = ((TextView) findViewById(R.id.parking_label_reviews));
                View separator5 = findViewById(R.id.separator5);
                parking_label_reviews.setVisibility(View.GONE);
                separator5.setVisibility(View.GONE);
            }
            else
            {
                ReviewsAdapter adapter = new ReviewsAdapter(DetailActivity.this, reviews);
                parking_reviews.setAdapter(adapter);
                setListViewHeightBasedOnChildren(parking_reviews);
            }
        }

        @Override
        protected ArrayList<Review> doInBackground(String... params) {
            ReviewController reviewController = new ReviewController(DetailActivity.this);
            ArrayList<Review> reviews = reviewController.getReviews(params[0]);
            return reviews;
        }
    }

        GetReviews getReviews = new GetReviews();
        getReviews.execute(id);

    }

    public class ReviewsAdapter extends ArrayAdapter<Review> {
        private final Context context;
        private final ArrayList<Review> items;

        public ReviewsAdapter(Context context, ArrayList<Review> items) {
            super(context, R.layout.parking_review, items);
            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.parking_review, parent, false);
            TextView item_parking_review = (TextView) rowView.findViewById(R.id.item_parking_review);
            TextView item_parking_title = (TextView) rowView.findViewById(R.id.title_review);
            TextView item_parking_date = (TextView) rowView.findViewById(R.id.item_parking_date);
            ImageView item_parking_image = (ImageView) rowView.findViewById(R.id.item_parking_image);

            Review item = getItem(position);
            if (item!= null) {

                switch(item.calification) {
                    case "1":
                        item_parking_image.setImageResource(R.drawable.star);
                        break;
                    case "2":
                        item_parking_image.setImageResource(R.drawable.star_2);
                        break;
                    case "3":
                        item_parking_image.setImageResource(R.drawable.star_3);
                        break;
                    case "4":
                        item_parking_image.setImageResource(R.drawable.star_4);
                        break;
                    case "5":
                        item_parking_image.setImageResource(R.drawable.star_5);
                        break;
                    default:
                        item_parking_image.setImageResource(R.drawable.star);
                        break;
                }
                item_parking_title.setText(item.author);
                item_parking_review.setText(item.review);
                item_parking_date.setText(item.date);
            }

            return rowView;
        }
    }

}
