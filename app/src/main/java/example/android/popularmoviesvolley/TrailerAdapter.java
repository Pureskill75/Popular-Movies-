package example.android.popularmoviesvolley;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {



    private final List<TrailerRequest> mTrailerList = new ArrayList<>();

    private final TrailerClickListener mListener;


    TrailerAdapter(TrailerClickListener listener) {
        this.mListener = listener;
    }


    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trailer_list_item,viewGroup,false);

        return new TrailerViewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder trailerViewHolder, int i) {

        trailerViewHolder.bindItems(i);
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }


    void getTrailerList(List<TrailerRequest> trailer) {
        this.mTrailerList.clear();
        if (trailer != null) {
            mTrailerList.addAll(trailer);
        }
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{

        final TrailerAdapter trailerAdapter;
        private TextView mTrailerTitle;
        private ImageView mPlayTrailer;



        private TrailerViewHolder(View itemView, TrailerAdapter trailerAdapter) {
            super(itemView);
            this.trailerAdapter = trailerAdapter;

            mTrailerTitle = itemView.findViewById(R.id.trailer_title);
            mTrailerTitle = itemView.findViewById(R.id.trailer_play_iv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onTrailerClicked(mTrailerList.get(getAdapterPosition()));
                        }

                    }
                }
            });

        }

        void bindItems(int position){
            mTrailerTitle.setText(mTrailerList.get(position).getmName());
        }
    }
}