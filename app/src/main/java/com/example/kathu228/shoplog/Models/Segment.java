package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

/**
 * Created by afdoch on 7/12/17.
 */

// Segment ParseObject that is contained in a list and whih wraps multiple items of a list together

@ParseClassName("Segment")
public class Segment extends ParseObject {

    public Segment(){
        //required for Parse
    }

    public Segment(String name, ShopList parentList){
        setName(name);
        setParent(parentList);

        saveInBackground();
    }

    public Segment(String name, ShopList parentList, List<Item> items){
        setName(name);
        setParent(parentList);
        addItems(items);

        saveInBackground();
    }

    // Get the name of the Segment
    public String getName() {
        return getString("name");
    }

    // Set the name of the Segment
    public void setName(String value) {
        put("name", value);
    }

    public String getColor(){
        return getString("color");
    }

    public void setColor(String color){
        put("color",color);
    }

//    public List getItems(){
//
//    }

    public void addItems(List<Item> items){

    }

    public void addItem(Item item){

    }

    public ShopList getParent(){
        return (ShopList) getParseObject("parent_list");
    }

    public void setParent(ShopList parentList){
        put("parent_list",parentList);
    }

}
