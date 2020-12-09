package example.android.popularmoviesvolley.Room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;


import example.android.popularmoviesvolley.Model.Movies;

@Database(entities = {Movies.class}, version = 2, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {


    private static final String DATABASE_NAME = "movie_database";

    private static MovieDatabase INSTANCE;

    public abstract MovieDao movieDao();


    public static synchronized MovieDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MovieDatabase.class, MovieDatabase.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return INSTANCE;
    }


}

