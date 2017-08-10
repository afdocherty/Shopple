package com.example.kathu228.shopple.Helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.kathu228.shopple.Activities.ItemListActivity;
import com.example.kathu228.shopple.Models.ShopList;
import com.example.kathu228.shopple.R;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by afdoch on 7/28/17.
 */

public class NotificationHandler {

    private static final String SP_NOTIFICATIONS = "Notifications";
    private static final String SP_NOTIFICATIONS_ACTIVE = "active";
    private static final String SP_NOTIFICATIONS_REQCODES = "request_codes";
    private static final int M_NOTIFICATIONS_ID = 001;

    // Public method to toggle notifications for this list
    public static boolean toggleNotifications(String shopListObjectId, Context context) {
        SharedPreferences activeNotifications = context.getSharedPreferences(SP_NOTIFICATIONS, Context.MODE_PRIVATE);
        Set<String> set = activeNotifications.getStringSet(SP_NOTIFICATIONS_ACTIVE, null);
        if (set != null && set.contains(shopListObjectId)) {
            // Disable notifications
            // Close the notification associated with the ShopList Object ID
            closeNotification(shopListObjectId, context);
            removeFromPreferences(shopListObjectId, activeNotifications, set);
            return false;
        } else {
            // Enable notifications
            startNotification(shopListObjectId, context);
            addToPreferences(shopListObjectId, activeNotifications, set);
            return true;
        }
    }

    // Public method to force notifications on for this list
    public static void forceEnableNotifications(String shopListObjectId, Context context) {
        SharedPreferences activeNotifications = context.getSharedPreferences(SP_NOTIFICATIONS, Context.MODE_PRIVATE);
        Set<String> set = activeNotifications.getStringSet(SP_NOTIFICATIONS_ACTIVE, null);
        if (set == null || !set.contains(shopListObjectId)) {
            // Enable notifications
            startNotification(shopListObjectId, context);
            addToPreferences(shopListObjectId, activeNotifications, set);
        }
    }

    // Public method to force notifications off for this list
    public static void forceDisableNotifications(String shopListObjectId, Context context) {
        SharedPreferences activeNotifications = context.getSharedPreferences(SP_NOTIFICATIONS, Context.MODE_PRIVATE);
        Set<String> set = activeNotifications.getStringSet(SP_NOTIFICATIONS_ACTIVE, null);
        if (set != null && set.contains(shopListObjectId)) {
            // Disable notifications
            // Close the notification associated with the ShopList Object ID
            closeNotification(shopListObjectId, context);
            removeFromPreferences(shopListObjectId, activeNotifications, set);
        }
    }

    public static boolean getNotificationStatus(String shopListObjectId, Context context) {
        SharedPreferences activeNotifications = context.getSharedPreferences(SP_NOTIFICATIONS, Context.MODE_PRIVATE);
        Set<String> set = activeNotifications.getStringSet(SP_NOTIFICATIONS_ACTIVE, null);
        if (set == null) {
            return false;
        } else {
            return set.contains(shopListObjectId);
        }
    }

    // Add the current list to Shared Preferences
    private static void addToPreferences(String shopListObjectId, SharedPreferences activeNotifications, Set<String> set) {
        SharedPreferences.Editor editor = activeNotifications.edit();
        if (set != null) {
            // Add the shopListObjectId to the set
            set.add(shopListObjectId);
            editor.putStringSet(SP_NOTIFICATIONS_ACTIVE, set);
            editor.apply();
        } else {
            // Otherwise create a new set and add the shopListObjectId to it
            Set<String> newSet = new HashSet<String>();
            newSet.add(shopListObjectId);
            editor.putStringSet(SP_NOTIFICATIONS_ACTIVE, newSet);
            editor.apply();
        }
    }

    // Remove the current list from Shared Preferences (do nothing if String Set doesn't exist)
    private static void removeFromPreferences(String shopListObjectId, SharedPreferences activeNotifications, Set<String> set) {
        SharedPreferences.Editor editor = activeNotifications.edit();
        // Remove the shopListObjectId from the set
        set.remove(shopListObjectId);
        editor.putStringSet(SP_NOTIFICATIONS_ACTIVE, set);
        editor.apply();
    }

    // Create a notification
    private static void startNotification(String shopListObjectId, Context context) {
        // Get the name of the list
        String shopListName = ShopList.getShopListById(shopListObjectId).getName();

        // Build Notification , setOngoing keeps the notification always in status bar. Specify the list in the Content Text
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_double_check)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText(context.getString(R.string.notification_desc) + " " + shopListName)
                        .setOngoing(true)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setPriority(1);

        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification(StopScript.class in this case)
        Intent notificationIntent = new Intent(context, ItemListActivity.class);
        // Pass along the ShopList Tag so the notification can know which list to display
        notificationIntent.putExtra(ShopList.SHOPLIST_PENDINTENT_TAG, shopListObjectId);

        // Use a unique request code for the notification
        SharedPreferences activeNotifications = context.getSharedPreferences(SP_NOTIFICATIONS, Context.MODE_PRIVATE);
        Set<String> requestCodesSet = activeNotifications.getStringSet(SP_NOTIFICATIONS_REQCODES, null);
        int uniqueRequestCode = findUniqueRequestCode(activeNotifications, requestCodesSet);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addParentStack(ItemListActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent contentIntent = stackBuilder.getPendingIntent(uniqueRequestCode, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(shopListObjectId, M_NOTIFICATIONS_ID, mBuilder.build());
    }

    // Method to find unique int using stored elapsedRealtime values
    private static int findUniqueRequestCode(SharedPreferences activeNotifications, Set<String> requestCodesSet) {
        int guess = (int) SystemClock.elapsedRealtime();
        String guessString = "" + guess;
        SharedPreferences.Editor editor = activeNotifications.edit();
        if (requestCodesSet == null) {
            Set<String> newSet = new HashSet<String>();
            newSet.add(guessString);
            editor.putStringSet(SP_NOTIFICATIONS_REQCODES, newSet);
            editor.apply();
            return guess;
        } else if (!requestCodesSet.contains(guessString)) {
            requestCodesSet.add(guessString);
            editor.putStringSet(SP_NOTIFICATIONS_REQCODES, requestCodesSet);
            editor.apply();
            return guess;
        } else {
            return findUniqueRequestCode(activeNotifications, requestCodesSet);
        }
    }

    private static void closeNotification(String shopListObjectId, Context context) {
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.cancel(shopListObjectId, M_NOTIFICATIONS_ID);
    }
}
