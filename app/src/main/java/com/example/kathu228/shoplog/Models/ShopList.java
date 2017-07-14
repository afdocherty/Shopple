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

    // TODO - create relation to User, so on the collaborators view you can query the relation
    // for all the users in it, i.e.
    //      ParseQuery<ParseObject> query = relation.getQuery();
    // Or even specify for the current user within the query for the ShopList fragment, i.e.
    //      query.whereEqualTo("user", ParseUser.getCurrentUser());
}
