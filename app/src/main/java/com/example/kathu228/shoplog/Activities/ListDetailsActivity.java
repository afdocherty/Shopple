package com.example.kathu228.shoplog.Activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.kathu228.shoplog.Fragments.ListDetailsFragment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

public class ListDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_details));

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment, containing the ShopList Object ID
        ListDetailsFragment listDetailsFragment = ListDetailsFragment.newInstance(getIntent().getStringExtra(ShopList.SHOPLIST_TAG));
        ft.replace(R.id.itemlist_frame, listDetailsFragment);
        // Complete the changes added above
        ft.commit();
    }
}
