package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathu228.shoplog.Helpers.ItemAdapter;
import com.example.kathu228.shoplog.Helpers.SimpleItemTouchHelperCallback;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class ItemlistFragment extends Fragment implements SegmentDialogFragment.SegmentDialogListener{

    // parameters
    //private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvItems;
    private EditText etAddItem;
    private ImageView ivAddItem;
    public ItemAdapter itemAdapter;
    public ArrayList<Item> items;
    public String shopListObjectId;
    private FloatingActionButton fabAddSegment;
    private LinearLayout llDummy;
    private ProgressBar pbLoading;

    ShopList shopList;

    public static ItemlistFragment newInstance(String shopListObjectId, boolean isNew) {
        ItemlistFragment itemlistFragment = new ItemlistFragment();

        // Supply shopListFragment as argument
        Bundle args = new Bundle();
        args.putString(ShopList.SHOPLIST_TAG, shopListObjectId);
        args.putBoolean(ShopList.SHOPLIST_NEW_TAG, isNew);
        itemlistFragment.setArguments(args);

        return itemlistFragment;
    }

    public String getShopListObjectId() {
        return shopListObjectId;
    }

    // Call this method to launch the edit dialog
    private void showSegmentDialog() {
        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
        shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        SegmentDialogFragment segmentDialogFragment = SegmentDialogFragment.newInstance(shopListObjectId);
        // SETS the target fragment for use later when sending results
        segmentDialogFragment.setTargetFragment(this, 0);
        segmentDialogFragment.show(fm, "fragment_segment_dialog");
    }

    @Override
    public void onFinishSegmentDialog(Item segHeader) {
        // find completed header and add segment header right above
        addSegmentToUI(segHeader);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_itemlist, container, false);

        // Make sure the list with the specified shopListObjectId exists
        shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);

        if (shopListObjectId == null || ShopList.getShopListById(shopListObjectId) == null) {
            throw new IllegalStateException("ShopList " + shopListObjectId + " is null");
        }

        shopList = ShopList.getShopListById(shopListObjectId);

        // find the RecyclerView
        rvItems = (RecyclerView) v.findViewById(R.id.rvItem);

        // initialize the array of items
        items = new ArrayList<>();

        // construct the adapter
        itemAdapter = new ItemAdapter(items, shopList, v);
        // Set layout manager to position the items
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        // Attach the adapter to the recyclerview to populate items
        rvItems.setAdapter(itemAdapter);

        // Shift focus to dummy view to prevent auto-focusing on EditText
        llDummy = (LinearLayout) v.findViewById(R.id.llDummy);
        llDummy.setFocusableInTouchMode(true);
        if (!getArguments().getBoolean(ShopList.SHOPLIST_NEW_TAG)) {
            llDummy.requestFocus();
        }

        etAddItem = (EditText) v.findViewById(R.id.etAddItem);
        ivAddItem = (ImageView) v.findViewById(R.id.ivAddItem);
        pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);

        pbLoading.setVisibility(View.VISIBLE);
        // Put onclicklistener onto add button to add item to list
        ivAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        // Create add FAB and force visible (Note: must be defined before etAddItem OnEditorActionListener!)
        fabAddSegment = (FloatingActionButton) v.findViewById(R.id.fabAddSegment);
        fabAddSegment.setVisibility(View.VISIBLE);
        fabAddSegment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSegmentDialog();
            }
        });

        // Adds item from edittext if press enter or done on keyboard
        etAddItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    addItem();
                }
                return false;
            }
        });

        // 3rd-party workaround to catch keyboard open/close events
        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            fabAddSegment.setVisibility(View.INVISIBLE);
                        } else {
                            fabAddSegment.setVisibility(View.VISIBLE);
                        }
                    }
                });

            // 3rd-party workaround to catch keyboard open/close events
            KeyboardVisibilityEvent.setEventListener(
                    getActivity(),
                    new KeyboardVisibilityEventListener() {
                        @Override
                        public void onVisibilityChanged(boolean isOpen) {
                            if (isOpen) {
                                fabAddSegment.setVisibility(View.INVISIBLE);
                            } else {
                                fabAddSegment.setVisibility(View.VISIBLE);
                            }
                        }
                    });

            // attaches touchHelper to the recyclerview
            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(itemAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(rvItems);

//            //find the swipe refresh layout and add the onRefreshListener
//            swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
//            //swipeContainer.setColorSchemeResources(R.color.);
//            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    //call method to repopulate the timeline
//                    addItems();
//                    swipeContainer.setRefreshing(false);
//                }
//            });

        return v;
}

    @Override
    public void onResume() {
        super.onResume();
        //clear current items
        items.clear();
        // Populate the items array
        populateList();
    }

    @Override
    public void onPause() {
        shopList.stopLiveQueries();
        super.onPause();
    }

    public void populateList() {
        shopList.getUncategorizedSegment().getItemsInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                items.addAll(objects);
                shopList.getUncheckedSegmentItems(new FindCallback<Item>() {
                    @Override
                    public void done(List<Item> objects, ParseException e) {
                        items.addAll(objects);
                        shopList.getCheckedItems(new FindCallback<Item>() {
                            @Override
                            public void done(List<Item> objects, ParseException e) {
                                items.addAll(objects);
                                pbLoading.setVisibility(View.INVISIBLE);
                                itemAdapter.notifyDataSetChanged();
                                startLiveQueries();
                            }
                        });
                    }
                });
            }
        });

    }


    // add item to list
    public void addItem() {
        String body = etAddItem.getText().toString();
        // Does not add empty item
        if (!body.equals("")) {
            // Changed to Item.ItemCallback()
            shopList.addItem(body, new Item.ItemCallback() {
                @Override
                public void done(Item item) {
                    etAddItem.setText("");
                    items.add(0, item);
                    itemAdapter.notifyItemInserted(0);
                    rvItems.scrollToPosition(0);
                }
            });
        }
    }

    //TODO- FOX BUG WHERE ITEMS ARE ADDED TO THE UI TWICE
    public void addItems(List<String> newItems){
        final ArrayList<Item> itemsCopy = items;
        for (String itemName : newItems){
            shopList.addItem(itemName, new Item.ItemCallback() {
                @Override
                public void done(final Item item) {
                    items.add(0,item);
                    itemAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    private void startLiveQueries() {
        //Live Queries
        shopList.startItemLiveQuery(new SubscriptionHandling.HandleEventsCallback<Item>() {
            @Override
            public void onEvents(ParseQuery<Item> query, final SubscriptionHandling.Event event, final Item object) {
                if (!object.getEditSession().equals(ParseUser.getCurrentUser().getSessionToken())){
                    ItemlistFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (event){
                                case CREATE:
                                    addItemToUI(object);
                                    break;
                                case UPDATE:
                                    deleteItemFromUI(object);
                                    addItemToUI(object);
                                    break;
                                case LEAVE:
                                    deleteItemFromUI(object);
                                    break;
                                case DELETE:
                                    deleteItemFromUI(object);
                                    break;
                            }
                        }
                    });
                }
            }
        });

        shopList.startSegmentLiveQuery(new SubscriptionHandling.HandleEventsCallback<Segment>() {
            @Override
            public void onEvents(ParseQuery<Segment> query, final SubscriptionHandling.Event event, final Segment object) {
                if (!object.getEditSession().equals(ParseUser.getCurrentUser().getSessionToken())){
                    ItemlistFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (event){
                                case ENTER:
                                    addSegmentToUI(object.getHeader());
                                    break;
//                                case CREATE:
//                                    addSegmentToUI(object.getHeader());
//                                    break;
                                case UPDATE:
                                    updateSegmentInUI(object);
                                    break;
                                case DELETE:
                                    deleteItemFromUI(object.getHeader());
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    // add segment header to UI
    private void addSegmentToUI(final Item newSegHeader){
        shopList.getSegments(new FindCallback<Segment>() {
            @Override
            public void done(List<Segment> objects, ParseException e) {
                Item header = shopList.getCompletedHeader();
                if (objects.size()>1)
                    header = objects.get(1).getHeader();
                int pos = getItemIndex(header);
                items.add(pos, newSegHeader);
                itemAdapter.notifyItemInserted(pos);
            }
        });
    }

    private void updateSegmentInUI(Segment segment){
        int index = getItemIndex(segment.getHeader());
        items.remove(index);
        items.add(index,segment.getHeader());
        itemAdapter.notifyItemChanged(index);
    }

    // add item to segment if passed, else add to uncategorized (front-end)
    private void addItemToUI(Item item){
        if (item.isChecked()){
            int newPos = getItemIndex(shopList.getCompletedHeader())+1;
            items.add(newPos,item);
            itemAdapter.notifyItemInserted(newPos);
        } else {
            Segment segment = item.getSegment();
            int newPos = 0;
            if (!segment.getObjectId().equals(shopList.getUncategorizedSegment().getObjectId()))
                newPos = getItemIndex(segment.getHeader()) + 1;
            items.add(newPos, item);
            itemAdapter.notifyItemInserted(newPos);
        }
    }

    // delete item from UI
    private void deleteItemFromUI(Item item){
        int pos = getItemIndex(item);
        if (pos == -1)
            Toast.makeText(getContext(),String.format("Item %s wasn't found on the local arraylist", item.getBody()), Toast.LENGTH_LONG).show();
            //throw new IndexOutOfBoundsException(String.format("Item %s wasn't found on the local arraylist", item.getBody()));
        else {
            items.remove(pos);
            itemAdapter.notifyItemRemoved(pos);
        }
    }

    private int getItemIndex(Item item){
        for (int i=0; i<items.size(); i++){
            item.fetchWhenNeeded();
            if (item.getObjectId().equals(items.get(i).getObjectId()))
                return i;
        }
        return -1;

    }
}
