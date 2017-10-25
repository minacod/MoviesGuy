package com.example.shafy.moviesguy.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by shafy on 24/10/2017.
 */


public class MoviesContract {

    public static final String AUTHORITY = "com.example.shafy.moviesguy";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITES="favorites";


    public static class FavoritesEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String TABLE_NAME="favorites";
        public static final String COL_MOVIE_ID="id";
        public static final String COL_MOVIE_NAME="name";
        public static final String COL_MOVIE_PLOT="plot";
        public static final String COL_MOVIE_DATE="date";
        public static final String COL_MOVIE_REVIEW="review";
        public static final String COL_MOVIE_POSTER="poster";
    }

}
