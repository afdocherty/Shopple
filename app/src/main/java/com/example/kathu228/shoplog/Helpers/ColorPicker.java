package com.example.kathu228.shoplog.Helpers;

import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

/**
 * Created by afdoch on 7/28/17.
 */

public class ColorPicker {

    // Kelly's 22 colors of maximum contrast (20 below since white and black are excluded)
    private static final int[] colors = {
            R.color.new_1,
            R.color.new_2,
            R.color.new_3,
            R.color.new_4,
            R.color.new_5,
            R.color.new_6,
            R.color.new_7,
            R.color.new_8,
    };

    // Method to return a new color and color number for the list to use
    public static int[] getNewColor(String shopListObjectId) {
        ShopList shopList = ShopList.getShopListById(shopListObjectId);
        if (shopList == null) {
            throw new IllegalStateException("ShopList " + shopListObjectId + " is null");
        }
        int[] results = new int[2];
        // Get the current color number of the list
        results[0] = shopList.getColorNum();
        // Get the color associated with that number (wraps around)
        results[1] = colors[results[0] % colors.length];
        // Increment color number for future use
        shopList.incrementColorNum(null);
        // Return the results bundled in an array
        return results;
    }

    public static int getColor(int colorNum){
        return colors[colorNum % colors.length];
    }
}
