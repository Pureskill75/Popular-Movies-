package example.android.popularmoviesvolley;

class Constants {


    private static final String API_KEY = BuildConfig.ApiKey;

    static final String POPULAR_URL = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
    static final String TOP_RATED_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
    static final String BASE_URL = "http://api.themoviedb.org/3/movie/299534/";
    static final String EXTRA_URL = "poster_path";
    static final String TITLE_TEXT = "title";
    static final String OVERVIEW_TEXT = "overview";
    static final String RELEASE = "release_date";
    static final String VOTE_AVERAGE = "vote_average";
    static final String MOVIE_ID = "id";
    static final String COMPLETE_URL = "http://api.themoviedb.org/3/movie/299534/videos?api_key=030fd31f68fb1124416af6b9240b2f31";
    static final String VIDEO_ENDPOINT = "videos?api_key=030fd31f68fb1124416af6b9240b2f31";
}
