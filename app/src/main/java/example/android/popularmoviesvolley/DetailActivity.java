package example.android.popularmoviesvolley;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;

import example.android.popularmoviesvolley.ImageUtils.Utils;
import example.android.popularmoviesvolley.Room.AppExecutors;
import example.android.popularmoviesvolley.Room.MovieDatabase;

import static example.android.popularmoviesvolley.Constants.MOVIE_ID;


public class DetailActivity extends AppCompatActivity {


    private static final String KEY_ID = "id";
    private static final String KEY_URL = "key";
    private static final String KEY_NAME = "name";


    private List<TrailerRequest> mTrailerList = new ArrayList<>();

    private MovieDatabase movieDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_actvity);

        ImageView imageView = findViewById(R.id.image_iv);
        ImageView mFavourites = findViewById(R.id.fav_image_view);
        ImageView mPlayTrailer = findViewById(R.id.trailer_image_view);
        ImageView mReadReviews = findViewById(R.id.review_image_view);

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

        TrailerRequest trailerRequest = new TrailerRequest(KEY_ID, KEY_URL, KEY_NAME);

        mFavourites.setOnClickListener(v -> {
            addToFavourites(movies);
            Toast.makeText(DetailActivity.this, "Added to Favourites", Toast.LENGTH_SHORT).show();
        });


        mPlayTrailer.setOnClickListener(v -> playTrailer(trailerRequest));

        mReadReviews.setOnClickListener(v -> Toast.makeText(DetailActivity.this, "Show Reviews", Toast.LENGTH_SHORT).show());

    }


    private void extractTrailer() {

        Intent intent = getIntent();
        String movieId = intent.getStringExtra(MOVIE_ID);

        //https://api.themoviedb.org/3/movie/157336/videos?api_key=###

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

       String url = "http://api.themoviedb.org/3/movie/299534/videos?api_key=030fd31f68fb1124416af6b9240b2f31";



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            mTrailerList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject results = jsonArray.getJSONObject(i);
                                //Get json data as strings
                                String idNumber = results.optString(KEY_ID);
                                String movieKey = results.optString(KEY_URL);
                                String movieName = results.optString(KEY_NAME);

                                mTrailerList.add(new TrailerRequest(idNumber, movieKey, movieName));

                            }

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

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerRequest.getmKey()));
        startActivity(intent);

    }


    //add movie to favourite list/ room database
    private void addToFavourites(final Movies movies) {
        AppExecutors.getInstance().diskIO().execute(() -> movieDatabase.movieDao().insertMovie(new Movies[]{movies}));
    }

}
