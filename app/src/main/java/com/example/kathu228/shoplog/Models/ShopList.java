package com.example.kathu228.shoplog.Models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by afdoch on 7/12/17.
 * 
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
        saveInBackground();
    }

    // Relation to ParseUsers
    private ParseRelation<ParseUser> getUsersRelation() {
        return getRelation("users");
    }

    // Add a user to the relation
    public void addUser(ParseUser user) {
        getPreviousUserRelation().remove(user);
        getUsersRelation().add(user);
        saveInBackground();
    }

    // Remove a user from the relation
    public void removeUser(ParseUser user) {
        getUsersRelation().remove(user);
        getPreviousUserRelation().add(user);
        saveInBackground();
    }

    private ParseRelation<ParseUser> getPreviousUserRelation(){
        return getRelation("previous_users");
    }

    public void getUserList(FindCallback<ParseUser> callback){
        ParseQuery query = getUsersRelation().getQuery();
        query.findInBackground(callback);
    }

    public void getItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
        query.whereEqualTo("parent",this);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void getCheckedItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("checked",true);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void getUncheckedItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("checked",false);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void addItem(){
        //TODO- implement this
    }

    public void removeItem(Item item){
        item.deleteInBackground();
    }

    public void getSegments(FindCallback<Segment> callback){
        ParseQuery<Segment> query = new ParseQuery<Segment>(Segment.class);
        query.whereEqualTo("parent",this);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void addSegment(){
        //TODO- implement this
    }

    public void removeSegment(Segment segment){
        //TODO- implement this... moves items inside into uncategorized
    }


}
