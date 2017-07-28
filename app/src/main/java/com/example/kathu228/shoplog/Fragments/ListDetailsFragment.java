package com.example.kathu228.shoplog.Fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kathu228.shoplog.Activities.ItemListActivity;
import com.example.kathu228.shoplog.Activities.ShopListsActivity;
import com.example.kathu228.shoplog.Helpers.DetailsAdapter;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.NOTIFICATION_SERVICE;
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

    private ImageView ivEditName;
    private TextView tvListName;

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
        cvNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNotifcations();
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

    // Ran when user clicks on notification button on the toolbar (to activate notifications for this list)
    public void toggleNotifcations() {
        SharedPreferences activeNotifications = getActivity().getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        Set<String> set = activeNotifications.getStringSet("active", null);
        if (set != null && set.contains(shopListObjectId)) {
            // Disable notifications
            // Close the notification associated with the ShopList Object ID
            closeNotification(shopListObjectId);
            removeFromPreferences(shopListObjectId, activeNotifications, set);
        } else {
            // Enable notifications
            startNotification(shopListObjectId);
            addToPreferences(shopListObjectId, activeNotifications, set);
        }
    }

    // Add the current list to Shared Preferences
    private void addToPreferences(String shopListObjectId, SharedPreferences activeNotifications, Set<String> set) {
        SharedPreferences.Editor editor = activeNotifications.edit();
        if (set != null) {
            // Add the shopListObjectId to the set
            set.add(shopListObjectId);
            editor.putStringSet("active", set);
            editor.apply();
        } else {
            // Otherwise create a new set and add the shopListObjectId to it
            Set<String> newSet = new HashSet<String>();
            newSet.add(shopListObjectId);
            editor.putStringSet("active", newSet);
            editor.apply();
        }
    }

    // Remove the current list from Shared Preferences (do nothing if String Set doesn't exist)
    private void removeFromPreferences(String shopListObjectId, SharedPreferences activeNotifications, Set<String> set) {
        SharedPreferences.Editor editor = activeNotifications.edit();
        // Remove the shopListObjectId from the set
        set.remove(shopListObjectId);
        editor.putStringSet("active", set);
        editor.apply();
    }

    // Create a notification
    private void startNotification(String shopListObjectId) {
        // Get the name of the list
        String shopListName = ShopList.getShopListById(shopListObjectId).getName();

        // Build Notification , setOngoing keeps the notification always in status bar. Specify the list in the Content Text
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setContentTitle("ShopLog")
                        .setContentText("Click to go to " + shopListName)
                        .setOngoing(true)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setPriority(1);

        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification(StopScript.class in this case)
        Intent notificationIntent = new Intent(getActivity(), ItemListActivity.class);
        // Pass along the ShopList Tag so the notification can know which list to display
        notificationIntent.putExtra(ShopList.SHOPLIST_PENDINTENT_TAG, shopListObjectId);

        // Use a unique request code for the notification
        SharedPreferences activeNotifications = getActivity().getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        Set<String> requestCodesSet = activeNotifications.getStringSet("request_codes", null);
        int uniqueRequestCode = findUniqueRequestCode(activeNotifications, requestCodesSet);
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), uniqueRequestCode,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(shopListObjectId, M_NOTIFICATIONS_ID, mBuilder.build());
    }

    // Method to find unique int using stored elapsedRealtime values
    private int findUniqueRequestCode(SharedPreferences activeNotifications, Set<String> requestCodesSet) {
        int guess = (int) SystemClock.elapsedRealtime();
        String guessString = "" + guess;
        SharedPreferences.Editor editor = activeNotifications.edit();
        if (requestCodesSet == null) {
            Set<String> newSet = new HashSet<String>();
            newSet.add(guessString);
            editor.putStringSet("request_codes", newSet);
            editor.apply();
            return guess;
        } else if (!requestCodesSet.contains(guessString)) {
            requestCodesSet.add(guessString);
            editor.putStringSet("request_codes", requestCodesSet);
            editor.apply();
            return guess;
        } else {
            return findUniqueRequestCode(activeNotifications, requestCodesSet);
        }
    }

    private void closeNotification(String shopListObjectId) {
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.cancel(shopListObjectId, M_NOTIFICATIONS_ID);
    }

//    // Turn notifications on if not already activated
//    private void autoActivateNotifications() {
//        if (!notificationsEnabled) {
//            // Enable notifications
//            startNotification(shopListObjectId);
//            notificationsEnabled = true;
//            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.notifications_enabled), Toast.LENGTH_SHORT).show();
//        }
//    }

    // Set the color of the notification button in the Toolbar
//    private void setNotificationBtnColor() {
//        ImageView ivNotificationBtn = (ImageView) findViewById(R.id.ivNotificationBtn);
//        if (notificationsEnabled) {
//            ivNotificationBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_alert_green));
//        } else {
//            ivNotificationBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_alert));
//        }
//    }

}
