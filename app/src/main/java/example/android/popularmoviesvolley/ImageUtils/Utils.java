package example.android.popularmoviesvolley.ImageUtils;


public class Utils {

//Methods to help sort through Image sizes
    private static final String API_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String API_POSTER_SIZE_ORIGINAL = "original";
    private static final String API_POSTER_SIZE_W92 = "w92";
    private static final String API_POSTER_SIZE_W154 = "w154";
    private static final String API_POSTER_SIZE_W185 = "w185";
    private static final String API_POSTER_SIZE_W342 = "w342";
    private static final String API_POSTER_SIZE_W500 = "w500";
    private static final String API_POSTER_SIZE_W780 = "w780";

    public static String buildPosterUrl(String suffix) {
        return buildPosterUrl(suffix, API_POSTER_SIZE_ORIGINAL);
    }

    private static String buildPosterUrl(String suffix, String size) {
        return API_POSTER_BASE_URL + size + suffix;
    }

    public static String buildPosterUrl(String suffix, int minWidth) {
        String size;
        if (minWidth <= 92) {
            size = API_POSTER_SIZE_W92;
        } else if (minWidth <= 154) {
            size = API_POSTER_SIZE_W154;
        } else if (minWidth <= 185) {
            size = API_POSTER_SIZE_W185;
        } else if (minWidth <= 342) {
            size = API_POSTER_SIZE_W342;
        } else if (minWidth <= 500) {
            size = API_POSTER_SIZE_W500;
        } else if (minWidth <= 780) {
            size = API_POSTER_SIZE_W780;
        } else {
            size = API_POSTER_SIZE_ORIGINAL;
        }
        return buildPosterUrl(suffix, size);
    }

}
