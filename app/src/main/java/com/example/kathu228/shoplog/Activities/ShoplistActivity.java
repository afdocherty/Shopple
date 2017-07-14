package com.example.kathu228.shoplog.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kathu228.shoplog.Fragments.ItemlistFragment;
import com.example.kathu228.shoplog.Models.Item;
import com.example.kathu228.shoplog.R;

import java.util.ArrayList;

public class ShoplistActivity extends AppCompatActivity{
    ArrayList<Item> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoplist);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.itemlist_frame, new ItemlistFragment());
        // Complete the changes added above
        ft.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shoplist, menu);
        return true;
    }

    //ran when user presses the info button on the toolbar (allows user to add people to list)
    public void onListInfo(MenuItem item) {
        Intent i = new Intent(this, AddPeopleActivity.class);
        startActivityForResult(i,20);
    }

    @Override
    //REQUEST CODES:
    // 20-from shop list info (AddPeopleActivity). Returns nothing
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 20) {
            //Code to be executed after you come back from the add people activity
            //(Maybe refresh the shopping list?)
            Log.d("Tag","works");
            ArrayList<String> peopleAdded = data.getStringArrayListExtra("people_added");
            Toast.makeText(this,String.valueOf(peopleAdded.size()),Toast.LENGTH_LONG).show();
        }
    }
}
