package com.example.shafy.moviesguy.utilities;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by shafy on 25/09/2017.
 */

public class NetworkUtils {

    final private static String POPULAR_MOVIES_URL="https://api.themoviedb.org/3/movie/popular";
    final private static String TOP_RATED_URL="https://api.themoviedb.org/3/movie/top_rated";
    final private static String BASE_URL="https://api.themoviedb.org/3/movie";
    final private static String VIDEO="/videos";
    final private static String REVIEW="/reviews";
    final  private static String API_KEY="api_key";


    public static URL buildUrl(String mode,String apiKey){
        Uri builtUri;
        switch (mode) {
            case "POPULAR":
                builtUri = Uri.parse(POPULAR_MOVIES_URL).buildUpon()
                        .appendQueryParameter(API_KEY, apiKey).build();
                break;
            case "TOP":
                builtUri = Uri.parse(TOP_RATED_URL).buildUpon()
                        .appendQueryParameter(API_KEY, apiKey).build();
                break;
            default:
                return null;
        }
        URL url =null;
        try{
            url =new URL(builtUri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static URL buildUrl(String mode,String apiKey,String id){
        Uri builtUri;
        switch (mode) {
            case "VIDEO":
                builtUri = Uri.parse(BASE_URL+"/"+id+VIDEO).buildUpon()
                        .appendQueryParameter(API_KEY, apiKey).build();
                break;
            case "REVIEW":
                builtUri = Uri.parse(BASE_URL+"/"+id+REVIEW).buildUpon()
                        .appendQueryParameter(API_KEY, apiKey).build();
                break;
            default:
                return null;
        }

        URL url =null;
        try{
            url =new URL(builtUri.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException{
        HttpURLConnection urlConnection =(HttpURLConnection)url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner =new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }
            else {
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
    }



}
