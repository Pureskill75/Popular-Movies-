package example.android.popularmoviesvolley;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;


import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import example.android.popularmoviesvolley.Room.AppExecutors;
import example.android.popularmoviesvolley.Room.MovieDatabase;
import example.android.popularmoviesvolley.Room.MovieViewModel;

import static example.android.popularmoviesvolley.Constants.EXTRA_URL;
import static example.android.popularmoviesvolley.Constants.MOVIE_ID;
import static example.android.popularmoviesvolley.Constants.OVERVIEW_TEXT;
import static example.android.popularmoviesvolley.Constants.RELEASE;
import static example.android.popularmoviesvolley.Constants.TITLE_TEXT;
import static example.android.popularmoviesvolley.Constants.VOTE_AVERAGE;


public class MainActivity extends AppCompatActivity implements MyMoviesAdapter.OnItemClickListener, MovieClickListener {

    RecyclerView mRecyclerView;
    private MyMoviesAdapter myMoviesAdapter;
    private FavouritesAdapter favouritesAdapter;
    private ArrayList<Movies> mMoviesList;
    public static RequestQueue mRequestQueue;
    private List<Movies> mFavList = new ArrayList<>();
    private MovieDatabase database;
    private final String SORT_KEY = "SORT_KEY";
    private final String LIST_STATE = "LIST_STATE";
    private String sortOrder = Constants.POPULAR_URL;
    Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mMoviesList = new ArrayList<>();
        myMoviesAdapter = new MyMoviesAdapter(this, mMoviesList);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setAdapter(myMoviesAdapter);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        favouritesAdapter = new FavouritesAdapter(this);
        mRecyclerView.setAdapter(favouritesAdapter);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Instance of database
        database = MovieDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null) {
            sortOrder = savedInstanceState.getString(SORT_KEY);
            mRecyclerView.scrollToPosition(savedInstanceState.getInt(LIST_STATE));
        }

        mRequestQueue = Volley.newRequestQueue(this);
        parseMovieJSON(Constants.POPULAR_URL);

        setupViewModel();



    }


    // parse Json using volley to make network call
    private String parseMovieJSON(final String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url, null,
                response -> {


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
                }, volleyError -> {
            if (volleyError instanceof NetworkError) {

                if (checkConnection()) {


                    parseMovieJSON(url);


                } else if (!checkConnection()) {

                    Toast.makeText(MainActivity.this, "Check Network Connection", Toast.LENGTH_LONG).show();
                    mMoviesList = null;

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
                mRecyclerView.setAdapter(favouritesAdapter);
                break;


            case R.id.delete:

                showDeleteConfirmationDialog();
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViewModel() {
        MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getAllMovies().observe(this, movies -> {
            favouritesAdapter.getFavouriteList(movies);
            mFavList = movies;
        });
    }


    private void deleteFavourites() {



        if (mFavList.size() > 0) {
            // Remove all favourites from DB
            AppExecutors.getInstance().diskIO().execute(() -> database.movieDao().deleteMovie(mFavList));
            Toast.makeText(this, "Movies deleted", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Favourite List is Empty", Toast.LENGTH_SHORT).show();
        }
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


    //Detail intents to throw data to Movie Detail activity for displaying favorites details .
    @Override
    public void onMovieClicked(int position) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        Movies clickedItem = mFavList.get(position);

        detailIntent.putExtra(EXTRA_URL, clickedItem.getPosterPath());
        detailIntent.putExtra(TITLE_TEXT, clickedItem.getOriginalTitle());
        detailIntent.putExtra(OVERVIEW_TEXT, clickedItem.getOverview());
        detailIntent.putExtra(RELEASE, clickedItem.getReleaseDate());
        detailIntent.putExtra(VOTE_AVERAGE, clickedItem.getVoteAverage());
        detailIntent.putExtra(MOVIE_ID, clickedItem.getMovie_id());

        startActivity(detailIntent);
    }


    private void showDeleteConfirmationDialog() {
        /*
        AlertDialog.Builder to confirm deletion of favourites list while
        I find a way to make the Delete Icon appear when user is in favourites activity.
        */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Favourites List?");
        builder.getContext();
        builder.setPositiveButton("Delete", (dialog, id) -> {
            // User clicked the "Delete" button, so delete the pet.
            deleteFavourites();
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mRecyclerView.getAdapter() == favouritesAdapter) {
            mRecyclerView.setAdapter(myMoviesAdapter);
        } else {
            super.onBackPressed();
        }
    }


    // A reference to the ConnectivityManager to check state of network connectivity (Mobile and wifi).
    private boolean checkConnection() {

        boolean wifiConnected = false;
        boolean mobileDataConnected = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for (NetworkInfo info : networkInfo) {
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
