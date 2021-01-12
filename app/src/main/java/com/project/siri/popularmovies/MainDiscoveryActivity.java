package com.project.siri.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Scanner;

public class MainDiscoveryActivity extends AppCompatActivity implements ImageAdapter.ListItemClickListener {


    List<String> listImages;
    ImageView imageView1;
    static String list_type = "popular";
    static String getWhat = "movies";
    int num_list_items = 20;
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    JSONArray results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery);
        listImages = new ArrayList<String>();
        getMovies(list_type); //Default
    }


    public void getMovies(String sort_setting)
    {
        if(isConnectedToInternet())
        {
            getWhat = "movies";
            String BASE_URL = "http://api.themoviedb.org/3/movie/";
            String API_KEY = getString(R.string.movie_db_api_key);
            Uri builtUri = Uri.parse(BASE_URL + sort_setting + "?api_key=" + API_KEY);
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

    public void getTrailers(String id)
    {
        if(isConnectedToInternet())
        {
            getWhat = "trailers";
            String BASE_URL = "http://api.themoviedb.org/3/movie/";
            String API_KEY = getString(R.string.movie_db_api_key);
            Uri builtUri = Uri.parse(BASE_URL + id + "/videos?api_key=" + API_KEY);
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
            if(getWhat.equals("movies")) {
                parseJSON(s);
            }
            else if(getWhat.equals("trailers"))
            {
                parseTrailers(s);
            }
        }
    }

    private String parseTrailers(String s) {

        return s;
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

    private void parseJSON(String s) {
        listImages.clear();
        results = null;
        try {
            JSONObject jsonObject = new JSONObject(s);
            results = jsonObject.getJSONArray("results");
            //Log.d("Results", results.toString());
            for(int i=0;i<results.length();i++)
            {
                JSONObject movie = (JSONObject) results.get(i);
                Log.d("MOVIES", movie.getString("poster_path"));
                String imgBaseUrl = "http://image.tmdb.org/t/p/w185/";
                listImages.add(imgBaseUrl + movie.getString("poster_path"));
                Log.d("Images",imgBaseUrl + movie.getString("poster_path"));
            }

            recyclerView = (RecyclerView)findViewById(R.id.img_recycler);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(),2);
            gridLayoutManager.setAutoMeasureEnabled(true);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setHasFixedSize(true);
            imageAdapter = new ImageAdapter(getBaseContext(),listImages,num_list_items,this);
            imageAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(imageAdapter);
            //Picasso.with(getBaseContext()).load(imgBaseUrl+movie.getString("poster_path")).into (imageView1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(getBaseContext(),String.valueOf(position),Toast.LENGTH_LONG).show();

        if(list_type.equals("favourite"))
        {
            Cursor cr = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,null,null,null,null);
            cr.moveToPosition(position);
            Intent intent = new Intent(MainDiscoveryActivity.this, DetailsViewActivity.class);
            Bundle bundle = new Bundle();
           // bundle.putString("_id",cr.getString(0));
            bundle.putString("id", cr.getString(1));
            bundle.putString("original_title", cr.getString(2));
            bundle.putString("img_url", cr.getString(6));
            bundle.putString("overview", cr.getString(3));
            bundle.putString("vote_average", cr.getString(5));
            bundle.putString("release_date", cr.getString(4));
            intent.putExtra("movie_bundle", bundle);
            startActivity(intent);
            cr.close();
        }

        else {
            try {
                JSONObject movie = (JSONObject) results.get(position);

                String imgBaseUrl = "http://image.tmdb.org/t/p/w185/";
                String imgUrl = imgBaseUrl + movie.optString("poster_path", "fall_back");

                Intent intent = new Intent(MainDiscoveryActivity.this, DetailsViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", movie.optString("id", "fallback"));
                bundle.putString("original_title", movie.optString("original_title", "fall_back"));
                bundle.putString("img_url", imgUrl);
                bundle.putString("overview", movie.optString("overview", "fallback"));
                bundle.putString("vote_average", movie.optString("vote_average", "fallback"));
                bundle.putString("release_date", movie.optString("release_date", "fallback"));
                intent.putExtra("movie_bundle", bundle);
                startActivity(intent);
                //Toast.makeText(getBaseContext(),original_title+" "+imgUrl+" "+overview+" "+vote_average+" "+release_date,Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.d("OnItemClick", e.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.popular:
                getMovies("popular");
                Log.d("popular","popular");
                list_type = "popular";
                break;
            case R.id.top_rated:
                getMovies("top_rated");
                list_type = "top_rated";
                break;
            case R.id.favourite:
                getFavMovies();
                list_type = "favourite";
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFavMovies() {

        //0 -- _id
        //1 -- title
        //2 -- synopsis
        //3 -- release_date
        //4 -- rating
        //5 -- url

        listImages.clear();

        Cursor cr = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,null,null,null,null);
        cr.moveToFirst();
        while(!cr.isAfterLast())
        {
            listImages.add(cr.getString(6));
            Log.d("Images",cr.getString(6));

            cr.moveToNext();
        }
        cr.close();

        recyclerView = (RecyclerView)findViewById(R.id.img_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getBaseContext(),2);
        gridLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        imageAdapter = new ImageAdapter(getBaseContext(),listImages,listImages.size(),this);
        imageAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(imageAdapter);

        if(listImages.size()==0)
            Toast.makeText(getBaseContext(),"NO Favourite Movies",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        Log.d("abc","Resumed");
        if(list_type.equals("favourite"))
            getFavMovies();
        super.onResume();
    }
}
