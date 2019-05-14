package example.android.popularmoviesvolley;

class TrailerRequest {


    private String mKey, mName;

    TrailerRequest(String movieKey, String movieName) {
        this.mKey = movieKey;
        this.mName = movieName;
    }


    String getmKey() {
        return mKey;
    }

    String getmName() {
        return mName;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
}
