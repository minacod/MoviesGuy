package com.example.shafy.moviesguy.utilities;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shafy on 26/09/2017.
 */

public class TmdbJsonUtils {
    private MovieDataUtils [] mMovies;
    public TmdbJsonUtils (){
        mMovies=null;
    }

    public MovieDataUtils [] getmMoviesFormJson(String jsonResponse) throws JSONException {

        String TMDB_LIST="results";
        String TMDB_ID="id";
        String TMDB_NAME="original_title";
        String TMDB_Poster_URL="poster_path";
        String TMDB_RATING="vote_average";
        String TMDB_OVERVIEW="overview";
        String TMDB_RELEASE_DATE="release_date";
        String IMAGE_API_URL="https://image.tmdb.org/t/p/";
        String IMAGE_FORMAT="w185";
        String id;
        String name;
        String releaseDate ;
        String overview;
        String rating;
        String posterUrl;

        JSONObject jsonResponseObject = new JSONObject(jsonResponse);
        JSONArray moviesJsonArray = jsonResponseObject.getJSONArray(TMDB_LIST);
        mMovies = new MovieDataUtils[moviesJsonArray.length()];

        for (int i=0;i<moviesJsonArray.length();i++){
            JSONObject movie = moviesJsonArray.getJSONObject(i);
            id=movie.getString(TMDB_ID);
            name=movie.getString(TMDB_NAME);
            posterUrl=IMAGE_API_URL+IMAGE_FORMAT+movie.getString(TMDB_Poster_URL);
            rating=movie.getString(TMDB_RATING);
            releaseDate=movie.getString(TMDB_RELEASE_DATE);
            overview=movie.getString(TMDB_OVERVIEW);
            mMovies[i]=new MovieDataUtils(id,name,posterUrl,rating,releaseDate,overview);
        }
        return mMovies;
    }
    public String [][] getTrailersFormJson(String jsonResponse) throws JSONException {

        String TMDB_LIST="results";
        String TMDB_ID="id";
        String TMDB_NAME="name";
        String TMDB_KEY="key";
        String id;
        String name;
        String key ;

        JSONObject jsonResponseObject = new JSONObject(jsonResponse);
        JSONArray trailersJsonArray = jsonResponseObject.getJSONArray(TMDB_LIST);
        String[][] trailers=new String[trailersJsonArray.length()][3];

        for (int i=0;i<trailersJsonArray.length();i++){
            JSONObject trailer = trailersJsonArray.getJSONObject(i);
            id=trailer.getString(TMDB_ID);
            name=trailer.getString(TMDB_NAME);
            key = trailer.getString(TMDB_KEY);
            trailers[i]=new String[]{id,name,key};
        }
        return trailers;
    }

    public String [][] getReviewsFormJson(String jsonResponse) throws JSONException {

        String TMDB_LIST="results";
        String TMDB_ID="id";
        String TMDB_NAME="author";
        String TMDB_CONTENT="content";
        String TMDB_URL="url";
        String id;
        String name;
        String content;
        String url;

        JSONObject jsonResponseObject = new JSONObject(jsonResponse);
        JSONArray reviewsJsonArray = jsonResponseObject.getJSONArray(TMDB_LIST);
        String[][] reviews=new String[reviewsJsonArray.length()][4];

        for (int i=0;i<reviewsJsonArray.length();i++){
            JSONObject review = reviewsJsonArray.getJSONObject(i);
            id=review.getString(TMDB_ID);
            name=review.getString(TMDB_NAME);
            content = review.getString(TMDB_CONTENT);
            url = review.getString(TMDB_URL);
            reviews[i]=new String[]{id,name,content,url};
        }
        return reviews;
    }

}
