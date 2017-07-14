package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by afdoch on 7/12/17.
 */

// Segment ParseObject that is contained in a list and whih wraps multiple items of a list together

@ParseClassName("Segment")
public class Segment extends ParseObject {

    public Segment(){
        //required for Parse
    }

    public Segment(String name, String color){
        setName(name);
        setColor(color);
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

}
