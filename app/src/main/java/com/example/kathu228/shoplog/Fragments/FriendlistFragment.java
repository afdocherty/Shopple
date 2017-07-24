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
import android.widget.Toast;

import com.example.kathu228.shoplog.Helpers.FriendlistAdapter;
import com.example.kathu228.shoplog.Models.Query;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fmonsalve on 7/12/17.
 *
 */

public class FriendlistFragment extends Fragment {

    public interface FriendFragmentListener{
        ShopList getShopList();
        void friendsFragmentFinished();
    }

    private FriendlistAdapter peopleAdapter;
    private List<ParseUser> people;
    private RecyclerView rvPeople;

    private TextView tvPeopleAdded;
    private Button confirmBtn;

    private List<ParseUser> peopleAdded;
    private ShopList shopList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        peopleAdded = new ArrayList<>();
        shopList = ((FriendFragmentListener) getActivity()).getShopList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friendlist,container,false);
        //find recycler view
        rvPeople= (RecyclerView) v.findViewById(R.id.people_list);
        //init the ArrayList (data source) to users not in shoplist
        shopList.getUsersNotInList(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                people = objects;
                //construct the adapter from this data source
                peopleAdapter = new FriendlistAdapter(people,R.layout.item_person,FriendlistFragment.this);
                //RecyclerView setup (layout manager, use adapter)
                rvPeople.setLayoutManager(new LinearLayoutManager(getContext()));
                //set the adapter
                rvPeople.setAdapter(peopleAdapter);
            }
        });

        //Shows a toast with the name of the first user of this list UNNECESSARY
        shopList.getUserList(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                Toast.makeText(getContext(),Query.getNameOfUser(objects.get(0)),Toast.LENGTH_LONG).show();
            }
        });

        tvPeopleAdded = (TextView) v.findViewById(R.id.tvPeopleAdded);
        confirmBtn = (Button) v.findViewById(R.id.confirmBtn);

        tvPeopleAdded.setText("No people added");
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopList.addUsers(peopleAdded, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ((FriendFragmentListener) getActivity()).friendsFragmentFinished();
                    }
                });
            }
        });

        return v;
    }

    public void addPerson(ParseUser person){
        peopleAdded.add(person);
        tvPeopleAdded.setText(formatNumPeople(peopleAdded.size()));
    }

    public void removePerson(ParseUser person){
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
