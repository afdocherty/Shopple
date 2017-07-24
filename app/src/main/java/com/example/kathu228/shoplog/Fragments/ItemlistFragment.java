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
import com.example.kathu228.shoplog.Helpers.ShoplogClient;
import com.example.kathu228.shoplog.Helpers.SimpleItemTouchHelperCallback;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
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
    private ShoplogClient client;
    private RecyclerView rvItems;
    private EditText etAddItem;
    private ImageButton ibAddItem;
    private ItemAdapter itemAdapter;
    private ArrayList<Item> items;
    private String shopListObjectId;
    private FloatingActionButton fabAddSegment;

    ShopList listTest;

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
        listTest = listTest.getShopListById(shopListObjectId);
        listTest.addSegment(segmentName,null);
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
        shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        Log.d("ItemlistFragment", "objId: " + shopListObjectId);
        listTest = listTest.getShopListById(shopListObjectId);
        // construct the adapter
        itemAdapter = new ItemAdapter(items, listTest);
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
                String body = etAddItem.getText().toString();
                // Does not add empty item
                if (!body.equals("")) {
                    Item addedItem = listTest.addItem(body, null);
                    etAddItem.setText("");
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
                        Item addedItem = listTest.addItem(body, null);
                        etAddItem.setText("");
                        addItem(addedItem);
                    }
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
        addItems();
    }


    public void addItems() {

        // Clear the items list
        items.clear();
        //addUncheckedItems();
        listTest.getItems(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                if (e == null){
                    Log.d("ItemListFragment", "Unchecked Items found!!");
                    // indUncat keeps track of where to add the uncategorized items
                    int indUncat = 0;
                    // indLastUncomp keeps track of where the last uncompleted item is
                    int indLastUncomp = 0;
                    for (Item object: objects){

                        if (object.isItem()){
//                            if (object.isChecked()){
//                                items.add(object);
//                                itemAdapter.notifyItemInserted(items.size());
//                            }
//                            else{
//                                items.add(completed,object);
//                                itemAdapter.notifyItemInserted(completed);
//                                completed++;
//                            }
                            Segment segment = object.getSegment();
                            if (object.isChecked()) {
                                Item completedHeader = listTest.getCompletedSegment().getHeader();
                                if (items.indexOf(completedHeader) == -1) {
                                    items.add(completedHeader);
                                    items.add(object);
                                } else {
                                    items.add(object);
                                }
//                                items.add(object);
                            }
                            // if object is uncategorized, add to beginning of uncategorized
                            else if (segment.isUncategorized()){
                                items.add(0,object);
//                                itemAdapter.notifyItemInserted(0);
                                indUncat++;
                                indLastUncomp++;
                            }
                            else{
                                Item segHeader = segment.getHeader();
                                // if segment not in list, add segment item to list and add object under it
                                // else, find index of segment header and add item to the end
                                int indSeg = items.indexOf(segHeader);
                                if (indSeg==-1){
                                    // if uncompleted, add after uncategorized items
                                    // else, add completed header and item to the end
                                    if (segHeader.isHeader()){
                                        items.add(indUncat,segHeader);
                                        items.add(indUncat+1,object);
                                        indLastUncomp+=2;
                                    }
                                    else{
                                        items.add(segHeader);
                                        items.add(object);
                                    }
                                }
                                else{
                                    if (segHeader.isHeader()){
                                        items.add(indSeg+1,object);
                                        indLastUncomp++;
                                    }
                                    else{
                                        items.add(object);

                                    }
                                }
                            }
                        }

                        else if (object.isCompletedHeader()){
                            if (items.indexOf(object)==-1){
                                items.add(indLastUncomp, object);
                            }
                        }

                        else if (object.isHeader()){
                            if (items.indexOf(object)==-1) {
                                items.add(indUncat, object);
//                                itemAdapter.notifyItemInserted(indLastUncomp);
                                indLastUncomp++;
                            }
                        }
                    }
                    itemAdapter.notifyDataSetChanged();

                }
                else{
                    Log.d("ItemListFragment", "Unchecked Items not found");
                }
            }
        });
    }


    public void addUncheckedItems(){
        listTest.getUncheckedItems(new FindCallback<Item>() {
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
        listTest.getCheckedItems(new FindCallback<Item>() {
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
    public void addItem(Item item){
        items.add(0, item);
        itemAdapter.notifyItemInserted(0);
        rvItems.scrollToPosition(0);
    }
}
