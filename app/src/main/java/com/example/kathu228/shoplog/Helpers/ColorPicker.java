package com.example.kathu228.shoplog.Helpers;

import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

/**
 * Created by afdoch on 7/28/17.
 */

public class ColorPicker {

    // Kelly's 22 colors of maximum contrast (20 below since white and black are excluded)
    private static final int[] colors = {
            R.color.kelly_1,
            R.color.kelly_2,
            R.color.kelly_3,
            R.color.kelly_4,
            R.color.kelly_5,
            R.color.kelly_6,
            R.color.kelly_7,
            R.color.kelly_8,
            R.color.kelly_9,
            R.color.kelly_10,
            R.color.kelly_11,
            R.color.kelly_12,
            R.color.kelly_13,
            R.color.kelly_14,
            R.color.kelly_15,
            R.color.kelly_16,
            R.color.kelly_17,
            R.color.kelly_18,
            R.color.kelly_19,
            R.color.kelly_20,
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
        results[1] = colors[results[0] % 20];
        // Increment color number for future use
        shopList.incrementColorNum(null);
        // Return the results bundled in an array
        return results;
    }
}
