package com.example.kathu228.shoplog.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.kathu228.shoplog.Helpers.ItemAdapter;
import com.example.kathu228.shoplog.Helpers.ShoplogClient;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.Segment;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ItemlistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemlistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemlistFragment extends Fragment implements ItemAdapter.ItemAdapterListener {
    // parameters
    private SwipeRefreshLayout swipeContainer;
    private ShoplogClient client;
    RecyclerView rvItems;
    EditText etAddItem;
    ImageButton ibAddItem;
    ItemAdapter itemAdapter;
    ArrayList<Item> items;

    // TODO - Temporary for MVP
    com.example.kathu228.shoplog.Models.List list1;
    com.example.kathu228.shoplog.Models.List listTest;
    Segment seg1;
    Segment segTest;
    Item item1;
    Item itemTest;
    ParseUser userTest;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ItemlistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemlistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemlistFragment newInstance(String param1, String param2) {
        ItemlistFragment fragment = new ItemlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_itemlist, container, false);
        // find the RecyclerView
        rvItems = (RecyclerView) v.findViewById(R.id.rvItem);
        // find the edittext to add item
        etAddItem = (EditText) v.findViewById(R.id.etAddItem);
        // find the add button
        ibAddItem = (ImageButton) v.findViewById(R.id.ibAddItem);
        // initialize the array of items
        items = new ArrayList<>();
        // construct the adapter
        itemAdapter = new ItemAdapter(items, this);

        ibAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = etAddItem.getText().toString();
                Item addedItem = new Item();
                addedItem.setBody(body);
                etAddItem.setText("");
                addItem(addedItem);
            }
        });

        // TODO: setup recycler and adapter

        addItems();

        return v;
    }

    public void addItems() {

        // TODO - Temporary for MVP to get Items for ONLY the first list -> first segment
        ParseUser user = ParseUser.getCurrentUser();

        queryListForItem();
    }

    private void queryListForItem() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("List");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(java.util.List<ParseObject> results, ParseException e) {
                if (e == null) {
                    Log.d("ItemListFragment", "List found");
                    // Grab the first list (for MVP) - TODO change
                    listTest = (com.example.kathu228.shoplog.Models.List) results.get(0);
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
                                            }
//                                            itemTest = items.get(0);
//                                            Log.d("ItemListFragment", "segTest item name: " + itemTest.getBody());
                                        }
                                    }
                                });

                            }
                        }
                    });
                } else {
                    Log.d("ItemListFragment", "List not found. Error: " + e.toString());
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemSelected(View view, int position) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
