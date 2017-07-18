package com.example.kathu228.shoplog;

import android.app.Application;

import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * Created by afdoch on 7/12/17.
 */

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        ParseObject.registerSubclass(ShopList.class);
        ParseObject.registerSubclass(Segment.class);
        ParseObject.registerSubclass(Item.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.parse_app_id))
                .clientKey(null)
                .server(getResources().getString(R.string.server) + "/parse").build());

        ParseFacebookUtils.initialize(this);
    }
}
