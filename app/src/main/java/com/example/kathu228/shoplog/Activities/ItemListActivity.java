package com.example.kathu228.shoplog.Activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathu228.shoplog.Fragments.ItemlistFragment;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

import java.util.ArrayList;

public class ItemListActivity extends AppCompatActivity{

    ArrayList<Item> items;
    private boolean notificationsEnabled; // TODO - make field of ShopList
    private static final int mNotificationsId = 001; // Constant id to use for ItemListActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_items));

        // Set the color of the Notification Button ImageView
        setNotificationBtnColor();

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment, containing the ShopList Object ID
        ItemlistFragment itemlistFragment = ItemlistFragment.newInstance(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
        ft.replace(R.id.itemlist_frame, itemlistFragment);
        // Complete the changes added above
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set the toolbar title to name of shoplist
        setToolbarTitle();
    }

    // Set the color of the notification button in the Toolbar
    private void setNotificationBtnColor() {
        ImageView ivNotificationBtn = (ImageView) findViewById(R.id.ivNotificationBtn);
        if (notificationsEnabled) {
            ivNotificationBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_alert_green));
        } else {
            ivNotificationBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_alert_white));
        }
    }

    //ran when user presses the info button on the toolbar (allows user to add people to list)
    public void onListInfo(View view) {
        Intent i = new Intent(this, ListDetailsActivity.class);
        // Give the intent the ShopList Object ID
        i.putExtra(ShopList.SHOPLIST_TAG,getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
        startActivity(i);
    }

    // Ran when user clicks on notification button on the toolbar (to activate notifications for this list)
    public void toggleNotifcations(View view) {
        if (notificationsEnabled) {
            // Disable notifications
            // Close the notification associated with the ShopList Object ID
            closeNotification(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
            ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.ic_add_alert_white));
            notificationsEnabled = false;
        } else {
            // Enable notifications
            startNotification(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
            ((ImageView) view).setImageDrawable(getResources().getDrawable(R.drawable.ic_add_alert_green));
            notificationsEnabled = true;
        }
    }

    // Sets up the toolbar title
    private void setToolbarTitle(){
        TextView tvShopListName = (TextView)findViewById(R.id.tvListName);
        String shopListObjectId = getIntent().getStringExtra(ShopList.SHOPLIST_TAG);
        String shopListName = ShopList.getShopListById(shopListObjectId).getName();
        tvShopListName.setText(shopListName);
    }

    // Create a notification
    private void startNotification(String shopListObjectId) {
        // Get the name of the list
        String shopListName = ShopList.getShopListById(shopListObjectId).getName();

        // Build Notification , setOngoing keeps the notification always in status bar. Specify the list in the Content Text
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_check_box)
                        .setContentTitle("ShopLog")
                        .setContentText("Click to go to " + shopListName)
                        .setOngoing(true)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setPriority(1);

        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification(StopScript.class in this case)
        Intent notificationIntent = new Intent(this, ItemListActivity.class);
        // Pass along the ShopList Tag so the notification can know which list to display
        notificationIntent.putExtra(ShopList.SHOPLIST_TAG, getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(shopListObjectId, mNotificationsId, mBuilder.build());
    }

    private void closeNotification(String shopListObjectId) {
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.cancel(shopListObjectId, mNotificationsId);
    }

    // Initialize dialog to start map for My Store or Nearby Stores
    public void initMaps(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set titlexw
        alertDialogBuilder.setTitle("Start my Trip");

        // set dialog message
        alertDialogBuilder
                .setMessage("Route me to:")
                .setCancelable(true)
                .setPositiveButton("My Store",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // Activate notifications automatically
                        autoActivateNotifications();
                        // Route to hardcoded address (Safeway in Queen Anne, Seattle, WA), avoiding ferries if possible
                        // TODO - test this code
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(getResources().getString(R.string.my_store_address)) + "&avoid=f");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                })
                .setNegativeButton("Nearby Stores",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // Activate notifications automatically
                        autoActivateNotifications();
                        // Search grocery stores that are nearby FB Seattle Office
                        // TODO - test this code
                        Uri gmmIntentUri = Uri.parse("geo:" + getResources().getString(R.string.FB_lat_long) + "?q=" + getResources().getString(R.string.search_entry));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    // Turn notifications on if not already activated
    private void autoActivateNotifications() {
        if (!notificationsEnabled) {
            // Enable notifications
            startNotification(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
            notificationsEnabled = true;
            setNotificationBtnColor();
            Toast.makeText(this, getResources().getString(R.string.notifications_enabled), Toast.LENGTH_SHORT).show();
        }
    }
}
