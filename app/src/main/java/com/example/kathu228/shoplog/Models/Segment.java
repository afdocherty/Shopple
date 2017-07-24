package com.example.kathu228.shoplog.Models;

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

    static final int UNCATEGORIZED_SEGMENT=0;
    static final int COMPLETED_SEGMENT=2;
    static final int ADDITIONAL_SEGMENT=1;
    int type;

    public Segment(){
        //required for Parse
    }

    Segment(String name, ShopList parentList, int segmentType, @Nullable SaveCallback callback){
        put("name", name);
        setParent(parentList);
        type = segmentType;
        switch (segmentType){
            case UNCATEGORIZED_SEGMENT:
                setSegmentType(UNCATEGORIZED_SEGMENT);
            case COMPLETED_SEGMENT:
                addCompletedHeaderItem(name, parentList, callback);
                setSegmentType(COMPLETED_SEGMENT);
            case ADDITIONAL_SEGMENT:
                addHeaderItem(name, parentList, callback);
                setSegmentType(ADDITIONAL_SEGMENT);
        }
    }

    // Get the name of the Segment
    public String getName() {
        return getString("name");
    }

    // Set the name of the Segment
    public void setName(String value, @Nullable SaveCallback callback) {
        put("name", value);

        nullableSaveInBackground(callback);
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

    public Item addItem(String name, @Nullable SaveCallback callback){
        return new Item(name,getParent(), this, Item.ITEM,callback);
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

    public void setSegmentType(int segmentType){
        type = segmentType;
    }

    public boolean isUncategorized(){
        return (type==UNCATEGORIZED_SEGMENT);
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
