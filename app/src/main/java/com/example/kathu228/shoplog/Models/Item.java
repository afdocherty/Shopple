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
        if (segment != null)
            put("segment_created_at",segment.getCreatedAt());
        setBody(body);
        setParent(parent);
        if (segment != null)
            put("segment",segment);
        put("checked",false);
        put("visible",true);
        setType(type);
        nullableSaveInBackground(callback);
    }

    void initializeVariables(String body, ShopList parent, Segment segment, int type, @Nullable SaveCallback callback){
        if (segment != null )
            put("segment_created_at",segment.getCreatedAt());
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
        fetchWhenNeeded();
        return getString("body");
    }

    // Set the body text of the Item
    void setBody(String value) {
        put("body", value);
    }

    public void setChecked(boolean value, @Nullable SaveCallback callback) {
        put("checked",value);
        nullableSaveInBackground(callback);
    }

    public boolean isChecked(){
        fetchWhenNeeded();
        return getBoolean("checked");
    }

    public void setVisible(boolean value, @Nullable SaveCallback callback){
        put("visible",value);
        nullableSaveInBackground(callback);
    }

    public boolean isVisible(){
        fetchWhenNeeded();
        return getBoolean("visible");
    }

    // type = 0 for item, type = 1 for header (e.g. Completed Items)
    private void setType(int type) {
        put("type", type);
    }

    public int getType() {
        fetchWhenNeeded();
        return getInt("type");
    }

    public boolean isItem() {
        fetchWhenNeeded();
        return (getInt("type")==ITEM);
    }

    public boolean isHeader() {
        fetchWhenNeeded();
        return (getInt("type")==HEADER);
    }

    public boolean isCompletedHeader() {
        fetchWhenNeeded();
        return (getInt("type")==COMPLETED_HEADER);
    }

    private void setParent(ShopList parent){
        put("parent",parent);
    }

    public ShopList getParent(){
        fetchWhenNeeded();
        return (ShopList) getParseObject("parent");
    }

    public void setSegment(Segment segment, @Nullable SaveCallback callback) {
        if (isCompletedHeader())
            throw new NullPointerException("CompleteHeader doesn't have a segment");
        put("segment_created_at",segment.getCreatedAt());
        put("segment",segment);
        nullableSaveInBackground(callback);
    }

    public Segment getSegment(){
        if (isCompletedHeader())
            throw new NullPointerException("CompleteHeader doesn't have a segment");
        fetchWhenNeeded();
        return (Segment) getParseObject("segment");
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
