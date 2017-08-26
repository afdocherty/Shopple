package com.example.kathu228.shopple.Models;

import android.support.v7.util.ListUpdateCallback;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.kathu228.shopple.ParseApplication.parseLiveQueryClient;

/**
 * Created by fmonsalve on 7/17/17.
 *
 */

public class Query {

//    public static void getAllUsers(FindCallback<ParseUser> callback){
//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.findInBackground(callback);
//    }
    private static ParseQuery<ShopList> query;

    public static void findShoplistsForUser(ParseUser user, FindCallback<ShopList> callback) {
        ParseQuery<ShopList> query = ParseQuery.getQuery(ShopList.class);
        query.whereEqualTo("users", user);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public static String getNameOfUser(ParseUser user){
        return user.getString("name");
    }

    public static ParseUser getParseUserById(String id){
        try {
            ParseUser user = ParseUser.createWithoutData(ParseUser.class, id);
            user.fetchIfNeeded();
            return user;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void startShoplistsLiveQuery(SubscriptionHandling.HandleEventsCallback<ShopList> callback){
        query = ParseQuery.getQuery(ShopList.class);
        SubscriptionHandling<ShopList> subscriptionHandling = parseLiveQueryClient.subscribe(query);
        subscriptionHandling.handleEvents(callback);
    }

    public static void stopShoplistsLiveQuery(){
        parseLiveQueryClient.unsubscribe(query);
    }

    public interface ListCallback<E>{
        void onDone(List<E> list);
    }
    public static void getFacebookFriendsList(final ListCallback callback){
        GraphRequest friendRequest = GraphRequest.newMyFriendsRequest(
        AccessToken.getCurrentAccessToken(),
        new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray jsonArray, GraphResponse response) {
                ArrayList<String> fbFriendIds = new ArrayList<>();
                for (int i = 0; i<jsonArray.length(); i++){
                    try {
                        String id = jsonArray.getJSONObject(i).getString("id");
                        fbFriendIds.add(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callback.onDone(fbFriendIds);
            }
        });
        friendRequest.executeAsync();
    }
}
