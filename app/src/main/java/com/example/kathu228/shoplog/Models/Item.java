package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by kathu228 on 7/11/17.
 *
 */

// Item ParseObject that contains the details associated with a segment item

@ParseClassName("Item")
public class Item extends ParseObject{

    public Item(){
        //Needed for Parse
    }

    Item(String body, ShopList parent, Segment segment){
        setBody(body);
        setChecked(false);
        setParent(parent);
        setSegment(segment);
        setVisible(true);
        setType(0);
        saveInBackground();
    }

    Item(String body, ShopList parent, Segment segment, int type){
        setBody(body);
        setChecked(false);
        setParent(parent);
        setSegment(segment);
        setVisible(true);
        setType(type);
        saveInBackground();
    }

    // Get the body text of the Item
    public String getBody() {
        return getString("body");
    }

    // Set the body text of the Item
    private void setBody(String value) {
        put("body", value);
    }

    public void setChecked(boolean value) {
        put("checked",value);
        saveInBackground();
        if (value)
            setSegment(getParent().getCompletedSegment());
        else
            setSegment(getParent().getUncategorizedSegment());
    }

    public boolean isChecked(){
        return getBoolean("checked");
    }

    public void setVisible(boolean value){
        put("visible",value);
        saveInBackground();
    }

    public boolean isVisible(){
        return getBoolean("visible");
    }

    // type = 0 for item, type = 1 for header (e.g. Completed Items)
    public void setType(int type) {
        put("type", type);
        saveInBackground();
    }

    public int getType() {
        return getInt("type");
    }

    private void setParent(ShopList parent){
        put("parent",parent);
    }

    public ShopList getParent(){
        return (ShopList) getParseObject("parent");
    }

    public void setSegment(Segment segment){
        put("segment",segment);
        saveInBackground();
    }

    public Segment getSegment(){
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
