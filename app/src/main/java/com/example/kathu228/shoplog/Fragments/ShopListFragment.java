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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathu228.shoplog.Activities.ItemListActivity;
import com.example.kathu228.shoplog.Helpers.ShoplistAdapter;
import com.example.kathu228.shoplog.Helpers.SimpleItemTouchHelperCallback;
import com.example.kathu228.shoplog.Models.Query;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

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
    private TextView tvDirection;
    private ImageView ivDirection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shoplist,container,false);

        Log.d("debug","hello");

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

        // Add directions to make new list
        tvDirection = (TextView) v.findViewById(R.id.tvDirection);
        ivDirection = (ImageView) v.findViewById(R.id.ivDirection);

        // Add ShopList FAB
        fabAddShopList = (FloatingActionButton) v.findViewById(R.id.fabAddShopList);
        // On click, create the list and jump into it
        fabAddShopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO format better
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                // Create a new list as the current user, automatically naming it w/ timestamp
                ShopList.getInstance("New List on " + currentDateTimeString, new ShopList.ShoplistCallback() {
                    @Override
                    public void done(ShopList list) {
                        //Query.addUserToShoplist(ParseUser.getCurrentUser(), shopList);
                        addShopList(list);

                        Intent intent = new Intent(getActivity(), ItemListActivity.class);
                        // Pass in ShopList ObjectId
                        intent.putExtra(ShopList.SHOPLIST_TAG, list.getObjectId());
                        intent.putExtra(ShopList.SHOPLIST_NEW_TAG, true);
                        startActivity(intent);
                    }
                });
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


        //TODO- Live queries

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<ShopList> query = ParseQuery.getQuery(ShopList.class);
        query.whereEqualTo("users", ParseUser.getCurrentUser());
        SubscriptionHandling<ShopList> subscriptionHandling = parseLiveQueryClient.subscribe(query);
        subscriptionHandling.handleEvents(new SubscriptionHandling.HandleEventsCallback<ShopList>() {
            @Override
            public void onEvents(ParseQuery<ShopList> query, SubscriptionHandling.Event event, ShopList object) {
                Log.d("debug", "User has been added to a list");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "User has been added to a list", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<ShopList>() {
            @Override
            public void onEvent(ParseQuery<ShopList> query, ShopList object) {
                Log.d("debug", "User has been added to a list 1");
            }
        });

//        Query.listenForNewLists(ParseUser.getCurrentUser(), new SubscriptionHandling.HandleEventCallback() {
//            @Override
//            public void onEvent(ParseQuery query, ParseObject object) {
//                Toast.makeText(getContext(),"User added to new list",Toast.LENGTH_LONG).show();
//                //shopLists.add(0,(ShopList)object);
//            }
//        });

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
                    addDirections();
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

    // shows directions if no lists and hides if there are
    private void addDirections(){
        if (shopLists.size()==0){
            tvDirection.setVisibility(View.VISIBLE);
            ivDirection.setVisibility(View.VISIBLE);
        }
        else{
            tvDirection.setVisibility(View.GONE);
            ivDirection.setVisibility(View.GONE);
        }
    }
}
