package com.example.kathu228.shoplog.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kathu228.shoplog.Activities.ItemListActivity;
import com.example.kathu228.shoplog.Helpers.ShoplistAdapter;
import com.example.kathu228.shoplog.Helpers.SimpleItemTouchHelperCallback;
import com.example.kathu228.shoplog.Models.Query;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by afdoch on 7/18/17.
 */

public class ShopListFragment extends Fragment {

    private ShoplistAdapter shoplistAdapter;
    private List<ShopList> shopLists;
    private RecyclerView rvShopList;
    private SwipeRefreshLayout swipeContainer;
    private FloatingActionButton fabAddShopList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shoplist,container,false);

        rvShopList = (RecyclerView) v.findViewById(R.id.rvShopList);
        // initialize the array of items
        shopLists = new ArrayList<>();
        // construct the adapter
        shoplistAdapter = new ShoplistAdapter(shopLists, R.layout.item_list);
        // Set layout manager to position the items
        rvShopList.setLayoutManager(new LinearLayoutManager(getContext()));
        // Attach the adapter to the recyclerview to populate items
        rvShopList.setAdapter(shoplistAdapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(shoplistAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvShopList);

        // Add ShopList FAB
        fabAddShopList = (FloatingActionButton) v.findViewById(R.id.fabAddShopList);
        // On click, create the list and jump into it
        fabAddShopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                // Create a new list as the current user, automatically naming it w/ timestamp
                ShopList shopList = new ShopList("New List at " + currentDateTimeString,null);
                //Query.addUserToShoplist(ParseUser.getCurrentUser(), shopList);
                addShopList(shopList);

                Intent intent = new Intent(getActivity(), ItemListActivity.class);
                // Pass in ShopList ObjectId
                intent.putExtra(ShopList.SHOPLIST_TAG, shopList.getObjectId());
                intent.putExtra("listName", shopList.getName());
                startActivity(intent);
            }
        });

        //find the swipe refresh layout and add the onRefreshListener
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        //swipeContainer.setColorSchemeResources(R.color.);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //call method to repopulate the timeline
                addShopListsFromDatabase();
                swipeContainer.setRefreshing(false);
            }
        });

        return v;
    }

    // Refresh the list on resume
    @Override
    public void onResume() {
        super.onResume();
        // Populate the ShopList array
        addShopListsFromDatabase();
    }

    // Add the ShopLists for the current user to the database
    private void addShopListsFromDatabase() {
        // Clear the shopLists ArrayList
        shopLists.clear();
        // Populate shopLists with the Lists associated with the current user
        Query.findShoplistsForUser(ParseUser.getCurrentUser(), new FindCallback<ShopList>() {
            @Override
            public void done(List<ShopList> objects, ParseException e) {
                if (e == null) {
                    Log.d("ShopListFragment", "User lists loaded");
                    shopLists.addAll(objects);
                    shoplistAdapter.notifyDataSetChanged();
                } else {
                    Log.d("ShopListFragment", "User lists not loaded. Error: " + e.toString());
                }
            }
        });
    }

    // add ShopList to list
    public void addShopList(ShopList shopList){
        shopLists.add(0, shopList);
        shoplistAdapter.notifyItemInserted(0);
        rvShopList.scrollToPosition(0);
    }
}
