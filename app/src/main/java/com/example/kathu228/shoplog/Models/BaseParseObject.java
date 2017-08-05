package com.example.kathu228.shoplog.Models;

import android.support.annotation.Nullable;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by fmonsalve on 7/21/17.
 *
 */

public class BaseParseObject extends ParseObject {
    public void nullableSaveInBackground(@Nullable SaveCallback callback){
        put("edit_session", ParseUser.getCurrentUser().getSessionToken());
        if (callback != null)
            saveInBackground(callback);
        else
            saveInBackground();
    }
    public void nullableDeleteInBackground(@Nullable final DeleteCallback callback){
        put("edit_session", ParseUser.getCurrentUser().getSessionToken());
        nullableSaveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (callback != null)
                    deleteInBackground(callback);
                else
                    deleteInBackground();
            }
        });

    }

    public void fetchWhenNeeded(){
        try {
            fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getEditSession(){
        fetchWhenNeeded();
        return getString("edit_session");
    }
}
