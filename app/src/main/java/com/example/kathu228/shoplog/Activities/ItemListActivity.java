package com.example.kathu228.shoplog.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathu228.shoplog.Fragments.ItemlistFragment;
import com.example.kathu228.shoplog.Helpers.NotificationHandler;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ItemListActivity extends AppCompatActivity{

    ArrayList<Item> items;
    private ItemlistFragment itemlistFragment;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        context = this;
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_items));

        if (getIntent().hasExtra(ShopList.SHOPLIST_PENDINTENT_TAG)) {
            String updatedId = getIntent().getStringExtra(ShopList.SHOPLIST_PENDINTENT_TAG);
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace the contents of the container with the new fragment, containing the ShopList Object ID
            itemlistFragment = ItemlistFragment.newInstance(getIntent().getStringExtra(ShopList.SHOPLIST_PENDINTENT_TAG),
                    getIntent().getBooleanExtra(ShopList.SHOPLIST_NEW_TAG, false));
            ft.replace(R.id.itemlist_frame, itemlistFragment);
            // Complete the changes added above
            ft.commit();
        } else {
            Log.d("ItemListActivity", "Pending intent extra not there!");
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace the contents of the container with the new fragment, containing the ShopList Object ID
            itemlistFragment = ItemlistFragment.newInstance(getIntent().getStringExtra(ShopList.SHOPLIST_TAG),
                    getIntent().getBooleanExtra(ShopList.SHOPLIST_NEW_TAG, false));
            ft.replace(R.id.itemlist_frame, itemlistFragment);
            // Complete the changes added above
            ft.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set the toolbar title to name of shoplist
        setToolbarTitle();



//        if (getIntent().hasExtra(SHOPLIST_PENDINTENT_TAG)) {
//            String updatedObjectId = getIntent().getStringExtra(SHOPLIST_PENDINTENT_TAG);
//            if (!itemlistFragment.getShopListObjectId().equals(updatedObjectId)) {
//                // Begin the transaction
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                // Replace the contents of the container with the new fragment, containing the ShopList Object ID
//                itemlistFragment = ItemlistFragment.newInstance(getIntent().getStringExtra(ShopList.SHOPLIST_TAG),
//                        getIntent().getBooleanExtra(ShopList.SHOPLIST_PENDINTENT_TAG, false));
//                ft.replace(R.id.itemlist_frame, itemlistFragment);
//                // Complete the changes added above
//                ft.commit();
//            }
//        }
    }

    //ran when user presses the info button on the toolbar (allows user to add people to list)
    public void onListInfo(View view) {
        Intent i = new Intent(this, ListDetailsActivity.class);
        // Give the intent the ShopList Object ID
        if (getIntent().hasExtra(ShopList.SHOPLIST_PENDINTENT_TAG)) {
            i.putExtra(ShopList.SHOPLIST_TAG,getIntent().getStringExtra(ShopList.SHOPLIST_PENDINTENT_TAG));
        } else {
            i.putExtra(ShopList.SHOPLIST_TAG,getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
        }

        startActivity(i);
    }

    // Sets up the toolbar title
    private void setToolbarTitle(){
        TextView tvShopListName = (TextView)findViewById(R.id.tvListName);
        String shopListObjectId;
        if (getIntent().hasExtra(ShopList.SHOPLIST_PENDINTENT_TAG)) {
            shopListObjectId = getIntent().getStringExtra(ShopList.SHOPLIST_PENDINTENT_TAG);
        } else {
            shopListObjectId = getIntent().getStringExtra(ShopList.SHOPLIST_TAG);
        }
        String shopListName = ShopList.getShopListById(shopListObjectId).getName();
        tvShopListName.setText(shopListName);
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
                        NotificationHandler.forceEnableNotifications(itemlistFragment.shopListObjectId, context);
                        Toast.makeText(context, context.getResources().getString(R.string.notifications_enabled), Toast.LENGTH_SHORT).show();

                        // Route to hardcoded address (Safeway in Queen Anne, Seattle, WA), avoiding ferries if possible
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
                        NotificationHandler.forceEnableNotifications(itemlistFragment.shopListObjectId, context);
                        Toast.makeText(context, context.getResources().getString(R.string.notifications_enabled), Toast.LENGTH_SHORT).show();

                        // Search grocery stores that are nearby FB Seattle Office
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

    public void launchCamera(View view) {
        dispatchTakePictureIntent();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap.getHeight();
        }
    }
}
