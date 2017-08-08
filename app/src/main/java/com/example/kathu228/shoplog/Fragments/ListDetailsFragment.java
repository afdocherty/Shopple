package com.example.kathu228.shoplog.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kathu228.shoplog.Activities.ShopListsActivity;
import com.example.kathu228.shoplog.Helpers.DetailsAdapter;
import com.example.kathu228.shoplog.Helpers.NotificationHandler;
import com.example.kathu228.shoplog.Models.Item;
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
public class ListDetailsFragment extends Fragment implements EdittextDialogFragment.EdittextDialogListener, YesNoDialogFragment.YesNoDialogListener, IconPickerDialogFragment.IconPickerDialogListener{

    private View v;
    private LinearLayout llListNameContent;
    private RelativeLayout rlCollaboratorsTitle;
    private RelativeLayout rlNotifications;
    private RelativeLayout rlChangeIcon;
    private RelativeLayout rlLeaveList;
    private ImageView listIcon;

    private TextView tvListName;
    private TextView tvNotifications;
    private List<ParseUser> collabs;
    private RecyclerView rvCollabs;
    private DetailsAdapter collabAdapter;
    private String shopListObjectId;
    private ShopList shopList;
    private boolean nStatus;

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
        v = inflater.inflate(R.layout.fragment_list_details, container, false);

        // Make sure the list with the specified shopListObjectId exists
        shopListObjectId = getArguments().getString(ShopList.SHOPLIST_TAG);
        if (shopListObjectId == null || ShopList.getShopListById(shopListObjectId) == null) {
            throw new IllegalStateException("ShopList " + shopListObjectId + " is null");
        }

        shopList = ShopList.getShopListById(shopListObjectId);

        // Set list icon
        changeIconByNum(shopList.getIconNum());
        listIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIconPickerDialog(shopList);
            }
        });

        // List Name
        // Click on ivEditName to edit the name of the list
        tvListName = (TextView) v.findViewById(R.id.tvListName);
        final String name = shopList.getName();
        llListNameContent = (LinearLayout) v.findViewById(R.id.llListNameContent);
        llListNameContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEdittextDialog(name,shopList);
            }
        });

        // Collaborators
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

        rlCollaboratorsTitle = (RelativeLayout) v.findViewById(R.id.rlCollaboratorsTitle);
        rlCollaboratorsTitle.setOnClickListener(new View.OnClickListener() {
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

        // Notifications
        rlNotifications = (RelativeLayout) v.findViewById(R.id.rlNotifications);
        tvNotifications = (TextView) v.findViewById(R.id.tvNotifications);
        // Set the notification text to the current notification state
        rlNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle notifications
                boolean result = NotificationHandler.toggleNotifications(shopListObjectId, getActivity());
                // Set the notification text to the new notification state
                tvNotifications.setText(toggleNotificationText(result));
                setNotificationIcon(result);
            }
        });

        // Change Icon
        rlChangeIcon = (RelativeLayout) v.findViewById(R.id.rlChangeIcon);
        rlChangeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIconPickerDialog(shopList);
            }
        });

        // Leave List
        rlLeaveList = (RelativeLayout) v.findViewById(R.id.rlLeaveList);
        rlLeaveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYesNoDialog(shopList.getName(),shopList);
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        tvListName.setText(shopList.getName());
        nStatus = NotificationHandler.getNotificationStatus(shopListObjectId, getActivity());
        tvNotifications.setText(toggleNotificationText(nStatus));
        setNotificationIcon(nStatus);
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
            return getActivity().getString(R.string.disable_notifications);
        } else {
            return getActivity().getString(R.string.enable_notifications);
        }
    }

    private void setNotificationIcon (boolean enabled) {
        ImageView ivToggleNotification = (ImageView) v.findViewById(R.id.ivToggleNotification);
        if (enabled) {
            ivToggleNotification.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_notification_disable));
        } else {
            ivToggleNotification.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_notification_enable));
        }
    }

    private void showEdittextDialog(String listName, ShopList shopList){
        FragmentManager fm = getFragmentManager();
        EdittextDialogFragment edittextDialogFragment = EdittextDialogFragment.newInstance(getActivity().getString(R.string.change_list_name), shopList);
        edittextDialogFragment.setListener(this);
        edittextDialogFragment.show(fm, "fragment_edittext_dialog");
    }

    private void showIconPickerDialog(ShopList shopList){
        FragmentManager fm = getFragmentManager();
        IconPickerDialogFragment iconPickerDialogFragment = IconPickerDialogFragment.newInstance(getActivity().getString(R.string.change_list_icon), shopList);
        iconPickerDialogFragment.setListener(this);
        iconPickerDialogFragment.show(fm, "fragment_icon_picker_dialog");
    }

    // Leave List Dialog
    private void showYesNoDialog(String name, ShopList shopList){
        FragmentManager fm = getFragmentManager();
        YesNoDialogFragment yesNoDialogFragment = YesNoDialogFragment.newInstance(getActivity().getString(R.string.leaving) + " " + name, getActivity().getString(R.string.confirm_leave),null,shopList,YesNoDialogFragment.LEAVELIST);
        yesNoDialogFragment.setListener(this);
        yesNoDialogFragment.show(fm, "fragment_yesno_dialog");
    }

    @Override
    public void onFinishEdittextDialog(Boolean yes, String title, ShopList mshopList, String newName) {
        if (yes){
            shopList.setName(newName, null);
            tvListName.setText(newName);
        }
    }

    @Override
    public void onFinishIconPickerDialog(Boolean yes, int iconNum, String title, ShopList mshopList) {
        if (yes) {
            shopList.setIconNum(iconNum, null);
            changeIconByNum(iconNum);
        }
    }

    private void changeIconByNum(int num) {
        listIcon = (ImageView) v.findViewById(R.id.ivListIcon);
        switch (num) {
            case IconPickerDialogFragment.ICON_NUM_GROCERY:
                // set to grocery bag
                listIcon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_grocery_bag));
                break;
            case IconPickerDialogFragment.ICON_NUM_PARTY:
                // set to solo cup
                listIcon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_solo_cup));
                break;
            case IconPickerDialogFragment.ICON_NUM_BBQ:
                // set to bbq grill
                listIcon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_bbq_grill));
                break;
        }
    }

    @Override
    public void onFinishYesNoDialog(Boolean yes, int type, Item mitem, ShopList mshopList) {
        if (yes){
            removeUserFromShoplist();
            NotificationHandler.forceDisableNotifications(shopList.getObjectId(), getActivity());
            Intent i = new Intent(getActivity(), ShopListsActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}
