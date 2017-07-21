package com.example.kathu228.shoplog.Activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kathu228.shoplog.Fragments.ItemlistFragment;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

import java.util.ArrayList;

public class ItemListActivity extends AppCompatActivity{

    ArrayList<Item> items;
    private boolean notificationsEnabled;
    private static final int mNotificationsId = 001; // Constant id to use for ItemListActivity
    private MenuItem notificationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(getIntent().getStringExtra("listName"));
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment, containing the ShopList Object ID
        ItemlistFragment itemlistFragment = ItemlistFragment.newInstance(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
        ft.replace(R.id.itemlist_frame, itemlistFragment);
        // Complete the changes added above
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_itemlist, menu);
        return true;
    }

    private void setNotificationBtnColor() {
        notificationBtn = (MenuItem) findViewById(R.id.notification_btn);
        if (notificationsEnabled) {
            notificationBtn.setIcon(R.drawable.ic_add_alert_green);
        } else {
            notificationBtn.setIcon(R.drawable.ic_add_alert_white);
        }
    }

    //ran when user presses the info button on the toolbar (allows user to add people to list)
    public void onListInfo(MenuItem item) {
        Intent i = new Intent(this, AddPeopleActivity.class);
        // Give the intent the ShopList Object ID
        i.putExtra(ShopList.SHOPLIST_TAG,getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
        startActivityForResult(i,20);
    }

    // Ran when user clicks on notification button on the toolbar (to activate notifications for this list)
    public void activateNotifcations(MenuItem item) {
        if (notificationsEnabled) {
            // Close the notification associated with the ShopList Object ID
            closeNotification(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
            // Disable notifications
            item.setIcon(R.drawable.ic_add_alert_white);
            invalidateOptionsMenu(); // Forces call to onPrepareOptionsMenu() to update the menu
            notificationsEnabled = false;
        } else {
            // Enable notifications
            startNotification(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
            item.setIcon(R.drawable.ic_add_alert_green);
            invalidateOptionsMenu(); // Forces call to onPrepareOptionsMenu() to update the menu
            notificationsEnabled = true;
        }
    }

    private void startNotification(String shopListObjectId) {
        // Get the name of the list
        String shopListName = ShopList.getShopListById(shopListObjectId).getName();

        // Build Notification , setOngoing keeps the notification always in status bar. Specify the list in the Content Text
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_check_box)
                        .setContentTitle("ShopLog")
                        .setContentText("Click to go to " + shopListName)
                        .setOngoing(true);

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

    @Override
    //REQUEST CODES:
    // 20-from shop list info (AddPeopleActivity). Returns nothing
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 20) {
            //Code to be executed after you come back from the add people activity
            //(Maybe refresh the shopping list?)
            // TODO Delete this and move to ItemList > Details > AddPeople
        }
    }
}
