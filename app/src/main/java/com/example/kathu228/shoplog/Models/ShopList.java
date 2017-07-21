package com.example.kathu228.shoplog.Models;

import android.support.annotation.Nullable;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by afdoch on 7/12/17.
 *
 */

// ShopList ParseObject that wraps multiple segments of a list together

@ParseClassName("ShopList")
public class ShopList extends BaseParseObject {

    public static final String SHOPLIST_TAG = "ShopList";

    public ShopList(){
        //required for Parse
    }

    public ShopList(String name, @Nullable final SaveCallback callback){
        put("name", name);
        put("uncategorized_segment",new Segment("uncategorized", this, Segment.UNCATEGORIZED_SEGMENT, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                put("completed_segment",new Segment("completed", ShopList.this, Segment.COMPLETED_SEGMENT, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        getUsersRelation().add(ParseUser.getCurrentUser());
                        nullableSaveInBackground(callback);
                    }
                }));
            }
        }));
    }

    // Get the name of the ShopList
    public String getName() {
        return getString("name");
    }

    // Set the name of the ShopList
    public void setName(String value, @Nullable SaveCallback callback) {
        put("name", value);
        nullableSaveInBackground(callback);
    }

    // Relation to ParseUsers
    private ParseRelation<ParseUser> getUsersRelation() {
        return getRelation("users");
    }

    // Add a user to the relation
    public void addUser(ParseUser user, @Nullable SaveCallback callback) {
        getPreviousUserRelation().remove(user);
        getUsersRelation().add(user);
        nullableSaveInBackground(callback);
    }

    // Remove a user from the relation
    public void removeUser(ParseUser user, @Nullable SaveCallback callback) {
        getUsersRelation().remove(user);
        getPreviousUserRelation().add(user);
        nullableSaveInBackground(callback);
    }

    private ParseRelation<ParseUser> getPreviousUserRelation(){
        return getRelation("previous_users");
    }

    public void getUserList(FindCallback<ParseUser> callback){
        ParseQuery query = getUsersRelation().getQuery();
        query.orderByAscending("name");
        query.findInBackground(callback);
    }

    public void getUsersNotInList(final FindCallback<ParseUser> callback){
        ParseQuery<ParseUser> allUsersQuery = ParseUser.getQuery();
        ParseQuery<ParseUser> usersInListQuery = getUsersRelation().getQuery();
        allUsersQuery.whereDoesNotMatchKeyInQuery("username","username",usersInListQuery);
        allUsersQuery.orderByAscending("name");
        allUsersQuery.findInBackground(callback);
    }

    public void getItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("visible",true);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void getCheckedItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("checked",true);
        query.whereEqualTo("visible",true);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void getUncheckedItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("checked",false);
        query.whereEqualTo("visible",true);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public Item addItem(String itemName, @Nullable SaveCallback callback){
        return new Item(itemName,this,getUncategorizedSegment(),Item.ITEM,callback);
    }

    public void removeItem(Item item, @Nullable DeleteCallback callback){
        item.nullableDeleteInBackground(callback);
    }

    public void removeItemsFromCompleted(){
        getCompletedSegment().getItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                for(Item object : objects){
                    object.setVisible(false,null);
                }
            }
        });
    }

    public void getSegments(FindCallback<Segment> callback){
        ParseQuery<Segment> query = new ParseQuery<Segment>(Segment.class);
        query.whereEqualTo("parent",this);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public Segment getUncategorizedSegment(){
        return (Segment) getParseObject("uncategorized_segment");
    }

    public Segment getCompletedSegment(){
        return (Segment) getParseObject("completed_segment");
    }

    public Segment addSegment(String name, @Nullable SaveCallback callback){
        return new Segment(name,this,Segment.ADDITIONAL_SEGMENT,callback);
    }

    public void removeSegment(Segment segment){

        segment.getItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                for (Item item : objects){
                    item.setSegment(getUncategorizedSegment(),null);
                }
            }
        });
        segment.deleteInBackground();

    }

    public static ShopList getShopListById(String id){
        try {
            ShopList list = ShopList.createWithoutData(ShopList.class, id);
            list.fetchIfNeeded();
            return list;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
