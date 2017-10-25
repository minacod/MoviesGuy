package com.example.shafy.moviesguy.utilities;

import java.io.Serializable;

/**
 * Created by shafy on 26/09/2017.
 */

public class MovieDataUtils implements Serializable {
    private String mId;
    private String mName;
    private String mReleaseDate ;
    private String mOverview;
    private String mRating;
    private String mPosterUrl;
    private byte[] mPoster;

    public MovieDataUtils(String id, String name, String posterUrl, String rating, String releaseDate, String overview){
        mId=id;
        mName=name;
        mReleaseDate=releaseDate;
        mOverview=overview;
        mRating=rating;
        mPosterUrl=posterUrl;
    }
    public MovieDataUtils(String id, String name, byte[] poster, String rating, String releaseDate, String overview){
        mId=id;
        mName=name;
        mReleaseDate=releaseDate;
        mOverview=overview;
        mRating=rating;
        mPoster=poster;
    }

    public String getmName() {
        return mName;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public String getmOverview() {
        return mOverview;
    }

    public String getmRating() {
        return mRating;
    }

    public String getmPosterUrl() {
        return mPosterUrl;
    }

    public String getmId() {
        return mId;
    }

    public byte[] getmPoster() {
        return mPoster;
    }
}
