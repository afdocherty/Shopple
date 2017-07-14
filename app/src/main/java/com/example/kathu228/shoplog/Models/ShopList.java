package com.example.kathu228.shoplog.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by afdoch on 7/12/17.
 */

// ShopList ParseObject that wraps multiple segments of a list together

@ParseClassName("ShopList")
public class ShopList extends ParseObject {

    public ShopList(){
        //required for Parse
    }

    public ShopList(String name){
        setName(name);
    }

    // Get the name of the ShopList
    public String getName() {
        return getString("name");
    }

    // Set the name of the ShopList
    public void setName(String value) {
        put("name", value);
    }
}
