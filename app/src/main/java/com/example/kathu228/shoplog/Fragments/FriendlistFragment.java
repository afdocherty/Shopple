package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kathu228.shoplog.Helpers.FriendlistAdapter;
import com.example.kathu228.shoplog.Helpers.ShoplogClient;
import com.example.kathu228.shoplog.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fmonsalve on 7/12/17.
 *
 */

public class FriendlistFragment extends Fragment {

    public FriendlistAdapter peopleAdapter;
    public List<String> people;
    public RecyclerView rvPeople;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friendlist,container,false);

        rvPeople= (RecyclerView) v.findViewById(R.id.people_list);
        //init the ArrayList (data source)
        people = Arrays.asList(ShoplogClient.getPeople());
        //construct the adapter from this data source
        peopleAdapter = new FriendlistAdapter(people,R.layout.item_person);
        //RecyclerView setup (layout manager, use adapter)
        rvPeople.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvPeople.setAdapter(peopleAdapter);

        return v;
    }
}
