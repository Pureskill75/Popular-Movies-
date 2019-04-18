package example.android.popularmoviesvolley;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static example.android.popularmoviesvolley.Constants.EXTRA_URL;
import static example.android.popularmoviesvolley.Constants.MOVIE_ID;
import static example.android.popularmoviesvolley.Constants.OVERVIEW_TEXT;
import static example.android.popularmoviesvolley.Constants.RELEASE;
import static example.android.popularmoviesvolley.Constants.TITLE_TEXT;
import static example.android.popularmoviesvolley.Constants.VOTE_AVERAGE;


public class MainActivity extends AppCompatActivity implements MyMoviesAdapter.OnItemClickListener {


    private static final String PRIMARY_KEY = "";
    RecyclerView mRecyclerView;
    private MyMoviesAdapter myMoviesAdapter;
    private ArrayList<Movies> mMoviesList;
    private RequestQueue mRequestQueue;


    private final String SORT_KEY = "SORT_KEY";
    private final String LIST_STATE = "LIST_STATE";
    private String sortOrder = Constants.POPULAR_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(myMoviesAdapter);

        mMoviesList = new ArrayList<>();

        if (savedInstanceState != null) {
            sortOrder = savedInstanceState.getString(SORT_KEY);
            mRecyclerView.scrollToPosition(savedInstanceState.getInt(LIST_STATE));
        }

        mRequestQueue = Volley.newRequestQueue(this);
        parseMovieJSON(Constants.POPULAR_URL);


    }

    // parse Json using volley to make network call
    private String parseMovieJSON(final String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            mMoviesList.clear();


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject movie = jsonArray.getJSONObject(i);

                                //Get json data as strings
                                //int id = movie.optInt("id");
                                String movie_id = movie.optString("id");
                                String posterPath = movie.optString("poster_path");
                                String originalTitle = movie.optString("title");
                                String overview = movie.getString("overview");
                                String releaseDate = movie.optString("release_date");
                                String voteAverage = movie.optString("vote_average");


                                mMoviesList.add(new Movies(movie_id, posterPath, originalTitle, overview, releaseDate, voteAverage));

                            }


                            myMoviesAdapter = new MyMoviesAdapter(MainActivity.this, mMoviesList);
                            myMoviesAdapter.setOnItemClickListener(MainActivity.this);
                            mRecyclerView.setAdapter(myMoviesAdapter);
                            myMoviesAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError instanceof NetworkError) {

                    if (checkConnection()) {


                        parseMovieJSON(url);


                    } else if (!checkConnection()) {

                        Toast.makeText(MainActivity.this, "Check Network Connection", Toast.LENGTH_LONG).show();
                        mMoviesList = null;

                    }


                }

            }
        });

        mRequestQueue.add(request);
        checkConnection();

        return url;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.most_popular:
                setTitle("Most Popular");
                // return Most Popular Movies list from API
                sortOrder = parseMovieJSON(Constants.POPULAR_URL);
                break;

            case R.id.top_rated:
                setTitle("Top Rated");
                // return Top Rated Movies list from API
                sortOrder = parseMovieJSON(Constants.TOP_RATED_URL);

                break;

            case R.id.Fav_list:
                //return list of my favourite movies
                setTitle("My Favourite Movies");
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORT_KEY, sortOrder);
        outState.putInt(LIST_STATE, ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int position = savedInstanceState.getInt(LIST_STATE);
        mRecyclerView.scrollToPosition(position);
    }

    //Detail intents to throw data to Movie Detail activity for displaying.
    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        Movies clickedItem = mMoviesList.get(position);

        detailIntent.putExtra(EXTRA_URL, clickedItem.getPosterPath());
        detailIntent.putExtra(TITLE_TEXT, clickedItem.getOriginalTitle());
        detailIntent.putExtra(OVERVIEW_TEXT, clickedItem.getOverview());
        detailIntent.putExtra(RELEASE, clickedItem.getReleaseDate());
        detailIntent.putExtra(VOTE_AVERAGE, clickedItem.getVoteAverage());
        detailIntent.putExtra(MOVIE_ID, clickedItem.getMovie_id());



        // Send the intent to launch a Movie detail activity
        startActivity(detailIntent);

    }


    // A reference to the ConnectivityManager to check state of network connectivity mobile and wifi
    private boolean checkConnection() {

        boolean wifiConnected = false;
        boolean mobileDataConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkInfos) {
            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                if (info.isConnected())
                    wifiConnected = true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE"))
                if (info.isConnected())
                    mobileDataConnected = true;

        }
        return wifiConnected || mobileDataConnected;

    }

}
