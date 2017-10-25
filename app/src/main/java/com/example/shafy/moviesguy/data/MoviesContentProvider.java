package com.example.shafy.moviesguy.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.shafy.moviesguy.data.MoviesContract;

/**
 * Created by shafy on 24/10/2017.
 */



public class MoviesContentProvider extends ContentProvider {
    public static final int MOVIES=100;
    public static final int MOVIE_WITH_ID = 101;
    public MoviesDbHelper mDbHelper;
    public static UriMatcher sUriMatcher=buildUriMatcher() ;

    @Override
    public boolean onCreate() {
        mDbHelper =new MoviesDbHelper(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.AUTHORITY,MoviesContract.PATH_FAVORITES,MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY,MoviesContract.PATH_FAVORITES+ "/#",MOVIE_WITH_ID);
        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int mode=sUriMatcher.match(uri);
        SQLiteDatabase db=mDbHelper.getReadableDatabase();
        Cursor result;
        switch (mode) {
            case MOVIES:
                result =db.query(MoviesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("failed to load this"+ uri.toString());
        }
        result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int mode=sUriMatcher.match(uri);
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        long result;
        Uri returnUri;
        switch (mode) {
            case MOVIES:
                result =db.insert(MoviesContract.FavoritesEntry.TABLE_NAME,
                        null,
                        values);
                if ( result > 0 ) {
                    returnUri = ContentUris.withAppendedId(MoviesContract.FavoritesEntry.CONTENT_URI, result);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("failed to load this"+ uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int mode=sUriMatcher.match(uri);
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int result=0;
        switch (mode) {
            case MOVIE_WITH_ID:
                String id=uri.getLastPathSegment();
                try {
                    result =db.delete(MoviesContract.FavoritesEntry.TABLE_NAME,
                            MoviesContract.FavoritesEntry.COL_MOVIE_ID+"=?",
                            new String[]{id});
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("failed to delete",uri.toString());
                }

                break;
            default:
                throw new UnsupportedOperationException("failed to load this"+ uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return result;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
