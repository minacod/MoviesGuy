package com.example.shafy.moviesguy.utilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;

import java.io.Serializable;

/**
 * Created by shafy on 26/09/2017.
 */

public class MovieDataUtils implements Parcelable {
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
    private MovieDataUtils(Parcel in){
        mId=in.readString();
        mName=in.readString();
        mReleaseDate=in.readString();
        mOverview=in.readString();
        mRating=in.readString();
        mPosterUrl=in.readString();
        int l=in.readInt();
        if(l>0){
        mPoster = new byte[l];
        in.readByteArray(mPoster);}
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mReleaseDate);
        dest.writeString(mOverview);
        dest.writeString(mRating);
        dest.writeString(mPosterUrl);
        if(mPoster!=null){
        dest.writeInt(mPoster.length);
        dest.writeByteArray(mPoster);}
        else
            dest.writeInt(0);
    }

    static final Parcelable.Creator<MovieDataUtils> CREATOR
            = new Parcelable.Creator<MovieDataUtils>() {

        @Override
        public MovieDataUtils createFromParcel(Parcel source) {
            return new MovieDataUtils(source);
        }

        @Override
        public MovieDataUtils[] newArray(int size) {
            return new MovieDataUtils[0];
        }
    };
}
