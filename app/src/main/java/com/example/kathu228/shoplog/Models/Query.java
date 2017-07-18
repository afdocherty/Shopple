package com.example.kathu228.shoplog.Models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by fmonsalve on 7/17/17.
 */

public class Query {
    public static List<User> getUsers(){
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                for (User user:objects){
                    Log.d("Users",user.getUsername());
                }
            }
        });
        try {
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        //ParseUser.getQuery().find();

    }
}
