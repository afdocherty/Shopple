package com.example.kathu228.shopple.Helpers;

import android.util.Log;

import com.example.kathu228.shopple.Models.Item;
import com.example.kathu228.shopple.Models.Segment;
import com.example.kathu228.shopple.Models.ShopList;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by fmonsalve on 7/25/17.
 */

@Deprecated
public class DEBUGGING {

    public static void test1(){
        ShopList.getInstance("list1", new ShopList.ShoplistCallback() {
            @Override
            public void done(final ShopList list) {
                list.addItem("milk", new Item.ItemCallback() {
                    @Override
                    public void done(Item item) {
                        final Item milk = item;
                        item.setChecked(true, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                list.addItem("eggs", new Item.ItemCallback() {
                                    @Override
                                    public void done(final Item item) {
                                        list.addItem("cramberries", new Item.ItemCallback() {
                                            @Override
                                            public void done(final Item item) {
                                                list.addItem("strawberries", new Item.ItemCallback() {
                                                    @Override
                                                    public void done(final Item item) {
                                                        item.setChecked(true, new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                list.addItem("apples", new Item.ItemCallback() {
                                                                    @Override
                                                                    public void done(final Item item) {
                                                                        list.addSegment("Fruits", new Segment.SegmentCallback() {
                                                                            @Override
                                                                            public void done(Segment segment) {
                                                                                segment.addItem("raspberry", new Item.ItemCallback() {
                                                                                    @Override
                                                                                    public void done(Item item) {
                                                                                        milk.setChecked(false, new SaveCallback() {
                                                                                            @Override
                                                                                            public void done(ParseException e) {
                                                                                                list.getSegments(new FindCallback<Segment>() {
                                                                                                    @Override
                                                                                                    public void done(final List<Segment> segments, ParseException e) {
                                                                                                        list.getUncategorizedSegment().getItemsInBackground(new FindCallback<Item>() {
                                                                                                            @Override
                                                                                                            public void done(List<Item> uncategorizedItems, ParseException e) {
                                                                                                                for (Item item : uncategorizedItems){
                                                                                                                    Log.d("debug",item.getBody());
                                                                                                                }
                                                                                                                for(Segment segment : segments){
                                                                                                                    List<Item> items = segment.getItems();
                                                                                                                    for (Item item : items){
                                                                                                                        Log.d("debug",item.getBody());
                                                                                                                    }
                                                                                                                }
                                                                                                                list.getCheckedItems(new FindCallback<Item>() {
                                                                                                                    @Override
                                                                                                                    public void done(List<Item> objects, ParseException e) {
                                                                                                                        for (Item item : objects){
                                                                                                                            Log.d("debug",item.getBody());
                                                                                                                        }
                                                                                                                    }
                                                                                                                });

                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
