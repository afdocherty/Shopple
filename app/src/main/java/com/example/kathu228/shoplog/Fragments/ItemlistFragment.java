package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kathu228.shoplog.Helpers.ItemAdapter;
import com.example.kathu228.shoplog.Helpers.ShoplogClient;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */

public class ItemlistFragment extends Fragment{

    // parameters
    private SwipeRefreshLayout swipeContainer;
    private ShoplogClient client;
    RecyclerView rvItems;
//    RecyclerView rvCompleted;
    EditText etAddItem;
    ImageButton ibAddItem;
    ItemAdapter itemAdapter;
    ArrayList<Item> items;

    // TODO - Temporary for MVP
    ShopList list1;
    ShopList listTest;
    Segment seg1;
    Segment segTest;
    Item item1;
    Item itemTest;
    ParseUser userTest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_itemlist, container, false);
        // find the RecyclerView
        rvItems = (RecyclerView) v.findViewById(R.id.rvItem);
//        rvCompleted = (RecyclerView) v.findViewById(R.id.rvCompleted);
        // initialize the array of items
        items = new ArrayList<>();
        // construct the adapter
        itemAdapter = new ItemAdapter(items, R.layout.item);
        // Set layout manager to position the items
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        // Attach the adapter to the recyclerview to populate items
        rvItems.setAdapter(itemAdapter);

        etAddItem = (EditText) v.findViewById(R.id.etAddItem);
        ibAddItem = (ImageButton) v.findViewById(R.id.ibAddItem);

        // Populate the items array
        addItems();


        // Put onclicklistener onto add button to add item to list
        ibAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etAddItem.getText().toString();
                // Does not add empty item
                if (!body.equals("")) {
                    Item addedItem = new Item(body, false);
                    etAddItem.setText("");
                    addItemToList(addedItem);
                    addItem(addedItem);
                }

            }
        });

        // Adds item from edittext if press enter or done on keyboard
        etAddItem.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String body = etAddItem.getText().toString();
                    // Does not add empty item
                    if (!body.equals("")) {
                        Item addedItem = new Item(body, false);
                        etAddItem.setText("");
                        addItemToList(addedItem);
                        addItem(addedItem);
                    }
                }
                return false;
            }
        });

        //find the swipe refresh layout and add the onRefreshListener
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        //swipeContainer.setColorSchemeResources(R.color.);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //call method to repopulate the timeline
                addItems();
                swipeContainer.setRefreshing(false);
            }
        });

        return v;
    }

    public void addItems() {

        // TODO - Temporary for MVP to get Items for ONLY the first list -> first segment
        //ParseUser user = ParseUser.getCurrentUser();

        // Clear the items list
        items.clear();
        // Find the items from the database
        queryListForItems();
    }

    private void queryListForItems() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ShopList");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(java.util.List<ParseObject> results, ParseException e) {
                if (e == null) {
                    Log.d("ItemListFragment", "ShopList found");
                    // Grab the first list (for MVP) - TODO change
                    listTest = (ShopList) results.get(0);
                    Log.d("ItemListFragment", "listTest name: " + listTest.getName());

                    ParseRelation<ParseObject> relationListToSegment = listTest.getRelation("segments");
                    relationListToSegment.getQuery().findInBackground(new FindCallback<ParseObject>() {
                        public void done(java.util.List<ParseObject> results, ParseException e) {
                            if (e != null) {
                                // There was an error
                                Log.d("ItemListFragment", "Segment not found. Error: " + e.toString());
                                e.printStackTrace();
                            } else {
                                // results have all the segments in the list
                                Log.d("ItemListFragment", "Segment found");
                                // Grab the first segment (for MVP) - TODO change
                                segTest = (Segment) results.get(0);
                                Log.d("ItemListFragment", "listTest segment name: " + segTest.getName());
                                ParseRelation<ParseObject> relationSegmentToItem = segTest.getRelation("items");
                                relationSegmentToItem.getQuery().findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(java.util.List<ParseObject> results, ParseException e) {
                                        if (e != null) {
                                            // There was an error
                                            Log.d("ItemListFragment", "Items not found. Error: " + e.toString());
                                            e.printStackTrace();
                                        } else {
                                            // results have all the items in the segment
                                            Log.d("ItemListFragment", "Items found");

                                            // Add the items to the items arraylist
                                            for (ParseObject parseObject : results) {
                                                items.add((Item) parseObject);


                                                Log.d("ItemListFragment", "Added item " + ((Item) parseObject).getBody() + " to items ArrrayList");
                                            }
                                            Collections.reverse(items);
                                            itemAdapter.notifyDataSetChanged();
//                                            itemTest = items.get(0);
//                                            Log.d("ItemListFragment", "segTest item name: " + itemTest.getBody());
                                        }
                                    }
                                });

                            }
                        }
                    });
                } else {
                    Log.d("ItemListFragment", "ShopList not found. Error: " + e.toString());
                }
            }
        });
    }

    // Add an item to the MVP list
    private void addItemToList (final Item item) {
        // MVP Hack to jump straight to Segment - TODO change
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Segment");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("ItemListFragment", "Segment found");
                    // Grab the first segment (for MVP) - TODO change
                    ParseRelation<ParseObject> relationSegmentToItem = objects.get(0).getRelation("items");
                    relationSegmentToItem.add(item);
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("ItemListFragment", "Item added!");
                            } else {
                                Log.d("ItemListFragment", "Item not added. Error: " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.d("ItemListFragment", "Segment not found. Error: " + e.toString());
                }
            }
        });
    }

    // Remove an item from the MVP list
    private void removeItemFromList (final Item item) {
        // MVP Hack to jump straight to Segment - TODO change
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Segment");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.d("ItemListFragment", "Segment found");
                    // Grab the first segment (for MVP) - TODO change
                    ParseRelation<ParseObject> relationSegmentToItem = objects.get(0).getRelation("items");
                    relationSegmentToItem.remove(item);
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("ItemListFragment", "Item removed!");
                            } else {
                                Log.d("ItemListFragment", "Item not removed. Error: " + e.toString());
                                e.printStackTrace();
                            }
                        }
                    });
                    item.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("ItemListFragment", "Item deleted");
                            }
                            else {
                                Log.d("ItemListFragment", "Item not deleted. Error: " + e.toString());
                            }
                        }
                    });

                } else {
                    Log.d("ItemListFragment", "Segment not found. Error: " + e.toString());
                }

            }
        });
    }

    // add item to list
    public void addItem(Item item){
        items.add(0, item);
        itemAdapter.notifyItemInserted(0);
        rvItems.scrollToPosition(0);
    }
}
