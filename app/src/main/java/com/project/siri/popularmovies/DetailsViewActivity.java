package com.project.siri.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.siri.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DetailsViewActivity extends AppCompatActivity {

    String movieId ;
    TextView tvTitle, tvSynopsis, tvRating, tvReleaseDate, tvReviewsTxt;
    Button btnFavourite, btnShare;
    ImageView imgPoster;
    ListView lvTrailers;
    static String getWhat = "trailers";
    RecyclerView recyclerView;
    static HashMap<String,String> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view);

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvSynopsis = (TextView)findViewById(R.id.tvSynopsisTxt);
        tvRating = (TextView)findViewById(R.id.tvRatingTxt);
        tvReleaseDate = (TextView)findViewById(R.id.tvReleaseDateTxt);
        imgPoster = (ImageView)findViewById(R.id.imgPoster);
        lvTrailers = (ListView)findViewById(R.id.lvTrailers);
        tvReviewsTxt = (TextView) findViewById(R.id.btnReviewsTxt);
        btnFavourite = (Button)findViewById(R.id.btnFavourite);
        btnShare = (Button) findViewById(R.id.btnShare);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, "Watch the Trailer!: "+ lvTrailers.getItemAtPosition(0).toString());
                share.putExtra(Intent.EXTRA_TEXT,"https://www.youtube.com/watch?v="+map.get(lvTrailers.getItemAtPosition(0)).toString() );

                startActivity(Intent.createChooser(share, "Share link!"));
            }
        });


        lvTrailers.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        Bundle bundle = getIntent().getBundleExtra("movie_bundle");
        tvTitle.setText(bundle.getString("original_title"));
        tvSynopsis.setText(bundle.getString("overview"));
        tvRating.setText(bundle.getString("vote_average"));
        tvReleaseDate.setText(bundle.getString("release_date"));
        Picasso.with(getBaseContext()).load(bundle.getString("img_url")).into (imgPoster);

        movieId = bundle.getString("id");

        getTrailersReviews(movieId,"trailers");


        ArrayList<String> favMovies = getFavMovies();
        updateFavPic(favMovies);

        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(favMovies.contains(movieId))
                {
                    //btnFavourite.setBackground(getResources().getDrawable(R.drawable.unfav));
                    int x = getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,MoviesContract.MovieEntry.COLUMN_TITLE + " =? ",new String[]{tvTitle.getText().toString()});
                    if(x>0)
                    {
                        Toast.makeText(getBaseContext(),"Removed from Favourites", Toast.LENGTH_LONG).show();
                        btnFavourite.setBackground(getResources().getDrawable(R.drawable.unfav));
                    }
                    //finish();
                }
                else
                {
                    //btnFavourite.setBackground(getResources().getDrawable(R.drawable.fav));
                    ContentValues cv = new ContentValues();
                    cv.put(MoviesContract.MovieEntry.COLUMN_MOVIEID,movieId);
                    cv.put(MoviesContract.MovieEntry.COLUMN_TITLE,tvTitle.getText().toString());
                    cv.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS,tvSynopsis.getText().toString());
                    cv.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,tvReleaseDate.getText().toString());
                    cv.put(MoviesContract.MovieEntry.COLUMN_RATING,tvRating.getText().toString());
                    cv.put(MoviesContract.MovieEntry.COLUMN_IMG_URL,bundle.getString("img_url"));
                    Uri uri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI,cv);
                    if(uri!=null)
                    {
                        //Toast.makeText(getBaseContext(),uri.toString(),Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(),"Added to Favourites", Toast.LENGTH_LONG).show();
                        btnFavourite.setBackground(getResources().getDrawable(R.drawable.fav));

                    }
                    //finish();
                }



            }
        });



        //imgPoster.setBackground(getResources().getDrawable(getResources().getIdentifier("@drawable/imgalbum",null,getPackageName())));
    }


    public ArrayList<String> getFavMovies()
    {
        ArrayList<String> favMovies1 = new ArrayList<String>();
        Cursor cr = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,null,null,null,null);
        cr.moveToFirst();
        while(!cr.isAfterLast())
        {
            favMovies1.add(cr.getString(1));
            cr.moveToNext();
        }
        cr.close();
        return favMovies1;
    }

    private void updateFavPic(ArrayList<String> favMovies2) {


        if(favMovies2.contains(movieId)) {
            btnFavourite.setBackground(getResources().getDrawable(R.drawable.fav));
        }
        else
        {
            btnFavourite.setBackground(getResources().getDrawable(R.drawable.unfav));
        }

    }

    public void getTrailersReviews(String id, String what)
    {
        if(isConnectedToInternet())
        {
            getWhat = what;
            String what_url_key;
            if(what.equals("trailers"))
                what_url_key = "videos";
            else
                what_url_key = "reviews";

            String BASE_URL = "http://api.themoviedb.org/3/movie/";
            String API_KEY = getString(R.string.movie_db_api_key);

             Uri builtUri = Uri.parse(BASE_URL + id + "/"+what_url_key+"?api_key=" + API_KEY);
            try {
                URL url = new URL(builtUri.toString());
                new tmdbQuery().execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getBaseContext(),"Sorry! No Internet Connection",Toast.LENGTH_LONG).show();


    }

    public boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public class tmdbQuery extends AsyncTask<URL, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            String s = getMoviesList(urls[0]);
            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(getWhat.equals("trailers"))
            {
                parseTrailers(s);
                getTrailersReviews(movieId,"reviews");

            }
            else if(getWhat.equals("reviews"))
            {
               /* Intent in = new Intent(DetailsViewActivity.this,ReviewActivity.class);
                in.putExtra("reviews",s);
                startActivity(in);*/

                parseReviews(s);

            }

        }
    }



    private void parseTrailers(String s) {

        JSONArray results;
        final ArrayList<String> list = new ArrayList<>();
         map = new HashMap<String, String>();

        try {
            JSONObject jsonObject = new JSONObject(s);
            results = jsonObject.getJSONArray("results");
            //Log.d("Results", results.toString());
            for (int i = 0; i < results.length(); i++) {
                JSONObject trailerInfo = (JSONObject) results.get(i);
                Log.d("tinfo",trailerInfo.toString());
                map.put(trailerInfo.getString("name"),trailerInfo.getString("key"));
                list.add(trailerInfo.getString("name"));
            }

            final ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, list);
            lvTrailers.setAdapter(adapter);
            lvTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    playTrailer(map.get(lvTrailers.getItemAtPosition(position)).toString());
                }
            });

           // playTrailer(results.get(0).toString());
        }
        catch (Exception e)
        {
            Log.e("Parsing trailers", e.toString());
        }
    }



    public String getMoviesList(URL url)
    {

        String s="";
        try {
            s = getResponseFromHttpUrl(url);
            return s;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s;
    }

    public String getResponseFromHttpUrl(java.net.URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = httpURLConnection.getInputStream();
            Scanner sc = new Scanner(in);
            sc.useDelimiter("\\A");
            boolean hasInput = sc.hasNext();
            if(hasInput)
                return sc.next();
            else
                return null;
        }
        finally {
            httpURLConnection.disconnect();
        }

    }


    public void playTrailer(String key)
    {
        String url = "https://www.youtube.com/watch?v="+key;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }


    private void parseReviews(String s) {
        JSONArray results;
        final ArrayList<Reviews> list = new ArrayList<Reviews>();

        final ArrayList<String> list_authors = new ArrayList<String>();
        final ArrayList<String> list_content = new ArrayList<String>();
        final ArrayList<String> list_url = new ArrayList<String>();


        try {
            JSONObject jsonObject = new JSONObject(s);
            results = jsonObject.getJSONArray("results");
            //Log.d("Results", results.toString());
            for (int i = 0; i < results.length(); i++) {
                JSONObject reviewInfo = (JSONObject) results.get(i);
                Log.d("tinfo",reviewInfo.toString());
                list.add(new Reviews(reviewInfo.getString("author"),
                        reviewInfo.getString("content"),
                        reviewInfo.getString("url"))
                );
               /* list_authors.add(reviewInfo.getString("author"));
                list_content.add(reviewInfo.getString("content"));
                list_url.add(reviewInfo.getString("url"));*/
                Log.e("details view act",reviewInfo.getString("author"));
            }

            recyclerView = (RecyclerView)findViewById(R.id.review_recycler);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(),1);
            gridLayoutManager.setAutoMeasureEnabled(true);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setHasFixedSize(true);

            final ReviewsAdapter adapter = new ReviewsAdapter(this,list);
            recyclerView.setAdapter(adapter);
            // playTrailer(results.get(0).toString());
        }
        catch (Exception e)
        {
            Log.e("Parsing reviews", e.toString());
        }
    }





}
