package example.android.popularmoviesvolley;

class TrailerRequest {


    private String key, name;

    public TrailerRequest(String movie_key, String movie_name) {
        this.key = movie_key;
        this.name = movie_name;
    }


    String getKey() {
        return key;
    }

    String getName() {
        return name;
    }

    public void setKey(String mKey) {
        this.key = mKey;
    }

    public void setName(String mName) {
        this.name = mName;
    }
}
