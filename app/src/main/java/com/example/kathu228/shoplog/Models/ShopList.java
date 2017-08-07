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

import java.util.Date;
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

//    @Deprecated
//    public ShopList(String name, @Nullable final SaveCallback callback){
//        getObjectId();
//        put("icon_number",0);
//        put("name", name);
//        put("uncategorized_segment",new Segment("Uncategorized", this, Segment.UNCATEGORIZED_SEGMENT, new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                put("completed_header",new Item("Completed Items", ShopList.this, null, Item.COMPLETED_HEADER, new Date(), new SaveCallback() {
//                    @Override
//                        public void done(ParseException e) {
//                            getUsersRelation().add(ParseUser.getCurrentUser());
//                            nullableSaveInBackground(callback);
//                    }
//                }));
//                }
//        }));
//    }

    private void initializeVariables(String name, @Nullable final SaveCallback callback){
        put("users_string","Personal");
        put("icon_number",0);
        put("name", name);
        put("uncategorized_segment",new Segment("Uncategorized", this, Segment.UNCATEGORIZED_SEGMENT, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                put("completed_header",new Item("Completed Items", ShopList.this, null, Item.COMPLETED_HEADER, new Date(), new SaveCallback() {
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

//    // Add a user to the relation
//    public void addUser(ParseUser user, @Nullable SaveCallback callback) {
//        getPreviousUserRelation().remove(user);
//        getUsersRelation().add(user);
//        nullableSaveInBackground(callback);
//    }

    public void addUsers(List<ParseUser> users, final @Nullable SaveCallback callback){
        for (ParseUser user : users){
            getPreviousUserRelation().remove(user);
            getUsersRelation().add(user);
        }
        //TODO - Clean this up so that we only save once (After tuesday)
        nullableSaveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                setUsersString(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        nullableSaveInBackground(callback);
                    }
                });
            }
        });
    }

    // Remove a user from the relation
    public void removeUser(ParseUser user, final @Nullable SaveCallback callback) {
        getUsersRelation().remove(user);
        getPreviousUserRelation().add(user);
        //TODO - Clean this up so that we only save once (After tuesday)
        nullableSaveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                setUsersString(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        nullableSaveInBackground(callback);
                    }
                });
            }
        });
    }

//    public void removeUsers(List<ParseUser> users, @Nullable SaveCallback callback){
//        for (ParseUser user : users){
//            getUsersRelation().remove(user);
//            getPreviousUserRelation().add(user);
//        }
//        nullableSaveInBackground(callback);
//    }

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

//    public void getItems(FindCallback<Item> callback){
//        ParseQuery<Item> query = new ParseQuery<>(Item.class);
//        query.whereEqualTo("parent",this);
//        query.whereEqualTo("visible",true);
//        query.whereEqualTo("type",Item.ITEM);
//        query.orderByDescending("_updated_at");
//        query.findInBackground(callback);
//    }

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

//    @Deprecated
//    public Item addItem(String itemName, @Nullable SaveCallback callback){
//        return new Item(itemName,this,getUncategorizedSegment(),Item.ITEM,callback);
//    }

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

//    public void removeItemsFromCompleted(){
//        getCheckedItems(new FindCallback<Item>() {
//            @Override
//            public void done(List<Item> objects, ParseException e) {
//                for(Item object : objects){
//                    if(object.isItem())
//                        object.setVisible(false,null);
//                }
//            }
//        });
//    }

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

//    @Deprecated
//    public Segment addSegment(String name, @Nullable SaveCallback callback){
//        return new Segment(name,this,Segment.ADDITIONAL_SEGMENT,callback);
//    }

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
        segment.nullableDeleteInBackground(null);
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

    public String getUsersString(){
        fetchWhenNeeded();
        String userString = getString("users_string");
        String find = firstName(Query.getNameOfUser(ParseUser.getCurrentUser())) + ", ";
        if (userString.contains(find)) {
            return userString.replace(find, "");
        } else {
            return userString.replace(", " + firstName(Query.getNameOfUser(ParseUser.getCurrentUser())), "");
        }
    }

    private void setUsersString(final SaveCallback callback){
        generateUsersString(new StringCallback() {
            @Override
            public void done(String names) {
                put("users_string",names);
                callback.done(null);
            }
        });
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

    interface StringCallback{
        void done(String names);
    }

    private void generateUsersString(final StringCallback callback){
        // Arranges users into the format "Personal"/"John Smith"/"John, Karen"
        getUserList(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                StringBuilder names = new StringBuilder();
                int len = objects.size();
                if (len==1){
                    names.append("Personal");
                }
                else {
                    for (ParseUser user : objects) {
                        String name = Query.getNameOfUser(user);
                        if (names.length() == 0) {
                            names.append(firstName(name));
                        } else {
                            names.append(", ");
                            names.append(firstName(name));
                        }
                    }
                }
                callback.done(names.toString());
            }
        });
    }

    // finds substring with only first name
    private String firstName(String name){
        int space = name.indexOf(" ");
        return name.substring(0,space);
    }

}
