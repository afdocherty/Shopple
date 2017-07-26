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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kathu228.shoplog.Activities.AddPeopleActivity;
import com.example.kathu228.shoplog.Activities.ShopListsActivity;
import com.example.kathu228.shoplog.Helpers.DetailsAdapter;
import com.example.kathu228.shoplog.Helpers.ShoplogClient;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import static com.example.kathu228.shoplog.Models.ShopList.getShopListById;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListDetailsFragment extends Fragment {

    private CardView cvCollaborators;
    private CardView cvLeaveList;

    private ImageView ivEditName;
    private TextView tvListName;

    private List<String> collabs;
    private RecyclerView rvCollabs;
    private DetailsAdapter collabAdapter;
    private String shopListObjectId;

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
        try {
            shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);

            //find recycler view
            rvCollabs = (RecyclerView) v.findViewById(R.id.rvCollabs);
            //init the ArrayList (data source)
            collabs = Arrays.asList(ShoplogClient.getPeople());
            //construct the adapter from this data source
            collabAdapter = new DetailsAdapter(collabs, R.layout.item_collab, this);
            //RecyclerView setup (layout manager, use adapter)
            rvCollabs.setLayoutManager(new LinearLayoutManager(getContext()));
            //set the adapter
            rvCollabs.setAdapter(collabAdapter);

            // Click on ivEditName to edit the name of the list
            tvListName = (TextView) v.findViewById(R.id.tvListName);
            tvListName.setText((ShopList.getShopListById(shopListObjectId)).getName());
            ivEditName = (ImageView) v.findViewById(R.id.ivEditName);
            ivEditName.setOnClickListener(new View.OnClickListener() {
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

            cvCollaborators = (CardView) v.findViewById(R.id.cvCollaborators);
            cvCollaborators.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), AddPeopleActivity.class);
                    // Give the intent the ShopList Object ID
                    i.putExtra(ShopList.SHOPLIST_TAG, shopListObjectId);
                    startActivity(i);

                    // TODO switch to modal overlay
//                CollaboratorsDialogFragment composeDialogFragment = CollaboratorsDialogFragment.newInstance(getArguments().getString(ShopList.SHOPLIST_TAG));
//                composeDialogFragment.show(getActivity().getSupportFragmentManager(), "fragment_collab_dialog");
                }
            });

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
        } catch (NullPointerException e) {
            Log.d("ListDetailsFragment", "ShopList not found by id");
            throw e;
        }

        return v;

    }

    // Remove the current user from the current ShopList
    private void removeUserFromShoplist() {
        // Get the shoplist by ID and remove the current user from that list
        getShopListById(getArguments().getString(ShopList.SHOPLIST_TAG)).removeUser(ParseUser.getCurrentUser(), null);
    }

}
