package com.example.kathu228.shoplog.Models;

import android.support.annotation.Nullable;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by fmonsalve on 7/21/17.
 *
 */

public class BaseParseObject extends ParseObject {
    public void nullableSaveInBackground(@Nullable SaveCallback callback){
        if (callback != null)
            saveInBackground(callback);
        else
            saveInBackground();
    }
    public void nullableDeleteInBackground(@Nullable DeleteCallback callback){
        if (callback != null)
            deleteInBackground(callback);
        else
            deleteInBackground();
    }

    public void fetchWhenNeeded(){
        try {
            fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
