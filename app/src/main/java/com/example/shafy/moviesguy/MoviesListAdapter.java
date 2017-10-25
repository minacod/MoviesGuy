package com.example.shafy.moviesguy;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shafy.moviesguy.utilities.BitmapUtils;
import com.example.shafy.moviesguy.utilities.MovieDataUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by shafy on 22/09/2017.
 */

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MoviesListViewHolder> {

    private MovieDataUtils[] mMovies;
    final private OnMoviePosterClicked mOnMoviePosterClicked;

    public MoviesListAdapter(MovieDataUtils[] movies, OnMoviePosterClicked listener){
        mMovies=movies;
        mOnMoviePosterClicked=listener;
    }

    @Override
    public MoviesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        int listItemLayoutId = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listItemLayoutId,parent,false);
        return new MoviesListViewHolder(view);
    }

    interface OnMoviePosterClicked{
        void onClickHandler(MovieDataUtils movie);
    }

    @Override
    public void onBindViewHolder(MoviesListViewHolder holder, int position) {

        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mMovies==null)
            return 0;
        else
            return mMovies.length;
    }

    class MoviesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mPoster;
        Context context;
        public MoviesListViewHolder(View view){
            super(view);
            mPoster=(ImageView)view.findViewById(R.id.iv_list_item);
            context=view.getContext();
            view.setOnClickListener(this);
        }

        public void bind(int itemNumber){

            String posterUrl = mMovies[itemNumber].getmPosterUrl();
            if(posterUrl!=null)
            Picasso.with(context).load(posterUrl).into(mPoster);
            else{
                Bitmap bm = BitmapUtils.getImage(mMovies[itemNumber].getmPoster());
                mPoster.setImageBitmap(bm);
            }
        }


        @Override
        public void onClick(View v) {
            int postion =getAdapterPosition();
            mOnMoviePosterClicked.onClickHandler(mMovies[postion]);
        }
    }
}
