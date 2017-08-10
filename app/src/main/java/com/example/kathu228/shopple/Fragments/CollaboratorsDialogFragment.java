package com.example.kathu228.shopple.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kathu228.shopple.Helpers.ModalFriendListAdapter;
import com.example.kathu228.shopple.Models.ShopList;
import com.example.kathu228.shopple.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by afdoch on 7/20/17.
 */

public class CollaboratorsDialogFragment extends DialogFragment {

    private ModalFriendListAdapter peopleAdapter;
    private List<ParseUser> people;
    private RecyclerView rvPeople;
    private ImageView ivCloseDialog;

    private TextView tvPeopleAdded;
    private Button confirmBtn;

    private List<ParseUser> peopleAdded;
    private ShopList shopList;

    // Defines the listener interface with a method passing back data result.
    public interface CollaboratorsDialogListener {
        void onFinishCollaboratorsDialog(List<ParseUser> newPeople);
    }

    public static CollaboratorsDialogFragment newInstance(String shopListObjectId) {
        CollaboratorsDialogFragment dialogFragment = new CollaboratorsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ShopList.SHOPLIST_TAG, shopListObjectId);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_collab_dialog, container);

        // Make sure the list with the specified shopListObjectId exists
        String shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        if (shopListObjectId == null || ShopList.getShopListById(shopListObjectId) == null) {
            throw new IllegalStateException("ShopList " + shopListObjectId + " is null");
        }
        shopList = ShopList.getShopListById(getArguments().getString(ShopList.SHOPLIST_TAG));

        // Close Button
        ivCloseDialog = (ImageView) v.findViewById(R.id.ivCloseDialog);
        ivCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        peopleAdded = new ArrayList<>();
        //find recycler view
        rvPeople = (RecyclerView) v.findViewById(R.id.people_list);
        //init the ArrayList (data source) to users not in shoplist
        shopList.getUsersNotInList(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                people = objects;
                //construct the adapter from this data source
                peopleAdapter = new ModalFriendListAdapter(people, R.layout.item_person, CollaboratorsDialogFragment.this, getActivity());
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
                //Toast.makeText(getContext(),Query.getNameOfUser(objects.get(0)),Toast.LENGTH_LONG).show();
            }
        });

        tvPeopleAdded = (TextView) v.findViewById(R.id.tvPeopleAdded);
        confirmBtn = (Button) v.findViewById(R.id.confirmBtn);

        tvPeopleAdded.setText(getActivity().getString(R.string.no_people_added));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopList.addUsers(peopleAdded, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        confirmBtn.setEnabled(false);
                        // Return new people back to activity through the implemented listener
                        CollaboratorsDialogListener listener = (CollaboratorsDialogListener) getActivity();
                        listener.onFinishCollaboratorsDialog(peopleAdded);
                        // Close the dialog and return back to the parent activity
                        dismiss();
                    }
                });
            }
        });

        return v;
    }

    public void addPerson(ParseUser person) {
        peopleAdded.add(person);
        tvPeopleAdded.setText(formatNumPeople(peopleAdded.size()));
    }

    public void removePerson(ParseUser person) {
        peopleAdded.remove(person);
        tvPeopleAdded.setText(formatNumPeople(peopleAdded.size()));
    }

    private String formatNumPeople(int numPeople) {
        if (numPeople == 1)
            return getActivity().getString(R.string.one_person_added);
        else if (numPeople == 0)
            return getActivity().getString(R.string.no_people_added);
        else
            return String.format("%s " + getActivity().getString(R.string.people_added), numPeople);
    }
}
