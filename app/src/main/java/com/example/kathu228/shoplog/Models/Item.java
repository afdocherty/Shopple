package com.example.kathu228.shoplog.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathu228 on 7/11/17.
 */

public class Item implements Parcelable{
    protected Item(Parcel in) {
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
