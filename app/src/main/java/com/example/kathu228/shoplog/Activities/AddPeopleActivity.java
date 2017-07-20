package com.example.kathu228.shoplog.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.kathu228.shoplog.Fragments.FriendlistFragment;
import com.example.kathu228.shoplog.Models.ShopList;
import com.example.kathu228.shoplog.R;

public class AddPeopleActivity extends AppCompatActivity implements FriendlistFragment.FriendFragmentListener {

    private ShopList shoplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        Intent intent = getIntent();
        shoplist = ShopList.getShopListById(intent.getStringExtra(ShopList.SHOPLIST_TAG));

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.add_people_frame, new FriendlistFragment());
        // Complete the changes added above
        ft.commit();
    }

    @Override
    public ShopList getShopList() {
        return shoplist;
    }

    @Override
    public void friendsFragmentFinished() {
        Intent i = new Intent();
        setResult(RESULT_OK,i);
        finish();
    }


}
