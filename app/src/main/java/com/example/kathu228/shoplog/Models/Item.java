package com.example.kathu228.shoplog.Models;

import android.support.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Created by kathu228 on 7/11/17.
 *
 */

// Item ParseObject that contains the details associated with a segment item

@ParseClassName("Item")
public class Item extends BaseParseObject{

    public interface ItemCallback{
        void done(Item item);
    }

    static final int ITEM = 0;
    static final int HEADER = 1;
    static final int COMPLETED_HEADER = 2;

    public Item(){
        //Needed for Parse
    }

    Item(String body, ShopList parent, Segment segment, int type, @Nullable SaveCallback callback){
        setBody(body);
        setParent(parent);
        put("segment",segment);
        put("checked",false);
        put("visible",true);
        setType(type);
        nullableSaveInBackground(callback);
    }

    void initializeVariables(String body, ShopList parent, Segment segment, int type, @Nullable SaveCallback callback){
        setBody(body);
        setParent(parent);
        put("segment",segment);
        put("checked",false);
        put("visible",true);
        setType(type);
        nullableSaveInBackground(callback);
    }

    // Get the body text of the Item
    public String getBody() {
        return getString("body");
    }

    // Set the body text of the Item
    void setBody(String value) {
        put("body", value);
    }

    public void setChecked(boolean value, @Nullable SaveCallback callback) {
        put("checked",value);
        if (value)
            put("segment",getParent().getCompletedSegment());

        else
            put("segment",getParent().getUncategorizedSegment());

        nullableSaveInBackground(callback);
    }

    public boolean isChecked(){
        return getBoolean("checked");
    }

    public void setVisible(boolean value, @Nullable SaveCallback callback){
        put("visible",value);
        nullableSaveInBackground(callback);
    }

    public boolean isVisible(){
        return getBoolean("visible");
    }

    // type = 0 for item, type = 1 for header (e.g. Completed Items)
    private void setType(int type) {
        put("type", type);
    }

    public int getType() {
        return getInt("type");
    }

    public boolean isItem() {
        int type = getType();
        return (getInt("type")==ITEM);
    }

    public boolean isHeader() {
        return (getInt("type")==HEADER);
    }

    public boolean isCompletedHeader() {
        return (getInt("type")==COMPLETED_HEADER);
    }

    private void setParent(ShopList parent){
        put("parent",parent);
    }

    public ShopList getParent(){
        return (ShopList) getParseObject("parent");
    }

    public void setSegment(Segment segment, @Nullable SaveCallback callback){
        put("segment",segment);
        nullableSaveInBackground(callback);
    }

    public Segment getSegment(){
        return (Segment) getParseObject("segment");
    }

    public String getSegmentName(){
        Segment segment = (Segment) getParseObject("segment");
        return segment.getName();
    }

    public static Item getItemById(String id){
        try {
            Item item = Item.createWithoutData(Item.class, id);
            item.fetchIfNeeded();
            return item;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
