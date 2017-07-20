package com.example.kathu228.shoplog.Models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by afdoch on 7/12/17.
 *
 */

// ShopList ParseObject that wraps multiple segments of a list together

@ParseClassName("ShopList")
public class ShopList extends ParseObject {

    public static final String SHOPLIST_TAG = "ShopList";

    public ShopList(){
        //required for Parse
    }

    public ShopList(String name){
        setName(name);
        put("uncategorized_segment",new Segment("uncategorized",this));
        put("completed_segment",new Segment("completed",this));
        addUser(ParseUser.getCurrentUser());
        addCompletedHeaderItem("Completed Items");
        saveInBackground();
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
        query.orderByAscending("name");
        query.findInBackground(callback);
    }

    public void getUsersNotInList(FindCallback<ParseUser> callback){
        ParseQuery<ParseUser> allUsersQuery = ParseQuery.getQuery(ParseUser.class);
        ParseQuery<ParseUser> usersInListQuery = getUsersRelation().getQuery();
        allUsersQuery.whereDoesNotMatchKeyInQuery("_id","_id",usersInListQuery);
        allUsersQuery.orderByAscending("name");
        allUsersQuery.findInBackground(callback);
    }

    public void getItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("visible",true);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void getCheckedItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("checked",true);
        query.whereEqualTo("visible",true);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void getUncheckedItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("checked",false);
        query.whereEqualTo("visible",true);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public Item addItem(String itemName){
        return new Item(itemName,this,getUncategorizedSegment(),Item.ITEM);
    }

    public Item addHeaderItem(String itemName){
        return new Item(itemName,this,getUncategorizedSegment(),Item.HEADER);
    }

    public Item addCompletedHeaderItem(String itemName){
        return new Item(itemName,this,getUncategorizedSegment(),Item.COMPLETED_HEADER);
    }

    public void removeItem(Item item){
        item.deleteInBackground();
    }

    public void removeItemsFromCompleted(){
        getCompletedSegment().getItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                for(Item object : objects){
                    object.setVisible(false);
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

    public Segment addSegment(String name){
        return new Segment(name,this);
    }

    public void removeSegment(Segment segment){
        segment.getItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                for (Item item : objects){
                    item.setSegment(getUncategorizedSegment());
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
