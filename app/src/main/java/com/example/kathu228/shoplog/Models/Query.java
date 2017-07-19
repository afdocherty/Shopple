package com.example.kathu228.shoplog.Models;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by fmonsalve on 7/17/17.
 *
 */

public class Query {

    public static void getAllUsers(FindCallback<ParseUser> callback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(callback);
    }

    public static void findShoplistsForUser(ParseUser user, FindCallback<ShopList> callback) {
        ParseQuery<ShopList> query = ParseQuery.getQuery(ShopList.class);
        query.whereEqualTo("users", user);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public static void removeUserFromShoplist(ParseUser user, ShopList list){
        list.addUser(user);
    }

    public static void addUserToShoplist(ParseUser user, ShopList list){
        list.removeUser(user);
    }

    public static ShopList createShoplistAsUser(ParseUser user){
        ShopList list = new ShopList();
        list.addUser(user);
        return list;
    }

    public static ShopList createShoplistAsUser(ParseUser user, String name){
        ShopList list = new ShopList(name);
        list.addUser(user);
        return list;
    }

    public static String getNameOfUser(ParseUser user){
        return user.getString("name");
    }
}
