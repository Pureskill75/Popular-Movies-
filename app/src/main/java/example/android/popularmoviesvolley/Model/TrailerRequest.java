package example.android.popularmoviesvolley.Model;

public class TrailerRequest {


    private String key;

    private String name;

    public TrailerRequest(String movie_key, String movie_name) {
        this.key = movie_key;
        this.name = movie_name;
    }


    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setKey(String mKey) {
        this.key = mKey;
    }

    public void setName(String mName) {
        this.name = mName;
    }
}
