package com.example.kathu228.shoplog.Helpers;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Felipe Monsalve on 7/6/17.
 * The purpose of this class is to declare a uniform way to handle errors in hhtp queries
 * that require JsonHttpResponseHandlers. Instead of passing a JsonHttpResponseHandler
 * and having to override all onSuccess and onFailure methods every time you use one,
 * you can pass this class, and only have to override methods that will require a custom
 * handling, other than the preferred handling specified here.
 */

public class CustomResponseHandler extends JsonHttpResponseHandler {

    private String TAG;

    public CustomResponseHandler(String TAG) {
        super();
        this.TAG=TAG;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d(TAG,response.toString());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d(TAG,response.toString());
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Log.d(TAG,errorResponse.toString());
        throwable.printStackTrace();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        Log.d(TAG,errorResponse.toString());
        throwable.printStackTrace();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        Log.d(TAG,responseString);
        throwable.printStackTrace();
    }
}
