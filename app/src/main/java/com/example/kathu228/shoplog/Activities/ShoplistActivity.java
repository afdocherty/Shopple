package com.example.kathu228.shoplog.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kathu228.shoplog.Fragments.ItemlistFragment;
import com.example.kathu228.shoplog.R;

public class ShoplistActivity extends AppCompatActivity implements ItemlistFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoplist);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
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
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
