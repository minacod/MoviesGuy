package com.example.shafy.moviesguy;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shafy.moviesguy.data.MoviesContract;
import com.example.shafy.moviesguy.utilities.BitmapUtils;
import com.example.shafy.moviesguy.utilities.MovieDataUtils;
import com.example.shafy.moviesguy.utilities.NetworkUtils;
import com.example.shafy.moviesguy.utilities.TmdbJsonUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class DetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String[][][]>,
        MoviesTrailersAdapter.OnTrailerClicked,
        MoviesReviewsAdapter.OnReviewClicked {

    private TextView mMovieName;
    private ImageView mMoviePoster;
    private byte[] mBytePoster;
    private TextView mMovieReleaseDate;
    private TextView mMovieUserRatings;
    private TextView mMovieOverview;
    private TextView mTRAILERS;
    private TextView mREVIEWS;
    private ImageView mFavorite;
    private static final int OFFLINE_LOADER_ID = 2;
    private static final int LOADER_ID = 3;
    private String[][] mTrailers;
    private String[][] mReviews;
    private RecyclerView mTrailersList;
    private RecyclerView mReviewsList;
    private MoviesTrailersAdapter moviesTrailersAdapter;
    private MoviesReviewsAdapter mReviewsAdapter;
    private LinearLayoutManager rManager;
    private LinearLayoutManager manager;
    private String apiKey = BuildConfig.API_KEY;
    private boolean savedAsFavorite;
    private static final String TRAILERS_URL_BUNDLE_KEY = "url";
    private static final String REVIEWS_URL_BUNDLE_KEY = "reviews_url";
    private static final String REVIEWS = "REVIEW";
    private static final String VIDEO = "VIDEO";
    private MovieDataUtils mMovie;
    private Cursor mCursor;
    private boolean isLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intentStartedthis = getIntent();
        mMovie = intentStartedthis.getParcelableExtra(getString(R.string.movie));

        if (savedInstanceState != null) {
            mTrailers = (String[][]) savedInstanceState.getSerializable("t");
            mReviews = (String[][]) savedInstanceState.getSerializable("r");
        }

        setContentView(R.layout.activity_details);
        savedAsFavorite = false;
        mTrailersList = (RecyclerView) findViewById(R.id.rv_trailer_list);
        mReviewsList = (RecyclerView) findViewById(R.id.rv_reviews_list);
        mMovieName = (TextView) findViewById(R.id.tv_movie_name);
        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mMovieUserRatings = (TextView) findViewById(R.id.tv_rating);
        mMovieOverview = (TextView) findViewById(R.id.tv_overview);
        mFavorite = (ImageView) findViewById(R.id.iv_fav);
        mTRAILERS = (TextView) findViewById(R.id.textView);
        mREVIEWS = (TextView) findViewById(R.id.textView2);

        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heartClicked();
            }
        });


        getSupportLoaderManager().initLoader(OFFLINE_LOADER_ID, null, offlineLoader);


        manager = new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        rManager = new LinearLayoutManager(DetailsActivity.this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        String topVote = getString(R.string.top_vote);
        if (mMovie != null) {
            mMovieName.setText(mMovie.getmName());
            mMovieReleaseDate.setText(mMovie.getmReleaseDate().substring(0, 4));
            topVote = mMovie.getmRating() + topVote;
            mMovieUserRatings.setText(topVote);
            mMovieOverview.setText(mMovie.getmOverview());
            if (mMovie.getmPosterUrl() != null)
                loadPoster();
            else {
                mBytePoster = mMovie.getmPoster();
                mMoviePoster.setImageBitmap(BitmapUtils.getImage(mBytePoster));
            }
        }

        if (NetworkUtils.isConnected(this)) {
            load();
        } else {
            Toast.makeText(this, R.string.check_internet, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("t",mTrailers);
        outState.putSerializable("r",mReviews);
    }


    //
    // Loader to Check from the database if the movie exists in favorites table
    //

    private LoaderManager.LoaderCallbacks<Cursor> offlineLoader = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(DetailsActivity.this) {

                @Override
                protected void onStartLoading() {

                    forceLoad();
                }

                @Override
                public Cursor loadInBackground() {
                    return getContext().getContentResolver().query(MoviesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            MoviesContract.FavoritesEntry.COL_MOVIE_ID + "=?", new String[]{mMovie.getmId()}, null);
                }

            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCursor = data;
            if (data.getCount() != 0) {
                savedAsFavorite = true;
                mFavorite.setImageResource(R.drawable.ic_favorite_black_48px);
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void load() {

        URL url = NetworkUtils.buildUrl(VIDEO, apiKey, mMovie.getmId());
        URL url2 = NetworkUtils.buildUrl(REVIEWS, apiKey, mMovie.getmId());
        Bundle LoaderBundle = new Bundle();
        if (url != null && url2 != null) {
            LoaderBundle.putString(TRAILERS_URL_BUNDLE_KEY, url.toString());
            LoaderBundle.putString(REVIEWS_URL_BUNDLE_KEY, url2.toString());
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader loader = loaderManager.getLoader(LOADER_ID);
            if (loader == null)
                loaderManager.initLoader(LOADER_ID, LoaderBundle, this);
            else
                loaderManager.restartLoader(LOADER_ID, LoaderBundle, this);
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }
    }

    //
    // Loader to get the trailers data and the reviews form the API
    //


    @Override
    public Loader<String[][][]> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<String[][][]>(this) {
            @Override
            protected void onStartLoading() {
                if (args == null)
                    return;
                else if (mTrailers == null || mReviews == null) {
                    forceLoad();
                } else {
                    deliverResult(new String[][][]{mTrailers, mReviews});
                }
            }

            @Override
            public String[][][] loadInBackground() {
                String rStringUrl = args.getString(REVIEWS_URL_BUNDLE_KEY);
                String tStringUrl = args.getString(TRAILERS_URL_BUNDLE_KEY);

                if ((rStringUrl == null || rStringUrl.isEmpty()) && (tStringUrl == null || tStringUrl.isEmpty()))
                    return null;


                String[][][] result = new String[2][][];
                String tresponse = null;
                String rresponse = null;
                try {
                    URL url = new URL(tStringUrl);
                    tresponse = NetworkUtils.getResponseFromHttpUrl(url);
                    URL url2 = new URL(rStringUrl);
                    rresponse = NetworkUtils.getResponseFromHttpUrl(url2);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                TmdbJsonUtils tmdbJsonUtils = new TmdbJsonUtils();
                try {
                    result[0] = tmdbJsonUtils.getTrailersFormJson(tresponse);
                    result[1] = tmdbJsonUtils.getReviewsFormJson(rresponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
                return result;


            }


            @Override
            public void deliverResult(String[][][] data) {
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[][][]> loader, String[][][] data) {
        mTrailers = data[0];
        mReviews = data[1];
        if (data[0].length == 0) {
            mTRAILERS.setText("No Trailers Available");
        } else {


            mTrailersList.setLayoutManager(manager);
            moviesTrailersAdapter = new MoviesTrailersAdapter(data[0], this);
            mTrailersList.setAdapter(moviesTrailersAdapter);
            mTrailersList.setHasFixedSize(true);
            mTrailersList.setVisibility(View.VISIBLE);

        }
        if (data[1].length == 0) {
            mREVIEWS.setText("No Reviews Available");
        } else {
            mReviewsList.setLayoutManager(rManager);
            mReviewsAdapter = new MoviesReviewsAdapter(data[1], this);
            mReviewsList.setAdapter(mReviewsAdapter);
            mReviewsList.setHasFixedSize(true);
            mReviewsList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[][][]> loader) {

    }

    @Override
    public void onTrailerClickListner(int position) {
        String youtubeBaseURL = "https://www.youtube.com/watch?v=";
        Uri uri = Uri.parse(youtubeBaseURL + mTrailers[position][2]);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }

    @Override
    public void onReviewClickedHandler(String key) {
        Uri uri = Uri.parse(key);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }



    //ADD of DELETE from the data base
    public void heartClicked() {
        if (!savedAsFavorite) {
            ContentValues cv = new ContentValues();
            cv.put(MoviesContract.FavoritesEntry.COL_MOVIE_ID, mMovie.getmId());
            cv.put(MoviesContract.FavoritesEntry.COL_MOVIE_DATE, mMovie.getmReleaseDate());
            cv.put(MoviesContract.FavoritesEntry.COL_MOVIE_NAME, mMovie.getmName());
            cv.put(MoviesContract.FavoritesEntry.COL_MOVIE_PLOT, mMovie.getmOverview());
            cv.put(MoviesContract.FavoritesEntry.COL_MOVIE_REVIEW, mMovie.getmRating());
            cv.put(MoviesContract.FavoritesEntry.COL_MOVIE_POSTER, mBytePoster);
            savedAsFavorite = true;
            getContentResolver().insert(MoviesContract.FavoritesEntry.CONTENT_URI, cv);
            mFavorite.setImageResource(R.drawable.ic_favorite_black_48px);
        } else {
            Uri uri = MoviesContract.FavoritesEntry.CONTENT_URI.buildUpon().appendPath(mMovie.getmId()).build();
            int c = getContentResolver().delete(uri, null, null);
            savedAsFavorite = false;
            mFavorite.setImageResource(R.drawable.ic_favorite_border_black_48px);
        }
    }


    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mBytePoster = BitmapUtils.getBytes(bitmap);
            mMoviePoster.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private void loadPoster() {
        Picasso.with(this).load(mMovie.getmPosterUrl()).into(target);
    }

    @Override
    public void onDestroy() {
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }
}
