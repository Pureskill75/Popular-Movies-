package example.android.popularmoviesvolley;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import example.android.popularmoviesvolley.ImageUtils.Utils;

public class MyMoviesAdapter extends RecyclerView.Adapter<MyMoviesAdapter.MoviesViewHolder> {


    private Context mContext;
    private ArrayList<Movies> mMoviesArrayList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    MyMoviesAdapter(Context context, ArrayList<Movies> MoviesArrayList) {
        mContext = context;
        mMoviesArrayList = MoviesArrayList;


    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_layout,
                parent, false);

        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MoviesViewHolder holder, int position) {
        final Movies movie = mMoviesArrayList.get(position);


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
        return mMoviesArrayList.size();
    }


    class MoviesViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;


        MoviesViewHolder(View itemView) {
            super(itemView);

            //grid layout image view position
            mImageView = itemView.findViewById(R.id.image_view_one);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });


        }

    }

}


