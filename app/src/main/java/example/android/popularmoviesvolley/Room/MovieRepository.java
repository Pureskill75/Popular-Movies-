//package example.android.popularmoviesvolley.Room;
//
//import android.app.Application;
//import android.arch.lifecycle.LiveData;
//import android.os.AsyncTask;
//
//import java.util.List;
//
//import example.android.popularmoviesvolley.Movies;
//
//public class MovieRepository {
//
//    private MovieDao movieDao;
//    private LiveData<List<Movies>> allMovies;
//
//    public MovieRepository(Application application) {
//        MovieDatabase dataBase = MovieDatabase.getInstance(application);
//        movieDao = dataBase.movieDao();
//
//        allMovies = movieDao.loadAllMovies();
//    }
//
//    public void insert(Movies movies) {
//
//        new InsertMovieAsyncTask(movieDao).execute(movies);
//
//    }
//
////    public void delete() {
////
////        new deleteMovieAsyncTask(movieDao).execute();
////
////    }
////
////    public void deleteAllMovies() {
////        new deleteAllMovieAsyncTask(movieDao).execute();
////
////    }
//
//    public LiveData<List<Movies>> getAllMovies() {
//        return allMovies;
//    }
//
//    private static class InsertMovieAsyncTask extends AsyncTask<Movies, Void, Void> {
//
//        private MovieDao movieDao;
//
//        private InsertMovieAsyncTask(MovieDao movieDao) {
//            this.movieDao = movieDao;
//        }
//
//        @Override
//        protected Void doInBackground(Movies... movies) {
//
//            movieDao.insertMovie(movies);
//
//            return null;
//        }
//    }
//
////    private static class deleteMovieAsyncTask extends AsyncTask<Movies, Void, Void> {
////
////        private MovieDao movieDao;
////
////        private deleteMovieAsyncTask(MovieDao movieDao) {
////            this.movieDao = movieDao;
////        }
////
////        protected Void doInBackground(Movies movies) {
////
////            movieDao.deleteMovie(movies);
////
////            return null;
////        }
////    }
////
////
////    private static class deleteAllMovieAsyncTask extends AsyncTask<Void, Void, Void> {
////
////        private MovieDao movieDao;
////
////        private deleteAllMovieAsyncTask(MovieDao movieDao) {
////            this.movieDao = movieDao;
////        }
////
////        @Override
////        protected Void doInBackground(Void... voids) {
////
////            movieDao.deleteAllMovies();
////
////            return null;
////        }
////    }
//
//
//}
