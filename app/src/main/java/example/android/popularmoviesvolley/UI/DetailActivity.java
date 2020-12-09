package example.android.popularmoviesvolley.UI;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import example.android.popularmoviesvolley.Adapters.ReviewsAdapter;
import example.android.popularmoviesvolley.Adapters.TrailerAdapter;
import example.android.popularmoviesvolley.BuildConfig;
import example.android.popularmoviesvolley.Constants;
import example.android.popularmoviesvolley.ImageUtils.Utils;
import example.android.popularmoviesvolley.Model.Movies;
import example.android.popularmoviesvolley.R;
import example.android.popularmoviesvolley.Model.ReviewsRequest;
import example.android.popularmoviesvolley.Room.AppExecutors;
import example.android.popularmoviesvolley.Room.MovieDatabase;
import example.android.popularmoviesvolley.Interface.TrailerClickListener;
import example.android.popularmoviesvolley.Model.TrailerRequest;


import static example.android.popularmoviesvolley.Constants.MOVIE_ID;


public class DetailActivity extends AppCompatActivity implements TrailerClickListener {


    private TrailerAdapter trailerAdapter;
    private ReviewsAdapter reviewsAdapter;
    private ArrayList<TrailerRequest> mTrailerList;
    private ArrayList<ReviewsRequest> mReviewsList;
    RecyclerView trailerRecyclerView;
    RecyclerView reviewsRecyclerView;
    private static final int DEFAULT_MOVIE_ID = 0;
    private int movieID;


    private MovieDatabase movieDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_actvity);

        ImageView imageView = findViewById(R.id.image_iv);
        ImageView mFavourites = findViewById(R.id.fav_image_view);

        //Trailers RecyclerView and adapter setup
        mTrailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this);
        trailerRecyclerView = findViewById(R.id.trailers_recycler_view);
        trailerRecyclerView.setAdapter(trailerAdapter);
        trailerRecyclerView.setHasFixedSize(true);
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        //reviews RecyclerView and adapter setup
        mReviewsList = new ArrayList<>();
        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        reviewsAdapter = new ReviewsAdapter();
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));


        extractTrailer();

        extractReviews();


        MainActivity.mRequestQueue = Volley.newRequestQueue(this);

        /*
        String intents for catching the data (String constants) from Main activity
        for displaying data in the detail activity
        */
        Intent intent = getIntent();
        String posterUrl = intent.getStringExtra(Constants.EXTRA_URL);
        String title = intent.getStringExtra(Constants.TITLE_TEXT);
        String overview = intent.getStringExtra(Constants.OVERVIEW_TEXT);
        String releaseDate = intent.getStringExtra(Constants.RELEASE);
        String voteAverage = intent.getStringExtra(Constants.VOTE_AVERAGE);
        String movieId = intent.getStringExtra(MOVIE_ID);


        // Detail activity title TextView
        TextView textView = findViewById(R.id.title_text_view);
        // Detail activity OverView TextView
        TextView overViewText = findViewById(R.id.plot_synopsis_text_view);
        // Detail activity ReleaseDate TextView
        TextView releaseTextView = findViewById(R.id.release_date_text_view);
        // Detail activity user ratings TextView
        TextView voteAverageTextView = findViewById(R.id.user_rating_text_view);

        //temporary display of movie id number
        TextView movieIdTextView = findViewById(R.id.movie_id_tv);

        // Load Detail activity ImageView using Picasso
        Picasso.get()
                .load(Utils.buildPosterUrl(posterUrl))
                .fit()
                .centerInside()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.sample_7)
                .into(imageView);

        // set data on to views
        textView.setText(title);
        overViewText.setText(overview);
        releaseTextView.setText(String.format(getString(R.string.release_date), releaseDate));
        voteAverageTextView.setText(String.format(getString(R.string.user_rating_tv), voteAverage));
        movieIdTextView.setText(movieId);
        movieIdTextView.setVisibility(View.GONE);


        //An instance of the Movie object
        final Movies movies = new Movies(movieId, posterUrl, title, overview, releaseDate, voteAverage);

        //Instance of database
        movieDatabase = MovieDatabase.getInstance(getApplicationContext());

        AppExecutors.getInstance().diskIO().execute(() -> {

            Movies movie = movieDatabase.movieDao().loadMovieById(Integer.parseInt(movies.getMovie_id()));

            if (movie != null) {
                movieID = Integer.parseInt(String.valueOf(movies.getMovie_id()));

            }
        });

        //Adding favourites into room database
        mFavourites.setOnClickListener(v -> {

            if (movieID == DEFAULT_MOVIE_ID) {
                movieID = Integer.parseInt(movies.getMovie_id());
                addToFavourites(movies);
                Toast.makeText(DetailActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();


            } else {
                movieID = DEFAULT_MOVIE_ID;
                removeFromFavs(movies);
                Toast.makeText(DetailActivity.this, "Removed From Favourites", Toast.LENGTH_SHORT).show();
            }

        });

    }

    //trailer Endpoint Extraction
    private void extractTrailer() {


        //https://api.themoviedb.org/3/movie/157336/videos?api_key=###

        Intent intent = getIntent();
        String movieId = intent.getStringExtra(MOVIE_ID);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter("api_key", BuildConfig.ApiKey);

        String url = builder.build().toString();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject results = jsonArray.getJSONObject(i);
                            //Get json data as strings
                            String movie_key = results.optString("key");
                            String movie_name = results.optString("name");

                            mTrailerList.add(new TrailerRequest(movie_key, movie_name));

                        }
                        trailerAdapter = new TrailerAdapter(DetailActivity.this);
                        trailerAdapter.getTrailerList(mTrailerList);
                        trailerRecyclerView.setAdapter(trailerAdapter);
                        trailerAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

            if (checkConnection()) {
                extractTrailer();
            } else if (!checkConnection()) {
                Toast.makeText(DetailActivity.this, "Check Network Connection", Toast.LENGTH_LONG).show();
                mReviewsList = null;
            }

        });
        MainActivity.mRequestQueue.add(request);

    }

    //trailer reviews Endpoint Extraction
    private void extractReviews() {

        //https://api.themoviedb.org/3/movie/244786/reviews?api_key=###

        Intent intent = getIntent();
        String movieId = intent.getStringExtra(MOVIE_ID);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter("api_key", BuildConfig.ApiKey);

        String url = builder.build().toString();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("results");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject results = jsonArray.getJSONObject(i);
                            //Get json data as strings
                            String review_author = results.optString("author");
                            String review_content = results.optString("content");

                            mReviewsList.add(new ReviewsRequest(review_author, review_content));

                        }

                        reviewsAdapter = new ReviewsAdapter();
                        reviewsAdapter.getReviewList(mReviewsList);
                        reviewsRecyclerView.setAdapter(reviewsAdapter);
                        reviewsAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

            if (checkConnection()) {
                extractReviews();
            } else if (!checkConnection()) {
                Toast.makeText(DetailActivity.this, "Check Network Connection", Toast.LENGTH_LONG).show();
                mReviewsList = null;
            }
        });

        MainActivity.mRequestQueue.add(request);

    }


    //add movie to favourite list/room database
    private void addToFavourites(final Movies movies) {

        AppExecutors.getInstance().diskIO().execute(() -> movieDatabase.movieDao().insertMovie(movies));

    }


    //delete movie from favourite list/room database
    private void removeFromFavs(final Movies movies) {

        AppExecutors.getInstance().diskIO().execute(() -> movieDatabase.movieDao().deleteMovie(movies));

    }

    @Override
    public void onTrailerClicked(TrailerRequest trailerRequest) {

        trailerAdapter.getTrailerList(mTrailerList);

        final String YOU_TUBE_WEB_URL = "http://www.youtube.com/watch?v=";
        final String YOU_TUBE_APP_URL = "vnd.youtube:";

        String key = trailerRequest.getKey();

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOU_TUBE_APP_URL + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOU_TUBE_WEB_URL + key));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(webIntent);
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