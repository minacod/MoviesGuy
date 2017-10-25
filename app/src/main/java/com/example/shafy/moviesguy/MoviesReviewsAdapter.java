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

public class MoviesReviewsAdapter extends RecyclerView.Adapter<MoviesReviewsAdapter.ViewHolder> {

    private TextView mAuthor;
    private TextView mContent;
    private ImageView mButton;
    private String[][] mData;
    private OnReviewClicked mOnReviewClicked;

    interface OnReviewClicked{
        void onReviewClickedHandler(String key);
    }
    public MoviesReviewsAdapter(String[][] data,OnReviewClicked clicked) {
        this.mData = data;
        mOnReviewClicked=clicked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.reviews_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ViewHolder(View view){
            super(view);
            mButton=(ImageView)view.findViewById(R.id.iv_open_review);
            mAuthor =(TextView)view.findViewById(R.id.tv_author);
            mContent =(TextView)view.findViewById(R.id.tv_content);
            mButton.setOnClickListener(this);

        }

        public void bindView(int position){
            mAuthor.setText(mData[position][1]);
            mContent.setText(mData[position][2]);

        }

        @Override
        public void onClick(View v) {
            mOnReviewClicked.onReviewClickedHandler(mData[getAdapterPosition()][3]);
        }
    }
}
