package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kathu228.shoplog.Helpers.ItemAdapter;
import com.example.kathu228.shoplog.Helpers.SimpleItemTouchHelperCallback;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */

public class ItemlistFragment extends Fragment implements SegmentDialogFragment.SegmentDialogListener{

    // parameters
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView rvItems;
    private EditText etAddItem;
    private ImageButton ibAddItem;
    private ItemAdapter itemAdapter;
    private ArrayList<Item> items;

    public String shopListObjectId;
    private FloatingActionButton fabAddSegment;
    private LinearLayout llDummy;

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
        FragmentTransaction ft = fm.beginTransaction();
        shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        SegmentDialogFragment segmentDialogFragment = SegmentDialogFragment.newInstance(shopListObjectId);
        // SETS the target fragment for use later when sending results
        segmentDialogFragment.setTargetFragment(this, 0);
        segmentDialogFragment.show(fm, "fragment_segment_dialog");
    }

    @Override
    public void onFinishSegmentDialog(Item segHeader) {
        // find completed header and add segment header right above
        items.add(segHeader);
        addItems();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_itemlist, container, false);

        try {
            // get id of shoplist
            shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
            Log.d("ItemlistFragment", "objId: " + shopListObjectId);
            shopList = ShopList.getShopListById(shopListObjectId);

            // find the RecyclerView
            rvItems = (RecyclerView) v.findViewById(R.id.rvItem);
//        rvCompleted = (RecyclerView) v.findViewById(R.id.rvCompleted);
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
            ibAddItem = (ImageButton) v.findViewById(R.id.ibAddItem);

            // Put onclicklistener onto add button to add item to list
            ibAddItem.setOnClickListener(new View.OnClickListener() {
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
            etAddItem.setOnEditorActionListener(new TextView.OnEditorActionListener(){
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

            // attaches touchHelper to the recyclerview
            ItemTouchHelper.Callback callback =
                    new SimpleItemTouchHelperCallback(itemAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(rvItems);

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
        } catch (NullPointerException e) {
            Log.d("ListDetailsFragment", "ShopList not found by id");
            throw e;
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        shopList = ShopList.getShopListById(shopListObjectId);
        // Populate the items array
        addItems();
    }


    public void addItems() {
        // Clear the items list
        items.clear();
        //addUncheckedItems();
        // getSegments does not include completed items
        // TODO: check order
        shopList.getSegments(new FindCallback<Segment>() {
            @Override
            public void done(List<Segment> objects, ParseException e) {
                items.addAll(shopList.getUncategorizedSegment().getItems());
                for (Segment segment: objects){
                    List<Item> mItems = segment.getItems();
                    items.addAll(mItems);
                }
                shopList.getCheckedItems(new FindCallback<Item>() {
                    @Override
                    public void done(List<Item> objects, ParseException e) {
                        items.addAll(objects);
                        itemAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    // add item to list
    public void addItem(){
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
}
