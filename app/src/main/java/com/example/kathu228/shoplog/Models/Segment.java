package com.example.kathu228.shoplog.Models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.kathu228.shoplog.Helpers.ColorPicker;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

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
    static final int ADDITIONAL_SEGMENT=1;

    public Segment(){
        //required for Parse
    }

    //TODO- Finish this (After presentation)
//    public static Segment createUncategorizedSegment() {
//        Segment s = new Segment();
//
//    }

    Segment(String name, ShopList parentList, int segmentType, @Nullable SaveCallback callback){
        put("name", name);
        Date date = new Date();
        put("created_time",date);
        setParent(parentList);

        switch (segmentType){
            case ADDITIONAL_SEGMENT:
                addHeaderItem(name, parentList, date, callback);
                break;
            default:
                nullableSaveInBackground(callback);
                break;
        }
    }

    void initializeVariables(String name, ShopList parentList, int segmentType, @Nullable SaveCallback callback){
        put("name", name);
        Date date = new Date();
        put("created_time",date);
        setParent(parentList);

        switch (segmentType){
            case ADDITIONAL_SEGMENT:
                int colorNum = ColorPicker.getNewColor(parentList.getObjectId())[0];
                put("color_number", colorNum);
                addHeaderItem(name, parentList, date, callback);
                break;
            default:
                nullableSaveInBackground(callback);
                break;
        }
    }

    // Get the name of the Segment
    public String getName() {
        fetchWhenNeeded();
        return getString("name");
    }

    // Set the name of the Segment
    public void setName(String value, @Nullable final SaveCallback callback) {
        put("name", value);

        Item header = getHeader();
        if (header != null){
            getHeader().setBody(value);
            getHeader().nullableSaveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    nullableSaveInBackground(callback);
                }
            });
        } else
            nullableSaveInBackground(callback);

    }

    public ShopList getParent(){
        fetchWhenNeeded();
        return (ShopList) getParseObject("parent_list");
    }

    private void setParent(ShopList parentList){
        put("parent_list",parentList);
    }

    public void getItemsInBackground(final FindCallback<Item> callback){
        ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
        query.whereEqualTo("segment",this);
        query.whereEqualTo("visible",true);
        query.whereEqualTo("checked",false);
        query.whereEqualTo("type",Item.ITEM);
        query.orderByDescending("_updated_at");
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                fetchWhenNeeded();
                if (getParseObject("header") != null)
                    objects.add(0,getHeader());
                callback.done(objects,e);
            }
        });
    }

    public List<Item> getItems(){
        ParseQuery<Item> query = new ParseQuery<>(Item.class);
        query.whereEqualTo("segment",this);
        query.whereEqualTo("visible",true);
        query.whereEqualTo("checked",false);
        query.whereEqualTo("type",Item.ITEM);
        query.orderByDescending("_updated_at");
        try {
            List<Item> list = query.find();
            fetchWhenNeeded();
            if (getParseObject("header") != null)
                list.add(0,getHeader());
            return list;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new NullPointerException("Error in getting items from fragment");
        }
    }

//    @Deprecated
//    public Item addItem(String name, @Nullable SaveCallback callback){
//        return new Item(name,getParent(), this, Item.ITEM,callback);
//    }

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

    private void addHeaderItem(String name, ShopList parent, Date date, final SaveCallback callback){
        put("header",new Item(name, parent, this, Item.HEADER, date, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                nullableSaveInBackground(callback);
            }
        }));
    }

    public Item getHeader(){
        fetchWhenNeeded();
        if (getParseObject("header")!=null)
            return (Item) getParseObject("header");
        else
            throw new NullPointerException("uncategorized segment doesn't have a header");
    }

    Date getCreatedTime(){
        fetchWhenNeeded();
        return getDate("created_time");
    }

    public int getColorNum(){
        fetchWhenNeeded();
        return getInt("color_number");
    }

    @Nullable
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