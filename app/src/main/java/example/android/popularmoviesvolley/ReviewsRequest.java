package example.android.popularmoviesvolley;

public class ReviewsRequest {


    private String author;

    private String content;

    ReviewsRequest(String review_author, String review_content) {
        this.author = review_author;
        this.content = review_content;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
