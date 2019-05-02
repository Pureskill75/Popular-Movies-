package example.android.popularmoviesvolley;


import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import example.android.popularmoviesvolley.ImageUtils.Utils;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavViewHolder> {

    private MovieClickListener mListener;
    private List<Movies> mFavList = new ArrayList<>();

    FavouritesAdapter(MovieClickListener mListener) {
        this.mListener = mListener;
    }


    @NonNull
    @Override
    public FavouritesAdapter.FavViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_layout, viewGroup, false);
        return new FavViewHolder(itemView, this);

    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {

        final Movies movie = mFavList.get(position);

        //load the appropriate Image view using Utils
        String posterUrl = Utils.buildPosterUrl(movie.getPosterPath());


        Picasso.get()
                .load(posterUrl)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.mImageView);

    }


    @Override
    public int getItemCount() {
        return mFavList.size();
    }


    void getFavouriteList(List<Movies> movies) {
        this.mFavList.clear();
        if (movies != null) {
            mFavList.addAll(movies);
        }
        notifyDataSetChanged();
    }


    class FavViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;


        final FavouritesAdapter favouritesAdapter;


        private FavViewHolder(@NonNull View itemView, FavouritesAdapter favouritesAdapter) {
            super(itemView);
            this.favouritesAdapter = favouritesAdapter;
            mImageView = itemView.findViewById(R.id.image_view_one);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onMovieClicked(position);
                    }
                }
            });
        }
    }


}


