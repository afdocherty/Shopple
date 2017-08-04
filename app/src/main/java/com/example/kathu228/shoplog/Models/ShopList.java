package com.example.kathu228.shoplog.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import java.util.List;

import static com.example.kathu228.shoplog.ParseApplication.parseLiveQueryClient;

/**
 * Created by afdoch on 7/12/17.
 *
 */

// ShopList ParseObject that wraps multiple segments of a list together

@ParseClassName("ShopList")
public class ShopList extends BaseParseObject {

    public interface ShoplistCallback{
        void done(ShopList list);
    }

    public static final String SHOPLIST_TAG = "ShopList";
    public static final String SHOPLIST_NEW_TAG = "new_list";
    public static final String SHOPLIST_PENDINTENT_TAG = "pending_intent";

    private ParseQuery<Item> currentItemLiveQuery;
    private ParseQuery<Segment> currentSegmentLiveQuery;


    public ShopList(){
        //required for Parse
    }

    @Deprecated
    public ShopList(String name, @Nullable final SaveCallback callback){
        put("icon_number",0);
        put("name", name);
        put("uncategorized_segment",new Segment("Uncategorized", this, Segment.UNCATEGORIZED_SEGMENT, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                put("completed_header",new Item("Completed Items", ShopList.this, null, Item.COMPLETED_HEADER, new SaveCallback() {
                    @Override
                        public void done(ParseException e) {
                            getUsersRelation().add(ParseUser.getCurrentUser());
                            nullableSaveInBackground(callback);
                    }
                }));
                }
        }));
    }

    private void initializeVariables(String name, @Nullable final SaveCallback callback){
        put("icon_number",0);
        put("name", name);
        put("uncategorized_segment",new Segment("Uncategorized", this, Segment.UNCATEGORIZED_SEGMENT, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                put("completed_header",new Item("Completed Items", ShopList.this, null, Item.COMPLETED_HEADER, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            getUsersRelation().add(ParseUser.getCurrentUser());
                            nullableSaveInBackground(callback);
                        } else
                            e.printStackTrace();
                    }
                }));
            }
        }));
    }

    // Get the name of the ShopList
    public String getName() {
        fetchWhenNeeded();
        return getString("name");
    }

    // Set the name of the ShopList
    public void setName(String value, @Nullable SaveCallback callback) {
        put("name", value);
        nullableSaveInBackground(callback);
    }

    // Relation to ParseUsers
    private ParseRelation<ParseUser> getUsersRelation() {
        fetchWhenNeeded();
        return getRelation("users");
    }

    // Add a user to the relation
    public void addUser(ParseUser user, @Nullable SaveCallback callback) {
        getPreviousUserRelation().remove(user);
        getUsersRelation().add(user);
        nullableSaveInBackground(callback);
    }

    public void addUsers(List<ParseUser> users, @Nullable SaveCallback callback){
        for (ParseUser user : users){
            getPreviousUserRelation().remove(user);
            getUsersRelation().add(user);
        }
        nullableSaveInBackground(callback);
    }

    // Remove a user from the relation
    public void removeUser(ParseUser user, @Nullable SaveCallback callback) {
        getUsersRelation().remove(user);
        getPreviousUserRelation().add(user);
        nullableSaveInBackground(callback);
    }

    public void removeUsers(List<ParseUser> users, @Nullable SaveCallback callback){
        for (ParseUser user : users){
            getUsersRelation().remove(user);
            getPreviousUserRelation().add(user);
        }
        nullableSaveInBackground(callback);
    }

    private ParseRelation<ParseUser> getPreviousUserRelation(){
        fetchWhenNeeded();
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
        query.whereEqualTo("type",Item.ITEM);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public void getCheckedItems(final FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("checked",true);
        query.whereEqualTo("visible",true);
        query.whereEqualTo("type",Item.ITEM);
        query.orderByDescending("_updated_at");
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                objects.add(0,getCompletedHeader());
                callback.done(objects,e);
            }
        });
    }

    public void getUncheckedSegmentItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<>(Item.class);
        query.whereEqualTo("parent",this);
        query.whereEqualTo("checked",false);
        query.whereEqualTo("visible",true);
        query.whereNotEqualTo("type",Item.COMPLETED_HEADER);
        query.whereNotEqualTo("segment",getUncategorizedSegment());
        query.orderByDescending("segment_created_at");
        query.addDescendingOrder("type");
        query.addDescendingOrder("_updated_at");
        query.findInBackground(callback);
    }

    @Deprecated
    public Item addItem(String itemName, @Nullable SaveCallback callback){
        return new Item(itemName,this,getUncategorizedSegment(),Item.ITEM,callback);
    }

    public void addItem(String itemName, @NonNull final Item.ItemCallback callback){
        final Item item = new Item();
        item.initializeVariables(itemName, this, getUncategorizedSegment(), Item.ITEM, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    callback.done(item);
                else
                    e.printStackTrace();

            }
        });
    }

    public void removeItem(Item item, @Nullable DeleteCallback callback){
        item.nullableDeleteInBackground(callback);
    }

    public void removeItemsFromCompleted(){
        getCheckedItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                for(Item object : objects){
                    if(object.isItem())
                        object.setVisible(false,null);
                }
            }
        });
    }

    public void getSegments(FindCallback<Segment> callback){
        ParseQuery<Segment> query = new ParseQuery<>(Segment.class);
        query.whereEqualTo("parent_list",this);
        query.whereExists("header");
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    public Segment getUncategorizedSegment(){
        fetchWhenNeeded();
        return (Segment) getParseObject("uncategorized_segment");
    }

    public Item getCompletedHeader(){
        fetchWhenNeeded();
        return (Item) getParseObject("completed_header");
    }

    @Deprecated
    public Segment addSegment(String name, @Nullable SaveCallback callback){
        return new Segment(name,this,Segment.ADDITIONAL_SEGMENT,callback);
    }

    public void addSegment(String name, @NonNull final Segment.SegmentCallback callback){
        final Segment segment = new Segment();
        segment.initializeVariables(name, this, Segment.ADDITIONAL_SEGMENT, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    callback.done(segment);
                else
                    e.printStackTrace();

            }
        });
    }

    public void removeSegment(Segment segment){

        List<Item> items = segment.getItems();
        for (Item item : items){
            item.setSegment(getUncategorizedSegment(),null);
        }
        segment.deleteInBackground();
    }

    public void incrementColorNum(@Nullable SaveCallback callback){
        increment("color_number");
        nullableSaveInBackground(callback);
    }

    public int getColorNum(){
        fetchWhenNeeded();
        return getInt("color_number");
    }

    public void setIconNum(int iconNum, @Nullable SaveCallback callback){
        put("icon_number", iconNum);
        nullableSaveInBackground(callback);
    }

    public int getIconNum(){
        fetchWhenNeeded();
        return getInt("icon_number");
    }

    public void startItemLiveQuery(SubscriptionHandling.HandleEventsCallback<Item> callback){
        currentItemLiveQuery = ParseQuery.getQuery(Item.class);
        currentItemLiveQuery.whereEqualTo("parent",this);
        currentItemLiveQuery.whereEqualTo("visible",true);
        currentItemLiveQuery.whereEqualTo("type",Item.ITEM);
        SubscriptionHandling<Item> subscriptionHandling = parseLiveQueryClient.subscribe(currentItemLiveQuery);
        subscriptionHandling.handleEvents(callback);
    }

    public void startSegmentLiveQuery(SubscriptionHandling.HandleEventsCallback<Segment> callback){
        currentSegmentLiveQuery = new ParseQuery<>(Segment.class);
        currentSegmentLiveQuery.whereEqualTo("parent_list",this);
        currentSegmentLiveQuery.whereExists("header");
        SubscriptionHandling<Segment> subscriptionHandling = parseLiveQueryClient.subscribe(currentSegmentLiveQuery);
        subscriptionHandling.handleEvents(callback);
    }

    public void stopLiveQueries(){
        parseLiveQueryClient.unsubscribe(currentItemLiveQuery);
        parseLiveQueryClient.unsubscribe(currentSegmentLiveQuery);
    }



    public static void getInstance(String name, @NonNull final ShoplistCallback callback){
        final ShopList list = new ShopList();
        list.initializeVariables(name, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    callback.done(list);
                else
                    e.printStackTrace();
            }
        });
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
