package com.example.kathu228.shoplog.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kathu228.shoplog.Helpers.FriendlistAdapter;
import com.example.kathu228.shoplog.Helpers.ShoplogClient;
import com.example.kathu228.shoplog.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fmonsalve on 7/12/17.
 *
 */

public class FriendlistFragment extends Fragment {

    public interface FriendFragmentListener{
        void friendsFragmentFinished(ArrayList<String> peopleAdded);
    }

    public FriendlistAdapter peopleAdapter;
    public List<String> people;
    public RecyclerView rvPeople;

    public TextView tvPeopleAdded;
    public Button confirmBtn;

    public ArrayList<String> peopleAdded;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        peopleAdded = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friendlist,container,false);
        //find recycler view
        rvPeople= (RecyclerView) v.findViewById(R.id.people_list);
        //init the ArrayList (data source)
        people = Arrays.asList(ShoplogClient.getPeople());
        //construct the adapter from this data source
        peopleAdapter = new FriendlistAdapter(people,R.layout.item_person,this);
        //RecyclerView setup (layout manager, use adapter)
        rvPeople.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvPeople.setAdapter(peopleAdapter);

        tvPeopleAdded = (TextView) v.findViewById(R.id.tvPeopleAdded);
        confirmBtn = (Button) v.findViewById(R.id.confirmBtn);

        tvPeopleAdded.setText("No people added");
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((FriendFragmentListener) getActivity()).friendsFragmentFinished(peopleAdded);
            }
        });

        return v;
    }

    public void addPerson(String person){
        peopleAdded.add(person);
        tvPeopleAdded.setText(formatNumPeople(peopleAdded.size()));
    }

    public void removePerson(String person){
        peopleAdded.remove(person);
        tvPeopleAdded.setText(formatNumPeople(peopleAdded.size()));
    }

    private String formatNumPeople(int numPeople){
        if (numPeople == 1)
            return "1 person added";
        else if (numPeople == 0)
            return "No people added";
        else
            return String.format("%s people added",numPeople);
    }

}
