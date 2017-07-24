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
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathu228.shoplog.Helpers.ItemAdapter;
import com.example.kathu228.shoplog.Helpers.SimpleItemTouchHelperCallback;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;

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

    private String shopListObjectId;
    private FloatingActionButton fabAddSegment;

    ShopList shopList;

    public static ItemlistFragment newInstance(String shopListObjectId) {
        ItemlistFragment itemlistFragment = new ItemlistFragment();

        // Supply shopListFragment as argument
        Bundle args = new Bundle();
        args.putString(ShopList.SHOPLIST_TAG, shopListObjectId);
        itemlistFragment.setArguments(args);

        return itemlistFragment;
    }

    // Call this method to launch the edit dialog
    private void showSegmentDialog() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SegmentDialogFragment segmentDialogFragment = SegmentDialogFragment.newInstance(shopListObjectId);
        // SETS the target fragment for use later when sending results
        segmentDialogFragment.setTargetFragment(this, 0);
        segmentDialogFragment.show(fm, "fragment_segment_dialog");
    }

    @Override
    public void onFinishSegmentDialog(String segmentName) {
        shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        Log.d("ItemlistFragment", "objId: " + shopListObjectId);
        shopList = shopList.getShopListById(shopListObjectId);
        shopList.addSegment(segmentName,null);
        Toast.makeText(getContext(), "New category "+segmentName+" created", Toast.LENGTH_SHORT).show();

    }

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

        // gt id of shoplist
        String shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        Log.d("ItemlistFragment", "objId: " + shopListObjectId);
        shopList = ShopList.getShopListById(shopListObjectId);

        // construct the adapter
        itemAdapter = new ItemAdapter(items, shopList);
        // Set layout manager to position the items
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        // Attach the adapter to the recyclerview to populate items
        rvItems.setAdapter(itemAdapter);

        etAddItem = (EditText) v.findViewById(R.id.etAddItem);
        ibAddItem = (ImageButton) v.findViewById(R.id.ibAddItem);


        // Put onclicklistener onto add button to add item to list
        ibAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
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

        fabAddSegment = (FloatingActionButton) v.findViewById(R.id.fabAddSegment);
        fabAddSegment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSegmentDialog();
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

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Populate the items array
        //addItems();
    }

    public void addItems() {
        // Clear the items list
        items.clear();
        //addUncheckedItems();
        shopList.getItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                if (e == null){
                    Log.d("ItemListFragment", "Unchecked Items found!!");
                    int completed = 0;
                    for (Item object: objects){
                        if (object.isItem()){
                            if (object.isChecked()){
                                items.add(object);
                                itemAdapter.notifyItemInserted(items.size());
                            }
                            else{
                                items.add(completed,object);
                                itemAdapter.notifyItemInserted(completed);
                                completed++;
                            }
                        }

                        else if (object.isCompletedHeader()){
                            items.add(completed,object);
                            itemAdapter.notifyItemInserted(completed);
                        }

                        else if (object.isHeader()){

                        }
                    }
//                    itemAdapter.notifyDataSetChanged();

                }
                else{
                    Log.d("ItemListFragment", "Unchecked Items not found");
                }
            }
        });
    }

    public void addUncheckedItems(){
        shopList.getUncheckedItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                if (e == null){
                    Log.d("ItemListFragment", "Unchecked Items found!!");
                    items.addAll(objects);
                    itemAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d("ItemListFragment", "Unchecked Items not found");
                }
            }
        });
    }
    public void addCheckedItems(){
        shopList.getCheckedItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                if (e == null){
                    Log.d("ItemListFragment", "Unchecked Items found!!");
                    items.addAll(objects);
                    itemAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d("ItemListFragment", "Unchecked Items not found");
                }
            }
        });
    }

    // add item to list
    public void addItem(){
        String body = etAddItem.getText().toString();
        // Does not add empty item
        if (!body.equals("")) {
            Item addedItem = shopList.addItem(body,null);
            etAddItem.setText("");
            items.add(0, addedItem);
            itemAdapter.notifyItemInserted(0);
            rvItems.scrollToPosition(0);
        }
    }
}
