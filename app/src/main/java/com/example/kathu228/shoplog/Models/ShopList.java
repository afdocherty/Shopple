package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by afdoch on 7/12/17.
 */

// ShopList ParseObject that wraps multiple segments of a list together

@ParseClassName("ShopList")
public class ShopList extends ParseObject {

    public ShopList(){
        //required for Parse
    }

    public ShopList(String name){
        setName(name);
    }

    // Get the name of the ShopList
    public String getName() {
        return getString("name");
    }

    // Set the name of the ShopList
    public void setName(String value) {
        put("name", value);
    }

    // Relation to ParseUsers
    public ParseRelation<ParseUser> getUsersRelation() {
        return getRelation("users");
    }

    // Add a user to the relation
    public void addUser(ParseUser user) {
        getUsersRelation().add(user);
        saveInBackground();
    }

    // Remove a user from the relation
    public void removeUser(ParseUser user) {
        getUsersRelation().remove(user);
        saveInBackground();
    }

    // TODO on the collaborators view you can query the relation
    // for all the users in it, i.e.
    //      ParseQuery<ParseObject> query = relation.getQuery();
    // Or even specify for the current user within the query for the ShopList fragment, i.e.
    //      query.whereEqualTo("user", ParseUser.getCurrentUser());
}
