package com.example.kathu228.shoplog.Helpers;

/**
 * Created by kathu228 on 7/17/17.
 * Allows implementation of swiping and dragging items.
 * ItemDismiss takes position of swiping for an item,
 * while ItemMove drags item from fromPosition to toPosition.
 */

public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
