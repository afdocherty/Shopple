package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by kathu228 on 7/11/17.
 */

// Item ParseObject that contains the details associated with a segment item

@ParseClassName("Item")
public class Item extends ParseObject{

    // Get the body text of the Item
    public String getBody() {
        return getString("body");
    }

    // Set the body text of the Item
    public void setBody(String value) {
        put("body", value);
    }
}
