package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.kathu228.shoplog.Helpers.DetailsAdapter;
import com.example.kathu228.shoplog.Helpers.ShoplistAdapter;
import com.example.kathu228.shoplog.Helpers.ShoplogClient;
import com.example.kathu228.shoplog.R;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListDetailsFragment extends Fragment {

    public EditText etListName;
    public ImageButton ibEditName;

    public CardView cvColor;

    public CardView cvLeaveList;

    public Button bEditCollab;
    public ImageButton ibEditCollab;

    public List<String> collabs;
    public RecyclerView rvCollabs;
    public DetailsAdapter collabAdapter;
    public String shopListObjectId;

    public static ListDetailsFragment newInstance(String shopListObjectId) {
        ListDetailsFragment listDetailsFragment = new ListDetailsFragment();
//        Bundle args = new Bundle();
//        args.putString(ShoplistAdapter.SHOPLIST_TAG, shopListObjectId);
//        ListDetailsFragment.setArguments(args);
        return listDetailsFragment;
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
        View v = inflater.inflate(R.layout.fragment_list_details, container, false);
        //find recycler view
        rvCollabs= (RecyclerView) v.findViewById(R.id.rvCollabs);
        //init the ArrayList (data source)
        collabs = Arrays.asList(ShoplogClient.getPeople());
        //construct the adapter from this data source
        collabAdapter = new DetailsAdapter(collabs,R.layout.item_collab,this);
        //RecyclerView setup (layout manager, use adapter)
        rvCollabs.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvCollabs.setAdapter(collabAdapter);

        shopListObjectId = getArguments().getString(ShoplistAdapter.SHOPLIST_TAG);

        etListName = (EditText) v.findViewById(R.id.etListName);
        ibEditName = (ImageButton) v.findViewById(R.id.ibEditName);

        cvColor = (CardView) v.findViewById(R.id.cvColor);

        cvLeaveList = (CardView) v.findViewById(R.id.cvLeaveList);

        bEditCollab = (Button) v.findViewById(R.id.bEditCollab);
        ibEditCollab = (ImageButton) v.findViewById(R.id.ibEditCollab);


        return v;

    }

}
