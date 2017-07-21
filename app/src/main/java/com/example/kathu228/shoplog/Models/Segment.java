package com.example.kathu228.shoplog.Models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Created by afdoch on 7/12/17.
 *
 */

// Segment ParseObject that is contained in a list and whih wraps multiple items of a list together

@ParseClassName("Segment")
public class Segment extends ParseObject {

    public Segment(){
        //required for Parse
    }

    Segment(String name, ShopList parentList){
        put("name", name);
        setParent(parentList);
        saveInBackground();
    }

    Segment(String name, ShopList parentList, SaveCallback callback){
        put("name", name);
        setParent(parentList);
        saveInBackground(callback);
    }

    // Get the name of the Segment
    public String getName() {
        return getString("name");
    }

    // Set the name of the Segment
    public void setName(String value) {
        put("name", value);
        saveInBackground();
    }

//    public String getColor(){
//        return getString("color");
//    }
//
//    public void setColor(String color){
//        put("color",color);
//    }

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

    public Item addItem(String name){
        return new Item(name,getParent(), this, Item.ITEM);
    }

    public Item addHeaderItem(String name){
        return new Item(name,getParent(), this, Item.HEADER);
    }

    public Item addComletedHeaderItem(String itemName){
        return new Item(itemName,getParent(), this, Item.COMPLETED_HEADER);
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
