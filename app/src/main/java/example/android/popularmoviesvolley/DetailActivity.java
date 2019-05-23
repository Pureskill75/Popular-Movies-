package example.android.popularmoviesvolley;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.squareup.picasso.Picasso;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import example.android.popularmoviesvolley.ImageUtils.Utils;
import example.android.popularmoviesvolley.Room.AppExecutors;
import example.android.popularmoviesvolley.Room.MovieDatabase;

import static example.android.popularmoviesvolley.Constants.MOVIE_ID;


public class DetailActivity extends AppCompatActivity implements TrailerClickListener {


    private TrailerAdapter trailerAdapter;
    private ReviewsAdapter reviewsAdapter;
    private ArrayList<TrailerRequest> mTrailerList;
    private ArrayList<ReviewsRequest> mReviewsList;
    RecyclerView trailerRecyclerView;
    RecyclerView reviewsRecyclerView;

    private MovieDatabase movieDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_actvity);

        ImageView imageView = findViewById(R.id.image_iv);
        ImageView mFavourites = findViewById(R.id.fav_image_view);
        TextView mTrailerTitle = findViewById(R.id.trailer_title);


        mTrailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this);
        trailerRecyclerView = findViewById(R.id.trailers_recycler_view);
        trailerRecyclerView.setAdapter(trailerAdapter);
        trailerRecyclerView.setHasFixedSize(true);
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));


        mReviewsList = new ArrayList<>();
        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        reviewsAdapter = new ReviewsAdapter();
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));


        //Instance of database
        movieDatabase = MovieDatabase.getInstance(getApplicationContext());


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
        mTrailerTitle.setText(title);


        //An instance of the Movie object
        final Movies movies = new Movies(movieId, posterUrl, title, overview, releaseDate, voteAverage);

        //Adding favourites into room database
        mFavourites.setOnClickListener(v -> {
            addToFavourites(movies);
            Toast.makeText(DetailActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
        });

    }


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
                new Response.Listener<JSONObject>() {

                    ImageView mPlayTrailer = findViewById(R.id.trailer_play_image);

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            mTrailerList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject results = jsonArray.getJSONObject(i);
                                //Get json data as strings
                                String movie_key = results.optString("key");
                                String movie_name = results.optString("name");

                                mTrailerList.add(new TrailerRequest(movie_key, movie_name));


                                mPlayTrailer.setOnClickListener(v -> {

                                    final String YOU_TUBE_WEB_URL = "http://www.youtube.com/watch?v=";
                                    final String YOU_TUBE_APP_URL = "vnd.youtube:";
                                    trailerAdapter.getTrailerList(mTrailerList);

                                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOU_TUBE_APP_URL + movie_key));
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOU_TUBE_WEB_URL + movie_key));
                                    try {
                                        startActivity(appIntent);
                                    } catch (ActivityNotFoundException e) {
                                        startActivity(webIntent);
                                    }

                                });

                            }
                            trailerAdapter = new TrailerAdapter(DetailActivity.this);
                            trailerAdapter.getTrailerList(mTrailerList);
                            trailerRecyclerView.setAdapter(trailerAdapter);
                            trailerAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, error -> {
            mTrailerList = null;
            error.printStackTrace();
        });
        MainActivity.mRequestQueue.add(request);

    }


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

                        mReviewsList.clear();

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
            mReviewsList = null;
            error.printStackTrace();
        });

        MainActivity.mRequestQueue.add(request);

    }


    //add movie to favourite list/ room database
    private void addToFavourites(final Movies movies) {
        AppExecutors.getInstance().diskIO().execute(() -> movieDatabase.movieDao().insertMovie(new Movies[]{movies}));
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

}