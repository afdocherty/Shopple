package com.example.kathu228.shoplog.Models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by kathu228 on 7/11/17.
 */

// Item ParseObject that contains the details associated with a segment item

@ParseClassName("Item")
public class Item extends ParseObject{

    public Item(){
        //Needed for Parse
    }

    public Item(String body, boolean checked){
        setBody(body);
        setChecked(checked);
        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("Item", "Item saved");
                } else {
                    Log.d("Item", "Item not saved. Error: " + e.toString());
                }
            }
        });
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
}
