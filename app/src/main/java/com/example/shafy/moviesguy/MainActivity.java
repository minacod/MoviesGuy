package com.example.shafy.moviesguy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shafy.moviesguy.data.MoviesContract;
import com.example.shafy.moviesguy.utilities.MovieDataUtils;
import com.example.shafy.moviesguy.utilities.NetworkUtils;
import com.example.shafy.moviesguy.utilities.TmdbJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviesListAdapter.OnMoviePosterClicked,
        LoaderManager.LoaderCallbacks<MovieDataUtils[]> {

    private RecyclerView mMoviesList;
    private ProgressBar mMainProgressBar;
    private TextView mMsg;
    private MoviesListAdapter mMoviesListAdapter;
    private String VIEWING_MODE;
    private String apiKey = BuildConfig.API_KEY;
    private SwipeRefreshLayout mRefreshLayout;

    private static final String MOVIES_URL_BUNDLE_KEY = "url";
    private static final int MOVIES_LOADER_ID = 1;
    private static final int FAV_MOVIES_LOADER_ID = 5;
    private boolean hideProgress;
    private boolean ifFavorite;

    private GridLayoutManager layoutManager;
    private Parcelable state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            VIEWING_MODE = savedInstanceState.getString("mode");
            ifFavorite=savedInstanceState.getBoolean("chk");
            state=savedInstanceState.getParcelable("state");
        }
        else
            VIEWING_MODE = getString(R.string.popular);
        mMoviesList = (RecyclerView) findViewById(R.id.rv_movies_list);
        mMainProgressBar = (ProgressBar) findViewById(R.id.pb_main_loading);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.sr_main);
        mMsg =(TextView) findViewById(R.id.tv_msg);
        hideProgress = true;

        layoutManager = new GridLayoutManager(MainActivity.this, 2, LinearLayoutManager.VERTICAL, false);

        mRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        hideProgress = false;
                        loadMovies();

                    }
                }
        );
        loadMovies();


    }


    //
    //Menu
    //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_Top: {
                VIEWING_MODE = getApplicationContext().getString(R.string.top);
                ifFavorite= false;
                loadMovies();
                break;
            }
            case R.id.action_Popular: {
                VIEWING_MODE = getApplicationContext().getString(R.string.popular);
                ifFavorite= false;
                loadMovies();
                break;
            }
            case R.id.fav: {
                LoaderManager moviesLoaderManager = getSupportLoaderManager();
                Loader movieaLoader = moviesLoaderManager.getLoader(FAV_MOVIES_LOADER_ID);
                ifFavorite= true;
                if (movieaLoader == null)
                    getSupportLoaderManager().initLoader(FAV_MOVIES_LOADER_ID, null, favoriteLoader);
                else
                    getSupportLoaderManager().restartLoader(FAV_MOVIES_LOADER_ID, null, favoriteLoader);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        layoutManager.onRestoreInstanceState(state);
    }

    //
    // saving instance
    //



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        state = layoutManager.onSaveInstanceState();
        outState.putParcelable("state",state);
        outState.putString("mode", VIEWING_MODE);
        outState.putBoolean("chk", ifFavorite);
    }

    //
    //  initiate the proper loader
    //

    private void loadMovies() {
        if (NetworkUtils.isConnected(this)&&!ifFavorite) {
            NetworkUtils test = new NetworkUtils();
            URL url = NetworkUtils.buildUrl(VIEWING_MODE, apiKey);
            Bundle moviesLoaderBundle = new Bundle();
            if (url != null) {
                moviesLoaderBundle.putString(MOVIES_URL_BUNDLE_KEY, url.toString());
                LoaderManager moviesLoaderManager = getSupportLoaderManager();
                Loader movieaLoader = moviesLoaderManager.getLoader(MOVIES_LOADER_ID);
                if (movieaLoader == null)
                    moviesLoaderManager.initLoader(MOVIES_LOADER_ID, moviesLoaderBundle, this);
                else
                    moviesLoaderManager.restartLoader(MOVIES_LOADER_ID, moviesLoaderBundle, this);
            } else {
                mMainProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_LONG).show();
            }
        }

        else {
            if(!NetworkUtils.isConnected(this))
                Toast.makeText(MainActivity.this, R.string.offline_refresh, Toast.LENGTH_SHORT).show();
            ifFavorite=true;
            LoaderManager moviesLoaderManager = getSupportLoaderManager();
            Loader movieaLoader = moviesLoaderManager.getLoader(FAV_MOVIES_LOADER_ID);
            if (movieaLoader == null)
                getSupportLoaderManager().initLoader(FAV_MOVIES_LOADER_ID, null, favoriteLoader);
            else
                getSupportLoaderManager().restartLoader(FAV_MOVIES_LOADER_ID, null, favoriteLoader);
            mRefreshLayout.setRefreshing(false);
        }
    }


    //
    // loader to load favorite movies
    //

    LoaderManager.LoaderCallbacks<MovieDataUtils[]> favoriteLoader = new LoaderManager.LoaderCallbacks<MovieDataUtils[]>() {
        @Override
        public Loader<MovieDataUtils[]> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<MovieDataUtils[]>(MainActivity.this) {
                MovieDataUtils[] mMovies;

                @Override
                protected void onStartLoading() {
                    if(ifFavorite){
                        mMoviesList.setVisibility(View.GONE);
                        mMsg.setVisibility(View.GONE);
                        mMainProgressBar.setVisibility(View.VISIBLE);
                        forceLoad();
                    }

                    else deliverResult(mMovies);
                }

                @Override
                public void deliverResult(MovieDataUtils[] data) {
                    super.deliverResult(data);
                }

                @Override
                public MovieDataUtils[] loadInBackground() {
                    Cursor result = getContentResolver().query(MoviesContract.FavoritesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null,
                            null);
                    int limit = result.getCount();
                    mMovies = new MovieDataUtils[limit];
                    for (int i = 0; i < limit; i++) {
                        result.moveToPosition(i);
                        String id = result.getString(result.getColumnIndex(MoviesContract.FavoritesEntry.COL_MOVIE_ID));
                        String name = result.getString(result.getColumnIndex(MoviesContract.FavoritesEntry.COL_MOVIE_NAME));
                        byte[] poster = result.getBlob(result.getColumnIndex(MoviesContract.FavoritesEntry.COL_MOVIE_POSTER));
                        String rating = result.getString(result.getColumnIndex(MoviesContract.FavoritesEntry.COL_MOVIE_REVIEW));
                        String releaseDate = result.getString(result.getColumnIndex(MoviesContract.FavoritesEntry.COL_MOVIE_DATE));
                        String overview = result.getString(result.getColumnIndex(MoviesContract.FavoritesEntry.COL_MOVIE_PLOT));
                        mMovies[i] = new MovieDataUtils(id, name, poster, rating, releaseDate, overview);
                    }
                    result.close();
                    return mMovies;
                }
            };
        }

        //
        // loader to load form the API
        //

        @Override
        public void onLoadFinished(Loader<MovieDataUtils[]> loader, MovieDataUtils[] data) {

            if(data.length!=0){

                mMoviesList.setLayoutManager(layoutManager);
                mMoviesListAdapter = new MoviesListAdapter(data, MainActivity.this);
                mMoviesList.setAdapter(mMoviesListAdapter);
                mMoviesList.setHasFixedSize(true);
                mMainProgressBar.setVisibility(View.INVISIBLE);
                mMoviesList.setVisibility(View.VISIBLE);}
            else {
                mMainProgressBar.setVisibility(View.INVISIBLE);
                mMsg.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<MovieDataUtils[]> loader) {

        }
    };


    @Override
    public Loader<MovieDataUtils[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieDataUtils[]>(this) {
            MovieDataUtils[] mMovies;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                if (mMovies == null) {
                    mMoviesList.setVisibility(View.INVISIBLE);
                    mMsg.setVisibility(View.GONE);
                    if (hideProgress)
                        mMainProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                } else {
                    deliverResult(mMovies);
                }
            }

            @Override
            public MovieDataUtils[] loadInBackground() {

                String stringUrl = args.getString(MOVIES_URL_BUNDLE_KEY);
                if (stringUrl == null || stringUrl.isEmpty())
                    return null;
                MovieDataUtils[] moviesListResult;
                String stringMoviesListResult = null;
                try {
                    URL url = new URL(stringUrl);
                    stringMoviesListResult = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                TmdbJsonUtils tmdbJsonUtils = new TmdbJsonUtils();
                try {
                    moviesListResult = tmdbJsonUtils.getmMoviesFormJson(stringMoviesListResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
                return moviesListResult;

            }

            @Override
            public void deliverResult(MovieDataUtils[] data) {
                mMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDataUtils[]> loader, MovieDataUtils[] data) {

        mMoviesList.setLayoutManager(layoutManager);
        mMoviesListAdapter = new MoviesListAdapter(data, MainActivity.this);
        mMoviesList.setAdapter(mMoviesListAdapter);
        mMoviesList.setHasFixedSize(true);
        mMainProgressBar.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<MovieDataUtils[]> loader) {

    }

    //
    // listItem ClickHandler
    //

    @Override
    public void onClickHandler(MovieDataUtils movie) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getApplicationContext().getString(R.string.movie), movie);
        startActivity(intent);
    }
}
