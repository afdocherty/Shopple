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

import java.util.ArrayList;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                    Log.d("LoginActivity", "List found");
                    listTest = (com.example.kathu228.shoplog.Models.List) results.get(0);
                    Log.d("LoginActivity", "listTest name: " + listTest.getName());

                    ParseRelation<ParseObject> relationListToSegment = listTest.getRelation("segments");
                    relationListToSegment.getQuery().findInBackground(new FindCallback<ParseObject>() {
                        public void done(java.util.List<ParseObject> results, ParseException e) {
                            if (e != null) {
                                // There was an error
                            } else {
                                // results have all the segments in the list
                                segTest = (Segment) results.get(0);
                                Log.d("LoginActivity", "listTest segment name: " + segTest.getName());
                                ParseRelation<ParseObject> relationSegmentToItem = segTest.getRelation("items");
                                relationSegmentToItem.getQuery().findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(java.util.List<ParseObject> results, ParseException e) {
                                        if (e != null) {
                                            // There was an error
                                        } else {
                                            // results have all the items in the segment
                                            for (ParseObject parseObject : results) {
                                                items.add((Item) parseObject);
                                            }
                                            itemTest = items.get(0);
                                            Log.d("LoginActivity", "segTest item name: " + itemTest.getBody());
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });
    }

    // Add an item to the MVP list
    private void addItemToList () {
        // TODO - Foster
    }

    // Remove an item from the MVP list
    private void removeItemFromList () {
        // TODO - Foster
    }

    // add item to list
//    public void addItem(Item item){
//        items.add(0, item);
//        // itemAdapter.notifyItemInserted(0);
//        rvItems.scrollToPosition(0);
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
