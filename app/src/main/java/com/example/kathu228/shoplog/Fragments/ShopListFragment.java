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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.kathu228.shoplog.Activities.ItemListActivity;
import com.example.kathu228.shoplog.Helpers.ShoplistAdapter;
import com.example.kathu228.shoplog.Helpers.SimpleItemTouchHelperCallback;
import com.example.kathu228.shoplog.Models.Query;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by afdoch on 7/18/17.
 */

public class ShopListFragment extends Fragment {

    private ShoplistAdapter shoplistAdapter;
    private List<ShopList> shopLists;
    private RecyclerView rvShopList;
    private SwipeRefreshLayout swipeContainer;
    private FloatingActionButton fabAddShopList;
    private RelativeLayout emptyState;
    private LinearLayout llDummy;
    private LottieAnimationView animationView;

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
        emptyState = (RelativeLayout) v.findViewById(R.id.rlNoLists);
        animationView = (LottieAnimationView) v.findViewById(R.id.animation_view);

        // Add ShopList FAB
        fabAddShopList = (FloatingActionButton) v.findViewById(R.id.fabAddShopList);
        // On click, create the list and jump into it
        fabAddShopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new list as the current user, automatically naming it w/ date
                ShopList.getInstance("List on " + formatNewListDate(), new ShopList.ShoplistCallback() {
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
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.rlContainer);
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
        startLiveQueries();
    }

    @Override
    public void onPause() {
        super.onPause();
        Query.stopShoplistsLiveQuery();
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
            emptyState.setVisibility(View.VISIBLE);
        }
        else{
            emptyState.setVisibility(View.GONE);
        }
    }

    // Get date in specified format for time-stamping new, unnamed lists
    private String formatNewListDate() {
        // (1) get today's date
        Date today = Calendar.getInstance().getTime();
        // (2) create a date "formatter" (the date format we want)
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy", Locale.US);
        // (3) create a new String using the date format we want
        return formatter.format(today);
    }

    private void startLiveQueries(){
        Query.startShoplistsLiveQuery(new SubscriptionHandling.HandleEventsCallback<ShopList>() {
            @Override
            public void onEvents(ParseQuery<ShopList> query, final SubscriptionHandling.Event event, final ShopList object) {
                object.containsUser(ParseUser.getCurrentUser(), new ShopList.BooleanCallback() {
                    @Override
                    public void done(boolean bool) {
                        if (bool){
                            ShopListFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addShopListsFromDatabase();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}

