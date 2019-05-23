package example.android.popularmoviesvolley;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private ArrayList<ReviewsRequest> mReviewList = new ArrayList<>();

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_list_item, parent, false);
        return new ReviewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {

        ReviewsRequest currentReviews = mReviewList.get(position);


        holder.mAuthorName.setText(currentReviews.getAuthor());
        holder.mReviewContent.setText(currentReviews.getContent());


    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }


    void getReviewList(ArrayList<ReviewsRequest> reviews) {
        this.mReviewList.clear();

        if (reviews != null) {
            mReviewList.addAll(reviews);
        }


        notifyDataSetChanged();
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        final ReviewsAdapter reviewsAdapter;

        private TextView mAuthorName;
        private TextView mReviewContent;


        ReviewHolder(View itemView, ReviewsAdapter reviewsAdapter) {
            super(itemView);
            this.reviewsAdapter = reviewsAdapter;

            mAuthorName = itemView.findViewById(R.id.author_text_view);
            mReviewContent = itemView.findViewById(R.id.review_content_text_view);

        }

    }
}
