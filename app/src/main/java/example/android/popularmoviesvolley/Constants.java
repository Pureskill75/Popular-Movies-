package example.android.popularmoviesvolley;

public class Constants {


    private static final String API_KEY = BuildConfig.ApiKey;

    public static final String POPULAR_URL = "http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
    public static final String TOP_RATED_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key="+ API_KEY ;
    public static final String EXTRA_URL = "poster_path";
    public static final String TITLE_TEXT = "title";
    public static final String OVERVIEW_TEXT = "overview";
    public static final String RELEASE = "release_date";
    public static final String VOTE_AVERAGE = "vote_average";
    public static String MOVIE_ID = "id";
}
