package com.example.kathu228.shopple.Models;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

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
}
