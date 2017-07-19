package com.example.kathu228.shoplog.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.kathu228.shoplog.Fragments.ShopListFragment;
import com.example.kathu228.shoplog.R;

public class ShopListsActivity extends AppCompatActivity {

    public static final int BACK_PRESSED = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_lists);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.shoplist_frame, new ShopListFragment());
        // Complete the changes added above
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        Log.d("ShopListsActivity", "onBackPressed");
        Intent i = new Intent();
        setResult(BACK_PRESSED, i);
        super.onBackPressed();
    }
}
