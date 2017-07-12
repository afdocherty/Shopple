package com.example.kathu228.shoplog.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathu228 on 7/11/17.
 */

public class Item implements Parcelable{

    public String body;
    public boolean  checked;

    protected Item(Parcel in) {
        this.body = in.readString();
        this.checked = in.readByte() != 0;
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
        dest.writeString(this.body);
        dest.writeByte((byte) (this.checked ? 1 : 0));
    }
}
