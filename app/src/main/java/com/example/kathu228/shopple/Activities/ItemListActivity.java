package com.example.kathu228.shopple.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kathu228.shopple.Fragments.ItemlistFragment;
import com.example.kathu228.shopple.Fragments.OcrItemListDialogFragment;
import com.example.kathu228.shopple.Fragments.TripDialogFragment;
import com.example.kathu228.shopple.Helpers.NotificationHandler;
import com.example.kathu228.shopple.Models.Item;
import com.example.kathu228.shopple.Models.ShopList;
import com.example.kathu228.shopple.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemListActivity extends AppCompatActivity implements TripDialogFragment.TripDialogListener, OcrItemListDialogFragment.OcrItemListDialogListener{

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
        //TODO - Push
//        ParseUser user = ParseInstallation.getCurrentInstallation().getParseUser("user");
//        if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
//            Log.d("trst","test");
//        ParseQuery pushQuery = ParseInstallation.getQuery();
//        pushQuery.whereEqualTo("user_name", Query.getNameOfUser(ParseUser.getCurrentUser()));
//        pushQuery.findInBackground(new FindCallback() {
//            @Override
//            public void done(List objects, ParseException e) {
//                for (Object object : objects){
//                    Log.d("Installation",((ParseInstallation) object ).getInstallationId());
//                }
//            }
//
//            @Override
//            public void done(Object o, Throwable throwable) {
//                Log.d("Installation","trial");
//            }
//        });

//        // Send push notification to query
//        HashMap<String,String> map = new HashMap<String, String>();
//        map.put("PARAM1KEY","PARAM1VALUE");
//        // here you can send parameters to your cloud code functions
//        // such parameters can be the channel name, array of users to send a push to and more...
//
//        ParseCloud.callFunctionInBackground("SendPush",map, new FunctionCallback<Object>() {
//
//            @Override
//            public void done(Object object, ParseException e) {
//                Log.d("test","success");
//            }
//        });

        showTripDialog();
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
            try{
                Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                final List<String> newItems = callMobileVisionOcr(imageBitmap);

                final FragmentManager fm = getSupportFragmentManager();
                final OcrItemListDialogFragment ocrItemListDialogFragment = OcrItemListDialogFragment.newInstance(listToArrayList(newItems));
                ocrItemListDialogFragment.setListener(this);
                new Handler().post(new Runnable() {
                    @Override public void run() {
                        // for some reason this must be called in the next loop
                        ocrItemListDialogFragment.show(fm, "fragment_ocritemlist_dialog");
                    }
                });

            }catch (Exception e){
                Log.d("ItemListActivity", getString(R.string.error));
            }

//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageBitmap.getHeight();
        }
    }


    protected List<String> callMobileVisionOcr(Bitmap imageBitmap){

        //list to be returned
        List<String> newItems = new ArrayList<>();

        // imageBitmap is the Bitmap image you're trying to process for text
        if(imageBitmap != null) {

            TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();

            if(!textRecognizer.isOperational()) {
                // Note: The first time that an app using a Vision API is installed on a
                // device, GMS will download a native libraries to the device in order to do detection.
                // Usually this completes before the app is run for the first time.  But if that
                // download has not yet completed, then the above call will not detect any text,
                // barcodes, or faces.
                // isOperational() can be used to check if the required native libraries are currently
                // available.  The detectors will automatically become operational once the library
                // downloads complete on device.
                Log.d("OCR", "Detector dependencies are not yet available.");

                // Check for low storage.  If there is low storage, the native library will not be
                // downloaded, so detection will not become operational.
                IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
                boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

                if (hasLowStorage) {
                    Log.d("OCR", "Low Storage");
                }
            }


            Frame imageFrame = new Frame.Builder()
                    .setBitmap(imageBitmap)
                    .build();

            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                List<? extends Text> lines = textBlock.getComponents();
                for (Text line : lines){
                    String lineString = line.getValue();
                    Log.d("OCR",lineString);
                    newItems.add(lineString);
                }
            }
        }
        return newItems;
    }

    private ArrayList<String> listToArrayList(List<String> list){
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.addAll(list);
        return arrayList;
    }

    private void showMyStore(){
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

    private void showNearbyStores(){
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

    private void showTripDialog() {
        FragmentManager fm = getSupportFragmentManager();
        TripDialogFragment tripDialogFragment = TripDialogFragment.newInstance();
        tripDialogFragment.setListener(this);
        tripDialogFragment.show(fm, "fragment_trip_dialog");
    }

    @Override
    public void onFinishTripDialog(Boolean favStore) {
        if (favStore){
            showMyStore();
        }
        else{
            showNearbyStores();
        }
    }

    @Override
    public void onFinishOcrItemListDialog(List<String> addItems) {
        itemlistFragment.addOCRItems(addItems);
    }

    @Override
    public void onRetryOcr() {
        dispatchTakePictureIntent();
    }
}
