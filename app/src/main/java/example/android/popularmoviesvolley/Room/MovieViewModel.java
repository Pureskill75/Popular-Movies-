package example.android.popularmoviesvolley.Room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import example.android.popularmoviesvolley.Movies;

public class MovieViewModel extends AndroidViewModel {


    private final LiveData<List<Movies>> allMovies;

    public LiveData<List<Movies>> getAllMovies() {
        return allMovies;
    }

    public MovieViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase database = MovieDatabase.getInstance(this.getApplication());
        allMovies = database.movieDao().loadAllMovies();
    }

}
