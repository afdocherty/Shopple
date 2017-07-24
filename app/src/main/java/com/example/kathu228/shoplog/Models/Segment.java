package com.example.kathu228.shoplog.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Created by afdoch on 7/12/17.
 *
 */

// Segment ParseObject that is contained in a list and whih wraps multiple items of a list together

@ParseClassName("Segment")
public class Segment extends BaseParseObject {

    public interface SegmentCallback{
        void done(Segment segment);
    }

    static final int UNCATEGORIZED_SEGMENT=0;
    static final int COMPLETED_SEGMENT=1;
    static final int ADDITIONAL_SEGMENT=2;

    public Segment(){
        //required for Parse
    }

    Segment(String name, ShopList parentList, int segmentType, @Nullable SaveCallback callback){
        put("name", name);
        setParent(parentList);

        switch (segmentType){
            case 1:
                addCompletedHeaderItem(name, parentList, callback);
                //Log.d("debug","complete header created");
                break;
            case 2:
                addHeaderItem(name, parentList, callback);
                //Log.d("debug","header created");
                break;
            default:
                break;
        }
        nullableSaveInBackground(callback);
    }

    void initializeVariables(String name, ShopList parentList, int segmentType, @Nullable SaveCallback callback){
        put("name", name);
        setParent(parentList);

        switch (segmentType){
            case 1:
                addCompletedHeaderItem(name, parentList, callback);
                //Log.d("debug","complete header created");
                break;
            case 2:
                addHeaderItem(name, parentList, callback);
                //Log.d("debug","header created");
                break;
            default:
                break;
        }
        nullableSaveInBackground(callback);
    }

    // Get the name of the Segment
    public String getName() {
        return getString("name");
    }

    // Set the name of the Segment
    public void setName(String value, @Nullable final SaveCallback callback) {
        put("name", value);

        getHeader().setBody(value);
        getHeader().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                nullableSaveInBackground(callback);
            }
        });
    }

    public ShopList getParent(){
        return (ShopList) getParseObject("parent_list");
    }

    private void setParent(ShopList parentList){
        put("parent_list",parentList);
    }

    public void getItems(FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
        query.whereEqualTo("segment",this);
        query.whereEqualTo("visible",true);
        query.orderByDescending("_updated_at");
        query.findInBackground(callback);
    }

    @Deprecated
    public Item addItem(String name, @Nullable SaveCallback callback){
        return new Item(name,getParent(), this, Item.ITEM,callback);
    }

    public void addItem(String itemName, @NonNull final Item.ItemCallback callback){
        final Item item = new Item();
        item.initializeVariables(itemName, getParent(), this, Item.ITEM, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    callback.done(item);
                else
                    e.printStackTrace();

            }
        });
    }

    private void addHeaderItem(String name, ShopList parent, final SaveCallback callback){
        put("header",new Item(name, parent, this, Item.HEADER, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                nullableSaveInBackground(callback);
            }
        }));
    }

    private void addCompletedHeaderItem(String name, ShopList parent, final SaveCallback callback){
        put("header",new Item(name, parent, this, Item.COMPLETED_HEADER, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                nullableSaveInBackground(callback);
            }
        }));
    }

    public Item getHeader(){
        if (getParseObject("header")!=null)
            return (Item) getParseObject("header");
        else
            throw new NullPointerException("uncategorized segment doesn't have a header");
    }

    public static Segment getSegmentById(String id){
        try {
            Segment segment = Segment.createWithoutData(Segment.class, id);
            segment.fetchIfNeeded();
            return segment;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}