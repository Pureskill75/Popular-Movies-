package example.android.popularmoviesvolley;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import static example.android.popularmoviesvolley.Constants.COMPLETE_URL;
import static example.android.popularmoviesvolley.Constants.MOVIE_ID;


public class DetailActivity extends AppCompatActivity implements TrailerClickListener {



    private TrailerAdapter trailerAdapter;
    private ArrayList<TrailerRequest> mTrailerList;
    private final String KEY_NAME = "name";
    private final String KEY_URL = "key";


    RecyclerView trailerRecyclerView;
    private MovieDatabase movieDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_actvity);

        ImageView imageView = findViewById(R.id.image_iv);
        ImageView mFavourites = findViewById(R.id.fav_image_view);
        ImageView mPlayTrailer = findViewById(R.id.trailer_play_iv);
        ImageView mReadReviews = findViewById(R.id.review_image_view);
        TextView mTrailerTitle = findViewById(R.id.trailer_title);


        mTrailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this);
        trailerRecyclerView = findViewById(R.id.trailers_recycler_view);
        trailerRecyclerView.setAdapter(trailerAdapter);
        trailerAdapter.getTrailerList(mTrailerList);

        //Instance of database
        movieDatabase = MovieDatabase.getInstance(getApplicationContext());

        mTrailerList = new ArrayList<>();

        extractTrailer();

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


        //An instance of the Movie object
        final Movies movies = new Movies(movieId, posterUrl, title, overview, releaseDate, voteAverage);


        final TrailerRequest trailerRequest = new TrailerRequest(KEY_NAME, KEY_URL);
        mTrailerTitle.setText(trailerRequest.getmName());



        mFavourites.setOnClickListener(v -> {
            addToFavourites(movies);
            Toast.makeText(DetailActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
        });


        mPlayTrailer.setOnClickListener(v -> playTrailer(trailerRequest));

        mReadReviews.setOnClickListener(v ->
                openReviews());
    }


    private void extractTrailer() {

//        Intent intent = getIntent();
//        String movieId = intent.getStringExtra(MOVIE_ID);
//
//        //https://api.themoviedb.org/3/movie/157336/videos?api_key=###
//
//        Uri.Builder builder = new Uri.Builder();
//        builder.scheme("http")
//                .authority("api.themoviedb.org")
//                .appendPath("3")
//                .appendPath("movie")
//                .appendPath(movieId)
//                .appendPath("videos")
//                .appendQueryParameter("api_key", BuildConfig.ApiKey);
//
//        String url = builder.build().toString();




        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,COMPLETE_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            mTrailerList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject results = jsonArray.getJSONObject(i);
                                //Get json data as strings
                                String movieKey = results.optString("key");
                                String movieName = results.optString("name");

                                mTrailerList.add(new TrailerRequest( movieKey, movieName));

                            }
                            trailerAdapter = new TrailerAdapter(DetailActivity.this);
                            trailerRecyclerView.setAdapter(trailerAdapter);
                            trailerAdapter.notifyDataSetChanged();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTrailerList = null;
                error.printStackTrace();
            }
        });
        MainActivity.mRequestQueue.add(request);

    }


    private void playTrailer(TrailerRequest trailerRequest) {

        final String YOU_TUBE_WEB_URL = "http://www.youtube.com/watch?v=";
        final String YOU_TUBE_APP_URL = "vnd.youtube:";

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOU_TUBE_APP_URL + trailerRequest.getmKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOU_TUBE_WEB_URL + trailerRequest.getmKey()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            startActivity(webIntent);
        }

    }


    //add movie to favourite list/ room database
    private void addToFavourites(final Movies movies) {
        AppExecutors.getInstance().diskIO().execute(() -> movieDatabase.movieDao().insertMovie(new Movies[]{movies}));
    }


    private void openReviews (){
        Intent reviewsIntent = new Intent(this, reviews.class);
        startActivity(reviewsIntent);
    }

    @Override
    public void onTrailerClicked(TrailerRequest trailerRequest) {

    }
}
