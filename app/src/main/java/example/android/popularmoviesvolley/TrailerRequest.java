package example.android.popularmoviesvolley;

class TrailerRequest {


    private String key, name;

    TrailerRequest(String movie_key, String movie_name) {
        this.key = movie_key;
        this.name = movie_name;
    }


    String getmKey() {
        return key;
    }

    String getmName() {
        return name;
    }

    public void setmKey(String mKey) {
        this.key = mKey;
    }

    public void setmName(String mName) {
        this.name = mName;
    }
}
