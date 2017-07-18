package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by kathu228 on 7/11/17.
 */

// Item ParseObject that contains the details associated with a segment item

@ParseClassName("Item")
public class Item extends ParseObject{

    public Item(){
        //Needed for Parse
    }

    public Item(String body, boolean checked, int type){
        setBody(body);
        setChecked(checked);
        setType(type);
        saveInBackground();
    }

    // Get the body text of the Item
    public String getBody() {
        return getString("body");
    }

    // Set the body text of the Item
    public void setBody(String value) {
        put("body", value);
    }

    public void setChecked(boolean value) {
        put("checked",value);
    }

    public boolean isChecked(){
        return getBoolean("checked");
    }

    // type = 0 for item, type = 1 for header (e.g. Completed Items)
    public void setType(int type) { put("type", type); }

    public int getType() {return getInt("type");}

}
