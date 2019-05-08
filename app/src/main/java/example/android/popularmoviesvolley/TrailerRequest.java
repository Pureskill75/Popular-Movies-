package example.android.popularmoviesvolley;

public class TrailerRequest {


    private String mId;

    private String mKey, mName;

    public TrailerRequest(String idNumber, String movieKey, String movieName){
        this.mId = idNumber;
        this.mKey =movieKey;
        this.mName= movieName;
    }



    public String getmId() {
        return mId;
    }

    public String getmKey() {
        return mKey;
    }

    public void getmName(String mName) {
        this.mName = mName;
    }

    public String getmName() {
        return mName;
    }
}
