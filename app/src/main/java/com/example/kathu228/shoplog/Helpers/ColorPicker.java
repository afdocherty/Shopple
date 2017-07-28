package com.example.kathu228.shoplog.Helpers;

import android.graphics.Color;

import java.util.HashSet;

/**
 * Created by afdoch on 7/28/17.
 */

public class ColorPicker {

    private static final int[] colors = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.GRAY, Color.DKGRAY};

    public int getNewColor(HashSet<Integer> existingColors) {
        for (int i : colors) {
            if (!existingColors.contains(i)) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException("No more colors!");
    }
}
