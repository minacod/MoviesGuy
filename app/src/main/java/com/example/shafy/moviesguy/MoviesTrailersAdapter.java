package com.example.shafy.moviesguy;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by shafy on 24/10/2017.
 */

public class MoviesTrailersAdapter extends RecyclerView.Adapter<MoviesTrailersAdapter.ViewHolder>  {

    TextView mTrailerNumber;
    ImageView mButton;
    String[][] mData;
    OnTrailerClicked mOnTrailerClicked;

    interface OnTrailerClicked{
        void onTrailerClickListner(int position);
    }

    public MoviesTrailersAdapter(String[][] data, OnTrailerClicked onTrailerClicked) {
        mData = data;
        mOnTrailerClicked=onTrailerClicked;
    }

    @Override
    public MoviesTrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.trailer_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesTrailersAdapter.ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ViewHolder(View view){
            super(view);
            mButton=(ImageView)view.findViewById(R.id.iv_open_trailer);
            mTrailerNumber=(TextView)view.findViewById(R.id.tv_trailer_number);
            mButton.setOnClickListener(this);
        }

        public void bindView(int position){
            mTrailerNumber.setText(mData[position][1]);
        }

        @Override
        public void onClick(View v) {
            int position =  getAdapterPosition();
            mOnTrailerClicked.onTrailerClickListner(position);
        }
    }
}
