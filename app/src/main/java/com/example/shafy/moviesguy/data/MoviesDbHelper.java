package com.example.shafy.moviesguy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shafy on 24/10/2017.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME="movies.db";
    private final static int DATABASE_VERSION=1;

    public MoviesDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQLQuery="CREATE TABLE " + MoviesContract.FavoritesEntry.TABLE_NAME+" ("+
                MoviesContract.FavoritesEntry.COL_MOVIE_ID+" INTEGER PRIMARY KEY, "+
                MoviesContract.FavoritesEntry.COL_MOVIE_NAME+" TEXT, "+
                MoviesContract.FavoritesEntry.COL_MOVIE_PLOT+" TEXT, "+
                MoviesContract.FavoritesEntry.COL_MOVIE_DATE+" TEXT, "+
                MoviesContract.FavoritesEntry.COL_MOVIE_REVIEW+" TEXT, "+
                MoviesContract.FavoritesEntry.COL_MOVIE_POSTER+" BLOB);"
                ;
        db.execSQL(SQLQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQLQuery="DROP TABLE IF EXISTS " + MoviesContract.FavoritesEntry.TABLE_NAME ;
        db.execSQL(SQLQuery);
        onCreate(db);
    }
}
