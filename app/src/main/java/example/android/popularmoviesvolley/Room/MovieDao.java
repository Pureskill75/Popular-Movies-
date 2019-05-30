package example.android.popularmoviesvolley.Room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import example.android.popularmoviesvolley.Movies;

@Dao
public interface MovieDao {


    @Query("SELECT * FROM movie_table ORDER BY movie_id")
    LiveData<List<Movies>> loadAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movies movies);

    @Delete
    void deleteMovie(Movies movies);

    @Delete
    void deleteAllMovies(List<Movies> movies);

    @Query("SELECT * FROM movie_table WHERE movie_id = :id")
    Movies loadMovieById(int id);

}
