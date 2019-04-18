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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Movies[] movies);

    @Delete
    void delete(Movies[] movies);


    @Query("DELETE FROM movie_table")
    void deleteAllMovies();


    //This is where we get all movies displayed in the recycler view (COME BACK TO THIS!!)

    @Query("SELECT * FROM movie_table WHERE id = :id")
    Movies loadMovieById(int id);


    @Query("SELECT * FROM movie_table ORDER BY id")
    LiveData<List<Movies>> getAllMovies();
}
