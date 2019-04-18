package example.android.popularmoviesvolley;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "movie_table")
public class Movies implements Parcelable {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "movie_id")
    private String movie_id;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @ColumnInfo(name = "movie_title")
    private String originalTitle;

    @ColumnInfo(name = "overview")
    private String overview;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @ColumnInfo(name = "vote_average")
    private String voteAverage;


    public Movies( String movie_id, String posterPath, String originalTitle, String overview, String releaseDate, String voteAverage) {
        this.movie_id = movie_id;
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;

    }

    @Ignore
    private Movies(Parcel in) {
        id = in.readInt();
        movie_id = in.readString();
        posterPath = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(movie_id);
        dest.writeString(posterPath);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(voteAverage);
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String mOriginalTitle) {
        this.originalTitle = mOriginalTitle;
    }


    public String getOverview() {
        return overview;
    }

    public void setOverview(String mOverview) {
        this.overview = mOverview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String mReleasedate) {
        this.releaseDate = mReleasedate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String mPosterPath) {
        this.posterPath = mPosterPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String mVoteAverage) {
        this.voteAverage = mVoteAverage;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String mMovie_id) {
        this.movie_id = mMovie_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
