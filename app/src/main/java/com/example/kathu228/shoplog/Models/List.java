package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by afdoch on 7/12/17.
 */

// List ParseObject that wraps multiple segments of a list together

@ParseClassName("List")
public class List extends ParseObject {

    // Get the name of the List
    public String getName() {
        return getString("name");
    }

    // Set the name of the List
    public void setName(String value) {
        put("name", value);
    }
}
