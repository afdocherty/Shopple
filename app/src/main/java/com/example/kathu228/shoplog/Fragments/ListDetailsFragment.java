package com.example.kathu228.shoplog.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kathu228.shoplog.Activities.ShopListsActivity;
import com.example.kathu228.shoplog.Helpers.DetailsAdapter;
import com.example.kathu228.shoplog.Helpers.NotificationHandler;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.kathu228.shoplog.Models.ShopList.SHOPLIST_TAG;
import static com.example.kathu228.shoplog.Models.ShopList.getShopListById;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListDetailsFragment extends Fragment {

    private CardView cvListName;
    private CardView cvNotifications;
    private CardView cvCollaborators;
    private CardView cvLeaveList;

    private TextView tvListName;
    private TextView tvNotifications;

    private List<ParseUser> collabs;
    private RecyclerView rvCollabs;
    private DetailsAdapter collabAdapter;
    private String shopListObjectId;

    private static final int M_NOTIFICATIONS_ID = 001; // Constant id to use for ItemListActivity

    public static ListDetailsFragment newInstance(String shopListObjectId) {
        ListDetailsFragment listDetailsFragment = new ListDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ShopList.SHOPLIST_TAG, shopListObjectId);
        listDetailsFragment.setArguments(args);
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

        // Make sure the list with the specified shopListObjectId exists
        shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        if (ShopList.getShopListById(shopListObjectId) == null) {
            throw new IllegalStateException("ShopList " + shopListObjectId + " is null");
        }

        // List Name Card
        // Click on ivEditName to edit the name of the list
        tvListName = (TextView) v.findViewById(R.id.tvListName);
        tvListName.setText((ShopList.getShopListById(shopListObjectId)).getName());
        cvListName = (CardView) v.findViewById(R.id.cvListName);
        cvListName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Change List Name");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        (ShopList.getShopListById(shopListObjectId)).setName(input.getText().toString(), null);
                        tvListName.setText(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        // Notifications Card
        cvNotifications = (CardView) v.findViewById(R.id.cvNotifications);
        tvNotifications = (TextView) v.findViewById(R.id.tvNotifications);
        // Set the notification text to the current notification state
        tvNotifications.setText(toggleNotificationText(NotificationHandler.getNotificationStatus(shopListObjectId, getActivity())));
        cvNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle notifications
                boolean result = NotificationHandler.toggleNotifications(shopListObjectId, getActivity());
                // Set the notification text to the new notification state
                tvNotifications.setText(toggleNotificationText(result));
            }
        });

        // Collaborators Card
        //find recycler view
        rvCollabs = (RecyclerView) v.findViewById(R.id.rvCollabs);
        //init the ArrayList (data source)
        collabs = new ArrayList<>();
        //construct the adapter from this data source
        collabAdapter = new DetailsAdapter(collabs, R.layout.item_collab, this);
        //RecyclerView setup (layout manager, use adapter)
        rvCollabs.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvCollabs.setAdapter(collabAdapter);

        cvCollaborators = (CardView) v.findViewById(R.id.cvCollaborators);
        cvCollaborators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent i = new Intent(getActivity(), AddPeopleActivity.class);
//                    // Give the intent the ShopList Object ID
//                    i.putExtra(ShopList.SHOPLIST_TAG, shopListObjectId);
//                    startActivity(i);
//
                // Open Modal Overaly (CollaboratorsDialogFragment)
                CollaboratorsDialogFragment composeDialogFragment = CollaboratorsDialogFragment.newInstance(getArguments().getString(ShopList.SHOPLIST_TAG));
                composeDialogFragment.show(getActivity().getSupportFragmentManager(), "fragment_collab_dialog");
            }
        });

        // Leave List Card
        cvLeaveList = (CardView) v.findViewById(R.id.cvLeaveList);
        cvLeaveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prompt the user to confirm leaving the list
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked, remove from list
                                removeUserFromShoplist();
                                Intent i = new Intent(getActivity(), ShopListsActivity.class);
                                startActivity(i);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked, close the dialog
                                dialog.cancel();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        addCollaborators();
    }

    // Update the collaborators of the list
    private void addCollaborators() {
        // First clear list
        collabs.clear();
        // Then get the user list and update the collabs array & adapter
        (ShopList.getShopListById(getArguments().getString(SHOPLIST_TAG))).getUserList(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                collabs.addAll(objects);
                collabAdapter.notifyDataSetChanged();
            }
        });
    }

    // Locally insert people into array
    public void addNewPeople(List<ParseUser> newPeople) {
        collabs.addAll(newPeople);
        collabAdapter.notifyDataSetChanged();
    }

    // Remove the current user from the current ShopList
    private void removeUserFromShoplist() {
        // Get the shoplist by ID and remove the current user from that list
        getShopListById(getArguments().getString(ShopList.SHOPLIST_TAG)).removeUser(ParseUser.getCurrentUser(), null);
    }

    private String toggleNotificationText(boolean enabled) {
        if (enabled) {
            return "Disable Notifications";
        } else {
            return "Enable Notifications";
        }
    }

}
