package example.android.popularmoviesvolley;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {


    private ArrayList<TrailerRequest> mTrailerList = new ArrayList<>();


    private TrailerClickListener mListener;


    TrailerAdapter(TrailerClickListener listener) {
        this.mListener = listener;
    }


    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trailer_list_item, viewGroup, false);

        return new TrailerViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder trailerViewHolder, int i) {

        trailerViewHolder.bindTrailerView(i);

    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }


    void getTrailerList(ArrayList<TrailerRequest> trailer) {
        this.mTrailerList.clear();

        if (trailer != null) {
            mTrailerList.addAll(trailer);
        }
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        final TrailerAdapter trailerAdapter;
        private TextView mTrailerTitle;
        private ImageView mTrailerThumbnail;


        private TrailerViewHolder(View itemView, TrailerAdapter trailerAdapter) {
            super(itemView);
            this.trailerAdapter = trailerAdapter;

            mTrailerTitle = itemView.findViewById(R.id.trailer_title);

            mTrailerThumbnail = itemView.findViewById(R.id.trailer_thumbnail_view);


            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onTrailerClicked(mTrailerList.get(getAdapterPosition()));
                    }

                }
            });

        }

        void bindTrailerView(int position) {
            mTrailerTitle.setText(mTrailerList.get(position).getName());
            String THUMBNAIL_URL = "https://img.youtube.com/vi/";
            String THUMBNAIL_END = "/0.jpg";
            Picasso.get().load(Uri.parse(THUMBNAIL_URL + mTrailerList.get(position).getKey() + THUMBNAIL_END)).fit().into(mTrailerThumbnail);
        }

    }

}