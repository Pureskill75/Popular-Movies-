package example.android.popularmoviesvolley.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import example.android.popularmoviesvolley.Model.Movies;

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
